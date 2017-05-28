package models;

/**
 * An instance contains the methods that must be implemented in order to solve
 * the game.
 */
public abstract class Spaceship {

	/**
	 * Explore space, trying to find the crashed ship (on Planet X) in as few
	 * steps as possible. Once you find Planet X, you must return from rescue()
	 * in order to complete the first phase of your mission. If you continue to
	 * move after finding the spaceship rather than returning, it will not
	 * count. If you return from this function while not on Planet X, it will
	 * count as a failure.
	 * <p>
	 * There is no limit to how many steps you can take, but your score is
	 * directly related to how long it takes you to find Planet X.
	 * <p>
	 * At every step, you know only your current planet's ID and the IDs of
	 * neighboring planets, as well as the strength of the ping from Planet X at
	 * each planet.
	 * <p>
	 * In order to get information about the current state, use functions
	 * getCurrentLocation(), getNeighbors(), and getPing() in the rescueStage.
	 * You know you are on Planet X when getPing() is 1.0.
	 * <p>
	 * Use function moveTo(long id) in the rescueStage to move to a neighboring
	 * planet by its ID. Doing this will change state to reflect your new
	 * position.
	 *
	 * @param state
	 *            an interface to view and manipulate the current state
	 */
	public abstract void rescue(RescueStage state);

	/**
	 * Get back to Earth safely. The faster your method, the better.
	 * <p>
	 * You now have access to the entire underlying graph, which can be accessed
	 * through ReturnStage. currentNode() and getEarth() return Node objects of
	 * interest, and getNodes() returns a collection of all nodes on the graph.
	 * <p>
	 * Note: time is measured as a function of distance and speed. You can
	 * grabSpeedUpgrade() to pick up a speed upgrade on your current planet
	 * (this will fail if no speed upgrade exists) and increase your speed by
	 * one stage. Use moveTo() to move to a destination node adjacent to your
	 * current node.
	 * <p>
	 * Some planets are hostile, and the path your spaceship takes can pass
	 * through only 2 hostile nodes on the way back to Earth. (i.e. you can
	 * moveTo() at most 2 hostile nodes). Returning with a path that contains 3
	 * or more hostile nodes will be considered a failed run. In addition,
	 * hitting a hostile Node will reduce your speed by one stage.
	 * <p>
	 * You must return from this function while your spaceship is parked on
	 * Earth. Returning from the wrong location will be considered a failed run.
	 *
	 * @param state
	 *            an interface to view and manipulate the current state
	 */
	public abstract void returnToEarth(ReturnStage state);
}