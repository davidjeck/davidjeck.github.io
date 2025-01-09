import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Transforms2D_complete extends JPanel {
	
	/**
	 * This method is called when the program starts and when the user 
	 * changes the setting of the popup menu.
	 */
	private void draw(Graphics2D g) {
		g.translate(300,300);  // Moves (0,0) to the center of the display.
		int whichTransform = transformSelect.getSelectedIndex(); // Selected item number, 0 to 9.

		// TODO Apply transforms here, depending on the value of whichTransform, from 1 to 9!

		switch (whichTransform) {
		case 1:
			g.scale(0.5, 0.5);
			break;
		case 2:
			g.rotate(Math.PI/4);
			break;
		case 3:
			g.scale(0.5,-1);
			break;
		case 4:
			g.shear(0.25, 0);
			break;
		case 5:
			g.translate(0, -225);
			g.scale(1, 0.5);
			break;
		case 6:
			g.rotate(Math.PI/2);
			g.shear(0.3, 0);
			break;
		case 7:
			g.scale(-0.5, -1);
			break;
		case 8:
			g.translate(-50,100);
			g.rotate(Math.PI/6);
			g.scale(1, 0.5);
			break;
		case 9:
			g.translate(100,0);
			g.shear(0,0.25);
			g.rotate(Math.PI);
			break;
		}



		// Do NOT change the following line.  This will draw the image, subject
		// to whatever transformations have been applied.
		g.drawImage(pic, -200, -150, null); // Draw image with center at (0,0).
	}
	
	//------------------------------------------------------------------------------

	private class Display extends JPanel {
		protected void paintComponent(Graphics g1) {
			super.paintComponent(g1);
			Graphics2D g = (Graphics2D)g1.create();
			draw(g);
		}
	}

	private Display display;
	private BufferedImage pic;
	private JComboBox<String> transformSelect;

	public Transforms2D_complete() throws IOException {
		pic = ImageIO.read(getClass().getClassLoader().getResource("shuttle.jpg"));
		display = new Display();
		display.setBackground(Color.YELLOW);
		display.setPreferredSize(new Dimension(600,600));
		transformSelect = new JComboBox<String>();
		transformSelect.addItem("None");
		for (int i = 1; i < 10; i++) {
			transformSelect.addItem("No. " + i);
		}
		transformSelect.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				display.repaint();
			}
		});
		setLayout(new BorderLayout(3,3));
		setBackground(Color.GRAY);
		setBorder(BorderFactory.createLineBorder(Color.GRAY,10));
		JPanel top = new JPanel();
		top.setLayout(new FlowLayout(FlowLayout.CENTER));
		top.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		top.add(new JLabel("Transform: "));
		top.add(transformSelect);
		add(display,BorderLayout.CENTER);
		add(top,BorderLayout.NORTH);
	}


	public static void main(String[] args) throws IOException {
		JFrame window = new JFrame("2D Transforms");
		window.setContentPane(new Transforms2D_complete());
		window.pack();
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation( (screen.width - window.getWidth())/2, (screen.height - window.getHeight())/2 );
		window.setVisible(true);
	}

}
