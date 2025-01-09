
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * An object of type GraphicalCard is a PokerCard, but including
 * an image of the card along with the ability to draw the image
 * at a specified position in a GraphicsContext.
 */
public class GraphicalCard extends PokerCard {
	
	private Image image;  // The image for this card.
	
	/**
	 * Create a card with a given value and suit.  The parameters
	 * are the same as for the constructor in class PokerCard.
	 */
	public GraphicalCard(int value, int suit) {
		super(value,suit);
		char s = getSuitAsString().charAt(0);
		String v = getValueAsString();
		if (v.length() > 2)
			v = v.substring(0,1);
		image = new Image("cards/" + s + v + ".jpg");
	}
	
	/**
	 * Draw the card image with its upper left corner at (x,y).
	 */
	public void draw(GraphicsContext g, double x, double y) {
		g.drawImage(image, x, y);
	}

}
