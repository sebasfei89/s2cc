package tesis.s2cc;

import android.util.Log;
import java.util.Observable;

public class RecognizedWord extends Observable {

	public interface Listener {
		void onWordChanged( String newWord, float confidence );
	}

	private static final String TAG = "RecognizedWord";

	private String mWord;
	private float mConfidence;
	private Listener mListener;

	public RecognizedWord( String word ) {
		mWord = word;
		mConfidence = -1.0f;
		mListener = null;
	}

	public void setListener( Listener listener ) {
		mListener = listener;
	}

	public String getWord() {
		return mWord;
	}

	public void update( String newWord ) {
		if (!newWord.equals(mWord)) {
			Log.v(TAG, "update: mWord=" + mWord + ", newWord=" + newWord);
			mWord = newWord;
			if (mListener != null) {
				mListener.onWordChanged(mWord, mConfidence);
			}
		}
	}

	public float getConfidence() {
		return mConfidence;
	}

	public void setConfidence( float confidence ) {
		if (mConfidence != confidence) {
			mConfidence = confidence;
			if (mListener != null) {
				mListener.onWordChanged(mWord, mConfidence);
			}
		}
	}
}
