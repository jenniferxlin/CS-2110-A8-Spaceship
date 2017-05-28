package models;

import utils.Constants;
import utils.PathFunctions;

import java.io.*;
import java.util.*;

/**
 * Library for random board generation. <br>
 * <br>
 * Node placement and Edge connections are done using the Delaunay Triangulation
 * Method: http://en.wikipedia.org/wiki/Delaunay_triangulation
 *
 */
public class BoardGeneration {
	/* Targeted/limiting parameters */
	private static final int MIN_NODES= 5;
	private static final int MAX_NODES= 100;
	private static final double AVERAGE_DEGREE= 2.5;

	/* Spatial information */
	private static final int WIDTH= 1600;
	private static final int HEIGHT= 1600;
	private static final int NODE_DEFAULT_DIAMETER= 25;
	private static final int NODE_BUFFER_RADIUS= NODE_DEFAULT_DIAMETER * 5;
	private static final int BUFFER= (int) (NODE_DEFAULT_DIAMETER * 2.5);

	/* The likelihood of each Node being hostile */
	private static final double DESIRED_HOSTILE= 0.30;

	/* The likelihood of each Node having a speed upgrade */
	private static final double DESIRED_UPGRADES= 0.20;

	/*
	 * Maximum amount of times the coordinates of a Node will be fixed to avoid
	 * having Nodes too close together.
	 */
	private static final int NUM_RETRIES= 100000;

	/* Sentinel value to mark a coordinate as invalid. */
	private static final int BAD_COORDINATE= Integer.MIN_VALUE;

	/**
	 * Generate a full set of random elements for b, using r for all random
	 * decisions.
	 *
	 * @param b
	 *            - a blank board to put stuff on.
	 * @param random
	 *            - used for all random decisions.
	 */
	public static void gen(Board b, Random random) {

		// Set dimensions
		b.setWidth(WIDTH + 2 * BUFFER);
		b.setHeight(HEIGHT + 2 * BUFFER);

		// Random number of planets
		final int numPlanets= random.nextInt(MAX_NODES - MIN_NODES + 1) + MIN_NODES;

		// Grab the list of planet names to be used in generating the planets
		ArrayList<String> planets= planetNames();

		// Create nodes and add to board them to board
		for (int i= 0; i < numPlanets; i++) {
			String name= i == 0 ? Constants.EARTH_NAME : planets.remove(random.nextInt(planets.size()));
			addNode(b, random, name, i);
		}

		spiderwebEdges(b, random);

		// Generate the location of the missing spaceship.
		Node posTarget= randomElement(b.getMutableNodes(), random);
		while (posTarget == b.getEarth()) {
			posTarget= randomElement(b.getMutableNodes(), random);
		}
		b.setTarget(posTarget);
		posTarget.name= Constants.CRASHED_PLANET_NAME;
		if (posTarget.isHostile())
			posTarget.setHostile(false);

		// Make sure that they can get home with < 3 hostiles.
		guaranteeSafePathHome(b, random);

		// Ensure there are no speed upgrades on longest path.
		List<Node> shortestPath= PathFunctions.shortestPath(b.getTarget(), b.getEarth());
		for (Node n : shortestPath) {
			n.setSpeedUpgrade(false);
		}

		// Set the node furthest from target, for getPing
		double maxDistance= 0;
		for (Node n : b.getMutableNodes()) {
			double nodeDistance= b.absoluteDistanceToTarget(n);
			if (nodeDistance > maxDistance) {
				maxDistance= nodeDistance;
			}
		}
		b.setFurthestNodeDistance(maxDistance);
	}

	/**
	 * Ensure a path from the target to Earth with < 3 hostile nodes. If no path
	 * at all exists from target to Earth, re-generate the board. If no path
	 * with < 3 hostile nodes exists, create a path with < 3 hostile nodes.
	 * 
	 * @param b
	 *            the Board to modify
	 * @param random
	 *            used to re-generate b if needed
	 */
	private static void guaranteeSafePathHome(Board b, Random random) {
		Node start= b.getTarget();
		Node end= b.getEarth();

		LinkedList<Node> path= (LinkedList<Node>) PathFunctions.longestPath(start, end);

		if (path == null) {
			// Didn't find any path to the end...
			b.getMutableNodes().clear();
			b.getMutableEdges().clear();
			gen(b, random);
			return;
		}

		// Store number of hostile nodes in hostiles
		int hostiles= 0;
		for (Node n : path) {
			if (n.isHostile()) {
				hostiles++;
			}
		}

		if (hostiles >= 3) {
			// No path to end which had < 3 hostiles.
			// Fix the number of hostiles
			for (Node n : path) {
				if (n.isHostile() && hostiles >= 3) {
					n.setHostile(false);
					hostiles--;
				}
			}
			return;
		}
	}

