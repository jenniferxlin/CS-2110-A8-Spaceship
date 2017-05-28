package models;

import java.util.HashSet;
import java.util.Set;
import java.util.Random;
import java.util.Iterator;
import java.util.Collections;

/**
 * A Board represents the physical layout of a game. It has nodes, edges, and a
 * reference to the game state that it's attached to. Boards are randomly
 * generated from a seed.
 */
public final class Board {

	public final long seed; // The random seed from which this Board was
							// generated

	private Node earth; // The node at which the spaceship starts

	private Node target; // The node that rescue is looking for

	private double furthestNodeDistance; // The distance of the node furthest
											// from target

	private HashSet<Edge> edges; // All edges in this board

	protected int minLength; // Min length among all edges

	protected int maxLength; // Max length among all edges

	private HashSet<Node> nodes; // All nodes in this board

	private int width; // width and height of this GUI
	private int height;

	/**
	 * Return the volume of a ping from the crashed spaceship's distress beacon
	 * to node n. This is inversely correlated with the distance between n and
	 * the target planet
	 * 
	 * The returned value d satisfies 0 <= d <= 1. If d = 1, n is the target
	 * node. If d = 0, n is the node furthest from the target node.
	 */
	public double getPing(Node n) {
		return 1.0 - absoluteDistanceToTarget(n) / furthestNodeDistance;
	}

	/** Return the absolute distance between n1 and n2. */
	static double distanceBetween(Node n1, Node n2) {
		return Math.sqrt(((n1.getX() - n2.getX()) * (n1.getX() - n2.getX()))
				+ ((n1.getY() - n2.getY()) * (n1.getY() - n2.getY())));
	}

	/** Return the absolute distance from n to the target. */
	double absoluteDistanceToTarget(Node n) {
		return distanceBetween(n, target);
	}

	/** Return an immutable Set containing all the Nodes in this board. */
	public Set<Node> getNodes() {
		return Collections.unmodifiableSet(nodes);
	}

	/** Return the HashSet of all Nodes in this board */
	HashSet<Node> getMutableNodes() {
		return nodes;
	}

	/** Return the number of Nodes in this board. */
	public int getNodesSize() {
		return nodes.size();
	}

	/**
	 * Return the Node with ID id in this board if it exists, null otherwise.
	 */
	public Node getNode(long id) {
		for (Node n : nodes) {
			if (n.getId() == id)
				return n;
		}

		return null;
	}

	/** Return the starting Earth Node. */
	public Node getEarth() {
		return earth;
	}

	/** Return the unique target Node for which the spaceship is searching. */
	public Node getTarget() {
		return target;
	}

	/**
	 * Make n the Earth Node --i.e. the starting Node and the Node to which the
	 * ship must return in the second phase.
	 * 
	 * @throws IllegalArgumentException
	 *             if n is not in this board
	 */
	void setEarth(Node n) throws IllegalArgumentException {
		if (!nodes.contains(n))
			throw new IllegalArgumentException("Can't find Earth!");
		earth= n;
	}

	/**
	 * Make n the target Node --the nod the ship must reach in first phase.
	 * 
	 * @throws IllegalArgumentException
	 *             if n is not in this board
	 */
	void setTarget(Node n) throws IllegalArgumentException {
		if (!nodes.contains(n))
			throw new IllegalArgumentException("Can't find target!");
		target= n;
	}

	/** Return the immutable Set of Edges in this board. */
	public Set<Edge> getEdges() {
		return Collections.unmodifiableSet(edges);
	}

	/** Return the set of Edges in this board. */
	HashSet<Edge> getMutableEdges() {
		return edges;
	}

	/** Return the number of Edges in this board. */
	public int getEdgesSize() {
		return edges.size();
	}

	/** Return the maximum length of all edges on the board. */
	public int getMaxLength() {
		return maxLength;
	}

	/** Return the minimum length of all edges on the board. */
	public int getMinLength() {
		return minLength;
	}

	/** Return furthest node distance. */
	public double getFurthestNodeDistance() {
		return furthestNodeDistance;
	}

	/** Set furthest node distance to d. */
	public void setFurthestNodeDistance(double d) {
		furthestNodeDistance= d;
	}

	/** Return width of the GUI. */
	public int getWidth() {
		return width;
	}

	/** Set width of the GUI to w. */
	public void setWidth(int w) {
		width= w;
	}

	/** Return height of the GUI */
	public int getHeight() {
		return height;
	}

	/** Set height of the GUI to h */
	public void setHeight(int h) {
		height= h;
	}

	/**
	 * Return a String representation of this board, including edges and nodes.
	 */
	@Override
	public String toString() {
		String output= "";
		Iterator<Node> nodesIterator= nodes.iterator();
		while (nodesIterator.hasNext()) {
			Node n= nodesIterator.next();
			output += n + "\t";
			Iterator<Edge> edgesIterator= n.getExits().iterator();
			while (edgesIterator.hasNext()) {
				Edge e= edgesIterator.next();
				output += e.getOther(n).name + "-" + e.length;
				if (edgesIterator.hasNext())
					output += "\t";
			}
			if (nodesIterator.hasNext())
				output += "\n";
		}
		return output;
	}

	/** Return a new random board for g seeded with seed s. */
	public static Board randomBoard(long s) {
		return new Board(new Random(s), s);
	}

	/** Return a new random board for g seeded with seed s and Random r */
	private Board(Random r, long s) {
		seed= s;

		nodes= new HashSet<Node>();
		edges= new HashSet<Edge>();

		BoardGeneration.gen(this, r);
	}

}