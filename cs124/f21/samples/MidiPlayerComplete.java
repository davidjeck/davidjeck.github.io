
import java.util.Scanner;
import midi.*;


/**
 * This program provides a simple interface for Midi sound synthesis.  The user
 * can enter commands to set the volume and instrument used.  The user can enter
 * a tune by inputting a list of note numbers, and can have the program make a
 * random tune.  A list of commands is printed when the program is run.
 */
public class MidiPlayerComplete {

    private static Tune tune = null; // If not null, the most recently created Tune.
    private static SimpleSynth synth = new SimpleSynth();  // For playing notes.
    
    
    public static void main(String[] args) {
    
        System.out.println("""
            Wecome to MidiPlayer.  The following commands are available:
               
               volume N -- Set the volume level to N; N must be in the range 0 to 127.
               instrument N -- Set the instrument to instrument number N, range 0 to 127.
               instrument NAME -- Set the instrument by name; available names are:
                                  piano xylophone flute    guitar timpani
                                  oboe  trumpet   trombone organ  harp
               play <notes> -- Play a tune consisting of the specified notes.  The notes
                               are specified by numbers in the range 0 to 127, or -1 for a
                               period of silence.  Notes are sparated by spaces or commas.
                               The tune is also saved and can be replayed.
               random -- Create a play a random tune, and save the tune for replaying
               replay -- Replay the saved tuen, with the current volumn and instrument.
               
            Enter your commands at the '>>' prompt.
            Enter an empty line to exit.
            """);
        
        Scanner stdin = new Scanner(System.in);  // For reading user input.
                
        while (true) {
            
            System.out.println();
            System.out.print(">> ");
            String commandLine = stdin.nextLine().trim();
            if (commandLine.length() == 0)
                break;
            String[] commandArray = commandLine.split("[,\s]+");
            
            try {
                doCommand(commandArray);
            }
            catch (Exception e) {
                System.out.println("Sorry, an error occurred:");
                System.out.println("   " + e.getMessage());
                System.out.println("Try another command.");
            }
            
        }
        
        System.out.println();
        System.out.println("Bye!");

    } // end main()
    
    
    /**
     *  Carries out a command entered by the user.  The commandArray parameter
     *  contains the user's input line, divided into substrings that were
     *  separated by white space and commas.  commandArray[0] should be a
     *  word, giving the command.  Additional entries in the array are 
     *  data for the command (in String form).  This method throws
     *  IllegalArgumentException if the command or its data are bad.
     */
    private static void doCommand(String[] commandArray) {
        
        switch (commandArray[0].toLowerCase()) {
        case "replay":
            if ( tune == null)
                System.out.println("You don't have a tune yet!  Make one first.");
            else
                tune.play(synth);
            break;
        case "volume":
            if (commandArray.length < 2)
                throw new IllegalArgumentException("You must specify the volume.");
            try {
                synth.setVolume(Integer.parseInt(commandArray[1]));
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Volume must be specified as an integer.");
            }
            break;
        case "instrument":
            if (commandArray.length < 2)
                throw new IllegalArgumentException("You must specify the instrument.");
            setInstrument(commandArray[1]);
            break;
        case "play":
            if (commandArray.length < 2)
                throw new IllegalArgumentException("You must specify notes for the tune.");
            makeUserTune(commandArray);
            tune.play(synth);
            break;
        case "random":
            makeRandomTune();
            tune.play(synth);
            break;
        default:
            throw new IllegalArgumentException(commandArray[0] + " is not a legal command.");
        }
    } // end doCommand(); 
    
    
    /**
     * Create a random tune, and store it in the global variable named tune.
     */
    private static void makeRandomTune() {
        Tune song = new Tune();
        int note = 55 + (int)(Math.random()*11);
        for (int i = 0; i < 100; i++) {
            if (Math.random() < 0.04)
                song.add( new Note(-1,200));
            else {
                int newNote = 0;
                while (newNote < 36 || newNote > 84) {
                    int jump = 1 + (int)( 12*Math.pow(Math.random(), 6) ); 
                    if (Math.random() > 0.5)
                        jump = -jump;
                    newNote = note + jump;
                }
                note = newNote;
                song.add( new Note(note, 200));
            }
        }
        tune = song;
    }

    /**
     * Create a tune containing the sequence of notes in the commandArray
     * parameter, starting at index 1.  The strings in the array must
     * be numbers in the range -1 to 127, where -1 represents a pause.
     * The tune is saved in the global variable named tune.
     */
    private static void makeUserTune(String[] commandArray) {
        try {
            Tune newTune = new Tune();
            for (int i = 1; i < commandArray.length; i++) {
                int noteNum = Integer.parseInt(commandArray[i]);
                newTune.add(new Note(noteNum,200));
            }
            tune = newTune;
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Notes must be specified by integers.");
        }
    }
    
    /**
     * Set the Midi instrument to be used to play tunes.  the parameter can be
     * the name of one of the standard instruments (as printed out in the
     * instructions when the program runs), or it can be a Midi instrument
     * number in the range 0 to 127.
     */
    private static void setInstrument(String instrument) {
        switch (instrument.toLowerCase()) {
            case "piano" ->     synth.setInstrument(SimpleSynth.PIANO);
            case "xylophone" -> synth.setInstrument(SimpleSynth.XYLOPHONE);
            case "flute" ->     synth.setInstrument(SimpleSynth.FLUTE);
            case "guitar" ->    synth.setInstrument(SimpleSynth.GUITAR);
            case "timpani" ->   synth.setInstrument(SimpleSynth.TIMPANI);
            case "oboe" ->      synth.setInstrument(SimpleSynth.OBOE);
            case "violin" ->    synth.setInstrument(SimpleSynth.VIOLIN);
            case "trumpet" ->   synth.setInstrument(SimpleSynth.TRUMPET);
            case "trombone" ->  synth.setInstrument(SimpleSynth.TROMBONE);
            case "organ" ->     synth.setInstrument(SimpleSynth.CHURCH_ORGAN);
            case "harp" ->      synth.setInstrument(SimpleSynth.HARP);
            default -> {
                try {
                    synth.setInstrument(Integer.parseInt(instrument));
                }
                catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                          "The instrument must be a number or valid instrument name.\n" +
                          "   Valid names are:  piano xylophone flute    guitar timpani\n" +
                          "                     oboe  trumpet   trombone organ  harp"
                     );
                }
            }
        }
    }
    

} // end class MidiPlayer
