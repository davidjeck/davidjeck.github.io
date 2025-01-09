import midi.*;

/**
 * Tests the Tune class by making a short turn and playing it.
 */
public class TestTune {

	public static void main(String[] args) {
	    Tune tune = new Tune();
	    tune.add( new Note(48, 300) );
	    tune.add( new Note(-1,100) );
	    tune.add( new Note(48, 300) );
	    tune.add( new Note(-1,100) );
	    tune.add( new Note(48, 300) );
	    tune.add( new Note(-1,100) );
	    tune.add( new Note(44, 2000) );
	    SimpleSynth midi = new SimpleSynth();
	    midi.setVolume(50);
	    midi.setInstrument(SimpleSynth.OBOE);
	    tune.play(midi);
	}
}
