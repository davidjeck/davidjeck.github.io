import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.gl2.GLUT; 

/**
 * CPSC 424, Fall 2021, Lab 4:  Some objects in 3D.  The mouse can be used to rotate the 
 * object; press return or home to restore the original view.  The number keys 1 through 6
 * select the object that is displayed.
 */
public class First3D extends GLJPanel implements GLEventListener, KeyListener {

	/**
	 * A main routine to create and show a window that contains a
	 * panel of type First3DComplete.  The program ends when the user closes the 
	 * window.
	 */
	public static void main(String[] args) {
		JFrame window = new JFrame("Some Objects in 3D");
		First3D panel = new First3D();
		window.setContentPane(panel);
		window.pack();
		window.setResizable(false);
		window.setLocation(50,50);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}

	/**
	 * Constructor for class Lab4.
	 */
	public First3D() {
		super( new GLCapabilities(null) ); // Makes a panel with default OpenGL "capabilities".
		setPreferredSize( new Dimension(700,700) );
		addGLEventListener(this); // This panel will respond to OpenGL events.
		addKeyListener(this);
	}

	private Camera camera;  // For setting up the view and implementing rotation by mouse.

	//------------------- TODO: Complete this section! ---------------------

	private int objectNumber = 1;        // Which object to draw (1 ,2, 3, 4, 5, or 6)?
	                                     //   (Controlled by number keys.)

	private GLUT glut = new GLUT(); // An object for drawing GLUT shapes.

	/**
	 * Draws a cylinder with a sphere on each end, with its axis
	 * lying along the x-axis, centered at (0,0,0).
	 * @param gl2 The OpenGL graphics context.
	 */
	private void bar(GL2 gl2) {
	}

	/**
	 * Draws an object shaped line a square with sides made of
	 * cylinders and a sphere at each corner, lying in the
	 * xy-plane, centered at (0,0,0). 
	 * @param gl2 The OpenGL graphics context.
	 */
	private void square(GL2 gl2) {
	}

	/**
	 * Draws an object shaped line a cube with edges made of
	 * cylinders and a sphere at each corner, centered at (0,0,0).
	 * @param gl2 The OpenGL graphics context.
	 */
	private void cage(GL2 gl2) {
	}

	/**
	 * The method that draws the current object, selected by value of objectNumber.
	 * The value of objectNumber is 1, 2, 3, 4, 5, or 6.
	 * The parameter is the OpenGL context object, which contains OpenGL's state
	 * variables and constants.
	 */
	private void draw(GL2 gl2) {
	}
	

	//-------------------------------- Draw the Scene  --------------------------------------

	/**
	 * The method that draws the current object, with its modeling transformation.
	 * The parameter is the OpenGL context object, which contains OpenGL's state
	 * variables and constants.
	 */
	public void display(GLAutoDrawable drawable) {    

		GL2 gl2 = drawable.getGL().getGL2(); // The object that contains the OpenGL constants, state, and methods.

		gl2.glNormal3f(0,0,1); // (Make sure normal vector is correct for object 1.)

		gl2.glClearColor( 0, 0, 0, 1 ); // Background color (black).
		gl2.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );

		camera.apply(gl2); // Let the camera set up the view and projection.

