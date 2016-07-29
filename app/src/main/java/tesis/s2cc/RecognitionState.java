package tesis.s2cc;

enum RecognitionState {
	IDLE("IDLE"), STARTING("STARTING"), RECOGNIZING("RECOGNIZING"), STOPPING("STOPPING");

	public final String name;

	RecognitionState(String name) {
		this.name = name;
	}
}
