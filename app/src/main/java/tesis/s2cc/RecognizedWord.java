package tesis.s2cc;

import android.util.Log;
import java.util.Observable;

public class RecognizedWord extends Observable {

	private static final String TAG = "RecognizedWord";

	private String mWord;

	public RecognizedWord( String word ) {
		mWord = word;
	}

	public String getWord() {
		return mWord;
	}

	public void update( String word ) {
		Log.v(TAG, "update: mWord=" + mWord + ", word=" + word);
		mWord = word;
		notifyObservers(word);
	}
}
