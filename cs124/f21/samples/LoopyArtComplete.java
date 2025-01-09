
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
public class LoopyArtComplete extends Application {

    /**
     * Draws one work of art, with the type selected at random.
     */
    public void drawPicture(GraphicsContext g) {

        int artType; // For randomly selecting one of five types of art.
        artType = (int)(6*Math.random() + 1);
        System.out.println("Draw art type number " + artType);

        int x,y,x1,y1,x2,y2;  // xy-coordinates
        int i; // loop control variable

        if (artType == 1) {  // Vaguely Jackson Pollock
            g.setFill( Color.gray(Math.random()) );
            g.fillRect(0,0,800,600);
            g.setLineWidth(2);
            for (i = 0; i < 500; i++) {
                x1 = (int)(800*Math.random());
                y1 = (int)(600*Math.random());
                x2 = (int)(800*Math.random());
                y2 = (int)(600*Math.random());
                g.setStroke( Color.color(Math.random(), Math.random(), Math.random()) );
                g.strokeLine(x1, y1, x2, y2);
            }
        }
        else if (artType == 2) { // Vaguely Wassily Kandinsky 
            g.setFill(Color.BLACK);
            g.fillRect(0,0,800,600);
            g.setLineWidth(2);
            g.setStroke(Color.BLACK);
            for (x = 0; x < 800; x += 100) {
                for (y = 0; y < 600; y += 100) {
                    g.setFill( Color.hsb(360*Math.random(), 0.6, 1));
                    g.fillRect(x+1,y+1,98,98);
                    g.setFill( Color.color(Math.random(), Math.random(), Math.random()));
                    g.fillOval( x+20,y+20,60,60);
                    g.strokeOval( x+20, y+20, 60, 60);
                }
            }
        }
        else if (artType == 5) {  // Horizon at sunset (with moon in wrong position!)
            double red = 0;
            for (i = 0; i < 600; i++) {  // draw a gradient background
                g.setFill( Color.color( red, 0, 0.5 ) );
                g.fillRect(0,i,800,1);
                red = red + 0.0007;
            }
            g.setFill( Color.WHITE );
            for ( i = 0; i < 100; i ++) {
                g.fillOval( 796*Math.random(), 596*Math.random(), 4, 4 );
            }
            g.setFill( Color.color(0.7,0.7,0) );
            g.fillOval( 100+500*Math.random(), 50 + 200*Math.random(), 200, 200 ); // draw a full moon
        }
        else if (artType == 4) { // Radial lines
            g.setFill( Color.gray(Math.random()) );
            g.fillRect(0,0,800,600);
            g.setLineWidth(3);
            for (i = 0; i < 500; i++) {
                x = (int)(800*Math.random());
                y = (int)(600*Math.random());
                g.setStroke( Color.color(Math.random(), Math.random(), Math.random()) );
                g.strokeLine(400,300,x,y);
            }
        }
        else if (artType == 5) { // Vaguely Piet Mondrian
            int count;  // number of horizontal and vertical bars
            g.setFill( Color.hsb(360*Math.random(), 0.1, 1) );
            g.fillRect(0,0,800,600);
            count = 5 + (int)(10*Math.random());
            g.setStroke(Color.BLACK);
            g.setLineWidth(1);
            for (i = 0; i < count; i++) {
                double hue;
                int width;
                width = 5 + (int)(10*Math.random());
                g.setFill(Color.color(Math.random(),Math.random(),Math.random()));
                if (i % 2 == 1) {
                    y = 5 + (int)(580*Math.random());
                    g.fillRect(0, y, 800, width);
                    g.strokeLine(0, y, 800, y);
                    g.strokeLine(0, y+width, 800, y+width);
                }
                else {
                    x = 5 + (int)(780*Math.random());
                    g.fillRect(x, 0, width, 600);
                    g.strokeLine(x, 0, x, 600);
                    g.strokeLine(x+width, 0, x+width, 600);
                }
            }
        }
        else { // artType is 6: random translucent shapes
            g.setFill(Color.WHITE);
            g.fillRect(0, 0, 800, 600);
            g.setLineWidth(1);
            g.setStroke(Color.BLACK);
            for (i = 0; i < 1000; i++) {
                x = (int)(800*Math.random() - 50);
                y = (int)(600*Math.random() - 50);
                g.setFill( Color.hsb(360*Math.random(), 1, 1, 0.3) );
                if (Math.random() < 0.5) {
                    g.fillRect(x, y, 100, 100);
                    g.strokeRect(x, y, 100, 100);
                }
                else {
                    g.fillOval(x, y, 100, 100);
                    g.strokeOval(x, y, 100, 100);
                }
            }
        }
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
        stage.setTitle("Random Art"); // STRING APPEARS IN WINDOW TITLEBAR!
        stage.show();
        stage.setResizable(false);
        canvas.setOnMousePressed( e -> drawPicture(canvas.getGraphicsContext2D()) );
    } 

    public static void main(String[] args) {
        launch();
    }

} // end of Art
