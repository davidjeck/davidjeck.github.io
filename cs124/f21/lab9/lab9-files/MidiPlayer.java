
import java.util.Scanner;
import midi.*;

public class MidiPlayer {

	private static Tune tune = null; // If not null, the most recently created Tune.
	private static SimpleSynth synth = new SimpleSynth();  // For playing notes.
	
	
	public static void main(String[] args) {
		
		Scanner stdin = new Scanner(System.in);  // For reading user input.
		
		System.out.println("Enter your commands at the '>>' prompt.");
		System.out.println("Enter an empty line to exit.");
		
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
		stdin.close();

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
		
	} // end doCommand()
	

} // end class MidiPlayer
