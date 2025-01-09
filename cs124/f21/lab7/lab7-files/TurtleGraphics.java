import java.awt.Color;


/**
 * A program that draws in a window using turtle graphics subroutines.
 * This program requires Turtle.java, which creates the window and provides
 * static subroutines for controlling the turtle.
 */
public class TurtleGraphics {


	/**
	 * The main routine for this program uses a turtle to draw some
	 * things in the turtle window on the screen.  The program ends when
	 * the user closes the window.
	 */
	public static void main(String[] args) {


		//------------- Exercise 1: First Turtle Drawing ---------------



		Turtle.wait(1000);
		Turtle.reset();		

		//------------- Exercise 2: Using a Subroutine -----------------



		Turtle.wait(1000);
		Turtle.reset();

		//------------- Exercise 3: Writing a Subroutine ---------------

		// burst();

		Turtle.wait(1000);
		Turtle.reset();

		//------------- Exercise 4: Subroutine with Parameters ---------

		// star(5);

		Turtle.wait(1000);
		Turtle.reset();

		//------------- Exercise 5: Subroutine Calling Subroutine ------

		// spiral( 3, 0.04 );

		Turtle.wait(1000);
		Turtle.reset();

		//------------- Exercise 6: Random Walk ------------------------

		// finish with a call to a "random walk" subroutine.

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



} // end class TurtleGraphics
