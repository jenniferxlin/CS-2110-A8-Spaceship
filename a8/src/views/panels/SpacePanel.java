package views.panels;

import utils.Constants;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentListener;
import java.io.File;

/** JPanel where planets are drawn. */
@SuppressWarnings("serial")
public class SpacePanel extends JPanel {

	private Image backgroundImage; // Background image reflecting space

	/** Constructor: an instance with dimensions (width, height) */
	public SpacePanel(int width, int height) {
		setBackground(Color.LIGHT_GRAY);
		try {
			setBackgroundImage();
		} catch (Exception e) {
		}
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		setLayout(null);
		setPreferredSize(new Dimension(width, height));
	}

	/** Set background image - used during construction */
	private void setBackgroundImage() throws Exception {
		this.backgroundImage= ImageIO.read(
				new File(Constants.DIRECTORY + "/data/images/space.png"));
	}

	/** Setup listeners to which this panel responds */
	public void setupListener(ComponentListener listener) {
		addComponentListener(listener);
	}

	/** Customize what is drawn every frame */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
	}
}