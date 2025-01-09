import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * A solitaire card game in which the user tries to get good poker
 * hands in each row, column, and diagonal of a 5-by-5 grid of cards.
 * The user sees one card at a time and must place that card on the
 * grid by clicking one of the empty grid positions.  The game is
 * over when all 25 grid positions have been filled.
 *    A score is computed for the game by assigning points to the
 * hands represented by each row, column, and diagonal according to
 * the following scale:
 *         One Pair:         1 point;
 *         Two Pairs:        2 points;
 *         Triple:           3 points;
 *         Straight:         4 points;
 *         Flush:            6 points;
 *         Full House:       9 points;
 *         Four of a Kind:  25 points;
 *         Straight Flush:  50 points;
 *         Royal Flush:    250 points
 */
public class PokerSolitaireWithScore extends Application {
	
	private static final int CARD_WIDTH = 90;   // Each card image is 90 pixels wide.
	private static final int CARD_HEIGHT = 126; // Each card image is 126 pixels tall.
	
	private Canvas canvas;     // The canvas on which the game is played.
	private GraphicsContext g; // A graphics context for drawing on the canvas.
	
	private Image faceDownCard; // An image of the back of a card.
	private boolean gameInProgress; // Set to false between games.
	private GraphicalCard[] deck; // holds the 52 cards of a standard poker deck.
	private GraphicalCard[][] grid; // holds the cards that have been placed on the board.
	private int nextCard; // the card that is available for the user to play next.
	private PokerRank ranker = new PokerRank(); // used for scoring poker hands.
	private int[] pointsForRank = { 0, 1, 2, 3, 4, 6, 9, 25, 50, 250 }; // maps ranks to scores.
	
	
	/**
	 *  Draw the game board, showing the grid of cards and the next card
	 *  that the user must play if the game is in progress.  The score
	 *  is also displayed.  If a game is not in progress, then the back
	 *  of a card id drawn in place of a card in the Next Card position.
	 */
	private void draw() {
		g.setFill(Color.GREEN);
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		g.setFill(Color.BEIGE);
		g.setStroke(Color.SIENNA);
		g.setLineWidth(2);
		for (int row = 0; row < 5; row++) {
			int y = 20 + row*(CARD_HEIGHT + 20);  // y-coordinate for cards in this row
			for (int col = 0; col < 5; col++) {
				int x = 20 + col*(CARD_WIDTH + 20);  // x-coordinate for cards in this column
				if (grid[row][col] != null) {
					grid[row][col].draw(g, x, y);
				}
				else {
					g.fillRect(x, y, CARD_WIDTH, CARD_HEIGHT);
				}
				g.strokeRect(x - 1, y - 1, CARD_WIDTH + 2, CARD_HEIGHT + 2); // draw the border
			}
		}
		// Draw the next card at (x,y) = (630,50).
		if (gameInProgress)
			deck[nextCard].draw(g, 630, 50);
		else
			g.drawImage(faceDownCard, 630, 50);
		g.strokeRect(630 - 1, 50 - 1, CARD_WIDTH + 2, CARD_HEIGHT + 2); // draw the border
		g.setFill(Color.BEIGE);
		g.setFont(Font.font(20));
		g.fillText("Next Card", 625, 35);
		if (!gameInProgress) {
		   g.fillText("Click anywhere\nto start a\nnew game!", 730, 80);
		}
		putScores();
	}
	
	
	/**
	 * Computes and outputs the type of poker hand and the number of points for each
	 * row, column, and diagonal in the grid.  Also outputs the total score.  The
	 * output is drawn along the right edge of the game board.  This method is only
	 * called from draw().
	 */
	private void putScores() {
		g.setFont(Font.font(16));
		g.setFill(Color.WHITE);
		int y = 230;
		int total = 0;
		for (int row = 0; row < 5; row++) {
			ranker.clear();
			for (int col = 0; col < 5; col++) {
				if (grid[row][col] != null) {
					ranker.add(grid[row][col]);
				}
			}
			int rank = ranker.getHandType();
			int points = pointsForRank[rank];
			g.fillText("Row " + (row+1) + ": " + ranker.getHandTypeAsString() + "  (" + points + " points)", 600, y);
			y += 35;
			total = total + points;
		}
		for (int col = 0; col < 5; col++) {
			ranker.clear();
			for (int row = 0; row < 5; row++) {
				if (grid[row][col] != null) {
					ranker.add(grid[row][col]);
				}
			}
			int rank = ranker.getHandType();
			int points = pointsForRank[rank];
			g.fillText("Column " + (col+1) + ": " + ranker.getHandTypeAsString() + "  (" + points + " points)", 600, y);
			y += 35;
			total = total + points;
		}
		ranker.clear();
		for (int i = 0; i < 5; i++) {
			if (grid[i][i] != null) {
				ranker.add(grid[i][i]);
			}
		}
		int rank = ranker.getHandType();
		int points = pointsForRank[rank];
		g.fillText("Diagonal 1: " + ranker.getHandTypeAsString() + "  (" + points + " points)", 600, y);
		y += 35;
		total = total + points;
		ranker.clear();
		for (int i = 0; i < 5; i++) {
			if (grid[i][4-i] != null) {
				ranker.add(grid[i][4-i]);
			}
		}
		rank = ranker.getHandType();
		points = pointsForRank[rank];
		g.fillText("Diagonal 2: " + ranker.getHandTypeAsString() + "  (" + points + " points)", 600, y);
		y += 55;
		total = total + points;
		g.fillText("TOTAL POINTS: " + total, 600, y);
	}
	