	/**
	 * Add a Node to Board b with name name and ID id, using Random random to
	 * determine its coordinates. Return the added Node.
	 */
	private static Node addNode(Board b, Random random, String name, long id) {
		// Create node + prepare it to have a random location
		Node n= new Node(b, name, id);

		n.setX(BAD_COORDINATE);
		n.setY(BAD_COORDINATE);

		// Set the Node's hostility.
		if (random.nextDouble() <= DESIRED_HOSTILE && !name.equals(Constants.EARTH_NAME)) {
			n.setHostile(true);
		} else {
			n.setHostile(false);
		}

		// Set whether the node has a speed upgrade.
		if (random.nextDouble() <= DESIRED_UPGRADES) {
			n.setSpeedUpgrade(true);
		} else {
			n.setSpeedUpgrade(false);
		}

		// Randomly place the node
		int rt= 0;
		while (n.getX() == BAD_COORDINATE || n.getY() == BAD_COORDINATE) {
			// Increment so we don't potentially loop forever
			rt++;

			// Set coordinates
			n.setX(random.nextInt(WIDTH + 1) + BUFFER);
			n.setY(random.nextInt(HEIGHT + 1) + BUFFER);

			// Check other existing nodes. If too close, re-randomize this
			// node's location
			if (rt <= NUM_RETRIES) {
				for (Node n2 : b.getMutableNodes()) {
					if (n2.getDistance(n) < NODE_BUFFER_RADIUS) {
						n.setX(BAD_COORDINATE);
						n.setY(BAD_COORDINATE);
						break;
					}
				}
			}
		}

		// Add / set fields accordingly
		b.getMutableNodes().add(n);
		if (name.equals(Constants.EARTH_NAME)) {
			b.setEarth(n);
		}

		return n;
	}

	/**
	 * Create an edge with a random length that connects n1 and n2 and add to
	 * the correct collections. Return the created edge.
	 */
	private static Edge addEdge(Board b, Random random, Node n1, Node n2) {
		Edge e= new Edge(b, n1, n2, (int) Board.distanceBetween(n1, n2));
		b.getMutableEdges().add(e);
		n1.addExit(e);
		n2.addExit(e);
		return e;
	}

	/* The maximum number of attempts to get to average node degree */
	private static int MAX_EDGE_ITERATIONS= 1000;

	/**
	 * Create a spiderweb of edges by creating concentric hulls, then connecting
	 * between the hulls. Create a connected, planar graph.
	 */
	private static void spiderwebEdges(Board b, Random r) {
		HashSet<Node> nodes= new HashSet<Node>();
		nodes.addAll(b.getMutableNodes());
		ArrayList<HashSet<Node>> hulls= new ArrayList<HashSet<Node>>();

		// Create hulls, add edges
		while (!nodes.isEmpty()) {
			HashSet<Node> nds= addGiftWrapEdges(b, r, nodes);
			hulls.add(nds);
			for (Node n : nds) {
				nodes.remove(n);
			}
		}
		// At this point, there are either 2*n or 2*n-1 edges, depending
		// if the inner most hull had a polygon in it or not.

		// Connect layers w/ random edges - try to connect each node to its
		// closest on the surrounding hull
		// Guarantee that the map is connected after this step
		for (int i= 0; i < hulls.size() - 1; i++) {
			for (Node n : hulls.get(i + 1)) {
				Node c= Collections.min(hulls.get(i), new DistanceComparator(n));
				if (!lineCrosses(b, n, c)) {
					addEdge(b, r, n, c);
				}
			}
		}

		// Create a hashmap of node -> hull the node is in within hulls.
		HashMap<Node, Integer> hullMap= new HashMap<Node, Integer>();
		for (int i= 0; i < hulls.size(); i++) {
			for (Node n : hulls.get(i)) {
				hullMap.put(n, i);
			}
		}
		final int maxHull= hulls.size() - 1;

		// If the innermost hull has size 1 or 2, add edges to guarantee that
		// every node
		// has degree at least 2
		HashSet<Node> lastHull= hulls.get(hulls.size() - 1);
		if (lastHull.size() < 3) {
			HashSet<Node> penultimateHull= hulls.get(hulls.size() - 2); // Exists.
																			// Just
																			// cause.
			int e= 1;
			if (lastHull.size() == 1)
				e= 2;
			for (Node n : lastHull) {
				if (n.getExitsSize() < 2) {
					int i= 0;
					while (i < e) {
						Node n2= randomElement(penultimateHull, r);
						if (!lineCrosses(b, n, n2) && !n.isConnectedTo(n2)) {
							addEdge(b, r, n, n2);
							i++;
						}
					}
				}
			}
		}

		int iterations= 0;

		while (b.getMutableEdges().size() < b.getMutableNodes().size() * AVERAGE_DEGREE
				&& iterations < MAX_EDGE_ITERATIONS) {
			// Get random node
			Node n= randomElement(b.getMutableNodes(), r);
			int hull= hullMap.get(n);
			// Try to connect to a node on the hull beyond this one.
			if (hull < maxHull) {
				for (Node c : hulls.get(hull + 1)) {
					if (!lineCrosses(b, n, c) && !n.isConnectedTo(c)) {
						addEdge(b, r, n, c);
						break;
					}
				}
			}
			// Try to connect to a node on the hull outside this one
			if (hull > 0) {
				for (Node c : hulls.get(hull - 1)) {
					if (!lineCrosses(b, n, c) && !n.isConnectedTo(c)) {
						addEdge(b, r, n, c);
						break;
					}
				}
			}
			iterations++;
		}

		// Fix triangulation such that it's cleaner.
		delaunayTriangulate(b, r);
	}

