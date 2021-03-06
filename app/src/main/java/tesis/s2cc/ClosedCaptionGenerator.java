package tesis.s2cc;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class ClosedCaptionGenerator implements RecognitionListener {

	private static final String TAG = "ClosedCaptionGenerator";

	public interface Listener {
		void onBeforeStart();
		void onRecognitionStarted();
		void onRecognitionStopped();
		void onWordRecognized( RecognizedWord rWord );
	}

	private RecognitionState mState;
	private SpeechRecognizer mSpeechRecognizer;
	private Listener mListener;
	private Deque<RecognitionSession> mSessions;
	private boolean mStopRequested;
	private long mStartTime;

	public ClosedCaptionGenerator( SpeechRecognizer recognizer, Listener listener ) {
		mState = RecognitionState.IDLE;
		mSpeechRecognizer = recognizer;
		mStopRequested = false;
		mListener = listener;
		mSessions = new ArrayDeque<>();
		mStartTime = 0;
	}

	public void start() {
		Log.v(TAG, "start: state=" + mState.name);
		if (mState != RecognitionState.IDLE) throw new AssertionError("Expected to be in IDLE state! State was: " + mState.name);

		if (!mStopRequested) {
			mStartTime = SystemClock.elapsedRealtime();
		}
		mStopRequested = false;
		mSpeechRecognizer.destroy();
		mSpeechRecognizer.setRecognitionListener(this);
		updateState(RecognitionState.STARTING);
		mListener.onBeforeStart();
		mSpeechRecognizer.startListening( getRecognitionIntent() );
	}

	public void stop() {
		Log.v(TAG, "stop: state=" + mState.name);

		mStopRequested = true;
		if (mState == RecognitionState.RECOGNIZING) {
			updateState(RecognitionState.STOPPING);
			mSpeechRecognizer.stopListening();
		}
	}

	public void destroy() {
		mSpeechRecognizer.destroy();
		mSpeechRecognizer = null;
	}

	private Intent getRecognitionIntent() {
		Log.v(TAG, "startRecognition: state=" + mState.name);
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-AR");
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 2);
		intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
		intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000);
		intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1500);
		/* TODO: modificar estos parametros y ver como interfiere en la velocidad del reconocimiento
			intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
		*/
		return intent;
	}

	private void updateState( RecognitionState state ) {
		Log.v(TAG, "updateState: old_state=" + mState.name + ", new_state=" + state.name);
		mState = state;
		switch (mState) {
			case RECOGNIZING:
				mListener.onRecognitionStarted();
				break;
			case IDLE:
				mListener.onRecognitionStopped();
				break;
		}
	}

	private void onRecognitionStopped() {
		mState = RecognitionState.IDLE;
		if (mStopRequested) {
			mListener.onRecognitionStopped();
		} else {
			start();
		}
	}

	@Override
	public void onReadyForSpeech(Bundle bundle) {
		Log.v(TAG, "onReadyForSpeech: state=" + mState.name);
		if (mListener == null) throw new AssertionError("Listener is not set");

		if (mStopRequested) {
			stop();
		} else {
			mSessions.add(new RecognitionSession(mStartTime, mListener));
			updateState(RecognitionState.RECOGNIZING);
		}
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
		onRecognitionStopped();
	}

	@Override
	public void onPartialResults(Bundle bundle) {
		ArrayList<String> results = bundle.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
		assert results != null;
		Log.v(TAG, "onPartialResults: results=" + results.size());

		for (String result : results) {
			Log.v(TAG, "\tresult: " + result);
		}

		mSessions.getLast().updateWords(results.get(0).split(" "));
	}

	@Override
	public void onResults(Bundle bundle) {
		ArrayList<String> results = bundle.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
		float[] confidences = bundle.getFloatArray(android.speech.SpeechRecognizer.CONFIDENCE_SCORES);

		if (results == null) throw new AssertionError("Fail to get recognition results from bundle");
		if (confidences == null) throw new AssertionError("Fail to get confidence scores from bundle");

		Log.v(TAG, "onResults: results=" + results.size() + ", confidences=" + confidences.length);
		int i = 0;
		for (String result : results) {
			Log.v(TAG, "\tresult[" + i + "]: " + result);
			if (i < confidences.length) {
				Log.v(TAG, "\tconfidence[" + i + "]: " + confidences[i]);
			}
			i++;
		}

		mSessions.getLast().addConfidence(results.get(0).split(" "), confidences[0]);
		onRecognitionStopped();
	}

	@Override
	public void onBeginningOfSpeech() {}

	@Override
	public void onEndOfSpeech() {}

	@Override
	public void onRmsChanged(float v) {}

	@Override
	public void onBufferReceived(byte[] bytes) {}

	@Override
	public void onEvent(int i, Bundle bundle) {}

	enum RecognitionState {
		IDLE("IDLE"), STARTING("STARTING"), RECOGNIZING("RECOGNIZING"), STOPPING("STOPPING");

		public final String name;

		RecognitionState(String name) {
			this.name = name;
		}
	}
}
