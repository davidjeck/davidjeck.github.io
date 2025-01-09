
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
 * In this case, 1000 sqaures all start at the center and move randomly.
 */
public class BrownianMotionComplete1 extends Application {
    
    private ShapeInfo[] squares;  // ShapeInfo objects representing moving squares.

    /**
     * Draws one frame of the animation.  
     */
    private void drawFrame(GraphicsContext g) {

        g.setFill(Color.WHITE);
        g.fillRect(0, 0, 600, 600); // First, fill the entire image with a background color.
        
        for (int i = 0; i < squares.length; i++) {
           g.setFill( squares[i].color );
           g.fillRect( squares[i].x - squares[i].size/2, 
                         squares[i].y - squares[i].size/2, squares[i].size, squares[i].size );
           squares[i].x += 4*Math.random() - 2;
           squares[i].y += 4*Math.random() - 2;
        }
        

    } // end DrawFrame
    
    /**
     * This method is called just once, when the program just starts.  It creates an array
     * of ShapeInfo objects located at (300,300), with size 10 and with random colors.
     */
    private void initialize(GraphicsContext g) {
        squares = new ShapeInfo[1000];
        for (int i = 0; i < squares.length; i++) {
            squares[i] = new ShapeInfo();
            squares[i].x = 300;
            squares[i].y = 300;
            squares[i].size = 10;
            squares[i].color = Color.color(Math.random(),Math.random(),Math.random());
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
        animator.start();
    } 

    public static void main(String[] args) {
        launch();
    }
    
} // end BrownianMotion
