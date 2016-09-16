package tesis.s2cc;

import android.util.Log;
import java.util.Observable;

public class RecognizedWord extends Observable {

	public interface Listener {
		void onWordChanged( String oldWord, String newWord );
	}

	private static final String TAG = "RecognizedWord";

	private String mWord;
	private float mConfidence;
	private Listener mListener;
	private long mTimeStamp;

	public RecognizedWord( String word, long timestamp ) {
		mWord = word;
		mConfidence = -1.0f;
		mTimeStamp = timestamp;
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
			String oldWord = mWord;
			mWord = newWord;
			if (mListener != null) {
				mListener.onWordChanged(oldWord, newWord);
			}
		}
	}

	public float getConfidence() {
		return mConfidence;
	}

	public void setConfidence( float confidence ) {
		mConfidence = confidence;
	}

	public long getTimeStamp() {
		return mTimeStamp;
	}
}
