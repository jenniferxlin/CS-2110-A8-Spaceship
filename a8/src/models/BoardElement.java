package models;

/** An instance represents an entity attached to a board. */
public interface BoardElement {

	/** The name this Object has when drawn on the board */
	public String getMappedName();

	/** Return the x coordinate of this Object */
	public int getX();

	/** Return the y coordinate of this Object. */
	public int getY();
}