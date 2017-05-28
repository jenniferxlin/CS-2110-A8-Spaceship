package views.panels;

import javax.swing.*;

/** Menu bar for starting and resetting simulation. */
@SuppressWarnings("serial")
public class TopMenu extends JMenuBar {

	private JMenuItem startItem;
	private JMenuItem resetItem;
	private JMenuItem newMap;

	/** Constructor: a top menu */
	public TopMenu() {
		super();
		// File menu
		JMenu fileMenu= new JMenu("File");
		startItem= new JMenuItem("Start");
		resetItem= new JMenuItem("Reset");
		newMap= new JMenuItem("New random map");

		// Build menu
		fileMenu.add(startItem);
		fileMenu.add(resetItem);
		fileMenu.add(newMap);
		add(fileMenu);
	}

	/** Return menu item for starting the game */
	public JMenuItem getStartItem() {
		return startItem;
	}

	/** Return menu item for resetting the map */
	public JMenuItem getResetItem() {
		return resetItem;
	}

	/** Return menu item for getting a new map */
	public JMenuItem getNewMap() {
		return newMap;
	}

}