	/**
	 * Gift-wrap the nodes - create a concentric set of edges that surrounds set
	 * nodes, with random edge lengths. Return a set of nodes that is the nodes
	 * involved in the gift-wrapping.
	 */
	private static HashSet<Node> addGiftWrapEdges(Board b, Random r, HashSet<Node> nodes) {
		HashSet<Node> addedNodes= new HashSet<Node>();
		// Base case - 0 or 1 node. Nothing to do.
		if (nodes.size() <= 1) {
			addedNodes.add(nodes.iterator().next());
			return addedNodes;
		}

		// Base case - 2 nodes. Add the one edge connecting them and return.
		if (nodes.size() == 2) {
			Iterator<Node> n= nodes.iterator();
			Node n1= n.next();
			Node n2= n.next();
			addEdge(b, r, n1, n2);
			addedNodes.add(n1);
			addedNodes.add(n2);
			return addedNodes;
		}

		// Non base case - do actual gift wrapping alg
		Node first= Collections.min(nodes, xComp);
		Node lastHull= first;
		Node endpoint= null;
		do {
			for (Node n : nodes) {
				if (endpoint == null || n != lastHull 
						&& isLeftOfLine(lastHull, endpoint, n) 
						&& !lastHull.isConnectedTo(n)) {
					endpoint= n;
				}
			}

			addEdge(b, r, lastHull, endpoint);
			addedNodes.add(lastHull);

			lastHull= endpoint;
		} while (lastHull != first);

		return addedNodes;
	}

	/**
	 * Return true iff e2 is left of the line start -> e1. Helper for
	 * giftwrapping method
	 */
	private static boolean isLeftOfLine(Node start, Node e1, Node e2) {
		Vector a= start.getVectorTo(e1);
		Vector b= start.getVectorTo(e2);
		return Vector.cross(a, b) <= 0;
	}

	/**
	 * Return true iff the line that would be formed by connecting the two given
	 * nodes crosses an existing edge. Helper for gift-wrapping and
	 * spider-webbing methods.
	 */
	private static boolean lineCrosses(Board b, Node n1, Node n2) {
		Edge e= new Edge(b, n1, n2, Edge.DUMMY_LENGTH);
		for (Edge e1 : b.getMutableEdges()) {
			if (e1.intersects(e))
				return true;
		}
		return false;
	}

