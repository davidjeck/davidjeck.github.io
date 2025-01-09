import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Transforms2D_FX extends Application {

	private Image pic;  // The image of the space shuttle.
	private ComboBox<String> transformChoice;  // The popup menu

	/**
	 * This method is called when the program starts and when the user 
	 * changes the setting of the popup menu.
	 */
	private void draw(GraphicsContext g) {
		g.save(); // Save current graphics state.
		g.setFill(Color.YELLOW);
		g.fillRect(0,0,600,600);
		g.translate(300,300);
		int whichTransform = transformChoice.getSelectionModel().getSelectedIndex();
		// whichTransform is the number of the selected item in the popup menu, from 0 to 0.

		// TODO Apply transforms here, depending on the value of whichTransform, from 1 to 9!



		// Do NOT change the following line.  This will draw the image, subject
		// to whatever transformations have been applied.
		g.drawImage(pic,-200,-150);   // Draw image with center at (0,0).
		g.restore(); // Restore graphics state to initial value.
	}

	public void start(Stage stage) throws Exception {
		pic = new Image("shuttle.jpg");
		Canvas canvas = new Canvas(600, 600);
		transformChoice = new ComboBox<>();
		transformChoice.getItems().add("None");
		for (int i = 1; i < 10; i++) {
			transformChoice.getItems().add("Number " + i);
		}
		transformChoice.getSelectionModel().select(0);
		BorderPane content = new BorderPane(canvas);
		HBox top = new HBox(10, new Label("Transform: "), transformChoice);
		top.setAlignment(Pos.CENTER);
		top.setStyle("-fx-border-color: black; -fx-padding: 5px");
		content.setTop(top);
		Scene scene = new Scene(content);
		stage.setScene(scene);
		stage.setTitle("Lab 1: Transforms2D");
		draw(canvas.getGraphicsContext2D());
		transformChoice.setOnAction( e -> draw(canvas.getGraphicsContext2D()) );
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}

}
