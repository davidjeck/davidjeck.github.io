import java.awt.Color;


/**
 * A program that draws in a window using turtle graphics subroutines.
 * This program requires Turtle.java, which creates the window and provides
 * static subroutines for controlling the turtle.
 */
public class TurtleGraphicsComplete {


	/**
	 * The main routine for this program uses a turtle to draw some
	 * things in the turtle window on the screen.  The program ends when
	 * the user closes the window.
	 */
	public static void main(String[] args) {


		//------------- Exercise 1: First Turtle Drawing ---------------

		Turtle.lineWidth(4);
		Turtle.color(Color.RED);
		Turtle.penUp();
		Turtle.moveTo(-7, -5);  // Draw red triangle with vertex at (-7,-5).
		Turtle.penDown();
		Turtle.forward(14);
		Turtle.turn(120);
		Turtle.forward(14);
		Turtle.turn(120);
		Turtle.forward(14);
		Turtle.turn(120);
		Turtle.penUp();
		Turtle.forward(7); // Move to center of one side of the red triangle.
		Turtle.penDown();
		Turtle.color(Color.BLUE);
		Turtle.turn(60);
		Turtle.forward(7); // Draw the blue triangle inside the red triangle
		Turtle.turn(120);
		Turtle.forward(7);
		Turtle.turn(120);
		Turtle.forward(7);
		
		Turtle.wait(1000);
		Turtle.reset();		

		//------------- Exercise 2: Using a Subroutine -----------------

		int i;
		for (i = 1; i <= 9; i++) {  // Draw a set of 8 nested squares.
			Turtle.penUp();
			Turtle.moveTo( -i, -i );
			Turtle.penDown();
			square(2*i);
		}

		Turtle.wait(1000);
		Turtle.reset();

		//------------- Exercise 3: Writing a Subroutine ---------------

		Turtle.setDelay(10);
		burst();

		Turtle.wait(1000);
		Turtle.reset();

		//------------- Exercise 4: Subroutine with Parameters ---------

		Turtle.lineWidth(3);
		star(5);
		Turtle.penUp();
		Turtle.moveTo(-8, 6);
		Turtle.penDown();
		Turtle.color(Color.BLUE);
		star(7);
		Turtle.penUp();
		Turtle.moveTo(-4, -7);
		Turtle.penDown();
		Turtle.color(Color.CYAN);
		star(3);
		Turtle.penUp();
		Turtle.moveTo(5, 7);
		Turtle.penDown();
		Turtle.color(Color.BLACK);
		star(4);

		Turtle.wait(1000);
		Turtle.reset();

		//------------- Exercise 5: Subroutine Calling Subroutine ------

		Turtle.setDelay(3);
		Turtle.setTurtleIsVisible(false);
		spiral(3,0.04);

		Turtle.wait(1000);
		Turtle.reset();

		//------------- Exercise 6: Random Walk ------------------------

		// finish with a call to a "random walk" subroutine.
		
		Turtle.setDelay(10);
		randomWalk(0.3,false);

	} // end of main()


	/**
	 * Draws a square with a specified size.  A corner of the square is at the
	 * current turtle position, and the first side of the square is oriented along
	 * the turtle's current direction.  After drawing the square, the turtle is
	 * back at its starting position and is facing the same direction as when it started.
	 */
	private static void square(double size) {
		int i;
		for (i = 0; i < 4; i++) {
			Turtle.forward(size);
			Turtle.turn(90);
		}
	}


	/**
	 * Draws 200 lines, randomly colored and of random length and facing random
	 * directions, radiating out of the center point of the window.
	 */
	private static void burst() {
		int i;
		double x;  // random length for the line
		Turtle.lineWidth(4);
		for (i = 0; i < 200; i++) {
			Turtle.face(360*Math.random());
			Turtle.randomHSB();
			x = 10*Math.random();
			Turtle.forward(x);
			Turtle.back(x);
		}
	}

	
	/**
	 * Draws a square with a specified size.  A corner of the square is at the
	 * current turtle position, and the first side of the square is oriented along
	 * the turtle's current direction.  After drawing the square, the turtle is
	 * back at its starting position and is facing the same direction as when it started.
	 */
	private static void star(double size) {
		int i;
		for (i = 0; i < 5; i++) {
			Turtle.forward(size);
			Turtle.turn(-144);
		}
	}


	/**
	 * Draws a "spiral" of squares.  The squares start small and increase in
	 * size until the size is greater than or equal to 8.  After drawing each
	 * square, the turtle turns angleIncrement degrees, and lengthIncrement
	 * is added to the length of the side of the square.
	 */
	private static void spiral(double angleIncrement, double lengthIncrement) {
		double length;  // length of side of next square to be drawn
		length = lengthIncrement;
		while (length < 8) {
			square(length);
			length += lengthIncrement;
			Turtle.turn(angleIncrement);
		}
	}
	
	
	/**
	 * The turtle move in a "random walk".  It moves forward by an amount equal to
	 * the length parameter, faces a random direction, and repeats this indefinitely.
	 * If the turtle moves outside the window, it jumps back to the center and chooses
	 * a new color at random.  There are two types of random walk: square and triangular.
	 * In a square random walk, the possible angles are 0, 90, 180, and 270.  For a
	 * triangular random walk, the possible angles are 0, 120, and 240.  The second
	 * parameter tells the subroutine which kind of random walk to draw.
	 */
	private static void randomWalk(double length, boolean triangular) {
		while (true) {
			if (triangular)
				Turtle.face(120 * (int)(3*Math.random()));
			else
				Turtle.face(90 * (int)(4*Math.random()));
			Turtle.forward(length);
			if (Math.abs(Turtle.getTurtleX()) > 11 || Math.abs(Turtle.getTurtleY()) > 11) {
				Turtle.penUp();
				Turtle.home();
				Turtle.penDown();
				Turtle.randomHSB();
			}
		}
	}

} // end class TurtleGraphics
