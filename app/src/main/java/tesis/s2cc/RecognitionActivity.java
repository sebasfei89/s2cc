package tesis.s2cc;

import android.content.Context;
import android.content.res.ColorStateList;
import android.media.AudioManager;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

public class RecognitionActivity extends AppCompatActivity implements ClosedCaptionGenerator.Listener {

	private static final String TAG = "RecognitionActivity";

	private FloatingActionButton mToggleBtn;
	private FloatingActionButton mAddWordBtn;
	private FloatingActionButton mDeleteWordBtn;
	private FloatingActionButton mNewWordBtn;
	private FlexboxLayout mSepButtons;
	private FlexboxLayout mContainer;
	private int mOriginalVolume;
	private boolean mIsRecognizing;
	private ClosedCaptionGenerator mCCGenerator;
	private CCTokenView mSelectedWord;
	private ArrayList<CCTokenView> mTokenViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_recognition );

		SpeechRecognizer recognizer = SpeechRecognizer.createSpeechRecognizer( getApplicationContext() );
		mCCGenerator = new ClosedCaptionGenerator( recognizer, this );

		mContainer = (FlexboxLayout) findViewById( R.id.RecognitionResults );
		mSepButtons = (FlexboxLayout) findViewById( R.id.sep_buttons );
		mToggleBtn = (FloatingActionButton) findViewById( R.id.start_stop );
		mAddWordBtn = (FloatingActionButton) findViewById( R.id.add_word );
		mDeleteWordBtn = (FloatingActionButton) findViewById( R.id.delete_word );
		mNewWordBtn =  (FloatingActionButton) findViewById( R.id.new_word);

		mTokenViews = new ArrayList<>();
		mSelectedWord = null;
		mIsRecognizing = false;
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
		muteVolume();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
		mCCGenerator.stop();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop");
		super.onStop();
		restoreVolume();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
		mCCGenerator.destroy();
	}

	public void onToggleRecognition(View view) {
		Log.v(TAG, "onToggleRecognition");
		mToggleBtn.setEnabled(false);
		hideActionButtons();
		if (mIsRecognizing) {
			mToggleBtn.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_light)));
			mCCGenerator.stop();
		} else {
			mToggleBtn.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
			mCCGenerator.start();
		}
	}

	public void showActionButtons( CCTokenView word, boolean showWordButtons, boolean showSeparatorButtons) {
		mSelectedWord = word;
		mAddWordBtn.setVisibility( (showWordButtons || showSeparatorButtons) ? View.VISIBLE : View.GONE );
		mDeleteWordBtn.setVisibility( showWordButtons ? View.VISIBLE : View.GONE );
		int sepVisibility = showSeparatorButtons ? View.VISIBLE : View.GONE;
		mNewWordBtn.setVisibility( sepVisibility );
		mSepButtons.setVisibility( sepVisibility );
	}

	public void hideActionButtons() {
		mSelectedWord = null;
		mAddWordBtn.setVisibility(View.GONE);
		mDeleteWordBtn.setVisibility(View.GONE);
		mNewWordBtn.setVisibility(View.GONE);
		mSepButtons.setVisibility(View.GONE);
	}

	@Override
	public void onRecognitionStarted() {
		Log.v(TAG, "onRecognitionStarted");
		mToggleBtn.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
		mToggleBtn.setEnabled(true);
		mIsRecognizing = true;
	}

	@Override
	public void onRecognitionStopped() {
		Log.v(TAG, "onRecognitionStopped");
		mToggleBtn.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_green_light)));
		mToggleBtn.setEnabled(true);
		mIsRecognizing = false;
	}

	@Override
	public void onWordRecognized( RecognizedWord rWord ) {
		Log.v(TAG, "onWordRecognized: " + rWord.getWord());

		CCTokenView tokenView = new CCTokenView(this, new CCToken(rWord.getWord()));
		mTokenViews.add( tokenView );
		tokenView.show(mContainer);
	}

	private void muteVolume() {
		AudioManager aMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mOriginalVolume = aMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		aMgr.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
	}

	private void restoreVolume() {
		AudioManager aMgr = (AudioManager) getSystemService( Context.AUDIO_SERVICE );
		aMgr.setStreamVolume( AudioManager.STREAM_MUSIC, mOriginalVolume, 0 );
	}

	public void onWordAccepted( View view ) {
		Log.v(TAG, "onWordAccepted");
		assert mSelectedWord != null;

		CCTokenView last = mSelectedWord;
		String acceptString = "";
		ArrayList<CCTokenView> pending = new ArrayList<>();
		boolean keepAccepting = true;
		for (CCTokenView v : mTokenViews) {
			if (keepAccepting) {
				acceptString += v.getWord();
				v.onDeleted(mContainer);
				if (v == last) {
					keepAccepting = false;
				} else {
					acceptString += " ";
				}
			}
			else {
				pending.add(v);
			}
		}
		mTokenViews = pending;

		Log.i(TAG, "Accepted string: " + acceptString);
		hideActionButtons();
		Toast.makeText(this, acceptString, Toast.LENGTH_SHORT).show();
	}

	public void onWordDeleted( View view ) {
		Log.v(TAG, "onWordDeleted");
		assert mSelectedWord != null;

		mTokenViews.remove(mSelectedWord);
		mSelectedWord.onDeleted(mContainer);
	}

	public void onNewWord( View view ) {
		Log.v(TAG, "onNewWord");
		assert mSelectedWord != null;

		CCTokenView tokenView = new CCTokenView(this, new CCToken("<new word>"));
		int index = mTokenViews.indexOf(mSelectedWord) + 1;
		mTokenViews.add(index, tokenView);
		tokenView.show(index, mContainer);
		tokenView.focus();
	}

	public void setSeparatorText( View view ) {
		Log.v(TAG, "setSeparatorText");
		assert mSelectedWord != null;

		mSelectedWord.setSeparatorText(((Button) view).getText());
	}
}
