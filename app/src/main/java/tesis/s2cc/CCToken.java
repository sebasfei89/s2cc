package tesis.s2cc;

public class CCToken {

	private String mOriginalWord;
	private String mAcceptedWord;
	private long mTimeStamp;
	private float mConfidence;

	CCToken( String word, float confidence, long timestamp ) {
		mOriginalWord = word;
		mAcceptedWord = "";
		mConfidence = confidence;
		mTimeStamp = timestamp;
	}

	public String getOriginalWord() {
		return mOriginalWord;
	}

	public String getAcceptedWord() {
		return mAcceptedWord;
	}

	public float getConfidence() {
		return mConfidence;
	}

	public long getTimeStamp() {
		return mTimeStamp;
	}

	public void onRecognitionUpdate( String word, float confidence ) {
		mOriginalWord = word;
		mConfidence = confidence;
	}

	public void onWordEdited( String word ) {
		mAcceptedWord = word;
	}
}
