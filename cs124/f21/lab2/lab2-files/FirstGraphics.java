
import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 *  This file can be used to draw simple pictures.  Just fill in
 *  the definition of drawPicture with the code that draws your picture.
 */
public class FirstGraphics extends Application {

    /**
     * Draws a picture.  The parameters width and height give the size 
     * of the drawing area, in pixels.  
     */
    public void drawPicture(GraphicsContext g) {

        g.setFill(Color.WHITE);
        g.fillRect(0, 0, 800, 600); // First, fill the entire image with white.

        // As an example, draw some simple shapes, including some text
        // To get a different picture, just ERASE THIS CODE, and substitute your own. 
        
        g.setFill(Color.CYAN);
        g.fillRect(10,10,150,100);  // Fill the inside of a rectangle with cyan color.

        g.setStroke(Color.BLUE);
        g.setLineWidth(5);
        g.strokeRect(10,10,150,100); // Draw 5-pixel-wide outline of rectangle in blue.
        
        g.setStroke(Color.GREEN);
        g.strokeOval(200,200,200,200);  // Outline of a circle of diameter 200.
        g.strokeLine(200,300,400,300);  // Draws a diameter of the circle.
        
        g.setLineWidth(10);
        g.setFill( Color.color(0.0, 0.3, 0.5) ); // color with no red, 0.3 green, 0.5 blue
        g.fillOval(500, 50, 200, 500);  // A large dark blue-green oval
        g.setFill( Color.color(Math.random(), Math.random(), Math.random()) );  // A random color!
        g.fillOval(550, 100, 100, 400);
        
        g.setFont( Font.font(60) ); // Text will be in a size-60 font (about 60 pixels tall).
        g.setFill(Color.RED);
        g.fillText("Hello World!",30,570); // Draws text with lower left corner at (30,570).

    } // end drawPicture()
    

    //------ Implementation details: DO NOT EXPECT TO UNDERSTAND THIS ------

    public void start(Stage stage) {
        int width = 800;   // The width of the image.  You can modify this value!
        int height = 600;  // The height of the image. You can modify this value!
        Canvas canvas = new Canvas(width,height);
        drawPicture(canvas.getGraphicsContext2D());
        BorderPane root = new BorderPane(canvas);
        root.setStyle("-fx-border-width: 4px; -fx-border-color: #444");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("My First Graphics"); // STRING APPEARS IN WINDOW TITLEBAR!
        stage.show();
        stage.setResizable(false);
    } 

    public static void main(String[] args) {
        launch();
    }

} // end FirstGraphics
