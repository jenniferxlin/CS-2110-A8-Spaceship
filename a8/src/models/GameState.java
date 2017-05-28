package models;

import controllers.Driver;
import controllers.GUIDriver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** An instance keeps track of the time and position of a ship in a galaxy. */
public class GameState implements RescueStage, ReturnStage {
	/* The driver that runs this particular game */
	private Driver driver;

	/*
	 * The maximum runtime allowed for both rescue and return, respectively, as
	 * well as the units of these time value
	 */
	private static long rescueTimeout= 5;
	private static long returnTimeout= 10;
	private static TimeUnit timeUnit= TimeUnit.SECONDS;

	/* The lowest possible score, in case the solution fails */
	public static final double MINIMUM_SCORE= Double.MAX_VALUE;

	/* The increments in which speed should change */
	private static final double DELTA_SPEED= 0.2;

	/* The galaxy associated with this instance */
	private final Board board;

	/* The ship used to run this instance */
	private final Spaceship ship;

	/* The current position of the ship */
	private Node position;

	/* The amount of time spent since starting the rescue; >= 0 */
	private double timeElapsed;

	/* The total cumulative distance traveled since starting the rescue; >= 0 */
	private int distance;

	/* The current speed of the ship; speed >= 1 */
	private double speed;

	/*
	 * The current amount of hit points of the ship. If hp <= 0, then the
	 * solution fails
	 */
	private int hp;

	/* True if the crashed spaceship was rescued */
	private boolean rescueSuccessful;

	/* True if the ship returned to Earth */
	private boolean returnSuccessful;

	/* True if a solution timed out somewhere */
	private boolean timedOut= false;

	/* True if the game was terminated prematurely */
	private boolean terminated= false;

	/* Executes threaded tasks - used for timed method execution */
	private ExecutorService executor;

	/**
	 * Constructor: a new game instance with a Driver d, Board b, and with the
	 * Spaceship used to solve the game.
	 */
	public GameState(Driver d, Board b, Spaceship s) {
		driver= d;
		if (d instanceof GUIDriver) {
			rescueTimeout= 10;
			returnTimeout= 10;
			timeUnit= TimeUnit.MINUTES;
		}
		board= b;
		ship= s;
		position= board.getEarth();
		timeElapsed= 0;
		distance= 0;
		speed= 1;
		hp= 3;
		rescueSuccessful= false;
		returnSuccessful= false;
		executor= Executors.newSingleThreadExecutor();
	}

	/**
	 * Run a function which returns a V wrapped in a Callable class which will
	 * time out after a specified time frame. Return the function's V if the
	 * function succeeds or null if it fails. Precondition: fun.toString()
	 * returns a representation of the function
	 */
	private <T> T withTimeout(long timeout, TimeUnit timeUnit, Callable<T> fun) {
		Future<T> future= executor.submit(fun);
		try {
			return future.get(timeout, timeUnit);
		} catch (TimeoutException e) {
			Driver.errPrintln("Error: " + fun.toString() + " timed out.");
			timedOut= true;
		} catch (InterruptedException e) {
			Driver.outPrintln("Interrupted " + fun.toString() + " - probably resetting.");
		} catch (Exception e) {
			Driver.errPrintln("Error: " + fun.toString() + " threw " + e.toString());
		}
		return null;
	}

	/**
	 * Run through the game, one step at a time. Will run return() only if
	 * rescue() succeeds. Both rescue() and return() are timed based on timeout
	 * and timeUnit. Return the score of the solution, or MINIMUM_SCORE if it
	 * fails.
	 */
	public double run() {
		driver.setHp(hp);
		driver.setSpeed(speed);
		driver.setTime(timeElapsed);
		Callable<Boolean> rescueFun= new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return rescue();
			}