	/**
	 * Responds when the user clicks the board.  If no game is in progress, this will start a new
	 * game.  During a game, if the user clicks on an open spot on the board, the card from the
	 * Next Card box is placed in that position.  If the board is then full, the game ends.
	 * If not, a new Next Card is shown.
	 */
	private void doMouseDown(double x, double y) {
		if (!gameInProgress) {
			startGame();
			return;
		}
		for (int row = 0; row < 5; row++) {
			int r = 20 + row*(CARD_HEIGHT + 20);  // y-coordinate for cards in this row
			for (int col = 0; col < 5; col++) {
				int c = 20 + col*(CARD_WIDTH + 20);  // x-coordinate for cards in this column
				if ( x >= c && x < c + 90 && y >= r && y <= r + 126 && grid[row][col] == null) {
					grid[row][col] = deck[nextCard];
					nextCard++;
					if (nextCard == 25) {
						gameInProgress = false;
					}
					draw();
					return;
				}
			}
		}
	}

    /**
     * Make the array of 52 poker cards.  This method is called just once, at startup.
     */
	private void makeCards() {
		deck = new GraphicalCard[52];
		int i = 0;
		for (int s = 0; s < 4; s++) {
			for (int v = 2; v <= 14; v++) {
				deck[i] = new GraphicalCard(v, s);
				i++;
			}
		}		
	}
	
	/**
	 * Start a new game.  Shuffle the deck, remove all cards from the board, and
	 * select the first card to show in the Next Card box.
	 */
	private void startGame() {
		for (int top = 51; top > 0; top--) {
			int r = (int)((top+1)*Math.random());
			GraphicalCard temp = deck[r];
			deck[r] = deck[top];
			deck[top] = temp;
		}
		grid = new GraphicalCard[5][5];
		gameInProgress = true;
		nextCard = 0;
		draw();
	}
	
	
	/**
	 * Set up and show the window for the program.
	 */
	public void start( Stage stage ) {
		canvas = new Canvas(950,750);
		g = canvas.getGraphicsContext2D();
		BorderPane content = new BorderPane();
		content.setCenter(canvas);
		Scene scene = new Scene(content);
		stage.setScene(scene);
		stage.setTitle("Poker Solitaire");
		stage.setResizable(false);
		stage.show();
		faceDownCard = new Image("cards/back.jpg");
		canvas.setOnMousePressed( e -> doMouseDown( e.getX(), e.getY() ));
		makeCards();
		startGame();
	}
	
	
	/**
	 * Launch the application by calling its start() method.
	 */
	public static void main(String[] args) {
		launch(); 
	}

} // end class PokerSolitaire
