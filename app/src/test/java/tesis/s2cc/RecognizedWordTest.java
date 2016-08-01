package tesis.s2cc;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class RecognizedWordTest {

	private RecognizedWord word;

	@Before
	public void createLogHistory() {
		word = new RecognizedWord("test");
	}

	@Test
	public void initial_state_is_valid() {
		assertEquals("test", word.getWord());
		assertEquals("test", word.getToken().getOriginalWord());
		assertEquals("", word.getToken().getAcceptedWord());
	}

	@Test
	public void change_on_recognition_should_propagate() {
		word.update("changed");
		assertEquals("changed", word.getWord());
		assertEquals("changed", word.getToken().getOriginalWord());
		assertEquals("", word.getToken().getAcceptedWord());
	}

	@Test
	public void change_on_token_should_not_propagate() {
		word.getToken().onWordEdited("changedByUser");
		assertEquals("changedByUser", word.getToken().getAcceptedWord());
		assertEquals("test", word.getToken().getOriginalWord());
		assertEquals("test", word.getWord());
	}

	@Test
	public void user_input_should_override_recognition_change() {
		word.update("updated");
		word.getToken().onWordEdited("changedByUser");
		assertEquals("changedByUser", word.getToken().getAcceptedWord());
		assertEquals("updated", word.getToken().getOriginalWord());
		assertEquals("updated", word.getWord());
	}

	@Test
	public void change_on_recognition_should_not_override_user_input() {
		word.getToken().onWordEdited("changedByUser");
		word.update("updated");
		assertEquals("changedByUser", word.getToken().getAcceptedWord());
		assertEquals("updated", word.getToken().getOriginalWord());
		assertEquals("updated", word.getWord());
	}
}
