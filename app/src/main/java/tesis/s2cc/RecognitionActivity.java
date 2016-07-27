package tesis.s2cc;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class RecognitionActivity extends AppCompatActivity implements RecognitionListener {

	private static final String TAG = "RecognitionActivity";

	private enum RecognitionState {
		IDLE("IDLE"), STARTING("STARTING"), RECOGNIZING("RECOGNIZING"), STOPPING("STOPPING");

		public final String name;
		RecognitionState(String name) {
			this.name = name;
		}
	}

	private SpeechRecognizer speech_recognizer;
	private RecognitionState state;
	private Button toggleRecognitionBtn;
	private TextView recognitionResultsText;
	private boolean resetOnStop;
	private int originalVolume;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recognition);

		toggleRecognitionBtn = (Button) this.findViewById(R.id.ToggleRecognitionBtn);
		recognitionResultsText = (TextView) this.findViewById(R.id.RecognitionResults);
		speech_recognizer = SpeechRecognizer.createSpeechRecognizer(this.getApplicationContext());
		resetOnStop = false;
		state = RecognitionState.IDLE;
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
		AudioManager mAudioManager = ((AudioManager) getSystemService(Context.AUDIO_SERVICE));
		originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		resetOnStop = false;
		resetRecognizer();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
		if (state.ordinal() > RecognitionState.IDLE.ordinal()) {
			speech_recognizer.stopListening();
			state = RecognitionState.STOPPING;
		}
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop");
		super.onStop();
		speech_recognizer.cancel();
		((AudioManager) getSystemService(Context.AUDIO_SERVICE))
				.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
		speech_recognizer.destroy();
		speech_recognizer = null;
	}

	@Override
	public void onReadyForSpeech(Bundle bundle) {
		Log.v(TAG, "onReadyForSpeech");
		state = RecognitionState.RECOGNIZING;
		toggleRecognitionBtn.setText(R.string.stop);
		toggleRecognitionBtn.setEnabled(true);
	}

	@Override
	public void onBeginningOfSpeech() {
		Log.v(TAG, "onBeginningOfSpeech");
	}

	@Override
	public void onRmsChanged(float v) {
		//Log.v(TAG, "onRmsChanged: v=" + v);
	}

	@Override
	public void onBufferReceived(byte[] bytes) {
		Log.v(TAG, "onBufferReceived: bytes=" + bytes.length);
	}

	@Override
	public void onEndOfSpeech() {
		Log.v(TAG, "onEndOfSpeech");
	}

	@Override
	public void onError(int i) {
		String error_string = "UNKNOWN";
		switch (i) {
			case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
				error_string = "ERROR_NETWORK_TIMEOUT";
				break;
			case SpeechRecognizer.ERROR_NETWORK:
				error_string = "ERROR_NETWORK";
				break;
			case SpeechRecognizer.ERROR_AUDIO:
				error_string = "ERROR_AUDIO";
				break;
			case SpeechRecognizer.ERROR_SERVER:
				error_string = "ERROR_SERVER";
				break;
			case SpeechRecognizer.ERROR_CLIENT:
				error_string = "ERROR_CLIENT";
				break;
			case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
				error_string = "ERROR_SPEECH_TIMEOUT";
				break;
			case SpeechRecognizer.ERROR_NO_MATCH:
				error_string = "ERROR_NO_MATCH";
				break;
			case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
				error_string = "ERROR_RECOGNIZER_BUSY";
				break;
			case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
				error_string = "ERROR_INSUFFICIENT_PERMISSIONS";
				break;
		}
		Log.v(TAG, "onError: error=" + error_string + ", state=" + state.name);
		resetRecognizer();
	}

	@Override
	public void onResults(Bundle bundle) {
		ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		if (results != null) {
			float[] confidences = bundle.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
			Log.v(TAG, "onResults: results=" + results.size() + ", confidences=" + confidences.length);
			int i = 0;
			for (String result : results) {
				Log.v(TAG, "\tresult[" + i + "]: " + result);
				if (i < confidences.length) {
					Log.v(TAG, "\tconfidence[" + i + "]: " + confidences[i]);
				}
				i++;
			}
			recognitionResultsText.append(results.get(0) + ".\n");
		} else {
			Log.w(TAG, "onResults: no results");
		}
		resetRecognizer();
	}

	@Override
	public void onPartialResults(Bundle bundle) {
		ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		Log.v(TAG, "onPartialResults: results=" + results.size());
		for (String result : results) {
			Log.v(TAG, "\tresult: " + result);
			// TODO: Mostrar en la ui como resultado parcial, permitiendo al usuario editar/confirmar el texto.
			// recognitionResultsText.append(result + " ");
		}
	}

	@Override
	public void onEvent(int i, Bundle bundle) {
		Log.v(TAG, "onEvent: i=" + i);
	}

	public void onToggleRecognition(View view) {
		Log.v(TAG, "onToggleRecognition: state=" + state.name);
		switch (state) {
			case IDLE:
				startRecognition();
				break;
			case RECOGNIZING:
				resetOnStop = false;
				stopRecognition();
				break;
			case STARTING:
			case STOPPING:
				Log.w(TAG, "Toggle recognition called in invalid state: " + state.name);
				break;
		}
	}

	private void startRecognition() {
		Log.v(TAG, "startRecognition: state=" + state.name);
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-AR");
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
		intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1000);
		intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 500);
		/*intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);*/
		speech_recognizer.startListening(intent);
		state = RecognitionState.STARTING;
		resetOnStop = true;
		toggleRecognitionBtn.setText(R.string.starting);
		toggleRecognitionBtn.setEnabled(false);
	}

	private void stopRecognition() {
		Log.v(TAG, "stopRecognition: state=" + state.name);
		speech_recognizer.stopListening();
		state = RecognitionState.STOPPING;
		toggleRecognitionBtn.setText(R.string.stopping);
		toggleRecognitionBtn.setEnabled(false);
	}

	private void resetRecognizer() {
		Log.v(TAG, "resetRecognizer: state=" + state.name);
		speech_recognizer.destroy();
		speech_recognizer.setRecognitionListener(this);
		state = RecognitionState.IDLE;
		toggleRecognitionBtn.setText(R.string.start);
		toggleRecognitionBtn.setEnabled(true);
		if (resetOnStop) {
			startRecognition();
		}
	}

}