	/**
	 * Fix (pseudo) triangulation via the delaunay method. Alter the current
	 * edge set so that triangles are less skinny.
	 */
	private static void delaunayTriangulate(Board b, Random r) {

		// Amount of radians that angle sum necessitates switch
		final double FLIP_CONDITION= Math.PI;

		// Edge that should be removed, mapped to its new exits
		HashMap<Edge, Node[]> needsFlip= new HashMap<Edge, Node[]>();

		for (Node n1 : b.getMutableNodes()) {
			for (Edge e2 : n1.getMutableExits()) {
				Node n2= e2.getOther(n1);
				if (n2 != n1) {
					for (Edge e3 : n1.getMutableExits()) {
						Node n3= e3.getOther(n1);
						if (n3 != n2 && n3 != n1) {
							for (Edge e4 : n1.getMutableExits()) {
								Node n4= e4.getOther(n1);
								if (n4 != n3 && n4 != n2 && n4 != n1) {
									// Check all triangulated quads - n1
									// connected to n2,
									// n3, n4; n2 and n3 each connected to n4.
									// We already know that n1 is connected to
									// n2, n3, n4.
									// Check other part of condition.
									if (n2.isConnectedTo(n4) && n3.isConnectedTo(n4)) {
										// This is a pair of adjacent triangles.
										// Check angles to see if flip should be
										// made
										Edge e24= n2.getConnect(n4);
										Edge e34= n3.getConnect(n4);
										if (e2.radAngle(e24) + e3.radAngle(e34) > FLIP_CONDITION) {
											// Store the dividing edge as
											// needing a flip
											Node[] newExits= { n2, n3 };
											needsFlip.put(e4, newExits);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		for (Map.Entry<Edge, Node[]> e : needsFlip.entrySet()) {
			// Remove old edge
			b.getMutableEdges().remove(e.getKey());

			Node oldFirst= e.getKey().getFirstExit();
			Node oldSecond= e.getKey().getSecondExit();

			oldFirst.removeExit(e.getKey());
			oldSecond.removeExit(e.getKey());

			Node newFirst= e.getValue()[0];
			Node newSecond= e.getValue()[1];

			// Add new edge if it doesn't cross an existing edge
			if (!lineCrosses(b, newFirst, newSecond)) {
				addEdge(b, r, newFirst, newSecond);
			} else { // Otherwise, put old edge back
				addEdge(b, r, oldFirst, oldSecond);
			}
		}
	}

	/**
	 * Allows for sorting of Collections of Nodes by their GUI distance to each
	 * of the nodes in collection n. The node that is closest in the collection
	 * to the given node is the one that counts.
	 *
	 * @author MPatashnik
	 */
	private static class DistanceComparator implements Comparator<Node> {
		/**
		 * The node to which distance is compared
		 */
		protected final Node node;

		@Override
		public int compare(Node n1, Node n2) {
			double d= node.getDistance(n1) - node.getDistance(n2);
			if (d < 0)
				return -1;
			if (d > 0)
				return 1;
			return 0;
		}

		DistanceComparator(Node node) {
			this.node= node;
		}
	}

	/**
	 * An instance of the XComparator for sorting nodes. No real need to
	 * instantiate another one.
	 */
	private final static XComparator xComp= new XComparator();

	/**
	 * Allows for sorting a Collection of Nodes by the x coordinate. No need to
	 * instantiate beyond the xcomparator instantiated above.
	 */
	private static class XComparator implements Comparator<Node> {
		@Override
		public int compare(Node n1, Node n2) {
			return n1.getX() - n2.getX();
		}
	}

	/**
	 * Return a random element from elms using r. (Return null if elms is
	 * empty.)
	 */
	private static <T> T randomElement(Collection<T> elms, Random r) {
		if (elms.isEmpty())
			return null;

		Iterator<T> it= elms.iterator();
		T val= null;
		int rand= r.nextInt(elms.size()) + 1;
		for (int i= 0; i < rand; i++) {
			val= it.next();
		}
		return val;
	}

	/* Location of files for board generation */
	public static final String BOARD_GENERATION_DIRECTORY= 
			System.getProperty("user.dir") + "/data/board_generation";

	/**
	 * Return the planet names listed in BoardGeneration/planets.txt
	 */
	public static ArrayList<String> planetNames() {
		File f= new File(BOARD_GENERATION_DIRECTORY + "/planets.txt");
		BufferedReader read;
		try {
			read= new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {
			System.out.println("planets.txt not found. Aborting as empty list of planet names...");
			return new ArrayList<String>();
		}
		ArrayList<String> result= new ArrayList<String>();
		try {
			String line;
			while ((line= read.readLine()) != null) {
				// Strip non-ascii or null characters out of string
				line= line.replaceAll("[\uFEFF-\uFFFF \u0000]", "");
				result.add(line);
			}
			read.close();
		} catch (IOException e) {
			System.out.println("Error in file reading. Aborting as empty list of planet names...");
			return new ArrayList<String>();
		}
		return result;
	}

}
