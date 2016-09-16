package tesis.s2cc;

import android.os.SystemClock;

import java.util.ArrayList;

public class RecognitionSession {

	private long mStartTime;
	private ArrayList<RecognizedWord> mWords;
	private ClosedCaptionGenerator.Listener mListener;

	public RecognitionSession( long startTime, ClosedCaptionGenerator.Listener listener ) {
		mStartTime = startTime;
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
					RecognizedWord rWord = new RecognizedWord(word, SystemClock.elapsedRealtime()-mStartTime);
					mWords.add(rWord);
					mListener.onWordRecognized(rWord);
				}
			}
		}
	}

	public void addConfidence( String[] words, float confidence ) {
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (i < mWords.size() && mWords.get(i).getWord().equals(word)) {
				mWords.get(i).setConfidence(confidence);
			}
		}
	}
}
