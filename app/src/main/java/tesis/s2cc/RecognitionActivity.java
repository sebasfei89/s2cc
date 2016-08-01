package tesis.s2cc;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class RecognitionActivity extends AppCompatActivity {

	private static final String TAG = "RecognitionActivity";

	private Button toggleRecognitionBtn;
	private boolean resetOnStop;
	private int originalVolume;
	private OldClosedCaptionGenerator mCCGenerator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recognition);

		mCCGenerator = new OldClosedCaptionGenerator(this);
		resetOnStop = false;
		toggleRecognitionBtn = (Button) this.findViewById(R.id.ToggleRecognitionBtn);
		toggleRecognitionBtn.setFocusableInTouchMode(true);
		toggleRecognitionBtn.setFocusable(true);
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
		AudioManager mAudioManager = ((AudioManager) getSystemService(Context.AUDIO_SERVICE));
		originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		resetOnStop = false;
		mCCGenerator.reset();
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
}
