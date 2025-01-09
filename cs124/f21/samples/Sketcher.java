import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.stage.Stage;


/**
 * A program written as a GUI tutorial that allows the user to draw curves and
 * stamp text onto a canvas.
 */
public class Sketcher extends Application {
		
	private Canvas canvas;     // The canvas where the user will draw.
	private GraphicsContext g; // The graphics context for drawing on the canvas.
	private double prevX, prevY;  // Previous mouse position for "curve" tool.
	private double startX, startY;  // Starting mouse position for "burst".
	private TextField textField;  // Input for the "text stamper" tool.
	private String currentTool = "curve";  // What mouse tool is being used
	private Color textColor = Color.BLACK; // Color for text stamper tool.
	
	/**
	 * Create a menu bar to be added to the top of the program window.
	 */
	private MenuBar makeMenus() {
		MenuBar menubar;
		Menu menu;
		MenuItem item;
		
		menubar = new MenuBar();  // Create the menu bar.
		
		menu = new Menu("Tool");
		menubar.getMenus().add(menu);
		
		item = new MenuItem("Draw Curve");
		item.setOnAction( evt -> currentTool = "curve" );
		menu.getItems().add(item);
		item = new MenuItem("Draw Burst");
		item.setOnAction( evt -> currentTool = "burst" );
		menu.getItems().add(item);
		item = new MenuItem("Stamp Text");
		item.setOnAction( evt -> currentTool = "text" );
		menu.getItems().add(item);
		
		menu = new Menu("CurveColor");     // Create the color menu
		menubar.getMenus().add(menu); //  ... and add it to the menu bar.
		
		item = new MenuItem("Black");
		item.setOnAction( evt -> g.setStroke(Color.BLACK) );
		menu.getItems().add(item);
		item = new MenuItem("Red");
		item.setOnAction( evt -> g.setStroke(Color.RED) );
		menu.getItems().add(item);
		item = new MenuItem("Green");
		item.setOnAction( evt -> g.setStroke(Color.GREEN) );
		menu.getItems().add(item);
		item = new MenuItem("Cyan");
		item.setOnAction( evt -> g.setStroke(Color.CYAN) );
		menu.getItems().add(item);
		item = new MenuItem("Purple");
		item.setOnAction( evt -> g.setStroke(Color.PURPLE) );
		menu.getItems().add(item);
		item = new MenuItem("Orange");
		item.setOnAction( evt -> g.setStroke(Color.ORANGE) );
		menu.getItems().add(item);
		
		menu = new Menu("LineWidth");
		menubar.getMenus().add(menu);
		
		item = new MenuItem("1");
		item.setOnAction( evt -> g.setLineWidth(1) );
		menu.getItems().add(item);
		item = new MenuItem("2");
		item.setOnAction( evt -> g.setLineWidth(2) );
		menu.getItems().add(item);
		item = new MenuItem("3");
		item.setOnAction( evt -> g.setLineWidth(3) );
		menu.getItems().add(item);
		item = new MenuItem("5");
		item.setOnAction( evt -> g.setLineWidth(5) );
		menu.getItems().add(item);
		item = new MenuItem("10");
		item.setOnAction( evt -> g.setLineWidth(10) );
		menu.getItems().add(item);
		item = new MenuItem("15");
		item.setOnAction( evt -> g.setLineWidth(15) );
		menu.getItems().add(item);
		item = new MenuItem("20");
		item.setOnAction( evt -> g.setLineWidth(20) );
		menu.getItems().add(item);
		item = new MenuItem("30");
		item.setOnAction( evt -> g.setLineWidth(30) );
		menu.getItems().add(item);
			
		menu = new Menu("TextColor");     // Create the color menu
		menubar.getMenus().add(menu); //  ... and add it to the menu bar.
		
		item = new MenuItem("Black");
		item.setOnAction( evt -> textColor = Color.BLACK );
		menu.getItems().add(item);
		item = new MenuItem("Red");
		item.setOnAction( evt -> textColor = Color.RED );
		menu.getItems().add(item);
		item = new MenuItem("Green");
		item.setOnAction( evt -> textColor = Color.GREEN );
		menu.getItems().add(item);
		item = new MenuItem("Cyan");
		item.setOnAction( evt -> textColor = Color.CYAN );
		menu.getItems().add(item);
		item = new MenuItem("Purple");
		item.setOnAction( evt -> textColor = Color.PURPLE );
		menu.getItems().add(item);
		item = new MenuItem("Orange");
		item.setOnAction( evt -> textColor = Color.ORANGE );
		menu.getItems().add(item);
		
		menu = new Menu("TextSize");
		menubar.getMenus().add(menu);
		
		item = new MenuItem("12 point");
		item.setOnAction( evt -> g.setFont(Font.font(12)));
		menu.getItems().add(item);
		item = new MenuItem("14 point");
		item.setOnAction( evt -> g.setFont(Font.font(14)));
		menu.getItems().add(item);
		item = new MenuItem("18 point");
		item.setOnAction( evt -> g.setFont(Font.font(18)));
		menu.getItems().add(item);
		item = new MenuItem("24 point");
		item.setOnAction( evt -> g.setFont(Font.font(24)));
		menu.getItems().add(item);
		item = new MenuItem("36 point");
		item.setOnAction( evt -> g.setFont(Font.font(36)));
		menu.getItems().add(item);
		
		return menubar;
	}
	
