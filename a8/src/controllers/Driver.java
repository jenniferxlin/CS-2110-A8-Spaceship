package controllers;

import models.*;
import student.MySpaceship;

/** An instance runs the game and links the state to the user interface. */
public abstract class Driver {

	public static boolean shouldPrint= true; // Print to console iff true

	private long seed; // The seed used to generate this game

	private Spaceship spaceShip; // The solution that implements this game.

	private GameState gameState; // The state of this game.

	private Board board; // The board for this game.

	/** Constructor: An instance with a random seed. */
	public Driver() {
		this((long) (Math.random() * Long.MAX_VALUE));
	}

	/** Constructor: an instance with seed s */
	public Driver(long s) {
		this(s, new MySpaceship());
	}

	/** Constructor: an instance with seed s and spaceship sp */
	public Driver(long s, Spaceship sp) {
		setupState(s, sp);
	}

	/** Set up the game state to run with seed s and spaceship sp */
	protected void setupState(long s, Spaceship sp) {
		outPrintln("Running Game with seed: " + s);
		seed= s;
		spaceShip= sp;
		board= Board.randomBoard(s);
		gameState= new GameState(this, board, spaceShip);
	}

	/** Create a new Board and GameState using the current seed. */
	protected void reset() {
		gameState.terminate();
		board= Board.randomBoard(seed);
		gameState= new GameState(this, board, spaceShip);
	}

	/**
	 * Stop any running game and create a new Board and GameState using seed s.
	 */
	protected void setNewSeed(long s) {
		seed= s;
		gameState.terminate();
		board= Board.randomBoard(seed);
		gameState= new GameState(this, board, spaceShip);
	}

	/** Return this Driver's GameState. */
	public GameState getGameState() {
		return gameState;
	}

	/** Return this Driver's Board. */
	public Board getBoard() {
		return board;
	}

	/** Return the seed this Driver uses. */
	public long getSeed() {
		return seed;
	}

	/** Set the seed this driver uses to s. */
	protected void setSeed(long s) {
		seed= s;
		reset();
	}

	/** Return the spaceship used by this Driver. */
	public Spaceship getSpaceShip() {
		return spaceShip;
	}

	/** Print to the standard output stream if shouldPrint is set. */
	public static void outPrintln(String s) {
		if (shouldPrint)
			System.out.println(s);
	}

	/** Print to the standard error stream if shouldPrint is set. */
	public static void errPrintln(String s) {
		if (shouldPrint)
			System.err.println(s);
	}

	/** Run the game. */
	public void runGame() {
		gameState.run();
	}

	/** Begin the rescue stage of the mission. */
	public abstract void beginRescueStage();

	/** Begin the return stage of the mission. */
	public abstract void beginReturnStage();

	/** Set the cumulative distance traveled to d. */
	public abstract void setCumulativeDistance(int d);

	/**
	 * Set the ship to travel from Node n on Edge e. Precondition: n and e are
	 * not null.
	 */
	public abstract void setNodeAndEdge(Node n, Edge e);

	/** Set the time elapsed to t. */
	public abstract void setTime(double t);

	/**
	 * Move the ship along edge e. Precondition: e is not null.
	 */
	public abstract void moveShipAlong(Edge e);

	/** Set the current HP to hp. */
	public abstract void setHp(int hp);

	/** Set the current speed to s. */
	public abstract void setSpeed(double s);

	/**
	 * Pick up a speed upgrade (removing it) on Node n. Precondition: n is not
	 * null.
	 */
	public abstract void grabSpeedUpgrade(Node n);
}