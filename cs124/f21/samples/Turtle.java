import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * This class opens a window with a drawing area in which a virtual "turtle" 
 * moves around, possibly leaving a trail as it goes.  The turtle has a pen
 * that can be raised and lowered; when the pen is down, it leaves a
 * trail.  There are commands for moving the turtle and for telling
 * it which direction to face.  The turtle is shown as a translucent gray
 * triangle pointing in the direction that the turtle is facing (but the 
 * display of the turtle can be turned off).  Ordinarily, any change to the
 * picture produces an automatic repaint of the window (but this automatic
 * repainting can be turned off).  The color and width of the trail left
 * by the turtle can be changed.  For the purposes of the turtle, the
 * coordinate system on the drawing area extends from -10 at the left
 * to 10 at the right and from -10 at the bottom to 10 at the top.  By
 * default, there is a short (50 millisecond) delay between actions of the
 * turtle, but this delay can be changed by calling the setDelay() method.
 * It is assumed that the size of the panel does not change after it is
 * created.
 *    All of the methods for controlling the turtle are static methods
 * in this class.  There is no method for creating the window.  That is
 * done automatically.  If the user closes the window, the program that
 * is using it exits.
 */
public class Turtle {

	/* Private state variables that record the state of the turtle. */

	private static double turtleX, turtleY;   // Location of the turtle.
	private static double facing;  // What direction the turtle is facing, given in degrees.
	private static boolean penIsUp;  // Tells whether the pen is currently raised.

	private static int delay = 50; // Milliseconds of delay inserted between turtle actions.
	private static boolean turtleIsVisible = true; // Tells whether turtle should be displayed.
	private static boolean autoRepaint = true; // Tells whether repaint should be called automatically.


	/* The public interface of the class -- methods for manipulating the turtle. */

	/**
	 * Move the turtle forward in the direction it is facing for a given distance.
	 * (Note: negative distance will make the turtle back up.)
	 */
	public static void forward( double distance ) {
		double rad = facing / 180 * Math.PI;
		double dx = Math.cos( rad ) * distance;
		double dy = Math.sin( rad ) * distance;
		go(turtleX + dx, turtleY + dy);
	}

	/**
	 * Move the turtle in the direction opposite from the direction it is facing 
	 * for a given distance.  This is the same as forward( -distance ).  (Note: negative 
	 * distance will make the turtle move forward.)
	 */
	public static void back( double distance ) {
		forward( -distance );
	}

	/**
	 * Move the turtle dx units horizontally and dy units vertically from its current
	 * position.  The turtle's direction is not taken into consideration and does
	 * not change.
	 */
	public static void moveBy( double dx, double dy ) {
		go(turtleX + dx, turtleY + dy);
	}

	/**
	 * Move the turtle to the point (x,y).  The turtle's direction is not taken into 
	 * consideration and does not change.
	 */
	public static void moveTo( double x, double y ) {
		go(x,y);
	}

	/**
	 * Turn the turtle through a given angle, specified in degrees.  The angle is just
	 * added to the angle that gives the turtle's direction.  For example, 
	 * turn(90) is a left turn, and turn(180) reverses the direction of the turtle.
	 */
	public static void turn( double angle ) {
		facing += angle;
		if (turtleIsVisible && autoRepaint) {
			turtlePanel.repaint();
			doDelay(delay);
		}
	}

	/**
	 * Turn the turtle so that it faces is in given direction.  The angle is given in
	 * degrees.  Zero degrees means facing right.  Positive angles are counterclockwise
	 * from that orientation.
	 */
	public static void face( double angle ) {
		facing = angle;
		if (turtleIsVisible && autoRepaint) {
			turtlePanel.repaint();
			doDelay(delay);
		}
	}


	/**
	 * Move the turtle to (0,0), and set its direction angle to 0 (facing right).
	 */
	public static void home() {
		go(0,0);
		facing = 0;
		if (turtleIsVisible && autoRepaint) {
			turtlePanel.repaint();
			doDelay(delay);
		}
	}

	/**
	 * Restores the turtle panel to its initial state:  Any drawing is cleared, the turtle
	 * is placed at (0,0) facing right, and the color is set to Color.RED, and the line
	 * width is set to 1.  The turtle is made visible, and autorepaint is set to true.
	 * The pen is lowered.  Finally, the default delay is set to 50 milliseconds.
	 */
	public static void reset() {
		canvasGraphics.setColor(Color.WHITE);
		canvasGraphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		canvasGraphics.setColor(Color.RED);
		((Graphics2D)canvasGraphics).setStroke(new BasicStroke(1));
		turtleX = turtleY = facing = 0;
		turtleIsVisible = autoRepaint = true;
		penIsUp = false;
		delay = 50;
		turtlePanel.repaint();
		doDelay(delay);
	}

