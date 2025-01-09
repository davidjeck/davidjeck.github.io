package midi;

/**
 * A Tune objects represents a tune (that is, a sequence of Notes)
 * that can be played by a midi synthesizer. 
 */
public class Tune {

	private Note[] notes;  // Holds the notes in the tune.
	private int noteCount; // How many notes are stored in the array
	
	/**
	 * Create a Tune object with initial value 80 for the volume, 
	 * and with no notes.
	 */
	public Tune() {
		notes = new Note[1000];
		noteCount = 0;
	}
	
	/**
	 * Add a note to the Tune, represented as an object of type Note.
	 * @param note the Note to be added, which must be non-null
	 * @throws IllegalArgumentException if note is null.
	 */
	public void add(Note note) {
		if (note == null)
			throw new IllegalArgumentException("A Note in a Tune can't be null");
		notes[noteCount] = note;
		noteCount++;
	}
	
	/**
	 * Play the tune, using the given midi synthesizer.  The tune
	 * is played using the current volume and instrument set
	 * in the synthesizer.
	 */
	public void play( SimpleSynth midi ) {
		for (int i = 0; i < noteCount; i++) {
			notes[i].play(midi);
		}
	}
		
	/**
	 * Returns the number of notes in this Tune.
	 */
	public int getNoteCount() {
		return noteCount;
	}
	
	/**
	 * Returns one of the notes in this this tune.
	 * @param i The index of the note, that is, its position in the Tune, where the
	 *    the first note has index 0.  This must be in the range 0 through
	 *    getNoteCount() - 1.
	 */
	public Note getNote(int i) {
		return notes[i];
	}
	
} // end class Tune
