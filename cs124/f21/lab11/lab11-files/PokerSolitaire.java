import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
 */
public class PokerSolitaire extends Application {

	private static final int CARD_WIDTH = 90;   // Each card image is 90 pixels wide.
	private static final int CARD_HEIGHT = 126; // Each card image is 126 pixels tall.

	private Canvas canvas;     // The canvas on which the game is played.
	private GraphicsContext g; // A graphics context for drawing on the canvas.


	/**
	 *  Draw the game board, showing the grid of cards and the next card
	 *  that the user must play.
	 */
	private void draw() {
		g.setFill(Color.GREEN);
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		g.setFill(Color.BEIGE);
		g.setStroke(Color.SIENNA);
		g.setLineWidth(2);
		// Draw the 5-by-5 grid of cards.
		for (int row = 0; row < 5; row++) {
			int y = 20 + row*(CARD_HEIGHT + 20);  // y-coordinate for cards in this row
			for (int col = 0; col < 5; col++) {
				int x = 20 + col*(CARD_WIDTH + 20);  // x-coordinate for cards in this column
				g.fillRect(x, y, CARD_WIDTH, CARD_HEIGHT); // draw empty space
				g.strokeRect(x - 1, y - 1, CARD_WIDTH + 2, CARD_HEIGHT + 2); // draw a border
			}
		}
		// Draw the next available card, labeled "Next Card".
		g.strokeRect(630 - 1, 50 - 1, CARD_WIDTH + 2, CARD_HEIGHT + 2); // draw a border
		g.setFill(Color.BEIGE);
		g.setFont(Font.font(20));
		g.fillText("Next Card", 625, 35);
	}


	/**
	 * Set up and show the window for the program.
	 */
	public void start( Stage stage ) {
		canvas = new Canvas(780,750);
		g = canvas.getGraphicsContext2D();
		BorderPane content = new BorderPane();
		content.setCenter(canvas);
		Scene scene = new Scene(content);
		stage.setScene(scene);
		stage.setTitle("Poker Solitaire");
		stage.setResizable(false);
		stage.show();
		draw();
	}


	/**
	 * Launch the application by calling its start() method.
	 */
	public static void main(String[] args) {
		launch(); // (does not return; program ends when user closes the window)
	}

} // end class PokerSolitaire
