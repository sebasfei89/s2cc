package tesis.s2cc;

import java.util.ArrayList;

public class RecognitionSession {

	private ArrayList<RecognizedWord> mWords;
	private ClosedCaptionGenerator.Listener mListener;

	public RecognitionSession( ClosedCaptionGenerator.Listener listener ) {
		mWords = new ArrayList<>();
		mListener = listener;
	}

	public void updateWords( String[] words ) {
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (word.length() > 0) {
				if (i < mWords.size()) {
					mWords.get(i).update(word);
				} else {
					RecognizedWord rWord = new RecognizedWord(word);
					mWords.add(rWord);
					mListener.onWordRecognized(rWord);
				}
			}
		}
	}

	public void addConfidence( String[] words, float confidence ) {
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (word.length() > 0) {
				if (i < mWords.size() && mWords.get(i).getWord().equals(word)) {
					mWords.get(i).setConfidence(confidence);
				}
			}
		}
	}
}
