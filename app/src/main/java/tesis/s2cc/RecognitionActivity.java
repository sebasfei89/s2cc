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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	private CCToken mSelectedWord;
	private ArrayList<CCToken> mTokenViews;
	private RemoteConnection mRemoteConnection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recognition);

		SpeechRecognizer recognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
		mCCGenerator = new ClosedCaptionGenerator(recognizer, this);

		mContainer = (FlexboxLayout) findViewById(R.id.RecognitionResults);
		mSepButtons = (FlexboxLayout) findViewById(R.id.sep_buttons);
		mToggleBtn = (FloatingActionButton) findViewById(R.id.start_stop);
		mAddWordBtn = (FloatingActionButton) findViewById(R.id.add_word);
		mDeleteWordBtn = (FloatingActionButton) findViewById(R.id.delete_word);
		mNewWordBtn = (FloatingActionButton) findViewById(R.id.new_word);

		mTokenViews = new ArrayList<>();
		mSelectedWord = null;
		mIsRecognizing = false;
		mRemoteConnection = new RemoteConnection("192.168.0.15", 9876);
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
		if (mRemoteConnection.isConnected()) {
			mRemoteConnection.disconnect();
		}
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
			mRemoteConnection.connect();
			mToggleBtn.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
			mCCGenerator.start();
		}
	}

	public void showActionButtons(CCToken word, boolean showWordButtons, boolean showSeparatorButtons) {
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

		CCToken tokenView = new CCToken(this, rWord);
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
		if (mSelectedWord == null) throw new AssertionError("No CCToken selected");

		CCToken last = mSelectedWord;
		String acceptString = "";
		ArrayList<CCToken> pending = new ArrayList<>();
		boolean keepAccepting = true;
		JSONArray results = new JSONArray();
		for (CCToken v : mTokenViews) {
			if (keepAccepting) {
				String text = v.getText();
				try {
					JSONObject jsonToken = new JSONObject();
					jsonToken.put("timestamp", v.getTimeStamp());
					jsonToken.put("text", text);
					jsonToken.put("confidence", v.getConfidence());
					results.put(jsonToken);
				}
				catch (JSONException e) {
					Log.e(TAG, "Fail to serialize CCToken to JSON");
				}
				acceptString += text;
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
		mTokenViews.clear();
		mTokenViews = pending;

		Log.i(TAG, "Accepted string: " + acceptString);
		hideActionButtons();
		Toast.makeText(this, acceptString, Toast.LENGTH_SHORT).show();

		mRemoteConnection.send(results.toString());
	}

	public void onWordDeleted( View view ) {
		Log.v(TAG, "onWordDeleted");
		if (mSelectedWord == null) throw new AssertionError("No CCToken selected");

		mTokenViews.remove(mSelectedWord);
		mSelectedWord.onDeleted(mContainer);
		mSelectedWord = null;
	}

	public void onNewWord( View view ) {
		Log.v(TAG, "onNewWord");
		if (mSelectedWord == null) throw new AssertionError("No CCToken selected");

		CCToken tokenView = new CCToken(this, new RecognizedWord("<--->", mSelectedWord.getTimeStamp()));
		int index = mTokenViews.indexOf(mSelectedWord) + 1;
		mTokenViews.add(index, tokenView);
		tokenView.show(index, mContainer);
		tokenView.focus();
	}

	public void setSeparatorText( View view ) {
		Log.v(TAG, "setSeparatorText");
		if (mSelectedWord == null) throw new AssertionError("No CCToken selected");

		mSelectedWord.setSeparatorText(((Button) view).getText());
	}
}
