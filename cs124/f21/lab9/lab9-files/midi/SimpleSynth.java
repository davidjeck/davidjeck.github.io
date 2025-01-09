package midi;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;


/**
 * An object of type SimpleSynth provides a simplified interface to
 * the default Midi synthesizer, with methods for playing a note,
 * selecting a midi instrument, and setting the volume.  There is
 * also a static method that returns the name for a given instrument
 * number.
 */
public class SimpleSynth {

	private MidiChannel midiChannel;  // An interface to the actual Midi system.

	private int volume;     // Current volume, in the range 0 to 127
	private int instrument; // Current instrument number, in the range 0 to 127

	private boolean[] noteIsPlaying;  // keep track of which notes are currently playing

	/**
	 * Constants representing one of the standard Midi "instruments".
	 */
	public final static int
		PIANO = 0,
		HARPSICHORD = 6,
		XYLOPHONE = 13,
		CHURCH_ORGAN = 19,
		REED_ORGAN = 20,
		HARMONICA = 22,
		GUITAR = 24,
		ELECTRIC_GUITAR = 27,
		VIOLIN = 40,
		HARP = 46,
		TIMPANI = 47,
		TRUMPET = 56,
		TROMBONE = 57,
		OBOE = 68,
		FLUTE = 73,
		BANJO = 105,
		STEEL_DRUMS = 114;


	/**
	 * Constructor creates a midi channel for playing sounds.
	 * @throws IllegalStateException if any errors while trying to create the channel.
	 */
	public SimpleSynth() {
		try {
			Synthesizer midiSynthesizer = MidiSystem.getSynthesizer();
			midiSynthesizer.open();
			MidiChannel[] allChannels = midiSynthesizer.getChannels();
			midiChannel = allChannels[0];
			midiChannel.programChange(PIANO);
		} catch (MidiUnavailableException e) {
			throw new IllegalStateException("Midi support is not available!");
		}
		volume = 80;
		instrument = PIANO;
		noteIsPlaying = new boolean[128];
		try {
			// It seems to take a short time for a synthesizer to start up
			// properly, so wait for 200 milliseconds to let that happen.
			Thread.sleep( 200 );
		}
		catch (InterruptedException e) {
		}
	}

	/**
	 * Set the volume for future notes (until the next volume change).  The default
	 * volume level is 80.
	 * @param volumeLevel the volume, where 0 represents no sound at all,and 127 is full volume
	 * @throws IllegalArgumentException if the parameter is outside the range 0 to 127
	 */
	public void setVolume(int volumeLevel) {
		if (volumeLevel < 0 || volumeLevel > 127)
			throw new IllegalArgumentException("Midi volume level must be in the range 0 to 127");
		volume = volumeLevel;
	}

	/**
	 * Returns the current volume level, in the range 0 to 127.
	 */
	public int getVolume() {
		return volume;
	}

	/**
	 * Turns a specified note on.  Multiple notes can be playing at the same time.
	 * Note that for some instruments, the volume of a note fades over time, so that
	 * it can become inaudible before noteOff() is called.
	 * @param noteNumber The midi note number for the note to be played, from 0 to 127
	 * @throws IllegalArgumentException if the parameter is outside the range 0 to 127
	 */
	public void noteOn(int noteNumber) {
		if (noteNumber < 0 || noteNumber > 127)
			throw new IllegalArgumentException("Midi note numbers must be in the range 0 to 127");
		midiChannel.noteOn( noteNumber, volume );
		noteIsPlaying[noteNumber] = true;
	}

	/**
	 * Turns a specified note off.  Multiple notes can be playing at the same time.
	 * If the note is not currently on, or if the parameter is not a legal midi
	 * note number in the range 0 to 127, then the method call is simply ignored.
	 */
	public void noteOff(int noteNumber) {
		if (noteNumber >= 0 && noteNumber <= 127 && noteIsPlaying[noteNumber]) {
			midiChannel.noteOff( noteNumber );
			noteIsPlaying[noteNumber] = false;
		}
	}

