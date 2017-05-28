package views.components;

import utils.Constants;
import java.awt.*;
import java.awt.geom.Arc2D;

/** GUI representation of a planet */
@SuppressWarnings("serial")
public class Planet extends Circle {

	private Color leftColor; // Color of left border. RED if hostile, WHITE if
							 // not.

	private Color rightColor; // Color of right border. GREEN if has speed
							  // upgrade, WHITE if not. */

	/* Borders for the circle in the GUI */
	Arc2D leftBorder;
	Arc2D rightBorder;

	// MARK - static fields
	private static final int DIAMETER= 15;

	/** Constructor: an instance with name n and center (x, y) */
	public Planet(String n, int x, int y) {
		super(n, x, y, n.equals(Constants.EARTH_NAME) ? 
				new Color(16, 83, 63) : Constants.randomColor(),
				n.equals(Constants.EARTH_NAME) || 
				n.equals(Constants.CRASHED_PLANET_NAME) ? DIAMETER * 2 : DIAMETER);
		setHostile(false);
		setSpeedUpgrade(false);
		leftBorder= new Arc2D.Double(getCircle().getBounds2D(), -90., 180., Arc2D.OPEN);
		rightBorder= new Arc2D.Double(getCircle().getBounds2D(), 90., 180., Arc2D.OPEN);
	}

	/** Draw this Planet using g. */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d= (Graphics2D) g;
		g2d.setStroke(new BasicStroke(LINE_THICKNESS));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(leftColor);
		g2d.draw(leftBorder);
		g2d.setColor(rightColor);
		g2d.draw(rightBorder);
	}

	/** Set whether or not this planet is hostile depdning on h. */
	public void setHostile(boolean h) {
		if (h)
			leftColor= Color.RED;
		else
			leftColor= Color.WHITE;
		repaint();
	}

	/** Set whether or not this planet has a speed upgrade depending on s. */
	public void setSpeedUpgrade(boolean s) {
		if (s)
			rightColor= Color.GREEN;
		else
			rightColor= Color.WHITE;
		repaint();
	}
}