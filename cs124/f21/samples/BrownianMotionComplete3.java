
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
 * In this case, the canvas is divided into rows and columns of cells.
 * Each cell contains a moving disk that can't escape from the cell.
 */
public class BrownianMotionComplete3 extends Application {
    
    private ShapeInfo[][] disks;  // ShapeInfo objects representing moving disks.

    /**
     * Draws one frame of the animation.  It draws a grid of light gray squares
     * on a black background, with room between the sqaures where the black still
     * shows.  It draws one disk from the 2D array in each cell, then updates 
     * the position of the disk to get ready for the next frame.  If the motion
     * would move the disk outside the cell, then the motion is not applied.
     */
    private void drawFrame(GraphicsContext g) {

        g.setFill(Color.BLACK);
        g.fillRect(0, 0, 600, 600); // First, fill the entire image with a background color.
        
        for (int r = 0; r < 30; r++) {
            for (int c = 0; c < 40; c++) {
               g.setFill(Color.LIGHTGRAY);
               g.fillRect( 1 + 20*c, 1 +20*r, 18, 18 );
               ShapeInfo s = disks[r][c];
               g.setFill( s.color );
               g.fillOval( s.x - s.size/2, s.y - s.size/2, s.size, s.size );
               double newX = s.x + 4*Math.random() - 2;
               double newY = s.y + 4*Math.random() - 2;
               if (Math.abs( 10 + 20*c - newX ) <= 5 && Math.abs( 10 + 20*r - newY ) <= 5) {
                    // Only use the new values for s.x and s.y if the disk will lie 
                    // entirely in the disk's gray cell.
                  s.x = newX;
                  s.y = newY;
               } 
            }
        }
        

    } // end DrawFrame
    
    /**
     * This method is called just once, when the program just starts.  It creates
     * a 2D array of disks with diameter 10 and random HSB color.  The disks
     * are given positions at the centers of the rows and columns of cells.
     */
    private void initialize(GraphicsContext g) {
        disks = new ShapeInfo[30][40];
        for (int r = 0; r < 30; r++) {
            for (int c = 0; c < 40; c++) {
                disks[r][c] = new ShapeInfo();
                disks[r][c].x = 10+c*20;
                disks[r][c].y = 10+r*20;
                disks[r][c].size = 10;
                disks[r][c].color = Color.hsb(360*Math.random(),1,1);
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
