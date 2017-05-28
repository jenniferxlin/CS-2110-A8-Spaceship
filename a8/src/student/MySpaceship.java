package student;

import models.RescueStage;
import models.ReturnStage;
import models.Spaceship;
import models.NodeStatus;
import models.Node;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/** An instance implements the methods needed to complete the mission */
public class MySpaceship extends Spaceship {

	/**
	 * Explore the galaxy, trying to find the missing spaceship that has crashed
	 * on Planet X in as little time as possible. Once you find the missing
	 * spaceship, you must return from the function in order to symbolize that
	 * you've rescued it. If you continue to move after finding the spaceship
	 * rather than returning, it will not count. If you return from this
	 * function while not on Planet X, it will count as a failure.
	 * 
	 * At every step, you only know your current planet's ID and the ID of all
	 * neighboring planets, as well as the ping from the missing spaceship.
	 * 
	 * In order to get information about the current state, use functions
	 * currentLocation(), neighbors(), and getPing() in RescueStage. You know
	 * you are standing on Planet X when foundSpaceship() is true.
	 * 
	 * Use function moveTo(long id) in RescueStage to move to a neighboring
	 * planet by its ID. Doing this will change state to reflect your new
	 * position.
	 */
	@Override
	public void rescue(RescueStage state) {
		// TODO : Find the missing spaceship
		ArrayList<Long> visited = new ArrayList<Long>();
		depthSearch(state,visited, state.currentLocation());
	}

	public void bfs(RescueStage state){
		//Maintain two parallel queues to manage our data
		ArrayList<Long> queueID = new ArrayList<Long>();
		ArrayList<Double> queuePing = new ArrayList<Double>();
		
		//Make a visited set
		ArrayList<Long> visited = new ArrayList<Long>();
				
		//Add current node to the queue
		queueID.add(state.currentLocation());
		queuePing.add(state.getPing());
		
		Long firstID = state.currentLocation();

		while(!queueID.isEmpty() && !queuePing.isEmpty()){
			//Pop the first element in the queue
			Long currentID = queueID.get(0);
			queueID.remove(0);
			Double currentPing = queuePing.get(0);
			queuePing.remove(0);
			
			//If we are at the starting position, we simply do not move 
			if(!(firstID == currentID)){
				state.moveTo(currentID);
			}
			
			//If we are on the planet X, we return
			if(state.foundSpaceship()){
				return; 
			}
			else{
				//Gather the neighbors
				Collection<NodeStatus> neighbors = state.neighbors();
				
				//If we have no neighbors, we're at a dead end
				if(neighbors.isEmpty()){
					return;
				}
				else{
					//Loop through neighbors to add elements
					for(NodeStatus neighbor : neighbors){
						Long neighborID = neighbor.getId();
						Double neighborPing = neighbor.getPingToTarget();
						
						//Some nodes may have same ping
						if(!queueID.contains(neighborID)){
							queueID.add(neighborID);
							queuePing.add(neighborPing);
						}
					}
				}
				//Add current location to the visited set 
				visited.add(currentID);
			}
		}
		
		return;
	}
	
	
	public void depthSearch(RescueStage state, ArrayList visited, long ID){
		//Initialize the node we are currently on 
		visited.add(ID);
		
		//Found spaceship
		if(state.foundSpaceship()){
			return;
		}
		else{
			Collection<NodeStatus> neighbors = state.neighbors();
			
			if(!neighbors.isEmpty()){
				for(NodeStatus neighbor: neighbors){
					long neighborID = neighbor.getId();
					double neighborPing = neighbor.getPingToTarget();
					
					//Make sure that we haven't already visited this
					//node.
					if(!visited.contains(neighborID)){
						//Move to next available neighbor
						state.moveTo(neighborID);
						
						//Move to the next state and recurse.
						depthSearch(state, visited, neighborID);
						
						if(state.foundSpaceship()){
							return;
						}
						else{
							//Move back to the node we came from
							state.moveTo(ID);
						}
					}
				}
			}
			else{
				//No nodes to search
				return;
			}
		}
	}

	/**
	 * Get back to Earth, avoiding hostile troops and searching for speed
	 * upgrades on the way. Traveling through 3 or more planets that are hostile
	 * will prevent you from ever returning to Earth.
	 *
	 * You now have access to the entire underlying graph, which can be accessed
	 * through ScramState. currentNode() and getEarth() will return Node objects
	 * of interest, and getNodes() will return a collection of all nodes in the
	 * graph.
	 *
	 * You may use state.grabSpeedUpgrade() to get a speed upgrade if there is
	 * one, and can check whether a planet is hostile using the isHostile
	 * function in the Node class.
	 *
	 * You must return from this function while on Earth. Returning from the
	 * wrong location will be considered a failed run.
	 *
	 * You will always be able to return to Earth without passing through three
	 * hostile planets. However, returning to Earth faster will result in a
	 * better score, so you should look for ways to optimize your return.
	 */
	@Override
	public void returnToEarth(ReturnStage state) {
		// TODO: Return to Earth
		Node current = state.currentNode();
		Node earth = state.getEarth();
		
		//Using Shortest Paths Algorithm
		List<Node> shortest = Paths.shortestPath(current, earth);
		
		//Exit path 
		List<Node> homePath = new ArrayList<>();
		Node backTrack = null;
		
		//Traverse the path 
		for(int i = 0; i < shortest.size(); i++){
			if(backTrack == null){
				homePath.add(current);
			}
			else{
				HashMap<Node,Integer> previousNeighbors = backTrack.getNeighbors();
				
				Node potentialAdjacent = shortest.get(i);
				
				if(!previousNeighbors.containsKey(shortest.get(i))){
					
					for(HashMap.Entry<Node,Integer> info : previousNeighbors.entrySet()){
						Node neighbor = info.getKey();
						
						//Directly moves to neighbor
						if(neighbor.getNeighbors().containsKey(potentialAdjacent)){
							homePath.add(neighbor);
							homePath.add(potentialAdjacent);
							break;
						}
					}
				}
				else{
					homePath.add(potentialAdjacent);
				}
			}
			backTrack = shortest.get(i);

		}
		
		//Walk through the path without walking on current position
		int h = 1; 
		while((h < homePath.size())){
			state.moveTo(shortest.get(h));
			if(state.currentNode().hasSpeedUpgrade()){
				state.grabSpeedUpgrade();
			}
			h++;
		}
		
	}

}