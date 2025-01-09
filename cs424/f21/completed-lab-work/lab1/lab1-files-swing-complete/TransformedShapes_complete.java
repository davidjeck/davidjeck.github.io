import java.awt.*;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;


public class TransformedShapes_complete extends JPanel {

	// For managing a stack of transformation, making it possible to
	// save and restore the current transformation state.
	private ArrayList<AffineTransform> transformStack = new ArrayList<>();


	//------- For drawing ONLY while paintComponent is being executed! --------

	private Graphics2D g; // A copy of the graphics context from paintComponent.

	/**
	 * Saves a copy of the current transformation on the stack.
	 */
	private void pushTransform() {
		transformStack.add(g.getTransform());
	}

	/**
	 * Pops a transformation from the stack, and replaces the current transformation
	 * with the popped transformation.
	 */
	private void popTransform() {
		g.setTransform(transformStack.remove(transformStack.size() - 1));
	}

	/**
	 * Draws a filled circle of radius 50 (diameter 100) centered at (0,0), 
	 * subject to whatever transform(s) have been applied to g2.
	 */
	private void circle() {
		g.fillOval(-50,-50,100,100);
	}

	/**
	 * Draws a filled square with side 100 centered at (0,0), subject
	 * to whatever transform(s) have been applied to g2.
	 */
	private void square() {
		g.fillRect(-50,-50,100,100);
	}

	/**
	 * Draws a filled triangle with vertices at (-50,50), (50,50), and 
	 * (0,-50), subject to whatever transform(s) have been applied to g2.
	 */
	private void triangle() {
		g.fillPolygon(new int[] {-50,50,0}, new int[] {50,50,-50}, 3);
	}
	
	//-----------------------------------------------------------------------------------
	
	/**
	 * This method is called just once, when the program starts, to draw
	 * the content of the window.
	 */
	private void draw(Graphics2D g) {

        g.setColor(Color.WHITE);
        g.fillRect(0,0,600,600);
        g.setColor(Color.BLACK);
        
        pushTransform();
        g.translate(150,150);
        g.scale(2,2);
        circle();
        g.scale(0.5,0.5);
        g.setColor(Color.YELLOW);
        square();
        popTransform();
        
        pushTransform();
        g.translate(450,150);
        g.scale(2,2);
        g.setColor(Color.GREEN);
        square();
        g.scale(0.5,0.5);
        g.translate(0,50);
        g.scale(2,1);
        g.setColor(Color.WHITE);
        triangle();
        popTransform();
        
        pushTransform();
        g.translate(150,450);
        g.scale(0.75,0.75);
        g.setColor(Color.BLUE);
        g.scale(2,1);
        square();
        g.scale(0.5,1);
        pushTransform();
        g.translate(0,100);
        triangle();
        popTransform();
        g.translate(0,-100);
        g.scale(1,-1);
        triangle();
        popTransform();
        
        pushTransform();
        g.setColor(Color.RED);
        g.translate(450,450);
        pushTransform();
        g.translate(0,-100);
        g.scale(2,0.1);
        square();
        popTransform();
        pushTransform();
        g.translate(0,100);
        g.scale(2,0.1);
        square();
        popTransform();
        g.rotate(-Math.PI/4);
        g.scale(2*Math.sqrt(2),0.1);
        square();
        popTransform();
		
	} // end draw();
	
	

	//-----------------------------------------------------------------------------------


	protected void paintComponent(Graphics g1) {
		super.paintComponent(g1);
		g = (Graphics2D)g1.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		draw(g);
	} // end paintComponent()


	//--------------------------------------------------------------------------------------

	public TransformedShapes_complete() {
		setPreferredSize(new Dimension(600,600) );
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createLineBorder(Color.BLACK,4));
	}

	public static void main(String[] args)  {
		JFrame window = new JFrame("Drawing With Transforms");
		window.setContentPane(new TransformedShapes_complete());
		window.pack();
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation( (screen.width - window.getWidth())/2, (screen.height - window.getHeight())/2 );
		window.setVisible(true);
	}

}
