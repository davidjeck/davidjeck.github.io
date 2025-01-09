
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * This program shows a simple animation when run, using brownian motion.
 */
public class BrownianMotion extends Application {

	private double x;  // x-coordinate of the center of a moving square
	private double y;  // y-coordinate of the center of a moving square

	/**
	 * Draws one frame of the animation.  It is responsible for redrawing the
	 * entire drawing area. The parameter g is used for drawing.
	 */
	private void drawFrame(GraphicsContext g) {

		g.setFill(Color.WHITE);
		g.fillRect(0, 0, 600, 600); // First, fill the entire image with a background color.

		g.setFill(Color.RED); // Draw a 20-by-20 square with center at (x,y).
		g.fillRect( x-10, y-10, 20, 20 );

		x = x + (4*Math.random() - 2); // Update x random amount in the range -2 to 2.
		y = y + (4*Math.random() - 2); // Update y similarly.

	} // end DrawFrame

	/**
	 * This method is called just once, when the program just starts, before anything
	 * is drawn.  Its purpose is to initialize global variables and possibly to initialize 
	 * the state of the graphics context.
	 */
	private void initialize(GraphicsContext g) {
		x = 300;  // Set the initial values of x and y to put the point at the center.
		y = 300;
	}

	//------ Implementation details: NO NEED TO UNDERSTAND THIS ------

	public void start(Stage stage) {
		int width = 600;   // The width of the image.
		int height = 600;  // The height of the image.
		Canvas canvas = new Canvas(width,height);
		GraphicsContext g = canvas.getGraphicsContext2D();
		BorderPane root = new BorderPane(canvas);
		root.setStyle("-fx-border-width: 4px; -fx-border-color: #444");
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Brownian Motion"); // this string appears in the window titlebar.
		stage.show();
		stage.setResizable(false);
		// The following is not the best way to animate a JavaFX program.
		Runnable animationLoop = () -> {
			try {
				Thread.sleep(500);  // wait 1/2 second before starting the animation
			}
			catch (InterruptedException e) {
			}
			while (true) {
				Platform.runLater( () -> drawFrame(g) ); // Call doFrame(g) in the GUI thread.
				try {
					Thread.sleep(16);  // Pause about 1/60 second.
				}
				catch (InterruptedException e) {
				}
			}
		};
		Thread animator = new Thread(animationLoop);
		animator.setDaemon(true); // So the animation thread doesn't stop JVM from exiting.
		initialize(g);
		drawFrame(g); // Call drawFrame(g) the first time -- the picture shown for the first 1/2 second.
		animator.start();
	} 

	public static void main(String[] args) {
		launch();
	}

} // end BrownianMotion
