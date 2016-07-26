package tesis.s2cc;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.util.Log;

public class CCRecognizer implements RecognitionListener {

	private static final String TAG = "CCRecognizer";

	@Override
	public void onReadyForSpeech(Bundle bundle) {
		Log.v(TAG, "onReadyForSpeech");
	}

	@Override
	public void onBeginningOfSpeech() {
		Log.v(TAG, "onBeginningOfSpeech");
	}

	@Override
	public void onRmsChanged(float v) {
		Log.v(TAG, "onRmsChanged: v=" + v);
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
			case RecognizerIntent.RESULT_AUDIO_ERROR:
				error_string = "AUDIO";
				break;
			case RecognizerIntent.RESULT_CLIENT_ERROR:
				error_string = "CLIENT";
				break;
			case RecognizerIntent.RESULT_NETWORK_ERROR:
				error_string = "NETWORK";
				break;
			case RecognizerIntent.RESULT_NO_MATCH:
				error_string = "NOMATCH";
				break;
			case RecognizerIntent.RESULT_SERVER_ERROR:
				error_string = "SERVER";
				break;
		}
		Log.v(TAG, "onError: i=" + i + ", error=" + error_string);
	}

	@Override
	public void onResults(Bundle bundle) {
		Log.v(TAG, "onResults");
	}

	@Override
	public void onPartialResults(Bundle bundle) {
		Log.v(TAG, "onPartialResults");
	}

	@Override
	public void onEvent(int i, Bundle bundle) {
		Log.v(TAG, "onEvent: i=" + i);
	}
}
