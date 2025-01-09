import midi.SimpleSynth;
import midi.Note;

/** 
 * Tests the Note class by creating some notes and playing them.
 */
public class TestNote {

	public static void main(String[] args) {

		SimpleSynth synth = new SimpleSynth();  // Object to be used for playing notes.

		int beat = 200;  // Timing are given as multiples of this  many milliseconds.

		int[] notes = new int[] { // Code number for each note in the tune; -1 means pause
				60,60,60,62,64,-1,64,62,64,65,67
			};
		int[] times = new int[] { // Number of beats for the corresponding note in the tune.
				3, 3, 2, 1, 3, 4, 2, 1, 2, 1, 6
			};

		synth.setInstrument( SimpleSynth.PIANO );
		synth.setVolume(60);  // Quieter than the default

		for (int i = 0; i < notes.length; i++) { // Create a note and play it!
			Note note = new Note( notes[i], beat*times[i]);
			note.play(synth);
		}

	}

}
