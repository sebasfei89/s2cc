package tesis.s2cc;

import android.util.Log;

public class RecognizedWord {

	private static final String TAG = "RecognizedWord";

	private String mWord;
	private CCToken mToken;

	public RecognizedWord( String word ) {
		mWord = word;
		mToken = new CCToken(mWord);
	}

	public String getWord() {
		return mWord;
	}

	public CCToken getToken() {
		return mToken;
	}

	public void update( String word ) {
		Log.v(TAG, "update: mWord=" + mWord + ", word=" + word);
		mWord = word;
		mToken.onRecognitionUpdate(word);
	}
}
