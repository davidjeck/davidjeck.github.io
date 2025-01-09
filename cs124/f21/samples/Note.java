package midi;


/**
 * A Note object represents one note to be played by a midi system
 * for some specific amount of time.  The midi note number and
 * note duration in milliseconds are properties of the object
 * that cannot be changed after the note is created.  A Note
 * object can also represent a pause, that is, a delay during
 * which no sound it played.
 */
public class Note {

	private final int noteNumber;  // Midi note number 0 to 127, or -1 for a pause.
	private final int duration;    // Time to play the note, in milliseconds.
	
	/**
	 * Create a Note object to represent a midi note that plays for a specified
	 * amount of time.  The special value -1 for the note number indicates a
	 * pause of the specified duration.
	 * @param noteNumber  The midi note number for this note, or -1 for a pause.
	 * @param duration  The number of milliseconds that the note is to play.
	 * @throws IllegalArgumentException if the midi note number is not in the range
	 *    -1 through 127 or the duration is not greater than 0.
	 */
	public Note( int noteNumber,  int duration ) {
		if (noteNumber < -1 || noteNumber > 127)
			throw new IllegalArgumentException("Illegal value for a Midi note number, " + noteNumber);
		if (duration <= 0)
			throw new IllegalArgumentException("Duration of a note has to be positive.");
		this.noteNumber = noteNumber;
		this.duration = duration;
	}
	
	/**
	 * Play this note (or pause), for its given duration.
	 * @param midi  represents the midi synthesizer that is to be used to play the note.
	 */
	public void play( SimpleSynth midi ) {
		if (noteNumber != -1)
			midi.noteOn(noteNumber);
		try {
			Thread.sleep(duration);
		}
		catch (InterruptedException e) {
		}
		if (noteNumber != -1)
			midi.noteOff(noteNumber);
	}
	
	/**
	 * Returns this Note's midi note number.
	 */
	public int getNoteNumber() {
		return noteNumber;
	}
	
	/**
	 * Returns this Note's duration, in milliseconds.
	 */
	public int getDuration() {
		return duration;
	}
	
} // end class Note