	/**
	 * Make an HBox for the bottom of the window, containing a "Clear" button
	 * and a text input box to be used for the Text Stamper tool.
	 */
	private HBox makeBottom() {
		
		Button clearButton = new Button("Clear");
		clearButton.setOnAction( evt -> { 
			g.setFill(Color.WHITE); 
			g.fillRect(0,0,800,600); 
		});
		
		Label label = new Label("Text for Stamper:");
		
		textField = new TextField("Hello World");  // a text input box containing "Hello World"
		textField.setPrefColumnCount(30); // make it big enough to contain 30 chars
		
		HBox container = new HBox(15, clearButton, label, textField);
		container.setAlignment(Pos.CENTER); // center contents in the HBox
		container.setStyle( // CSS styling for the HBox
		    "-fx-padding: 5px; -fx-border-color: black; -fx-background-color: lightgray" );
		return container;
	}
	
	/**
	 * Respond to a mouse pressed event at (x,y) on the canvas.
	 * For the Sketcher Tool, just save values for prevX and prevY.
	 * For the Text Stamper Tool, place a copy of the text from the
	 * text input box on the canvas at the mouse position.
	 */
	private void doMouseDown(double x, double y) { 
		if (currentTool.equals("curve")) {
			prevX = x;
			prevY = y;
		}
		else if (currentTool.equals("text")) {
			g.setFill(textColor);
			g.fillText( textField.getText(), x, y );
		}
		else if (currentTool.equals("burst")) {
			startX = x;
			startY = y;
		}
	}
	
	/**
	 * Respond to a mouse dragged event at (x,y) on the canvas.
	 */
	private void doMouseDragged(double x, double y) {
		if (currentTool.equals("curve")) {
			g.strokeLine(prevX, prevY, x, y);
			prevX = x;
			prevY = y;
		}
		else if (currentTool.equals("burst")) {
			g.strokeLine(startX, startY, x, y);
		}
	}

	/**
	 * Create and set up the program window and event handling.
	 */
	public void start(Stage stage) {
		canvas = new Canvas(800,600);
		g = canvas.getGraphicsContext2D();
		g.setFill(Color.WHITE);
		g.fillRect(0, 0, 800, 600);
		g.setLineCap( StrokeLineCap.ROUND );
		BorderPane content = new BorderPane();
		content.setCenter(canvas);
		content.setTop( makeMenus() );
		content.setBottom( makeBottom() );
		Scene scene = new Scene(content);
		stage.setScene(scene);
		stage.setTitle("Sketcher: Draw on a Canvas");
		stage.setResizable(false); // User can't change window size.
		stage.show();
		canvas.setOnMousePressed( evt -> doMouseDown(evt.getX(),evt.getY()) );
		canvas.setOnMouseDragged( evt -> doMouseDragged(evt.getX(), evt.getY()) );
	}
	
	public static void main(String[] args) {
		launch(); // Run the Application; this will not return.
	}

}
