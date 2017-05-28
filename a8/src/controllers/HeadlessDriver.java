package controllers;

import models.Edge;
import models.Node;
import models.Spaceship;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * An instance runs the game and prints the state to the console. This will be
 * used to grade submissions.
 */
public class HeadlessDriver extends Driver {
	/** Constructor */
	public HeadlessDriver() {
		super();
	}

	/** Constructor: an instance using seed s */
	public HeadlessDriver(long s) {
		super(s);
	}

	/** Constructor: an instance using seed s and space ship sp */
	public HeadlessDriver(long s, Spaceship sp) {
		super(s, sp);
	}

	/** See {@link Driver#moveShipAlong(Edge)} */
	public void moveShipAlong(Edge e) {
		outPrintln("Ship is along the edge: " + e.toString());
	}

	@Override
	public void beginRescueStage() {
		outPrintln("=== Beginning Rescue ===");
	}

	@Override
	public void beginReturnStage() {
		outPrintln("=== Beginning Return ===");
	}

	@Override
	public void setTime(double t) {
		outPrintln("Total time taken is now: " + t);
	}

	@Override
	public void setHp(int Hp) {
		outPrintln("Total HP is now: " + Hp);
	}

	@Override
	public void setSpeed(double speed) {
		outPrintln("Speed is now " + speed);
	}

	@Override
	public void grabSpeedUpgrade(Node n) {
		outPrintln("Speed upgrade picked up on Planet " + n.name);
	}

	@Override
	public void setCumulativeDistance(int d) {
	}

	@Override
	public void setNodeAndEdge(Node n, Edge e) {
	}

	/**
	 * Run the Space Adventure game without a GUI. The handout says what the
	 * args are.
	 */
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

		HeadlessDriver driver= seedGiven ? new HeadlessDriver(seed) : new HeadlessDriver();
		driver.runGame();
		System.exit(0);
	}
}