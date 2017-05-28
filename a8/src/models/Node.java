package models;

import java.util.*;

/**
 * A Node (vertex) on the board of the game. Each Node represents a planet and
 * maintains: (1) a set of edges that exit it, (2) its hostility (only to be
 * concerned about in phase 2), and (3) any upgrades that increase your
 * spaceship's speed (phase 2 only). <br>
 * <br>
 * All methods that modify these collections are package protected, but all
 * getters are public for use by the user. Additionally, convenience methods
 * such as isConnectedTo(Node n) are provided for user use.
 */
public class Node implements BoardElement {

	/* The name of this node. Not final so we can denote Planet X */
	public String name;

	private final long id; // Unique identifier for this planet

	private final Board board; // The board in which this Node is contained

	private int x; // x, y coordinates of this node. Independent project space
	private int y;

	private boolean hostile; // true iff this planet is hostile

	private boolean speedUp; // true iff this planet currently has a speed
								// upgrade

	private HashSet<Edge> exits; // Edges leaving this Node

	/**
	 * Constructor: a Node named name on Board m with ID id with no edges
	 * leaving it
	 */
	Node(Board m, String name, long id) {
		this(m, name, null, id, false, false);
	}

	/**
	 * Constructor: a Node named name on Board m with ID id with no edges
	 * leaving it.
	 * 
	 * @param h
	 *            - true iff this planet is hostile
	 * @param s
	 *            - true iff this planet has a speed upgrade
	 */
	Node(Board m, String name, long id, boolean h, boolean s) {
		this(m, name, null, id, h, s);
	}

	/**
	 * Constructor: a Node named name on Board m with ID id with leaving edges
	 * exits
	 * 
	 * @param h
	 *            - true iff this planet is hostile
	 * @param s
	 *            - true iff this planet has a speed upgrade
	 */
	Node(Board m, String name, HashSet<Edge> exits, long id, boolean h, boolean s) {
		this.board= m;
		this.name= name;
		this.id= id;
		this.hostile= h;
		this.speedUp= s;
		this.exits= exits == null ? new HashSet<Edge>() : exits;
	}

	/** Return the set of edges leaving this node. */
	HashSet<Edge> getMutableExits() {
		return exits;
	}

	/** Return an immutable set of edges leaving this node. */
	public Set<Edge> getExits() {
		return Collections.unmodifiableSet(exits);
	}

	/** Return this Node's ID */
	public long getId() {
		return id;
	}

	/**
	 * Return a map of neighboring nodes to the lengths of the edges connecting
	 * them to this Node. To iterate over a HashMap, use HashMap.entrySet().
	 */
	public HashMap<Node, Integer> getNeighbors() {
		HashMap<Node, Integer> neighbors= new HashMap<Node, Integer>();
		for (Edge e : exits) {
			neighbors.put(e.getOther(this), e.length);
		}
		return neighbors;
	}

	/** Add e to this Node's set of exits */
	void addExit(Edge e) {
		exits.add(e);
	}

	/** Remove e from this Node's set of exits */
	void removeExit(Edge e) {
		exits.remove(e);
	}

	/** Return the number of exits from this node. */
	public int getExitsSize() {
		return exits.size();
	}

	/** Return true iff r is connected to this Node. */
	public boolean isExit(Edge r) {
		return exits.contains(r);
	}

	/**
	 * Return false if other.equals(this). Otherwise, return true iff one of the
	 * edges in exits leads to Node other, (this is connected to other via a
	 * single edge).
	 */
	public boolean isConnectedTo(Node other) {
		if (other.equals(this))
			return false;

		for (Edge r : exits) {
			if (r.isExit(other)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return the edge that this node shares with node n (null if not
	 * connected).
	 */
	public Edge getConnect(Node n) {
		for (Edge r : exits) {
			if (r.getOther(this).equals(n)) {
				return r;
			}
		}
		return null;
	}

	/** Return true iff this Planet */
	public boolean isHostile() {
		return hostile;
	}

	/** Set the hostile field for this node to h */
	void setHostile(boolean h) {
		hostile= h;
	}

	/** Return true if this Planet has a speed upgrade */
	public boolean hasSpeedUpgrade() {
		return speedUp;
	}

	/** Set speed upgrade on this Node to s */
	void setSpeedUpgrade(boolean s) {
		speedUp= s;
	}

	/**
	 * Return true iff n is a Node and is equal to this one. Two Nodes are equal
	 * if they have the same name - guaranteed to be unique within the context
	 * of a single game
	 */
	@Override
	public boolean equals(Object n) {
		if (n == null)
			return false;
		if (!(n instanceof Node))
			return false;
		return name.equals(((Node) n).name);
	}

	/**
	 * Return the hashCode of this node. Its hashCode is equal to the hashCode
	 * of its name. This is guaranteed to be unique within the context of a
	 * single game.
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	/** Return the name and coordinates of this Node. */
	@Override
	public String toString() {
		return String.format("%s: (%s, %s)", name, x, y);
	}

	/** Return the string that is mapped when this Node is drawn. */
	public String getMappedName() {
		return name;
	}

	/** Return the x location of this Node. */
	public int getX() {
		return x;
	}

	/** Set x location of this Node to x. */
	void setX(int x) {
		this.x= x;
	}

	/** Return the y location that this Node. */
	public int getY() {
		return y;
	}

	/** Set y location of this Node to y. */
	void setY(int y) {
		this.y= y;
	}

	/** Return the board on which this Node belongs. */
	public Board getBoard() {
		return board;
	}

	/** Get distance from this Node to another Node n. */
	public double getDistance(Node n) {
		return Math.sqrt((Math.pow(x - n.getX(), 2)) + (Math.pow(y - n.getY(), 2)));
	}

	/**
	 * Return the vector from the center of this node to the center of node n.
	 */
	Vector getVectorTo(Node n) {
		return new Vector(n.x - x, n.y - y);
	}
}