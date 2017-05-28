package models;

import java.util.Objects;

/**
 * An instance contains the ID of a Node and the ping volume at this Node. Used
 * in RescueStage in lieu of Nodes.
 */
public class NodeStatus implements Comparable<NodeStatus> {
	private final long id;
	private final double ping;

	/**
	 * Constructor: an instance with id nodeId and ping volume pingStrength to
	 * the crashed ship.
	 */
	NodeStatus(long nodeId, double pingStrength) {
		id= nodeId;
		ping= pingStrength;
	}

	/** Return the Id of the Node that corresponds to this NodeStatus. */
	public long getId() {
		return id;
	}

	/**
	 * Return the ping volume from the Node that corresponds to this NodeStatus.
	 */
	public double getPingToTarget() {
		return ping;
	}

	/**
	 * Return neg or pos number depending on whether this's ping is > or <
	 * other's ping. Students: Think of the intuition behind this. If the pings
	 * are equal, return neg, 0 or pos depending on whether this id is <, = or >
	 * other's id.
	 */
	@Override
	public int compareTo(NodeStatus other) {
		if (ping != other.ping) {
			return Double.compare(other.ping, ping);
		}
		return Long.compare(id, other.id);
	}

	/** Return true iff ob is a NodeStatus and has the same id as this one. */
	@Override
	public boolean equals(Object ob) {
		if (ob == this)
			return true;
		if (!(ob instanceof NodeStatus))
			return false;
		return id == ((NodeStatus) ob).id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
