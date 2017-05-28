package models;

import java.util.Collection;

/**
 * In the return stage, you must safely return to Earth as quickly as possible.
 * The rescued spaceship has information on the entire galaxy--- you now have
 * access to the entire graph of planets, some of which are hostile and others
 * which have speed upgrades that allow your ship to travel faster.
 * 
 * An instance provides all the necessary methods to move through the galaxy,
 * collect speed upgrades, and reach Earth.
 */
public interface ReturnStage {

	/** Return the Node corresponding to your current location in the graph. */
	public Node currentNode();

	/**
	 * Return the Node associated with your home planet, Earth. You have to move
	 * to this Node in order to get out.
	 */
	public Node getEarth();

	/**
	 * Return a collection containing all the nodes in the graph. They are in no
	 * particular order.
	 */
	public Collection<Node> allNodes();

	/**
	 * Change your location to n.
	 * 
	 * @throws IllegalArgumentException
	 *             if n is not adjacent to your location.
	 */
	public void moveTo(Node n);

	/**
	 * Pick up the speed upgrade on the current tile.
	 * 
	 * @throws IllegalStateException
	 *             if there is no upgrade at the current location, either
	 *             because there never was one or because it was already picked
	 *             up.
	 */
	public void grabSpeedUpgrade();

	/**
	 * Return the current speed of your ship; the higher the speed, the faster
	 * you move.
	 */
	public double getSpeed();
}