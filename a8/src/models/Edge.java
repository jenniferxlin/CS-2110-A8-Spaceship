package models;

import java.awt.geom.Line2D;

/**
 * Class Edge in SpaceShipRescue allows creation of the connections between
 * Nodes along which the Spaceship can travel. Each Edge is bidirectional and is
 * connected to exactly two Nodes. Functions getFirstExit() and getSecondExit()
 * allows access to these Nodes. Each Edge is weighted (has a length), which is
 * the amount of time (in arbitrary units) it takes for a Spaceship to cross
 * this Edge. One useful method is getOther(Node), which returns the other exit
 * of the Edge given either exit. <br>
 * <br>
 * Class Edge implements BoardElement, indicating that it is a component of the
 * Board state and is represented graphically via the app's GUI.
 */
public final class Edge implements BoardElement {

	/* Various static length references */
	public static final int DEFAULT_MIN_LENGTH= Integer.MAX_VALUE;
	public static final int DEFAULT_MAX_LENGTH= 0;
	public static final int DUMMY_LENGTH= Integer.MIN_VALUE;

	private Node[] exits; // The TWO Nodes to which this edge connects. */

	/*
	 * The length (weight) of this Edge. Uncorrelated with its graphical length
	 * on the GUI
	 */
	public final int length;

	private final Board board; // The board to which this Edge belongs.

	/**
	 * Constructor: an Edge on m with end nodes in exits and length
	 * lengthBtwnPlanets, which must be positive and non-zero.
	 *
	 * @throws IllegalArgumentException:
	 *             if exits is null or has length not equal to 2 if
	 *             lengthBtwnPlanets is less than 1 and not equal to
	 *             Edge.DUMMY_LENGTH if either of the nodes are null if exits[0]
	 *             and exits[1] are the same node
	 */
	Edge(Board m, Node exits[], int lengthBtwnPlanets) throws IllegalArgumentException {
		this(m, exits[0], exits[1], lengthBtwnPlanets);

		if (exits.length != 2)
			throw new IllegalArgumentException(
					"Incorrectly sized Node array passed into Edge constructor");
	}

	/**
	 * Constructor: An edge with end nodes firstExit and secondExit and length
	 * lengthBtwnPlanets, which must be positive and non-zero.
	 *
	 * @throws IllegalArgumentException:
	 *             if lengthBtwnPlanets is less than 1 and not equal to
	 *             Edge.DUMMY_LENGTH; if either of the nodes are null; if
	 *             firstExit and secondExit are the same node
	 */
	Edge(Board m, Node firstExit, Node secondExit, int lengthBtwnPlanets) 
			throws IllegalArgumentException {
		Node[] e= new Node[2];
		e[0]= firstExit;
		e[1]= secondExit;
		setExits(e);

		board= m;

		if (lengthBtwnPlanets <= 0 && lengthBtwnPlanets != Edge.DUMMY_LENGTH)
			throw new IllegalArgumentException(
					"lengthBtwnPlanets value " + lengthBtwnPlanets + " is an illegal value.");

		length= lengthBtwnPlanets;
	}

	/**
	 * Return the exits of this line, a length 2 array. Order of nodes in
	 * returned array has no significance.
	 */
	private Node[] getTrueExits() {
		return exits;
	}

	/**
	 * Return the first exit of this Edge. Which node is the first exit has no
	 * significance.
	 */
	public Node getFirstExit() {
		return exits[0];
	}

	/**
	 * Return the second exit of this Edge. Which node is the second exit has no
	 * significance.
	 */
	public Node getSecondExit() {
		return exits[1];
	}

	/**
	 * Return a copy of the exits of this line, a new length-2 array of Nodes.
	 * Copies the nodes into a new array to prevent interference with the exits
	 * of this node. (Setting the values of the return of this method will not
	 * alter the Edge object.)
	 */
	public Node[] getExits() {
		Node[] newExits= new Node[2];
		newExits[0]= exits[0];
		newExits[1]= exits[1];
		return newExits;
	}

	/**
	 * Set the exists of this edge to newExits. Used only in edge construction.
	 *
	 * @throws IllegalArgumentException:
	 *             if exits is null or has length not equal to 2; if either of
	 *             the nodes are null; if exits[0] and exits[1] are the same
	 *             node
	 **/
	private void setExits(Node[] newExits) throws IllegalArgumentException {
		if (newExits == null)
			throw new IllegalArgumentException(
					"Null Node array passed into Edge constructor");
		if (newExits.length != 2)
			throw new IllegalArgumentException(
					"Incorrectly sized Node array passed into Edge constructor");
		if (newExits[0] == null)
			throw new IllegalArgumentException(
					"First Node passed into Edge constructor is null");
		if (newExits[1] == null)
			throw new IllegalArgumentException(
					"Second Node passed into Edge constructor is null");
		if (newExits[0].equals(newExits))
			throw new IllegalArgumentException(
					"Two Nodes passed into Edge constructor refer to the same node");
		exits= newExits;
	}