	/**
	 * Tests whether a given note is currently playing.  Note that for
	 * some instruments, the volume of the note fades over time, so it can become
	 * inaudible even though it is still technically playing.  This method tests
	 * whether the note is technically playing, not whether it is audible.
	 * @param noteNumber the midi note number of the note to be tested
	 * @return true if the note is playing, false if not
	 * @throws IllegalArgumentException if the parameter is outside the range 0 to 127
	 */
	public boolean isNoteOn(int noteNumber) {
		if (noteNumber < 0 || noteNumber > 127)
			throw new IllegalArgumentException("Midi note numbers must be in the range 0 to 127");
		return noteIsPlaying[noteNumber];
	}

	/**
	 * Turns off any notes that are currently playing.
	 */
	public void allNotesOff() {
		midiChannel.allNotesOff();
		noteIsPlaying = new boolean[128];
	}

	/**
	 * Set the instrument that is to be used for future notes.  The default instrument is
	 * a "grand piano", instrument number zero.
	 * @param instrumentNumber the midi instrument number for the instrument.  There are
	 *    128 instruments, numbered 0 to 127.
	 * @throws IllegalArgumentException if the parameter is outside the range 0 to 127
	 */
	public void setInstrument(int instrumentNumber) {
		if (instrumentNumber < 0 || instrumentNumber > 127)
			throw new IllegalArgumentException("Midi instrument numbers must be in the range 0 to 127");
		midiChannel.programChange(instrumentNumber);
		instrument = instrumentNumber;
	}

	/**
	 * Returns the midi instrument number of the instruments that is currently in use.
	 */
	public int getInstrument() {
		return instrument;
	}

	/**
	 * A static method to get the name for a given standard Midi instrument.
	 * @param instrumentNumber  The number of the instrument, in the range 0 to 127. inclusive.
	 * @return the name corresponding to the instrument number.
	 * @throws IllegalArgumentException if the instrument number is not in the range 0 to 127.
	 */
	public static String getNameForInstrument(int instrumentNumber) {
		if (instrumentNumber < 0 || instrumentNumber > 127)
			throw new IllegalArgumentException("Midi instrument numbers must be in the range 0 to 127");
		return instrumentNames[instrumentNumber];
	}

