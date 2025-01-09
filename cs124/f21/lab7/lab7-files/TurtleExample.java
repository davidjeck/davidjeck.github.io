import java.awt.*;


/**
 * A program that draws in a window using turtle graphics subroutines.
 */
public class TurtleExample {


	/**
	 * The main routine for this program makes a turtle and uses it to draw
	 * some stuff in the turtle window on the screen.  The program ends when
	 * the user closes the window.
	 */
	public static void main(String[] args) {

		int i;  // A counter variable for for loops.

		//----------- Example 1:  A triangle ---------------

		Turtle.back(4);  // back up 4 units to put the triangle at the center of the base
		Turtle.forward(8);  // draw first side (using default one-pixel-wide red line)
		Turtle.turn(120);   // rotate 120 degrees
		Turtle.color(Color.BLUE);  // the next side will be blue.
		Turtle.forward(8);  // draw the second side
		Turtle.turn(120);
		Turtle.color(Color.GREEN);  // the last side will be green.
		Turtle.forward(8);  // draw the third side
		Turtle.turn(120);
		Turtle.setTurtleIsVisible(false); // hide turtle so you can admire the triangle


		Turtle.wait(2000);  // pause for two seconds
		Turtle.reset(); // return to initial state


		//------------ Example 2: Many squares ------------------

		Turtle.setDelay(20);  // Use a shorter delay so it draws faster.
		Turtle.color(Color.BLACK);
		for (i = 0; i < 60; i++) {
			square(7);  // Draw a square.  This is NOT a built in turtle command;
			// It's a subroutine defined later in this file!
			Turtle.turn(6);  // Turn 6 degrees before drawing the next square.
		}

		Turtle.wait(1000);
		Turtle.reset();


		//------------- Example 3: Many lines ---------------

		Turtle.setDelay(100);  // Restore delay to default, 100 milliseconds.
		Turtle.lineWidth(10);
		Turtle.face(0);  // Make sure turtle is facing to the right.
		for (i = -9; i <= 9; i++) {
			// Draw a wide, randomly colored line from (-9,i) to (9,i).
			Turtle.randomColor();
			Turtle.penUp();  // Turtle won't draw a line as it moves.
			Turtle.moveTo(-9, i);
			Turtle.penDown();  // Resume drawing.
			Turtle.forward(18);
		}

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

} // end class TurtleExample