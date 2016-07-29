package tesis.s2cc;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

public class RecognitionActivity extends AppCompatActivity {

	private static final String TAG = "RecognitionActivity";

	private Button toggleRecognitionBtn;
	private boolean resetOnStop;
	private int originalVolume;
	private RecognizedWord selectedWord;
	private ClosedCaptionGenerator mCCGenerator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recognition);

		mCCGenerator = new ClosedCaptionGenerator(this);
		selectedWord = null;
		resetOnStop = false;
		toggleRecognitionBtn = (Button) this.findViewById(R.id.ToggleRecognitionBtn);
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
		AudioManager mAudioManager = ((AudioManager) getSystemService(Context.AUDIO_SERVICE));
		originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		resetOnStop = false;
		mCCGenerator.resetRecognizer();

		// If keyboard was opened when app was paussed/stopped open it again
		if (selectedWord != null) {
			RecognizedWord tmp = selectedWord;
			selectedWord = null;
			showKeyboardFor(tmp);
		}
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
		mCCGenerator.stopRecognition();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop");
		super.onStop();
		((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
		mCCGenerator.destroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean handled = false;

		if (selectedWord != null) {
			handled = selectedWord.onKeyDown(keyCode, event);
		}
		return handled || super.onKeyDown(keyCode, event);
	}

	public void onToggleRecognition(View view) {
		Log.v(TAG, "onToggleRecognition");
		resetOnStop = false;
		mCCGenerator.toggleRecognition();
	}

	public void onRecognitionStateChanged( RecognitionState state ) {
		switch (state) {
			case IDLE:
				toggleRecognitionBtn.setText(R.string.start);
				toggleRecognitionBtn.setEnabled(true);
				if (resetOnStop) {
					mCCGenerator.startRecognition();
				}
				break;
			case STARTING:
				resetOnStop = true;
				toggleRecognitionBtn.setText(R.string.starting);
				toggleRecognitionBtn.setEnabled(false);
				break;
			case RECOGNIZING:
				toggleRecognitionBtn.setText(R.string.stop);
				toggleRecognitionBtn.setEnabled(true);
				break;
			case STOPPING:
				toggleRecognitionBtn.setText(R.string.stopping);
				toggleRecognitionBtn.setEnabled(false);
				break;
		}
	}

	public void showKeyboardFor(RecognizedWord word) {
		Log.v(TAG, "showKeyboardFor: word=" + word.text());
		if (selectedWord != null) {
			selectedWord.unSelect();
		}
		selectedWord = word;
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				keyboard.showSoftInput(findViewById(R.id.MainView).getRootView(), InputMethodManager.SHOW_IMPLICIT);
			}
		}, 100);
	}

	public void hideKeyboard() {
		Log.v(TAG, "hideKeyboard");
		InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
		selectedWord = null;
	}
}
