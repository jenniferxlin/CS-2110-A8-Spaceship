package controllers;

import models.Edge;
import models.Node;
import views.GUI;
import views.components.Line;
import views.components.Planet;
import views.components.Ship;
import views.panels.SidePanel;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** An instance runs the game and displays the state in a GUI. */
public class GUIDriver extends Driver {
	private GUI gui;
	private HashMap<Planet, Node> circleToNode= new HashMap<Planet, Node>();
	private HashMap<Node, Planet> nodeToCircle= new HashMap<Node, Planet>();
	private HashMap<Edge, Line> edgeToLine= new HashMap<Edge, Line>();
	private HashMap<Line, Edge> lineToEdge= new HashMap<Line, Edge>();
	private Ship ship;
	private int stage= 0; // 0 for rescue, 1 for return
	private boolean started= false; // True if this game has started

	/** Constructor: an instance with a new GUI with a random seed */
	public GUIDriver() {
		super();
		gui= new GUI();
		setupDriver();
	}

	/** Constructor: and instance with a GUI using seed s */
	public GUIDriver(long s) {
		super(s);
		gui= new GUI();
		setupDriver();
	}

	/** Set up GUI panel and menu (only called in constructor) */
	private void setupDriver() {
		setupSpacePanel();
		setupTopMenu();
	}

	/** Return the GUI */
	private GUI getGui() {
		return gui;
	}

	/** See {@link Driver#reset()} */
	@Override
	protected void reset() {
		super.reset();
		started= false;
		/* Clear the GUI */
		gui.getSpacePanel().removeAll();
		/* Set state / driver */
		setupState(getSeed(), getSpaceShip());
		setupSpacePanel();
		resetSidePanel();
	}

	/** Reset the side panel. */
	protected void resetSidePanel() {
		gui.updateSidebar(SidePanel.DISTANCE, "0");
		gui.updateSidebar(SidePanel.TIME, "0");
		gui.updateSidebar(SidePanel.SPEED, "0");
		gui.updateSidebar(SidePanel.HP, "3");
		gui.updateSidebar(SidePanel.LAST_PLANET_UPGRADE, "");
		gui.updateSidebar(SidePanel.LAST_PLANET_HOSTILITY, "");
		gui.updateSidebar(SidePanel.NEXT_PLANET_UPGRADE, "");
		gui.updateSidebar(SidePanel.NEXT_PLANET_HOSTILITY, "");
		gui.updateSidebar(SidePanel.LAST_PLANET_NAME, "");
		gui.updateSidebar(SidePanel.NEXT_PLANET_NAME, "");
		gui.updateSidebar(SidePanel.CURRENT_EDGE_DISTANCE, "");
	}

	/** See {@link Driver#setNewSeed(long)} */
	@Override
	protected void setNewSeed(long newSeed) {
		super.setNewSeed(newSeed);
		reset();
	}

	/** Add planets to the GUI */
	private void setupSpacePanel() {
		assert getGui() != null;
		assert getBoard() != null;

		// Clear the board
		gui.getSpacePanel().removeAll();
		if (circleToNode.size() != 0) {
			circleToNode.clear();
			nodeToCircle.clear();
			edgeToLine.clear();
			lineToEdge.clear();
		}

		// Make nodes
		for (Node n : getBoard().getNodes()) {
			Planet p= new Planet(n.getMappedName(), scaleX(n), scaleY(n));
			circleToNode.put(p, n);
			nodeToCircle.put(n, p);
			gui.getSpacePanel().add(p);
		}

		// Make lines
		for (Edge e : getBoard().getEdges()) {
			Line l= new Line(nodeToCircle.get(e.getExits()[0]), 
					nodeToCircle.get(e.getExits()[1]));
			edgeToLine.put(e, l);
			lineToEdge.put(l, e);
			gui.getSpacePanel().add(l);
		}

		// Make ship
		Planet earthCircle= nodeToCircle.get(getBoard().getEarth());
		ship= new Ship(earthCircle, (int) gui.getSidePanel().getSpeedSlider().getValue(), 
				getGameState().getSpeed());

		// Add everything
		gui.getSpacePanel().add(ship);
		for (Planet p : circleToNode.keySet()) {
			gui.getSpacePanel().add(p);
		}
		for (Line l : lineToEdge.keySet()) {
			gui.getSpacePanel().add(l);
		}

		// Add listener
		gui.getSpacePanel().addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				didResizeSpacePanel();
			}

			public void componentMoved(ComponentEvent e) {
			}

			public void componentShown(ComponentEvent e) {
			}

