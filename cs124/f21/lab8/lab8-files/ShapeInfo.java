import javafx.scene.paint.Color;

/**
 * A ShapeInfo object is a simple container for the specification
 * of a colored shape with a center point, a size, and a color.
 * All instance variables of the object are public.
 */
public class ShapeInfo {
	
	/**
	 * X-coordinate of the the center point of the shape.
	 */
	public double x;
	
	/**
	 * Y-coordinate of the center point of the shape.
	 */
	public double y;
	
	/**
	 * Size of the shape, such as the diameter of a disk or
	 * the length of side of a square.
	 */
	public double size;
	
	/**
	 * The color of the shape.
	 */
	public Color color;

} // end class ShapeInfo
