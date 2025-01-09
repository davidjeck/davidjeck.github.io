import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * A game of "hangman" in which the user tries to guess an
 * unknown word by guessing individual letters. The user loses
 * after seven incorrect letters are selected.  In this GUI
 * version, the user picks letters by clicking buttons.
 */
public class HangmanComplete extends Application {
	
	private GraphicsContext g;  // For drawing on the canvas.
	private Button nextWordBtn; // Starts a new game with a new word.
	private Button cheatBtn;    // Gives the user a free letter.
	private Button[] letterBtns;// One button for each letter of the alphabet.
	
	private boolean gameOver;   // Is a game currently in progress?
	private boolean cheated;    // Did the user use the cheat button in this game?
	private String guesses;     // Letters that have been guessed by the user.
	private int badGuesses;     // How many incorrect letters has the user picked?
	private String message;     // A message to be drawn at the top of the canvas.
	private String word;        // The word that the user is trying to guess.
	private WordList wordlist;  // The list of possible words for the game.
	
	
	/**
	 * This method is called at the start of the game and when the
	 * user clicks the "Next Word" button.  It initializes the game
	 * data, enables all of the letter buttons, and disables the
	 * next word and cheat buttons.  The canvas is drawn with just
	 * the scaffold and a blank space for each letter in the word.
	 */
	private void startGame() {
		gameOver = false;
		cheated = false;
		guesses = "";
		badGuesses = 0;
		nextWordBtn.setDisable(true);
		for (int i = 0; i < letterBtns.length; i++) {
			letterBtns[i].setDisable(false);
		}
		cheatBtn.setDisable(false);
		word = wordlist.removeRandomWord().toUpperCase();
		message = "The word has " + word.length() + " letters.  Let's play Hangman!";
		draw();
	}
	
	
	/**
	 * This method is called when the user clicks the cheat button.  It randomly
	 * selects a letter that is in the word but has not yet been guessed by the
	 * user.  This method can only be called when there is still at least one
	 * unguessed letter, since the cheat button is only enabled while a game
	 * is in progress.
	 */
	private void cheat() {
		cheated = true;
		String goodLetters = "";  // The remaining correct but unguessed letters.
		for (char ch = 'A'; ch <= 'Z'; ch++) {
			if (word.indexOf(ch) >= 0 && guesses.indexOf(ch) == -1) {
				// ch is a letter in the word, and has not yet been guessed by the user.
				goodLetters += ch;
			}
		}
		int index; // A random index into goodLetters, for picking a random uplayed letter.
		index = (int)(goodLetters.length()*Math.random());
		char letter = goodLetters.charAt(index);
		guesses += letter;
		if (wordIsComplete()) {
			// The game has ended because the free letter completed the word.
			message = "The word is complete.  You win (but you cheated)!";
			cheatBtn.setDisable(true);
			nextWordBtn.setDisable(false);
			for (Button b : letterBtns)
				b.setDisable(true);
			gameOver = true;
		}
		else {
			Button btn = letterBtns[ letter - 'A' ];
			btn.setDisable(true);
			message = "Your free letter is:  " + letter;
		}
		draw();
	}
	
	
	/**
	 * This method is called by other methods whenever the state of the game changes
	 * in a way that requires the content of the canvas to change.  It completely
	 * redraws the canvas based on the current state of the game.  The display includes
	 * two messages.  The first message is the value of the instance variable, messages;
	 * that value has to be set by the method that calls draw().
	 */
	private void draw() {
		g.setFill(Color.rgb(250, 230, 180));
		g.fillRect(0, 0, 650, 420);
		g.setFont(Font.font(20));
		if (message != null) {
			g.setFill(Color.RED);
			g.fillText(message, 30, 40);
		}
		if ( gameOver == true ) {
			g.setFill(Color.BLUE);
			g.fillText("Click \"Next word\" to play again.",30,70);
		}
		else {
			g.fillText("Bad Guesses Remaining:  " + (7-badGuesses), 30,70);
		}
		drawHangman(badGuesses);
		g.setStroke(Color.gray(0.3));
		g.setFill(Color.gray(0.3));
		g.setFont(Font.font(30));
		for (int i = 0; i < word.length(); i++) {
			g.strokeLine(10+i*50,400,50+i*50,400);
			if ( guesses.indexOf(word.charAt(i)) >= 0 ) {
				g.fillText("" + word.charAt(i), 20 + i*50, 395);
			}
		}
	}
	
	
	/**
	 * Draw the scaffold and the "hanged man."  The hanged man is
	 * drawn in 7 parts, depending on how many bad guesses the
	 * user has made.  The level parameter tells this method how
	 * many pieces of the hanged man to draw.
	 */
	private void drawHangman(int level) {
		g.setFill(Color.rgb(90,30,0));
		g.fillRect(300,320,200,10);
		g.fillRect(340,100,10,225);
		g.fillRect(340,120,100,7);
		g.strokeLine(430,125,430,160);
		g.setStroke(Color.rgb(0,70,0));
		if (level > 0)
			g.fillOval(415,140,30,40);
		if (level > 1)
			g.strokeLine(430,180,430,205);
		if (level > 2)
			g.strokeLine(395,225,430,200);
		if (level > 3)
			g.strokeLine(430,200,465,225);
		if (level > 4)
			g.strokeLine(430,205,430,245);
		if (level > 5)
			g.strokeLine(390,290,430,245);
		if (level > 6)
			g.strokeLine(430,245,470,290);
	}
	
	
	/**
	 *  This method is called from start() to make the button bar that
	 *  appears at the bottom of the window.  It contains a next word
	 *  button, a cheat button, and a quit button.
	 */
	private HBox makeControlButtons() {
		nextWordBtn = new Button("Next Word");
		nextWordBtn.setOnAction( e -> startGame() );
		cheatBtn = new Button("Cheat (free letter)");
		cheatBtn.setOnAction( e -> cheat() );
		Button quitBtn = new Button("Quit");
		quitBtn.setOnAction( e -> Platform.exit() );
		HBox box = new HBox(10,nextWordBtn,cheatBtn,quitBtn);
		box.setStyle("-fx-border-width: 3px 0 0 0 0; -fx-border-color: black; -fx-padding: 5px");
		box.setAlignment(Pos.CENTER);
		return box;
	}
	
	
	/**
	 *  This method is called from start() to make the 26 letter buttons
	 *  that appear at the top of the window.
	 */
	private GridPane makeAlphabetButtons() {
		GridPane letters = new GridPane();
		letterBtns = new Button[26];
		int ct = 0;
		for (char ch = 'A'; ch <= 'Z'; ch++) {
			Button btn = new Button("" + ch);
			btn.setOnAction( evt -> doLetterBtn(btn) );
			/* There are two rows of 13 buttons in the grid, and the
			 * canvas is 650 pixels wide.  By setting the preferred size
			 * of each button to 650/13, the total preferred size of the
			 * grid will be 650, matching the size of the canvas.  This
			 * will make stretch horizontally across the entire window.
			 * Without this change, there is a big gap to the right 
			 * of the gridpane.   The button is then added in column
			 * number ct%13, which has values 0 to 12, and in row number
			 * ct/13, which is either 0 or 1, giving two rows with
			 * 13 buttons in each row.*/
			btn.setPrefWidth(650/13);
			letters.add(btn,ct%13,ct/13);
			letterBtns[ct] = btn;
			ct++;
		}
		letters.setStyle("-fx-border-width: 3px 0 4px 0; -fx-border-color: black");
		return letters;
	}
	
	
	/**
	 * This method is called when the user clicks one of the 26 letter buttons.
	 * The parameter tells which button was clicked.
	 */
	private void doLetterBtn(Button btn) {
		char guess; // Which letter was clicked, the first (and only) char in the button text.
		guess = btn.getText().charAt(0);
		guesses = guesses + guess; // Add this letter to the list of letters guessed so far.
		btn.setDisable(true); // Disable button so that it can't be clicked again in this game.
		if (wordIsComplete()) {  // Game ends because all letters in the word have been guessed.
			if (cheated)
				message = "The word is complete.  You win (but you cheated)!";
			else
				message = "Congratulations!  You got it!";
			cheatBtn.setDisable(true);
			nextWordBtn.setDisable(false);
			for (Button b : letterBtns)
				b.setDisable(true);
			gameOver = true;
		}
		else if (word.indexOf(guess) >= 0) {  // Letter is in the word.
			message = "Yes, " + guess + " is in the word.  Pick your next letter.";
		}
		else {  // Letter is not in the word, so number of bad guesses goes up by one.
			badGuesses++;
			if (badGuesses == 7) { // Game ends becasue the user is out of guesses.
				message = "Sorry, you're hung!  The word is:  " + word;
				cheatBtn.setDisable(true);
				nextWordBtn.setDisable(false);
				for (Button b : letterBtns)
					b.setDisable(true);
				gameOver = true;
			}
			else {
				message = "Sorry, " + guess + " is not in the word.  Pick your next letter.";
			}
		}
		draw();
	}
	
	
	/**
	 * Tests whether all of the letters in the word have been guessed.
	 */
	private boolean wordIsComplete() {
		for (int i = 0; i < word.length(); i++) {
			char ch = word.charAt(i);
			if ( guesses.indexOf(ch) == -1 ) {
				return false;
			}
		}
		return true;
	}

	
	/**
	 * This method is called when the program starts to set up the
	 * GUI, including creating and laying out components and installing
	 * an event handler for each of the buttons.
	 */
	public void start(Stage stage) {
		Canvas canvas = new Canvas(650,420);
		g = canvas.getGraphicsContext2D();
		BorderPane mainPane = new BorderPane(canvas);
		mainPane.setBottom(makeControlButtons());
		mainPane.setTop(makeAlphabetButtons());
		Scene scene = new Scene(mainPane);
		stage.setScene(scene);
		stage.setTitle("Hangman");
		stage.setResizable(false);
		stage.show();
		wordlist = new WordList("wordlist.txt");
		g.setLineWidth(4);
		startGame();
	}
	
	public static void main(String[] args) {
		launch();  // Run the application!
	}

} // end class HangmanComplete
