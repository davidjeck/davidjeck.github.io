import textio.TextIO;

/**
 * Implements the game of Hangman.  The computer chooses a secret word.
 * The user guesses letters in the word.  If the user makes 7 incorrect
 * guesses, they lose.  If they guess all the letters in the word 
 * before they reach 7 bad guesses, they win.  After each guess,
 * the word is displayed with the guessed letters shown and with
 * letters that have not yet been guessed shown as underscores.
 * After each game, the user chooses whether to play again.  The
 * secret word is choosen at random from the words in the file
 * wordlist.txt, which is required for this program to run.
 */
public class HangmanComplete {

	public static void main(String[] args) {

		String[] words;  // The list of words from wordlist.txt.
		
		// Start by reading the words from the file.
		
		TextIO.readFile("wordlist.txt");
		int wordCt; // Number of words in the file, from the first line in the file.
		int index;  // for-loop variable.
		wordCt = TextIO.getlnInt();
		words = new String[wordCt];
		for (index = 0; index < wordCt; index++) {
			words[index] = TextIO.getln();
		}
		TextIO.readStandardInput();
		
		// Implement the game.

		String word;  // The word that the user is trying to guess, chosen at random.
		String lettersGuessed;  // The letters that the user has guessed so far.
		int badGuesses; // How many incorrect guesses has the user made?
		char letter;  // A letter guessed by the user.

		int i; // for-loop variable.
		int guessed;  // how many letters in the word have been guessed
		char ch; // one of the letters in a string

		System.out.println();
		System.out.println("Let's play Hangman!");

		while (true) {

			int randIndex;  // random index into the array of words
			randIndex = (int)(Math.random() * words.length);

			word = words[randIndex];  // Get the word from a random location in the array.
			lettersGuessed = "";
			badGuesses = 0;

			word = word.toUpperCase();  // Make sure the word is all uppercase.

			System.out.println();
			System.out.println("The word has " + word.length() + " letters.");
			System.out.println();

			System.out.print("What is your first letter? ");

			while (true) {

				// Get a letter from the user, making sure it hasn't already been used.
				
				do {
					letter = TextIO.getlnChar();
					letter = Character.toUpperCase(letter);
					if ( lettersGuessed.indexOf(letter) != -1) {
						System.out.print("You already guessed " + letter +"! Try another letter: ");
					}
				} while ( lettersGuessed.indexOf(letter) != -1 );
				lettersGuessed += letter;
				
				// Check if the letter is in the word, an respond appropriately.

				if (word.indexOf(letter) == -1) { // the letter is not in the word
					System.out.println("   Sorry, the word does not contains a " + letter + ".");
					badGuesses++;
					System.out.println("   You have now made " + badGuesses + " bad guesses.");
					if (badGuesses == 7) { 
						System.out.println();
						System.out.println("   YOU ARE HUNG!  You've lost.");
						System.out.println("   The word was:  " + word);
						break; // Game is over because the user ran out of bad guesses.
					}
					else {
						if (badGuesses == 6)
							System.out.println("   You only have one bad guess left!");
						else
							System.out.println("   You have " + (7 - badGuesses) + " guesses left.");
					}
				}
				else { // The letter is in the word.
					System.out.println("   Yes, the word contains a " + letter + ".");
				}
				
				// Show the user the word, with missing letters shown as "_", and check whether
				// the word is complete by counting letters that have been guessed.

				System.out.println();
				System.out.print("The word so far:   ");

				guessed = 0;
				for (i = 0; i < word.length(); i++) {
					ch = word.charAt(i);
					if ( lettersGuessed.indexOf(ch) == -1) {
						// User has not yet guessed ch; print an inderscore.
						System.out.print("_ ");
					}
					else {
						// User has guesses ch; output ch and count it.
						System.out.print(ch + " ");
						guessed++;
					}
				}
				System.out.println();

				if (guessed == word.length()) { // user has guessed all the letters
					System.out.println();
					System.out.println("   The word is complete!  You've won.");
					break;  // The game ends because the user has guessed all the letters.
				}
				
				// Show the user the list of letters they have already used, and
				// ask for the next letter.

				System.out.println();
				System.out.print("Your previous guesses: ");
				for (ch = 'A'; ch <= 'Z'; ch++) {
					if (lettersGuessed.indexOf(ch) >= 0)
						System.out.print(ch);
				}
				System.out.println();
				System.out.print("What is your next letter? ");
			} // end while
			
			// A game has ended. Ask whether the user wants to play again.
			
			System.out.println();
			System.out.print("Do you want to play again? ");
			if ( ! TextIO.getlnBoolean() ) {
				break; // Quit because user does not want to play again.
			}
			System.out.println();
			System.out.println();
			System.out.println("OK, I've picked another word!");
		}

		System.out.println();
		System.out.println("Thanks for playing!");
		System.out.println();

	} // end main()

} // end class Hangman
