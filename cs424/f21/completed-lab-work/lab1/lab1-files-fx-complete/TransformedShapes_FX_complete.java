import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TransformedShapes_FX_complete extends Application {
	
	private GraphicsContext g;
	
    //***************** Functions for drawing the three basic shapes *****************

	/**
	 * Draws a filled circle of radius 50 (diameter 100) centered at (0,0), 
	 * subject to whatever transform(s) have been applied to g.
	 */
	private void circle() {
		g.fillOval(-50, -50, 100, 100);
	}

	/**
	 * Draws a filled square with side 100 centered at (0,0), subject
	 * to whatever transform(s) have been applied to g.
	 */
	private void square() {
		g.fillRect(-50,-50,100,100);
	}

	/**
	 * Draws a filled triangle with vertices at (-50,50), (50,50), and 
	 * (0,-50), subject to whatever transform(s) have been applied to g.
	 */
	private void triangle() {
		g.beginPath();
        g.moveTo(-50,50);
        g.lineTo(50,50);
        g.lineTo(0,-50);
        g.closePath();
        g.fill();
	}
	
	//*************************************************************************
	
	/**
	 *  This function will be called once, to draw the content of the canvas.
	 */
	private void draw() {
		g.setFill(Color.WHITE);
		g.fillRect(0, 0, 600, 600);
        g.setFill(Color.BLACK);
        
        g.save();
        g.translate(150,150);
        g.scale(2,2);
        circle();
        g.scale(0.5,0.5);
        g.setFill(Color.YELLOW);
        square();
        g.restore();
        
        g.save();
        g.translate(450,150);
        g.scale(2,2);
        g.setFill(Color.rgb(0,255,0));
        square();
        g.scale(0.5,0.5);
        g.translate(0,50);
        g.scale(2,1);
        g.setFill(Color.WHITE);
        triangle();
        g.restore();
        
        g.save();
        g.translate(150,450);
        g.scale(0.75,0.75);
        g.setFill(Color.BLUE);
        g.scale(2,1);
        square();
        g.scale(0.5,1);
        g.save();
        g.translate(0,100);
        triangle();
        g.restore();
        g.translate(0,-100);
        g.scale(1,-1);
        triangle();
        g.restore();
        
        g.save();
        g.setFill(Color.RED);
        g.translate(450,450);
        g.save();
        g.translate(0,-100);
        g.scale(2,0.1);
        square();
        g.restore();
        g.save();
        g.translate(0,100);
        g.scale(2,0.1);
        square();
        g.restore();
        g.rotate(-45);
        g.scale(2*Math.sqrt(2),0.1);
        square();
        g.restore();


        //-------------------------------------------------------------------------

	} // end of draw();
	
	
	public void start(Stage stage) {
		Canvas canvas = new Canvas(600, 600);
		g = canvas.getGraphicsContext2D();
		StackPane content = new StackPane(canvas);
		content.setStyle("-fx-border-color: black; -fx-border-width: 3px");
		Scene scene = new Scene(content);
		stage.setScene(scene);
		stage.setTitle("Lab 1: TransformedShapes");
		draw();
		stage.show();
	}
	
	public static void main(String[] args) {
		launch();
	}
}