	/**
	 * Raise the turtle's pen, so it doesn't leave a trail when it moves.
	 */
	public static void penUp() {
		penIsUp = true;
	}

	/**
	 * Lower the turtle's pen, so it will leave a trail when it moves.
	 */
	public static void penDown() {
		penIsUp = false;
	}

	/**
	 * Set the color that will be used for the turtle's trail.
	 * @param c the color for the trail. A null value is treated as black.
	 */
	public static void color(Color c) {
		if (c == null)
			c = Color.BLACK;
		canvasGraphics.setColor(c);
	}

	/**
	 * Set the color that will be used for the turtle's trail.
	 * The color is given by RGB color values r, g, and b.  The
	 * parameters should be in the range 0 .0to 1.0; values outside
	 * the range will be clamped to lie in the legal range.
	 */
	public static void color(double r, double g, double b) {
		r = Math.min(1,Math.max(r,0));
		g = Math.min(1,Math.max(g,0));
		b = Math.min(1,Math.max(b,0));
		color(new Color( (int)(255*r), (int)(255*g), (int)(255*g) ));
	}

	/**
	 * Set the turtle's color to a random color, with randomly
	 * selected value for the red, blue, and green color components.
	 */
	public static void randomColor() {
		int r,g,b;
		r = (int)(256*Math.random());
		g = (int)(256*Math.random());
		b = (int)(256*Math.random());
		color(new Color(r,g,b));
	}

	/**
	 * Set the turtle's color to a random HSB color.  An HSB color is created with
	 * random hue and with brightness and saturation set to 1.  This is a
	 * random "spectral" color.
	 */
	public static void randomHSB() {
		float hue = (float)Math.random();
		Color c = Color.getHSBColor(hue, 1, 1);
		color(c);
	}

	/**
	 * Set the width of the line, in pixels, that will be used for drawing
	 * the trail left by the turtle as it moves.
	 * @param width The width of the line, in pixels.  Non-integer values are
	 * allowed.  If this parameter is less than 1, a line width of 1 is used.
	 */
	public static void lineWidth(double width) {
		if (width < 1)
			width = 1;
		if (width < 2)
			((Graphics2D)canvasGraphics).setStroke(new BasicStroke((float)width));
		else
			((Graphics2D)canvasGraphics).setStroke(new BasicStroke((float)width,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));			
	}

	/**
	 * Erase the picture by filling it with white.  The turtle does not move
	 * or change direction.
	 */
	public static void clear(){
		Color c = canvasGraphics.getColor();
		canvasGraphics.setColor(Color.WHITE);
		canvasGraphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		canvasGraphics.setColor(c);
		if (autoRepaint) {
			turtlePanel.repaint();
			doDelay(delay);
		}
	}

	/**
	 * Determines whether the turtle should be displayed.  The default is true.
	 * If the value is set to false, the turtle is not shown.
	 */
	public static void setTurtleIsVisible(boolean visible) {
		if (visible != turtleIsVisible) {
			turtleIsVisible = visible;
			if (autoRepaint) {
				turtlePanel.repaint();
				doDelay(delay);
			}
		}
	}

	/**
	 * Set the delay between actions of the turtle to the specified number of
	 * milliseconds.  If the value is less than or equal to zero, there is
	 * no delay.  If the delay is very short or zero, multiple turtle actions
	 * can take place between repaint of the drawing area.
	 */
	public static void setDelay(int milliseconds) {
		delay = Math.max(0,milliseconds);
	}

	/**
	 * Causes the program to pause for a specified number of milliseconds.  This is
	 * an extra pause, in addition to the default pause between turtle motions.
	 * Calling this method does not change the length of the default delay; to do
	 * that, use setDelay().
	 */
	public static void wait(int millisecondsDelay) {
		doDelay(millisecondsDelay);
	}

	/**
	 * Determines whether repaint should be called automatically whenever the picture
	 * changes.  The default is true.  If the value is set to false, repaint() is not
	 * called automatically, and you are responsible for calling this panel's repaint()
	 * method when you want the picture on the screen to be updated.  Turning off
	 * autorepaint can speed up long redraw operations and will prevent the user from
	 * seeing partially drawn pictures in the middle of the operation. Note that
	 * turning off autorepeat also eliminates any delay between turtle actions, no
	 * matter what the setting of the delay property.
	 */
	public static void setAutoRepaint(boolean auto) {
		autoRepaint = auto;
	}

	/**
	 *  Repaints the window, making any drawing that has been done by the turtle visible.
	 *  By default, repainting is done automatically, but when autorepaint is turned off
	 *  (by the setAutoRepaint() method, this method can be called this method can be
	 *  called to force repainting.
	 */
	public static void repaint() {
		turtlePanel.repaint();
	}