			public void componentHidden(ComponentEvent e) {
			}
		});

		gui.getSidePanel().getSpeedSlider().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				ship.setBaseSpeed((int) ((JSlider) e.getSource()).getValue());
			}
		});

		gui.repaint();
	}

	/**
	 * Setup the top menu's callbacks so we can start the game, reset the game,
	 * and select a seed
	 */
	private void setupTopMenu() {
		// On clicking on "Start"
		gui.getTopMenuBar().getStartItem().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!started) {
					started= true;
					Runnable runGame= new Runnable() {
						@Override
						public void run() {
							runGame();
						}

						@Override
						public String toString() {
							return "runGame()";
						}
					};
					ExecutorService executor= Executors.newSingleThreadExecutor();
					executor.submit(runGame);
				} else {
					errPrintln("Game has already started.");
				}
			}
		});

		// On clicking on "Reset"
		gui.getTopMenuBar().getResetItem().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});

		// On clicking on "New Map"
		gui.getTopMenuBar().getNewMap().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long newSeed= (long) (Math.random() * Long.MAX_VALUE);
				setNewSeed(newSeed);
			}
		});
	}

	// MARK - Geometry / scaling logic

	/** Scaled x-value for Node n. */
	private int scaleX(Node n) {
		return (int) (gui.getSpacePanel().getWidth() * 
				(((double) n.getX()) / getBoard().getWidth()));
	}

	/** Scaled y-value for Node n. */
	private int scaleY(Node n) {
		return (int) (gui.getSpacePanel().getHeight() * 
				(((double) n.getY()) / getBoard().getHeight()));
	}

	// MARK - event handlers

	/** Handler to be called when SpacePanel is resized */
	private void didResizeSpacePanel() {
		for (Planet p : circleToNode.keySet()) {
			p.setGUILocation(scaleX(circleToNode.get(p)), scaleY(circleToNode.get(p)));
		}
		for (Line l : lineToEdge.keySet()) {
			l.fixBounds();
		}
		ship.fixBounds();
		gui.getSpacePanel().repaint();
	}

	// MARK - required functionality

	/** See {@link Driver#moveShipAlong(Edge)} */
	public void moveShipAlong(Edge e) {
		ship.moveAlong(edgeToLine.get(e));
	}

	@Override
	public void beginRescueStage() {
		stage= 0;
		gui.updateSidebar(SidePanel.STAGE, "Rescue stage");
	}

	@Override
	public void beginReturnStage() {
		stage= 1;
		for (Map.Entry<Node, Planet> e : nodeToCircle.entrySet()) {
			Node n= e.getKey();
			Planet p= e.getValue();
			p.setHostile(n.isHostile());
			p.setSpeedUpgrade(n.hasSpeedUpgrade());
		}
		gui.updateSidebar(SidePanel.STAGE, "Return stage");
	}

	@Override
	public void setTime(double t) {
		gui.updateSidebar(SidePanel.TIME, "" + t);
	}

	@Override
	public void setHp(int Hp) {
		gui.updateSidebar(SidePanel.HP, "" + Hp);
	}

	@Override
	public void setSpeed(double speed) {
		gui.updateSidebar(SidePanel.SPEED, "" + speed);
		ship.setSpeedScale(speed);
	}

	@Override
	public void grabSpeedUpgrade(Node n) {
		nodeToCircle.get(n).setSpeedUpgrade(false);
	}

	@Override
	public void setCumulativeDistance(int d) {
		gui.updateSidebar(SidePanel.DISTANCE, "" + d);
	}

	@Override
	public void setNodeAndEdge(Node n, Edge e) {
		// update names
		gui.updateSidebar(SidePanel.LAST_PLANET_NAME, n.name);

		if (stage == 1) {
			// update upgrade values
			String lastUpgradeValue= n.hasSpeedUpgrade() ? 
					"Has speed upgrade" : "No speed upgrade";
			gui.updateSidebar(SidePanel.LAST_PLANET_UPGRADE, lastUpgradeValue);

			// update hostility values
			String lastHostilityValue= n.isHostile() ? "Hostile" : "Not hostile";
			gui.updateSidebar(SidePanel.LAST_PLANET_HOSTILITY, lastHostilityValue);
		}

		// get other node
		Node next= e.getOther(n);

		// update stats for next node
		gui.updateSidebar(SidePanel.NEXT_PLANET_NAME, next.name);
		if (stage == 1) {
			String nextUpgradeValue= next.hasSpeedUpgrade() ? 
					"Has speed upgrade" : "No speed upgrade";
			gui.updateSidebar(SidePanel.NEXT_PLANET_UPGRADE, nextUpgradeValue);
			String nextHostilityValue= next.isHostile() ? "Hostile" : "Not hostile";
			gui.updateSidebar(SidePanel.NEXT_PLANET_HOSTILITY, nextHostilityValue);
		}

		// update edge distance
		gui.updateSidebar(SidePanel.CURRENT_EDGE_DISTANCE, "" + e.length);
	}

	/** Run the Space Adventure game with a GUI. */
	public static void main(String[] args) {
		List<String> argList= new ArrayList<String>(Arrays.asList(args));
		int seedIndex= argList.indexOf("-s");
		long seed= 0;
		boolean seedGiven= false;
		if (seedIndex >= 0) {
			try {
				seed= Long.parseLong(argList.get(seedIndex + 1));
				seedGiven= true;
			} catch (NumberFormatException e) {
				Driver.errPrintln("Error, -s must be followed by a numerical seed");
				return;
			} catch (ArrayIndexOutOfBoundsException e) {
				Driver.errPrintln("Error, -s must be followed by a seed");
				return;
			}
		}
		if (seedGiven)
			new GUIDriver(seed);
		else
			new GUIDriver();
	}
}
