package views.components;

import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.JPanel;

/**
 * Graphics class Line that allows for the drawing of lines. Lines use a (p1,
 * p2) coordinate system, where p1 and p2 are planet objects that denote the
 * endpoints of this line. Each planet has (x, y) coordinates, so a Line can be
 * though of as having (x1, y1, x2, y2).
 */
@SuppressWarnings("serial")
public class Line extends JPanel {

	/** Default thickness of lines when they are drawn on the GUI */
	public static final int LINE_THICKNESS= 1;

	/** Default color of lines when they are drawn on the GUI */
	public static final Color DEFAULT_COLOR= Color.WHITE;

	private Planet p1; // Endpoints of this line are p1 and p2
	private Planet p2;

	private Color color; /// The color of this line

	/** Constructor: a line from p1 to p2 */
	public Line(Planet p1, Planet p2) {
		setP1(p1);
		setP2(p2);
		setColor(DEFAULT_COLOR);
		setOpaque(false);
		fixBounds();
	}

	/** Return the first endpoint of this line. */
	public Planet getP1() {
		return p1;
	}

	/** Set the first endpoint of this line to p */
	protected void setP1(Planet p) {
		p1= p;
	}

	/** Return the second endpoint of this line. */
	public Planet getP2() {
		return p2;
	}

	/** Set the second endpoint of this line to p. */
	private void setP2(Planet p) {
		p2= p;
	}

	/** Return the planet at the other end of the line from p */
	public Planet getOther(Planet p) {
		assert p1.equals(p) || p2.equals(p);
		return p1.equals(p) ? p2 : p1;
	}

	/** Return the x-coordinate of the first end of this line. */
	public int getX1() {
		return p1.getX1();
	}

	/** Return the ycoordinate of the first end of this line. */
	public int getY1() {
		return p1.getY1();
	}

	/** Return the x-coordinate of the second end of this line. */
	public int getX2() {
		return p2.getX1();
	}

	/** Return the ycoordinate of the second end of this line. */
	public int getY2() {
		return p2.getY1();
	}

	/** Return the width (x diff) of the line. Always positive. */
	public int getLineWidth() {
		return Math.abs(getX1() - getX2());
	}

	/** Return the height (y diff) of the line. Always positive. */
	public int getLineHeight() {
		return Math.abs(getY1() - getY2());
	}

	/** Return the length of this line */
	public int getLength() {
		double deltaX= p1.getX1() - p2.getX1();
		double deltaY= p1.getY1() - p2.getY1();
		return (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
	}

	/**
	 * Dynamically resize the drawing boundaries of this line based on the
	 * height and width of the line, with a minimum sized box of (40,40). Call
	 * whenever planets move to fix the drawing boundaries of this.
	 */
	public void fixBounds() {
		int minX= Math.min(getX1(), getX2());
		int minY= Math.min(getY1(), getY2());
		int width= Math.max(Math.abs(getX1() - getX2()), 40);
		int height= Math.max(Math.abs(getY1() - getY2()), 40);

		setBounds(minX, minY, width + 2, height + 2);
	}

	/** Return the color of this line. */
	private Color getColor() {
		return color;
	}

	/** Set the color of this line to c. */
	private void setColor(Color c) {
		color= c;
	}

	/** Return a String representation of this line */
	@Override
	public String toString() {
		return String.format("(%s,%s) -> (%s, %s)", 
				p1.getX1(), p1.getY1(), p2.getX1(), p2.getY1());
	}

	/** Paint this line using g */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d= (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setStroke(new BasicStroke(LINE_THICKNESS, BasicStroke.CAP_BUTT, 
							BasicStroke.JOIN_BEVEL, 0, new float[] { 1 }, 0));

		Line2D line2d= getX1() < getX2() && getY1() < getY2() 
				|| getX2() < getX1() && getY2() < getY1()
				? new Line2D.Double(1, 1, getLineWidth(), getLineHeight())
				: new Line2D.Double(1, getLineHeight(), getLineWidth(), 1);

		g2d.setColor(getColor());
		g2d.draw(line2d);
	}

	/**
	 * Return the size of the line, as a rectangular bounding box (x2 - x1, y2 -
	 * y1).
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Math.abs(getX2() - getX1()), Math.abs(getY2() - getY1()));
	}
}