	/**
	 * Returns the current x-coordinate of the turtle.
	 */
	public static double getTurtleX() {
		return turtleX;
	}


	/**
	 * Returns the current y-coordinate of the turtle.
	 */
	public static double getTurtleY() {
		return turtleY;
	}


	/**
	 * Returns the direction in which the turtle is facing, as an angle measured in
	 * degrees from the positive x-axis.  The value, x, that is returned is greater
	 * than 180 and less than or equal to -180.
	 */
	public static double getHeading() {
		double heading = facing - 360*Math.floor(facing/360);
		if (heading > 180)
			heading -= 360;
		return heading;
	}


	/* The private, implementation part of the class. */

	private static BufferedImage canvas;     // The offscreen, official copy of the picture, without the turtle.
	private static Graphics canvasGraphics;  // A graphics context for drawing on the canvas.

	private static TurtlePanel turtlePanel;  // The panel where the drawing is displayed.

	/**
	 * Private method used internally to move the turtle from its current position
	 * to (x,y).  If the pen is down, a line is drawn between the two points.
	 */
	private static void go(double x, double y) {
		if ( ! penIsUp ) {
			synchronized(turtlePanel) {
				double x1 = (0.5 + turtleX / 20.0)*canvas.getWidth();
				double y1 = (0.5 - turtleY / 20.0)*canvas.getHeight();
				double x2 = (0.5 + x / 20.0)*canvas.getWidth();
				double y2 = (0.5 - y / 20.0)*canvas.getHeight();
				canvasGraphics.drawLine( (int)x1, (int)y1, (int)x2, (int)y2 );
			}
		}
		turtleX = x;
		turtleY = y;
		//System.out.printf("%1.3f  %1.3f  %1.3f%n", getTurtleX(), getTurtleY(), getHeading());
		if (autoRepaint) {
			turtlePanel.repaint();
			doDelay(delay);
		}
	}

	/**
	 * Insert a pause of a specified number of milliseconds into the execution of
	 * the program.  If the number of milliseconds is zero or less, there is no pause.
	 * This private method is used internally to implement delays.
	 */
	private static void doDelay(int millis) {
		if (millis > 0) {
			try {
				Thread.sleep(millis);
			}
			catch (InterruptedException e) {
			}
		}
	}

	@SuppressWarnings("serial")
	private static class TurtlePanel extends JPanel {

		/**
		 * Create a panel for the turtle to draw in.  It will be a square with a specified 
		 * size.  The turtle is placed at (0,0), in the center of the panel, facing right.
		 * Note that there can only ever be one TurtlePanel, since some of the data 
		 * that is uses is static.
		 */
		public TurtlePanel(int preferredSize) {
			setPreferredSize( new Dimension(preferredSize,preferredSize) );
			canvas = new BufferedImage(preferredSize,preferredSize,BufferedImage.TYPE_INT_ARGB);
			canvasGraphics = canvas.createGraphics();
			canvasGraphics.setColor(Color.WHITE);
			canvasGraphics.fillRect(0, 0, preferredSize, preferredSize);
			canvasGraphics.setColor(Color.RED);
			((Graphics2D)canvasGraphics).setRenderingHint(
					RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}

		/**
		 * Draw the picture by copying the off-screen canvas onto the panel.  If the turtle
		 * is visible, draw it on top of the picture from the canvas.
		 */
		synchronized protected void paintComponent(Graphics g) {
			g.drawImage(canvas,0,0,getWidth(),getHeight(),null);
			if (turtleIsVisible) { // Draw the turtle.
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(30,30,30,130)); // somewhat transparent dark gray
				Path2D path = new Path2D.Double();
				double rad = facing / 180 * Math.PI;
				double dx = Math.cos(rad);
				double dy = Math.sin(rad);
				path.moveTo( getWidth()*(0.5 + (turtleX + dx)/20),   getHeight()*(0.5 - (turtleY + dy)/20) );
				path.lineTo( getWidth()*(0.5 + (turtleX - dy/3)/20), getHeight()*(0.5 - (turtleY + dx/3)/20) );
				path.lineTo( getWidth()*(0.5 + (turtleX + dy/3)/20), getHeight()*(0.5 - (turtleY - dx/3)/20) );
				path.closePath();
				g2.fill(path);
			}
		}
	}

	static {  // static initializer -- opens the window and creates the turtlePanel.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int maxSize = Math.min(screenSize.width,screenSize.height) - 120;
		int size = Math.min(800,maxSize);
		turtlePanel = new TurtlePanel(size);
		JFrame window = new JFrame("Turtle Land");
		window.setContentPane(turtlePanel);
		window.pack();
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocation( (screenSize.width - window.getWidth())/2,
				(screenSize.height - window.getHeight())/2 );
		window.setVisible(true);
	}


}
