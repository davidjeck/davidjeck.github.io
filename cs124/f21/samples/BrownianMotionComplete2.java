
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
 * In this case, the entire canvas is initially covered with translucent
 * squares, which then move by Brownian motion.
 */
public class BrownianMotionComplete2 extends Application {

    
    private ShapeInfo[][] squares; // ShapeInfo objects representing moving squares.

    /**
     * Draws one frame of the animation. Draws all the squares and updates the 
     * positions of the squares to prepare for the next frame. 
     */
    private void drawFrame(GraphicsContext g) {

        g.setFill(Color.WHITE);
        g.fillRect(0, 0, 600, 600); // First, fill the entire image with a background color.
        
        for (int r = 0; r < 30; r++) {
            for (int c = 0; c < 40; c++) {
               ShapeInfo s = squares[r][c];
               g.setFill( s.color );
               g.fillRect( s.x - s.size/2, s.y - s.size/2, s.size, s.size );
               s.x += 4*Math.random() - 2;
               s.y += 4*Math.random() - 2;
            }
        }
        

    } // end DrawFrame
    
    /**
     * This method is called just once, when the program just starts.  It creates a 2D
     * array of ShapeInfo objects with size 20 and random translucent colors.  Initally
     * the squares are arranged into rows and columns that exactly tile the canvas.
     */
    private void initialize(GraphicsContext g) {
        squares = new ShapeInfo[30][40];
        for (int r = 0; r < 30; r++) {
            for (int c = 0; c < 40; c++) {
                squares[r][c] = new ShapeInfo();
                squares[r][c].x = 10+c*20;
                squares[r][c].y = 10+r*20;
                squares[r][c].size = 20;
                squares[r][c].color = Color.hsb(360*Math.random(),1,1,0.5);
            }
        }
    }

    //------ Implementation details: DO NOT EXPECT TO UNDERSTAND THIS ------


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
        drawFrame(g); // Call drawFrame(g) the first time -- the picture for the first 1/2 second.
        animator.start(); // Comment this line out to eliminate the animation.
    } 

    public static void main(String[] args) {
        launch();
    }
    
} // end BrownianMotion
