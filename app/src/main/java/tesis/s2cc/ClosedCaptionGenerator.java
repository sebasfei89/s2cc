package tesis.s2cc;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class ClosedCaptionGenerator implements RecognitionListener {

	private static final String TAG = "SpeechRecognizer";

	private RecognitionState mState;
	private RecognitionActivity mActivity;
	private SpeechRecognizer mSpeechRecognizer;
	private ArrayList<RecognizedWord> mRecognizedWords;

	public ClosedCaptionGenerator( RecognitionActivity activity ) {
		mActivity = activity;
		mState = RecognitionState.IDLE;
		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mActivity.getApplicationContext());
		mRecognizedWords = new ArrayList<>();

		updateRecognizedWords("Hello World this is an automatically generated closed caption text".split(" "));
	}

	public void reset() {
		View focused = mActivity.getCurrentFocus();
		if (focused != null) {
			for (RecognizedWord w : mRecognizedWords) {
				if (w.getView() == focused) {
					w.showKeyboard();
				}
			}
		}
		resetRecognizer();
	}

	public void destroy() {
		mSpeechRecognizer.destroy();
		mSpeechRecognizer = null;
	}

	public void startRecognition() {
		Log.v(TAG, "startRecognition: state=" + mState.name);
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-AR");
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
		/* TODO: modificar estos parametros y ver como interfiere en la velocidad del reconocimiento
			intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1000);
			intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 500);
			intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
		*/

		mSpeechRecognizer.startListening(intent);
		updateState(RecognitionState.STARTING);
	}

	public void stopRecognition() {
		Log.v(TAG, "stopRecognition: state=" + mState.name);
		mSpeechRecognizer.stopListening();
		updateState(RecognitionState.STOPPING);
	}

	public void toggleRecognition() {
		Log.v(TAG, "onToggleRecognition: state=" + mState.name);
		switch (mState) {
			case IDLE:
				startRecognition();
				break;
			case RECOGNIZING:
				stopRecognition();
				break;
			default:
				Log.w(TAG, "Toggle recognition called in invalid state: " + mState.name);
				break;
		}
	}

	public void removeWord(RecognizedWord word) {
		Log.v(TAG, "removeWord: " + word.text());
		mRecognizedWords.remove(word);
	}

	private void resetRecognizer() {
		Log.v(TAG, "resetRecognizer: state=" + mState.name);
		mSpeechRecognizer.destroy();
		mSpeechRecognizer.setRecognitionListener(this);
		updateState(RecognitionState.IDLE);
	}

	private void updateRecognizedWords(String[] words) {
		for (int i = 0; i < words.length; i++) {
			if (words[i].length() > 0) {
				if (i < mRecognizedWords.size()) {
					mRecognizedWords.get(i).update(words[i]);
				} else {
					mRecognizedWords.add(new RecognizedWord(words[i], mActivity, this));
				}
			}
		}
	}

	private void updateState( RecognitionState state ) {
		Log.v(TAG, "updateState: old_state=" + mState.name + ", new_state=" + state.name);
		mState = state;
		mActivity.onRecognitionStateChanged(mState);
	}

	@Override
	public void onReadyForSpeech(Bundle bundle) {
		updateState(RecognitionState.RECOGNIZING);
	}

	@Override
	public void onBeginningOfSpeech() {
		Log.v(TAG, "onBeginningOfSpeech");
	}

	@Override
	public void onRmsChanged(float v) {}

	@Override
	public void onBufferReceived(byte[] bytes) {}

	@Override
	public void onEndOfSpeech() {
		Log.v(TAG, "onEndOfSpeech");
	}

	@Override
	public void onError(int i) {
		String error_string = "UNKNOWN";
		switch (i) {
			case android.speech.SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
				error_string = "ERROR_NETWORK_TIMEOUT";
				break;
			case android.speech.SpeechRecognizer.ERROR_NETWORK:
				error_string = "ERROR_NETWORK";
				break;
			case android.speech.SpeechRecognizer.ERROR_AUDIO:
				error_string = "ERROR_AUDIO";
				break;
			case android.speech.SpeechRecognizer.ERROR_SERVER:
				error_string = "ERROR_SERVER";
				break;
			case android.speech.SpeechRecognizer.ERROR_CLIENT:
				error_string = "ERROR_CLIENT";
				break;
			case android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
				error_string = "ERROR_SPEECH_TIMEOUT";
				break;
			case android.speech.SpeechRecognizer.ERROR_NO_MATCH:
				error_string = "ERROR_NO_MATCH";
				break;
			case android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
				error_string = "ERROR_RECOGNIZER_BUSY";
				break;
			case android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
				error_string = "ERROR_INSUFFICIENT_PERMISSIONS";
				break;
		}
		Log.v(TAG, "onError: error=" + error_string + ", state=" + mState.name);
		resetRecognizer();
	}

	@Override
	public void onResults(Bundle bundle) {
		ArrayList<String> results = bundle.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
		if (results != null) {
			float[] confidences = bundle.getFloatArray(android.speech.SpeechRecognizer.CONFIDENCE_SCORES);
			assert(confidences != null);
			Log.v(TAG, "onResults: results=" + results.size() + ", confidences=" + confidences.length);
			int i = 0;
			for (String result : results) {
				Log.v(TAG, "\tresult[" + i + "]: " + result);
				if (i < confidences.length) {
					Log.v(TAG, "\tconfidence[" + i + "]: " + confidences[i]);
				}
				i++;
			}
			updateRecognizedWords(results.get(0).split(" "));
		} else {
			Log.w(TAG, "onResults: no results");
		}
		resetRecognizer();
	}

	@Override
	public void onPartialResults(Bundle bundle) {
		ArrayList<String> results = bundle.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
		assert(results != null);
		Log.v(TAG, "onPartialResults: results=" + results.size());
		for (String result : results) {
			Log.v(TAG, "\tresult: " + result);
		}
		updateRecognizedWords(results.get(0).split(" "));
	}

	@Override
	public void onEvent(int i, Bundle bundle) {
		Log.v(TAG, "onEvent: i=" + i);
	}
}
