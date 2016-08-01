package tesis.s2cc;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.util.Log;

import java.util.ArrayList;

public class ClosedCaptionGenerator implements RecognitionListener {

	private static final String TAG = "ClosedCaptionGenerator";

	private ClosedCaptionGeneratorListener mListener;
	private ArrayList<RecognizedWord> mRecognizedWords;
	private ArrayList<CCToken> mTokens;

	public interface ClosedCaptionGeneratorListener {
		void onRecognitionStarted();
		void onRecognitionStopped();
	}

	public ClosedCaptionGenerator() {
		mRecognizedWords = new ArrayList<>();
		mTokens = new ArrayList<>();
	}

	public void setListener( ClosedCaptionGeneratorListener listener ) {
		mListener = listener;
	}

	public ArrayList<RecognizedWord> getRecognizedWords() {
		return mRecognizedWords;
	}

	public ArrayList<CCToken> getTokens() {
		return mTokens;
	}

	private void updateRecognizedWords( String[] words ) {
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (word.length() > 0) {
				if (i < mRecognizedWords.size()) {
					mRecognizedWords.get(i).update(word);
				} else {
					RecognizedWord rWord = new RecognizedWord(word);
					mRecognizedWords.add(rWord);
					mTokens.add(rWord.getToken());
					mTokens.add(new CCToken(" "));
				}
			}
		}
	}

	@Override
	public void onReadyForSpeech(Bundle bundle) {
		assert mListener != null;
		mListener.onRecognitionStarted();
	}

	@Override
	public void onError(int i) {
		Log.v(TAG, "onError: code=" + i);
		mListener.onRecognitionStopped();
	}

	@Override
	public void onPartialResults(Bundle bundle) {
		ArrayList<String> results = bundle.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
		assert results != null;
		Log.v(TAG, "onPartialResults: results=" + results.size());

		for (String result : results) {
			Log.v(TAG, "\tresult: " + result);
		}

		updateRecognizedWords(results.get(0).split(" "));
	}

	@Override
	public void onResults(Bundle bundle) {
		Log.v(TAG, "onResults");
		mListener.onRecognitionStopped();
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
}