			@Override
			public String toString() {
				return "rescue()";
			}
		};
		Callable<Boolean> returnFun= new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return returnToEarth();
			}

			@Override
			public String toString() {
				return "returnToEarth()";
			}
		};
		double score= MINIMUM_SCORE;
		Boolean rescueResult= withTimeout(rescueTimeout, timeUnit, rescueFun);
		if (rescueResult != null && rescueResult) {
			rescueSuccessful= true;
			Boolean returnResult= withTimeout(returnTimeout, timeUnit, returnFun);
			if (returnResult != null && returnResult) {
				returnSuccessful= true;
				score= getScore();
				Driver.outPrintln("Score: " + score);
			}
		}
		return score;
	}

	/**
	 * Run ship's rescue method. Return true if it succeeds or false if it
	 * fails.
	 */
	private boolean rescue() {
		driver.beginRescueStage();
		try {
			ship.rescue(rescueStage());
		} catch (Exception e) {
			if (!terminated) {
				if (e.getMessage() != null)
					Driver.errPrintln(e.getMessage());
				else
					Driver.errPrintln("Your solution to rescue() threw " + e.toString() + ".");
			}
			return false;
		}
		if (position.equals(board.getTarget())) {
			return true;
		} else {
			Driver.errPrintln("Your solution to rescue() returned at the wrong location.");
			return false;
		}
	}

	/**
	 * Run ship's returnToEarth method. Return true if it succeeds or false if
	 * it fails.
	 */
	private boolean returnToEarth() {
		driver.beginReturnStage();
		try {
			ship.returnToEarth(returnStage());
		} catch (Exception e) {
			if (!terminated) {
				if (e.getMessage() != null)
					Driver.errPrintln(e.getMessage());
				else
					Driver.errPrintln("Your solution to returnToEarth() threw " 
							+ e.toString() + ".");
			}
			return false;
		}
		if (position.equals(board.getEarth())) {
			return true;
		} else {
			Driver.errPrintln("Your solution to returnToEarth() returned at the wrong location.");
			return false;
		}
	}

	/** Return the unique ID of the current location. */
	@Override
	public long currentLocation() {
		return position.getId();
	}

	/** Return the strength of the ping at the current location. */
	@Override
	public double getPing() {
		return board.getPing(position);
	}

	/** Return rescueSuccessful. */
	public boolean getRescueSucceeded() {
		return rescueSuccessful;
	}

	/** Return returnSuccessful. */
	public boolean getReturnSucceeded() {
		return returnSuccessful;
	}

	/** Return timedOut. */
	public boolean getTimedOut() {
		return timedOut;
	}

	/**
	 * Return a collection of NodeStatuses containing each neighboring Node and
	 * the ping strength from the neighboring Node to the target.
	 */
	@Override
	public Collection<NodeStatus> neighbors() {
		Collection<NodeStatus> options= new ArrayList<>();
		for (Map.Entry<Node, Integer> n : position.getNeighbors().entrySet()) {
			options.add(new NodeStatus(n.getKey().getId(), board.getPing(n.getKey())));
		}
		return options;
	}

	/** Return whether or not the crashed spaceship has been reached. */
	@Override
	public boolean foundSpaceship() {
		return position == board.getTarget();
	}

	/** Return the Node that the ship is currently on. */
	@Override
	public Node currentNode() {
		return position;
	}

	/** Return the Node that corresponds to Earth. */
	@Override
	public Node getEarth() {
		return board.getEarth();
	}

	/** Return a Collection of every Node in the galaxy. */
	@Override
	public Collection<Node> allNodes() {
		return Collections.unmodifiableSet(board.getNodes());
	}

	/** Return time taken to travel distance d, based on current speed. */
	private double timeToTravel(int d) {
		return d / speed;
	}

	/**
	 * Move the ship to the Node whose ID is id.
	 * 
	 * @throws IllegalArgumentException
	 *             if the Node isn't a neighbor of the current position.
	 */
	@Override
	public void moveTo(long id) {
		for (Map.Entry<Node, Integer> entry : position.getNeighbors().entrySet()) {
			Node n= entry.getKey();
			if (n.getId() == id) {
				driver.setNodeAndEdge(position, position.getConnect(n));
				driver.moveShipAlong(position.getConnect(n));
				int length= entry.getValue();
				distance += length;
				driver.setCumulativeDistance(distance);
				timeElapsed += timeToTravel(length);
				driver.setTime(timeElapsed);
				position= n;
				return;
			}
		}
		throw new IllegalArgumentException("moveTo: Node must be adjacent to position");
	}

	/**
	 * Move the ship to Node n.
	 * 
	 * @throws IllegalArgumentException
	 *             if the Node isn't a neighbor of the current position or if
	 *             too many hostile planets are been visited.
	 */
	@Override
	public void moveTo(Node n) {
		if (!position.isConnectedTo(n))
			throw new IllegalArgumentException("moveTo: Node must be adjacent to position");

		driver.setNodeAndEdge(position, position.getConnect(n));
		driver.moveShipAlong(position.getConnect(n));
		int length= n.getConnect(position).length;
		distance += length;
		driver.setCumulativeDistance(distance);
		timeElapsed += timeToTravel(length);
		driver.setTime(timeElapsed);
		position= n;
		if (n.isHostile()) {
			--hp;
			driver.setHp(hp);
			if (speed > 1) {
				speed -= DELTA_SPEED;
				driver.setSpeed(speed);
			}
		}

		if (hp <= 0) {
			throw new IllegalStateException("Visited too many hostile planets!");
		}
	}

	/**
	 * Grab the speed upgrade on the current Node. Throw an
	 * IllegalArgumentException if the current Node doesn't have a speed
	 * upgrade.
	 */
	@Override
	public void grabSpeedUpgrade() {
		if (!position.hasSpeedUpgrade()) {
			throw new IllegalStateException(
					"grabSpeedUpgrade: Error, no speed upgrade on this tile");
		}
		speed += DELTA_SPEED;
		driver.setSpeed(speed);
		driver.grabSpeedUpgrade(position);
		position.setSpeedUpgrade(false);
	}

	/** Return the current speed of the ship. */
	@Override
	public double getSpeed() {
		return speed;
	}

	/** Return the solution's current score. */
	public double getScore() {
		return timeElapsed;
	}

	/**
	 * Return a RescueStageProxy which is this GameState, but can only access
	 * methods in RescueStage.
	 */
	private RescueStage rescueStage() {
		return new RescueStageProxy(this);
	}

	/**
	 * Return a ReturnStageProxy which is this GameState, but can only access
	 * methods in ReturnStage.
	 */
	private ReturnStageProxy returnStage() {
		return new ReturnStageProxy(this);
	}

	/** Terminate this game instance. */
	public void terminate() {
		executor.shutdownNow();
		terminated= true;
	}

	/**
	 * Proxy class, which purely implements RescueStage and has no access to any
	 * other methods.
	 */
	private class RescueStageProxy implements RescueStage {
		private GameState gameState;

		private RescueStageProxy(GameState gs) {
			gameState= gs;
		}

		@Override
		public long currentLocation() {
			return gameState.currentLocation();
		}

		@Override
		public double getPing() {
			return gameState.getPing();
		}

		@Override
		public Collection<NodeStatus> neighbors() {
			return gameState.neighbors();
		}

		@Override
		public boolean foundSpaceship() {
			return gameState.foundSpaceship();
		}

		@Override
		public void moveTo(long id) {
			gameState.moveTo(id);
		}
	}

	/**
	 * Proxy class which purely implements ReturnStage and has no access to any
	 * other methods.
	 */
	private class ReturnStageProxy implements ReturnStage {
		private GameState gameState;

		private ReturnStageProxy(GameState gs) {
			gameState= gs;
		}

		@Override
		public Node currentNode() {
			return gameState.currentNode();
		}

		@Override
		public Node getEarth() {
			return gameState.getEarth();
		}

		@Override
		public Collection<Node> allNodes() {
			return gameState.allNodes();
		}

		@Override
		public void moveTo(Node n) {
			gameState.moveTo(n);
		}

		@Override
		public void grabSpeedUpgrade() {
			gameState.grabSpeedUpgrade();
		}

		@Override
		public double getSpeed() {
			return gameState.getSpeed();
		}
	}
}