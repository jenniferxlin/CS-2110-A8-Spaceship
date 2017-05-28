package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import models.Edge;
import models.Node;

/** This class contains Dijkstra's shortest-path algorithm and some other methods. */
public class PathFunctions {

  /** Return a list of the nodes on the shortest path from start to end, or the
   *  empty list if a path does not exist. Note: The empty list is NOT "null";
   *  it is a list with 0 elements. */
  public static List<Node> shortestPath(Node start, Node end) {
    // The frontier set
    Heap<Node> F= new Heap<Node>();

    // Each node in the Settled and Frontier sets has an entry in map,
    // which gives its shortest distance from node start and the backpointer
    // of the node on a shortest path from node start.
    HashMap<Node, SFdata> map= new HashMap<Node, SFdata>();

    F.add(start, 0);
    map.put(start, new SFdata(0, null));

    // invariant: As presented in notes for Lecture 20
    while (F.size() > 0) {
      Node f= F.poll();
      if (f == end)
        return constructPath(f, map);

      SFdata fData= map.get(f);

      for (Edge edge : f.getExits()) {
        Node w= edge.getOther(f);
        int distToW= fData.distance + edge.length;
        SFdata wData= map.get(w);
        if (wData == null) { // if w is in the far-out set
          F.add(w, distToW);
          map.put(w, new SFdata(distToW, f));
        } else // w in settled or frontier set
        if (distToW < wData.distance) {
          F.updatePriority(w, distToW);
          wData.distance= distToW;
          wData.backPointer= f;
        }
      }
    }

    return new LinkedList<Node>(); // no path found
  }
  
  /** Return a list of the nodes on the shortest path from start to end, or the
   *  empty list if a path does not exist. Note: The empty list is NOT "null";
   *  it is a list with 0 elements. */
  public static List<Node> longestPath(Node start, Node end) {
    return longDfs(start, end, new LinkedList<Node>(), new ArrayList<Node>());
  }
  
  public static List<Node> longDfs(Node start, Node end, List<Node> path, ArrayList<Node> visited) {
    // Base case
    if(start == end) return path;
    
    HashMap<Node, Integer> neighbors = start.getNeighbors();
    Heap<Node> h = new Heap<Node>();
    // Add nodes to heap in sorted order with max at top.
    for(Entry<Node, Integer> e : neighbors.entrySet()) {
      Node n = e.getKey();
      if(!path.contains(n) && !visited.contains(n)) {
        // Want to find longest non-hostile path if possible.
        if(n.isHostile()) {
          h.add(n, e.getValue());
        }
        else {
          h.add(n, -e.getValue());
        }
      }
    }
    // Look through heap
    while(!h.isEmpty()) {
      Node n = h.poll();
      if(!path.contains(n) && !visited.contains(n)) {
        // Recursive call
        LinkedList<Node> newPath = new LinkedList<Node>();
        for(Node i : path) {
          newPath.add(i);
        }
        newPath.add(n);
        visited.add(n);
        List<Node> dfsPath = longDfs(n, end, newPath, visited);
        if(dfsPath != null) return dfsPath;
      }
    }
    return null;
  }

  /** Return the path from the start node to node end. Precondition: nData
   *  contains all the necessary information about the path. */
  public static List<Node> constructPath(Node end, HashMap<Node, SFdata> nData) {
    LinkedList<Node> path= new LinkedList<Node>();
    Node p= end;
    // invariant: All the nodes from p's successor to the end are in
    // path, in reverse order.
    while (p != null) {
      path.addFirst(p);
      p= nData.get(p).backPointer;
    }
    return path;
  }

  /** Return the sum of the weights of the edges on path path. */
  public static int pathDistance(List<Node> path) {
    if (path.size() == 0)
      return 0;
    synchronized (path) {
      Iterator<Node> iter= path.iterator();
      Node p= iter.next(); // First node on path
      int s= 0;
      // invariant: s = sum of weights of edges from start to p
      while (iter.hasNext()) {
        Node q= iter.next();
        s= s + p.getConnect(q).length;
        p= q;
      }
      return s;
    }
  }

  /** An instance contains information about a node: the previous node on a
   *  shortest path from the start node to this node and the distance of this
   *  node from the start node. */
  private static class SFdata {
    private Node backPointer; // backpointer on path from start node to this one
    private int distance; // distance from start node to this one

    /** Constructor: an instance with distance d from the start node and
     *  backpointer p. */
    private SFdata(int d, Node p) {
      distance= d; // Distance from start node to this one.
      backPointer= p; // Backpointer on the path (null if start node)
    }

    /** Return a representation of this instance. */
    public String toString() {
      return "dist " + distance + ", bckptr " + backPointer;
    }
  }
}