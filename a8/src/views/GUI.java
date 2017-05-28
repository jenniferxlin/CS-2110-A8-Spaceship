package views;

import views.panels.SidePanel;
import views.panels.SpacePanel;
import views.panels.TopMenu;
import javax.swing.*;
import java.awt.*;

/** An instance of the GUI driving the space exploration game. */
@SuppressWarnings("serial")
public class GUI extends JFrame {

	// MARK - static fields

	/* Defines buffers to base initial interface drawing on */
	public static final int X_BUFFER = 100;
	public static final int Y_BUFFER = 50;

	/* Specifies the minimum size the drawing board screen must be */
	public static final int DRAWING_BOARD_WIDTH_MIN = 400;
	public static final int DRAWING_BOARD_HEIGHT_MIN = 400;

	/* Dynamic based on the screen size the user has */
	public static final int DRAWING_BOARD_WIDTH;
	public static final int DRAWING_BOARD_HEIGHT;

	/* Two panels aside from the drawing board (fixed dimensions) */
	public static final int UPDATE_PANEL_HEIGHT = 100;
	public static final int SIDE_PANEL_WIDTH = 300;

	/* Set the (width, height) based on user's screen size */
	static {
		Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
		DRAWING_BOARD_WIDTH = s.width - SIDE_PANEL_WIDTH - 2 * X_BUFFER;
		DRAWING_BOARD_HEIGHT = (int) (s.height * 0.8) - UPDATE_PANEL_HEIGHT - 2 * Y_BUFFER;
	}

	// MARK - instance fields

	/* Various panels of the GUI */
	private SpacePanel spacePanel;
	private SidePanel sidePanel;
	private TopMenu menuBar;

	/** Constructor: a default GUIS */
	public GUI() {
		// Set minimum size
		setMinimumSize(new Dimension(SIDE_PANEL_WIDTH + DRAWING_BOARD_WIDTH_MIN,
				UPDATE_PANEL_HEIGHT + DRAWING_BOARD_HEIGHT_MIN));

		// What happens to the executable on closing the app
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// Initialize parts of the interface
		spacePanel = new SpacePanel(DRAWING_BOARD_WIDTH, DRAWING_BOARD_HEIGHT);
		sidePanel = new SidePanel(SIDE_PANEL_WIDTH, DRAWING_BOARD_HEIGHT);
		menuBar = new TopMenu();
		getContentPane().add(spacePanel, BorderLayout.CENTER);
		getContentPane().add(sidePanel, BorderLayout.EAST);
		setJMenuBar(menuBar);

		// JFrame stuff
		pack();
		validate();
		repaint();
		setLocation(X_BUFFER, Y_BUFFER);
		setVisible(true);
	}

	/** Return the drawing panel */
	public SpacePanel getSpacePanel() {
		return spacePanel;
	}

	/** Return the side panel */
	public SidePanel getSidePanel() {
		return sidePanel;
	}

	/** Return the top menu bar */
	public TopMenu getTopMenuBar() {
		return menuBar;
	}

	/**
	 * Update a label id in the side menu to have value v.. Pre: id is one of
	 * the constants provided in views.panels.SidePanel
	 */
	public void updateSidebar(int id, String v) {
		sidePanel.updateStat(id, v);
	}
}