	// An array that holds the official names for all 128 standard Midi "instruments".
	private static final String[] instrumentNames = {
			/*   0: */     "Acoustic Grand Piano",
			/*   1: */     "Bright Acoustic Piano",
			/*   2: */     "Electric Grand Piano",
			/*   3: */     "Honky-tonk Piano",
			/*   4: */     "Electric Piano 1",
			/*   5: */     "Electric Piano 2",
			/*   6: */     "Harpsichord",
			/*   7: */     "Clavi",
			/*   8: */     "Celesta",
			/*   9: */     "Glockenspiel",
			/*  10: */     "Music Box",
			/*  11: */     "Vibraphone",
			/*  12: */     "Marimba",
			/*  13: */     "Xylophone",
			/*  14: */     "Tubular Bells",
			/*  15: */     "Dulcimer",
			/*  16: */     "Drawbar Organ",
			/*  17: */     "Percussive Organ",
			/*  18: */     "Rock Organ",
			/*  19: */     "Church Organ",
			/*  20: */     "Reed Organ",
			/*  21: */     "Accordion",
			/*  22: */     "Harmonica",
			/*  23: */     "Tango Accordion",
			/*  24: */     "Acoustic Guitar (nylon)",
			/*  25: */     "Acoustic Guitar (steel)",
			/*  26: */     "Electric Guitar (jazz)",
			/*  27: */     "Electric Guitar (clean)",
			/*  28: */     "Electric Guitar (muted)",
			/*  29: */     "Overdriven Guitar",
			/*  30: */     "Distortion Guitar",
			/*  31: */     "Guitar harmonics",
			/*  32: */     "Acoustic Bass",
			/*  33: */     "Electric Bass (finger)",
			/*  34: */     "Electric Bass (pick)",
			/*  35: */     "Fretless Bass",
			/*  36: */     "Slap Bass 1",
			/*  37: */     "Slap Bass 2",
			/*  38: */     "Synth Bass 1",
			/*  39: */     "Synth Bass 2 10",
			/*  40: */     "Violin",
			/*  41: */     "Viola",
			/*  42: */     "Cello",
			/*  43: */     "Contrabass",
			/*  44: */     "Tremolo Strings",
			/*  45: */     "Pizzicato Strings",
			/*  46: */     "Orchestral Harp",
			/*  47: */     "Timpani",
			/*  48: */     "String Ensemble 1",
			/*  49: */     "String Ensemble 2",
			/*  50: */     "SynthStrings 1",
			/*  51: */     "SynthStrings 2",
			/*  52: */     "Choir Aahs",
			/*  53: */     "Voice Oohs",
			/*  54: */     "Synth Voice",
			/*  55: */     "Orchestra Hit",
			/*  56: */     "Trumpet",
			/*  57: */     "Trombone",
			/*  58: */     "Tuba",
			/*  59: */     "Muted Trumpet",
			/*  60: */     "French Horn",
			/*  61: */     "Brass Section",
			/*  62: */     "SynthBrass 1",
			/*  63: */     "SynthBrass 2",
			/*  64: */     "Soprano Sax",
			/*  65: */     "Alto Sax",
			/*  66: */     "Tenor Sax",
			/*  67: */     "Baritone Sax",
			/*  68: */     "Oboe",
			/*  69: */     "English Horn",
			/*  70: */     "Bassoon",
			/*  71: */     "Clarinet",
			/*  72: */     "Piccolo",
			/*  73: */     "Flute",
			/*  74: */     "Recorder",
			/*  75: */     "Pan Flute",
			/*  76: */     "Blown Bottle",
			/*  77: */     "Shakuhachi",
			/*  78: */     "Whistle",
			/*  79: */     "Ocarina",
			/*  80: */     "Lead 1 (square)",
			/*  81: */     "Lead 2 (sawtooth)",
			/*  82: */     "Lead 3 (calliope)",
			/*  83: */     "Lead 4 (chiff)",
			/*  84: */     "Lead 5 (charang)",
			/*  85: */     "Lead 6 (voice)",
			/*  86: */     "Lead 7 (fifths)",
			/*  87: */     "Lead 8 (bass + lead)",
			/*  88: */     "Pad 1 (new age)",
			/*  89: */     "Pad 2 (warm)",
			/*  90: */     "Pad 3 (polysynth)",
			/*  91: */     "Pad 4 (choir)",
			/*  92: */     "Pad 5 (bowed)",
			/*  93: */     "Pad 6 (metallic)",
			/*  94: */     "Pad 7 (halo)",
			/*  95: */     "Pad 8 (sweep)",
			/*  96: */     "FX 1 (rain)",
			/*  97: */     "FX 2 (soundtrack)",
			/*  98: */     "FX 3 (crystal)",
			/*  99: */     "FX 4 (atmosphere)",
			/* 100: */     "FX 5 (brightness)",
			/* 101: */     "FX 6 (goblins)",
			/* 102: */     "FX 7 (echoes)",
			/* 103: */     "FX 8 (sci-fi)",
			/* 104: */     "Sitar",
			/* 105: */     "Banjo",
			/* 106: */     "Shamisen",
			/* 107: */     "Koto",
			/* 108: */     "Kalimba",
			/* 109: */     "Bag pipe",
			/* 110: */     "Fiddle",
			/* 111: */     "Shanai",
			/* 112: */     "Tinkle Bell",
			/* 113: */     "Agogo",
			/* 114: */     "Steel Drums",
			/* 115: */     "Woodblock",
			/* 116: */     "Taiko Drum",
			/* 117: */     "Melodic Tom",
			/* 118: */     "Synth Drum",
			/* 119: */     "Reverse Cymbal",
			/* 120: */     "Guitar Fret Noise",
			/* 121: */     "Breath Noise",
			/* 122: */     "Seashore",
			/* 123: */     "Bird Tweet",
			/* 124: */     "Telephone Ring",
			/* 125: */     "Helicopter",
			/* 126: */     "Applause",
			/* 127: */     "Gunshot"
	};

} // end class SimpleSynth
