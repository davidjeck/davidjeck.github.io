import java.awt.*;
import javax.swing.*;

/**
 * A RandomArtPanel draws random pictures which might be taken to have
 * some vague resemblance to abstract art.  A new picture is produced every
 * four seconds.  There are three types of pictures:  random lines,
 * random circles, and random 3D rects.  The figures are drawn in
 * random colors on a background that is a random shade of gray.
 * A main() routine allows this class to be run as a program
 */
public class RandomArt extends JPanel {

    /**
     * The constructor creates a timer with a delay time of four seconds
     * (4000 milliseconds), and with a lambda expression of type
     * ActionListener that responds to an event by repainting this
     * panel.  It also starts the timer running.
     */
    public RandomArt() {
        Timer timer = new Timer(4000, e -> repaint() );
        timer.start();
    }

    /**
     * The paintComponent() method fills the panel with a random shade of
     * gray and then draws one of three types of random "art".  The type
     * of art to be drawn is chosen at random.
     */
    public void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D)g;  // for access to advanced features.
        g2.setStroke( new BasicStroke(2) ); // draw lines that are two pixels wide.
        g2.setRenderingHint(  // turn on antialiasting for smoother lines. 
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Note:  Since the next three lines fill the entire panel with
        // gray, there is no need to call super.paintComponent(g), since
        // any drawing that it does will only be covered up anyway.

        Color randomGray = Color.getHSBColor( 1.0F, 0.0F, (float)Math.random() );
        g.setColor(randomGray);
        g.fillRect( 0, 0, getWidth(), getHeight() );

        int artType = (int)(3*Math.random());

        switch (artType) {
        case 0:
            for (int i = 0; i < 500; i++) {
                int x1 = (int)(getWidth() * Math.random());
                int y1 = (int)(getHeight() * Math.random());
                int x2 = (int)(getWidth() * Math.random());
                int y2 = (int)(getHeight() * Math.random());
                Color randomHue = Color.getHSBColor( (float)Math.random(), 1.0F, 1.0F);
                g.setColor(randomHue);
                g.drawLine(x1,y1,x2,y2);
            }
            break;
        case 1:
            for (int i = 0; i < 200; i++) {
                int centerX =  (int)(getWidth() * Math.random());
                int centerY = (int)(getHeight() * Math.random());
                Color randomHue = Color.getHSBColor( (float)Math.random(), 1.0F, 1.0F);
                g.setColor(randomHue);
                g.drawOval(centerX - 50, centerY - 50, 100, 100);
            }
            break;
        case 2:
            for (int i = 0; i < 25; i++) {
                int centerX =  (int)(getWidth() * Math.random());
                int centerY = (int)(getHeight() * Math.random());
                int size = 30 + (int)(170*Math.random());
                Color randomColor = new Color( (int)(256*Math.random()), 
                        (int)(256*Math.random()), (int)(256*Math.random()) );
                g.setColor(randomColor);
                g.fill3DRect(centerX - size/2, centerY - size/2, size, size, true);
            }
            break;
        }

    } // end paintComponent()

    
    /**
     * main() routine makes it possible to run this class as a program.
     * It just opens a window that contains a RandomArt panel.
     */
    public static void main(String[] args) {
        JFrame window = new JFrame("Random Art ??");
        RandomArt content = new RandomArt();
        window.setContentPane(content);
        window.setSize(400,400);
        window.setLocation(100,100);
        window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        window.setVisible(true);
    }

} // end class RandomArt

