package tesis.s2cc;

public class CCToken {

	private static final String TAG = "CCToken";

	private String mOriginalWord;
	private String mAcceptedWord;

	CCToken( String word ) {
		mOriginalWord = word;
		mAcceptedWord = "";
	}

	public String getOriginalWord() {
		return mOriginalWord;
	}

	public String getAcceptedWord() {
		return mAcceptedWord;
	}

	public void onRecognitionUpdate( String word ) {
		mOriginalWord = word;
	}

	public void onWordEdited( String word ) {
		mAcceptedWord = word;
	}
}
