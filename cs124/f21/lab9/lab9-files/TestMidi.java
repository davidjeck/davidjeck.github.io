import midi.SimpleSynth;

/** 
 * Tests the Midi system on the computer on which it is run by playing
 * some notes (but without using a Note or Tune class).
 */
public class TestMidi {

	public static void main(String[] args) {

		SimpleSynth synth = new SimpleSynth();  // Object to be used for playing notes.

		int beat = 200;  // Timing are given as multiples of this  many milliseconds.

		int[] notes = new int[] { // Code number for each note in the tune.
				60,60,60,62,64,64,62,64,65,67,72,72,72,67,67,67,64,64,64,60,60,60,67,65,64,62,60
			};
		int[] times = new int[] { // Number of beats for the corresponding note in the tune.
				3, 3, 2, 1, 3, 2, 1, 2, 1, 6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 6
			};

		synth.setInstrument( SimpleSynth.GUITAR );
		synth.setVolume(60);  // Quieter than the default

		for (int i = 0; i < notes.length; i++) {
			synth.noteOn( notes[i] );  // Turn on the i-th note in the tune.
			try {
				Thread.sleep( beat*times[i] ); // Delay to give the note a chance to play.
			}
			catch (InterruptedException e) {
			}
			synth.noteOff( notes[i] ); // Turn off the same note.
		}

	}

}