		draw(gl2);  // Draw the content of the scene.

	} // end display()

	/** The init method is called once, before the window is opened, to initialize
	 *  OpenGL.  Here, it sets up a projection, turns on some lighting, and enables
	 *  the depth test.
	 */
	public void init(GLAutoDrawable drawable) {
		GL2 gl2 = drawable.getGL().getGL2();
		gl2.glEnable(GL2.GL_LIGHTING);  
		gl2.glEnable(GL2.GL_LIGHT0);
		gl2.glLightfv(GL2.GL_LIGHT0,GL2.GL_DIFFUSE,new float[] {0.7f,0.7f,0.7f},0);
		gl2.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, 1);
		gl2.glEnable(GL2.GL_COLOR_MATERIAL);
		gl2.glEnable(GL2.GL_DEPTH_TEST);
		gl2.glLineWidth(3);
		camera = new Camera();
		camera.setScale(10);
		camera.lookAt(0, 0, 20, 0, 0, 0, 0, 1, 0);
		camera.installTrackball(this);
	}

	public void dispose(GLAutoDrawable drawable) {
		// called when the panel is being disposed
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// called when user resizes the window
	}

	// ----------------  Methods from the KeyListener interface --------------

	/**
	 * Responds to keypressed events.  The Home or Return key restores the original
	 * view.  The number keys 1, 2, 3, 4, 5, and 6 select the current object number. 
	 * The panel is redrawn to reflect the change.
	 */
	public void keyPressed(KeyEvent evt) {
		int key = evt.getKeyCode();
		boolean repaint = true;
		if ( key == KeyEvent.VK_HOME || key == KeyEvent.VK_ENTER )
			camera.lookAt(0, 0, 20, 0, 0, 0, 0, 1, 0);
		else if (key == KeyEvent.VK_1)
			objectNumber = 1;
		else if (key == KeyEvent.VK_2)
			objectNumber = 2;
		else if (key == KeyEvent.VK_3)
			objectNumber = 3;
		else if (key == KeyEvent.VK_4)
			objectNumber = 4;
		else if (key == KeyEvent.VK_5)
			objectNumber = 5;
		else if (key == KeyEvent.VK_6)
			objectNumber = 6;
		else
			repaint = false;
		if (repaint)
			repaint();
	}
	public void keyTyped(KeyEvent e) { /* Required for KeyListener interface. */ }
	public void keyReleased(KeyEvent e) { /* Required for KeyListener interface. */ }


	//-------------------Data for stellated dodecahedron ------------------

	private final static double[][] dodecVertices  = {
			{-0.650000,0.000000,-0.248278},
			{0.401722,0.401722,0.401722},            // This array contains the coordinates
			{0.650000,0.000000,0.248278},            // for the vertices of the polyhedron
			{0.401722,-0.401722,0.401722},           // known as a stellated dodecahedron
			{0.000000,-0.248278,0.650000},
			{0.000000,0.248278,0.650000},            // Each row of the 2D array contains
			{0.650000,0.000000,-0.248278},           // the xyz-coordinates for one of
			{0.401722,0.401722,-0.401722},           // the vertices.
			{0.248278,0.650000,0.000000},
			{-0.248278,0.650000,0.000000}, 
			{-0.401722,0.401722,-0.401722},
			{0.000000,0.248278,-0.650000},
			{0.401722,-0.401722,-0.401722},
			{0.248278,-0.650000,0.000000},
			{-0.248278,-0.650000,0.000000},
			{-0.650000,0.000000,0.248278},
			{-0.401722,0.401722,0.401722},
			{-0.401722,-0.401722,-0.401722},
			{0.000000,-0.248278,-0.650000},
			{-0.401722,-0.401722,0.401722},
			{0.000000,1.051722,0.650000},
			{-0.000000,1.051722,-0.650000},
			{1.051722,0.650000,-0.000000},
			{1.051722,-0.650000,-0.000000},
			{-0.000000,-1.051722,-0.650000},
			{-0.000000,-1.051722,0.650000},
			{0.650000,0.000000,1.051722},
			{-0.650000,0.000000,1.051722},
			{0.650000,-0.000000,-1.051722},
			{-0.650000,0.000000,-1.051722},
			{-1.051722,0.650000,-0.000000},
			{-1.051722,-0.650000,0.000000}
	};

	private static int[][] dodecTriangles = {
			{16,9,20},
			{9,8,20},
			{8,1,20},              // This array specifies the faces of
			{1,5,20},              // the stellated dodecahedron.
			{5,16,20},
			{9,10,21},             // Each row in the 2D array is a list
			{10,11,21},            // of three integers.  The integers
			{11,7,21},             // are indices into the vertex array,
			{7,8,21},              // dodecVertices.  The vertices at
			{8,9,21},              // at those indices are the vertices
			{8,7,22},              // of one of the triangular faces of
			{7,6,22},              // the polyhedron.
			{6,2,22},
			{2,1,22},              // For example, the first row, {16,9,20},
			{1,8,22},              // means that vertices number 16, 9, and
			{6,12,23},             // 20 are the vertices of a face.
			{12,13,23}, 
			{13,3,23},             // There are 60 faces.
			{3,2,23},
			{2,6,23},
			{18,17,24},
			{17,14,24},
			{14,13,24},
			{13,12,24},
			{12,18,24},
			{14,19,25},
			{19,4,25},
			{4,3,25},
			{3,13,25},
			{13,14,25},
			{4,5,26},
			{5,1,26},
			{1,2,26},
			{2,3,26},
			{3,4,26},
			{15,16,27},
			{16,5,27},
			{5,4,27},
			{4,19,27},
			{19,15,27},
			{7,11,28},
			{11,18,28},
			{18,12,28},
			{12,6,28},
			{6,7,28},
			{10,0,29},
			{0,17,29},
			{17,18,29},
			{18,11,29},
			{11,10,29},
			{0,10,30},
			{10,9,30},
			{9,16,30},
			{16,15,30},
			{15,0,30},
			{17,0,31},
			{0,15,31},
			{15,19,31},
			{19,14,31},
			{14,17,31}
	};

} // end class Lab4
