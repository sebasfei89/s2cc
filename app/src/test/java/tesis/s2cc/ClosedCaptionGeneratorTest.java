package tesis.s2cc;

import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClosedCaptionGeneratorTest {

	private ClosedCaptionGenerator mCCGenerator;

	@Mock
	private Bundle mBundle;

	private class CCListenerMock implements ClosedCaptionGenerator.ClosedCaptionGeneratorListener {
		public boolean started;
		public boolean stopped;

		CCListenerMock() {
			started = false;
			stopped = false;
		}

		@Override
		public void onRecognitionStarted() {
			started = true;
		}

		@Override
		public void onRecognitionStopped() {
			stopped = true;
		}
	}

	@Before
	public void setUp() {
		mCCGenerator = new ClosedCaptionGenerator();
	}

	@Test
	public void should_call_onRecognitionStarted_when_onReadyForSpeech() {
		CCListenerMock ccListener = new CCListenerMock();
		mCCGenerator.setListener(ccListener);
		mCCGenerator.onReadyForSpeech(null);
		assertTrue(ccListener.started);
		assertFalse(ccListener.stopped);
	}

	@Test
	public void should_call_onRecognitionStopped_when_onError() {
		CCListenerMock ccListener = new CCListenerMock();
		mCCGenerator.setListener(ccListener);
		mCCGenerator.onError(1);
		assertFalse(ccListener.started);
		assertTrue(ccListener.stopped);
	}

	@Test
	public void should_call_onRecognitionStopped_when_onResults() {
		CCListenerMock ccListener = new CCListenerMock();
		mCCGenerator.setListener(ccListener);
		mCCGenerator.onResults(null);
		assertFalse(ccListener.started);
		assertTrue(ccListener.stopped);
	}

	@Test
	public void should_register_recognized_words_onPartialResults() {
		ArrayList<String> results = new ArrayList<>();
		results.add("Hello world");
		when(mBundle.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)).thenReturn(results);

		mCCGenerator.onPartialResults(mBundle);
		ArrayList<RecognizedWord> words = mCCGenerator.getRecognizedWords();
		assertEquals(2, words.size());
		assertEquals("Hello", words.get(0).getWord());
		assertEquals("Hello", words.get(0).getToken().getOriginalWord());
		assertEquals("", words.get(0).getToken().getAcceptedWord());
		assertEquals("world", words.get(1).getWord());
		assertEquals("world", words.get(1).getToken().getOriginalWord());
		assertEquals("", words.get(1).getToken().getAcceptedWord());

		ArrayList<CCToken> tokens = mCCGenerator.getTokens();
		assertEquals(4, tokens.size());
		assertEquals(tokens.get(0), words.get(0).getToken());
		assertEquals(" ", tokens.get(1).getOriginalWord());
		assertEquals(tokens.get(2), words.get(1).getToken());
		assertEquals(" ", tokens.get(3).getOriginalWord());
	}
}
