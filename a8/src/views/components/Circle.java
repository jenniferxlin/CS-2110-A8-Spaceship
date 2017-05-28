package views.components;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.JPanel;

/**
 * Graphics class Circle that allows for the drawing of circles. Circles use an
 * (x1, y1, diameter) coordinate system of where they are located on the board.
 *
 * Circle keeps track of its displayName, its diameter, its x / y coordinates,
 * and its color. It provides a means of setting the GUI position of itself when
 * the X and Y coordinates of the circle change.
 */
@SuppressWarnings("serial")
public abstract class Circle extends JPanel {
	// MARK - static fields
	private static final int PANEL_BUFFER= 10;
	static final int LINE_THICKNESS= 2;
	private static final int TEXT_HEIGHT= 15;
	private static final int TEXT_WIDTH= 80;

	// MARK - instance variables
	private String displayName; // Name given to this circle
	private int diameter; // diameter of this circle
	private int x1; // Center is (x1, y1)
	private int y1;
	private Color color; // Color of this circle
	private Ellipse2D circle;

	/**
	 * Constructor: a circle with display name dn, centere (x, y), diameter d,
	 * and color c.
	 */
	public Circle(String dn, int x, int y, Color c, int d) {
		displayName= dn;
		x1= x;
		y1= y;
		diameter= d;
		color= c;
		fixBounds();
		setOpaque(false);
		circle= new Ellipse2D.Double(PANEL_BUFFER / 2, PANEL_BUFFER / 2 + TEXT_HEIGHT, d, d);
	}

	/** Return the x-coordinate of the center. */
	int getX1() {
		return x1;
	}

	/** Set x-coordinate of center to x */
	void setX1(int x) {
		x1= x;
	}

	/** Return y-coordinate of center. */
	int getY1() {
		return y1;
	}

	/** Set y-coordinate of center to y. */
	void setY1(int y) {
		y1= y;
	}

	/** Return the diameter. */
	int getDiameter() {
		return diameter;
	}

	/** Return the Ellipse2D that represents this Circle. */
	Ellipse2D getCircle() {
		return circle;
	}

	/** Return the display name. */
	String getDisplayName() {
		return displayName;
	}

	/**
	 * Fix the boundaries so that all drawings will be within the bounds. Call
	 * after x, y, or diameter is changed.
	 */
	public void fixBounds() {
		int x= x1;
		int y= y1;
		int d= diameter;
		int dP= d + PANEL_BUFFER;
		setBounds(x - dP / 2, y - dP / 2, dP, dP);
		Rectangle oldBounds= getBounds();
		setBounds(oldBounds.x, oldBounds.y - TEXT_HEIGHT, 
					oldBounds.width + TEXT_WIDTH, oldBounds.height + TEXT_HEIGHT);
	}

	/** Set the GUI location to (x, y). */
	public void setGUILocation(int x, int y) {
		setX1(x);
		setY1(y);
		fixBounds();
	}

	/** Return a string representation of this circle. */
	@Override
	public String toString() {
		return String.format("(%s,%s), d=%s, name=%s", 
				(getX1() - getDiameter()) / 2, (getX1() - getDiameter()) / 2,
				getDiameter(), getDisplayName());
	}

	/** Draw the Circle --and text-- using g.. */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d= (Graphics2D) g;
		g2d.setStroke(new BasicStroke(LINE_THICKNESS));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw the circle
		g2d.setColor(color);
		g2d.fill(circle);

		// Draw the text
		g2d.setColor(Color.WHITE);
		g2d.drawString(getDisplayName(), 0, TEXT_HEIGHT / 2 + PANEL_BUFFER / 2);
	}

	/** Return a bounding square (of size diameter * diameter) of the circle. */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(getDiameter(), getDiameter());
	}
}