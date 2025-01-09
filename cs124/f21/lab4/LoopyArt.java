
import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 *  This program draws several kinds of random "art".  A new artwork is
 *  displayed every time the user clicks the window.
 */
public class LoopyArt extends Application {

    /**
     * Draws one work of random art, with the type selected at random.
     */
    public void drawPicture(GraphicsContext g) {

        int artType; // For randomly selecting which type of art to draw.
        artType = (int)(4*Math.random() + 1);
        
        System.out.println("Draw art type number " + artType);
        
        /* Draw the art!! */

    } // end drawPicture()


    //------ Implementation details: DO NOT EXPECT TO UNDERSTAND THIS ------

    public void start(Stage stage) {
        int width = 800;   // The width of the image.  You can modify this value!
        int height = 600;  // The height of the image. You can modify this value!
        Canvas canvas = new Canvas(width,height);
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        drawPicture(graphics);
        BorderPane root = new BorderPane(canvas);
        root.setStyle("-fx-border-width: 4px; -fx-border-color: #444");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Loopy Random Art"); // STRING APPEARS IN WINDOW TITLEBAR!
        stage.show();
        stage.setResizable(false);
        canvas.setOnMousePressed( e -> drawPicture(graphics) );
    } 

    public static void main(String[] args) {
        launch();
    }

} // end of Art