	/** Return true iff node is one of the exits of this Edge. */
	public boolean isExit(Node node) {
		return exits[0].equals(node) || exits[1].equals(node);
	}

	/**
	 * Return shared exit between this and Edge e (null if they don't share an
	 * exit).
	 */
	public Node sharedExit(Edge e) {
		if (exits[0].equals(e.getTrueExits()[0]) || exits[0].equals(e.getTrueExits()[1]))
			return exits[0];
		if (exits[1].equals(e.getTrueExits()[0]) || exits[1].equals(e.getTrueExits()[1]))
			return exits[1];
		return null;
	}

	/** Return true iff this Edge and e share an exit. */
	public boolean sharesExit(Edge e) {
		return sharedExit(e) != null;
	}

	/**
	 * Return the other exit that is not equal to n. (Return null if n is
	 * neither of the nodes in exits.)
	 */
	public Node getOther(Node n) throws IllegalArgumentException {
		if (exits[0].equals(n))
			return exits[1];
		if (exits[1].equals(n))
			return exits[0];

		throw new IllegalArgumentException("This edge does not have Node n");
	}

	/**
	 * Return true iff this edge and e are equal. Two Edges are equal if they
	 * have the same exits, even if they have different lengths. This ensures
	 * that only one edge connects each pair of nodes in duplicate-free
	 * collections.
	 */
	@Override
	public boolean equals(Object e) {
		if (e == null)
			return false;
		if (!(e instanceof Edge))
			return false;
		Edge e1= (Edge) e;
		Node[] exist1= e1.getTrueExits();
		return (exits[0].equals(exist1[1]) && exits[1].equals(exist1[0]))
				|| (exits[0].equals(exist1[0]) && exits[1].equals(exist1[1]));
	}

	/**
	 * Return the hash code for this edge. The hashCode is equal to the sum of
	 * the hashCodes of its first and second exit.
	 * {@code getFirstExit().hashCode() + getSecondExit().hashCode()}. Notably:
	 * This means the ordering of the exits for an edge doesn't matter for
	 * hashing
	 */
	@Override
	public int hashCode() {
		return exits[0].hashCode() + exits[1].hashCode();
	}

	/**
	 * Return a String representation of this edge:
	 * {@code getFirstExit().name + " to " + getSecondExit().name  *
	 /
	@Override
	public String toString() {
		return exits[0].name + " to " + exits[1].name;
	}

	/** Return a String to print when this object is drawn on a GUI */
	public String getMappedName() {
		return "" + length;
	}

	/** Return the x location the boarded name of this Edge. */
	public int getX() {
		int x1= getExits()[0].getX();
		int x2= getExits()[1].getX();
		return (int) (((double) (x1 + x2)) / 2);
	}

	/** Return the y location the boarded name of this Edge. */
	public int getY() {
		int y1= getExits()[0].getY();
		int y2= getExits()[1].getY();
		return (int) (((double) (y1 + y2)) / 2);
	}

	/** Return the Board to which this Edge belongs. */
	public Board getBoard() {
		return board;
	}

	// MARK - only for BoardGeneration purposes

	/**
	 * Return the angle between this edge and Edge e. Throws
	 * IllegalArgumentException if the involved edges don't share an endpoint
	 * Node.
	 */
	double radAngle(Edge e) throws IllegalArgumentException {
		Node commonEndpoint= sharedExit(e);

		if (commonEndpoint == null) {
			throw new IllegalArgumentException(
					"Can't measure angle between " + this + " and " + e 
					+ " because they don't share an endpoint");
		}

		Node otherPoint1= getOther(commonEndpoint);
		Node otherPoint2= e.getOther(commonEndpoint);

		Vector v= new Vector(otherPoint1.getX() - commonEndpoint.getX(),
				otherPoint1.getY() - commonEndpoint.getY());

		Vector v2= new Vector(otherPoint2.getX() - commonEndpoint.getY(),
				otherPoint2.getX() - commonEndpoint.getY());

		return Vector.radAngle(v, v2);
	}

	/**
	 * Return true iff e intersects this edge. (Return false if they share an
	 * endpoint.)
	 */
	boolean intersects(Edge e) {
		return !sharesExit(e) && Line2D.linesIntersect(
				getExits()[0].getX(), getExits()[0].getY(), 
				getExits()[1].getX(), getExits()[1].getY(),
				e.getExits()[0].getX(), e.getExits()[0].getY(),
				e.getExits()[1].getX(), e.getExits()[1].getY());
	}
}