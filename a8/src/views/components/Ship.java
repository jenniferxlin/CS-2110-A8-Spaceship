package views.components;

import utils.Constants;
import java.awt.*;

/** GUI representation of a space ship */
@SuppressWarnings("serial")
public class Ship extends Circle {

	// MARK - static fields
	private static final int DIAMETER= 13;
	private static final Color COLOR= Constants.SHIP_COLOR;

	// MARK - instance variables
	private Planet occupiedPlanet;
	private int baseSpeed;
	private double speedScale;

	/**
	 * Constructor: a Ship occupying occupiedPlanet with basespeed and
	 * sppedScale
	 */
	public Ship(Planet occupiedPlanet, int baseSpeed, double speedScale) {
		super("You", occupiedPlanet.getX1(), occupiedPlanet.getY1(), COLOR, DIAMETER);
		this.occupiedPlanet= occupiedPlanet;
		this.baseSpeed= baseSpeed;
		this.speedScale= speedScale;
	}

	/** Set the speed scale to s. */
	public void setSpeedScale(double s) {
		speedScale= s;
	}

	/** Set the base speed to s */
	public void setBaseSpeed(int s) {
		baseSpeed= s;
	}

	/** Return the speed of the ship */
	public int getSpeed() {
		return (int) (speedScale * baseSpeed);
	}

	/** Leave the currently occupied planet. */
	public void leavePlanet() {
		occupiedPlanet= null;
	}

	/** Set the occupied planet to p. */
	public void setOccupiedPlanet(Planet p) {
		occupiedPlanet= p;
	}

	/** Return true iff the ship currently occupies a planet. */
	public boolean planetOccupied() {
		return occupiedPlanet != null;
	}

	/** Move the ship along line l. */
	public void moveAlong(Line l) {
		Planet here= occupiedPlanet;
		Planet there= l.getOther(occupiedPlanet);
		int progress= 0;
		leavePlanet();
		while (progress < l.getLength()) {
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
			int remaining= l.getLength() - progress;
			progress += (remaining >= getSpeed() ? getSpeed() : remaining);
			double percent= (double) progress / (double) l.getLength();
			int newX= ((int) (percent * there.getX1() + (1 - percent) * here.getX1()));
			int newY= ((int) (percent * there.getY1() + (1 - percent) * here.getY1()));
			setGUILocation(newX, newY);
		}
		setOccupiedPlanet(there);
	}

	/**
	 * Fix the bounds for the Ship based on the (x, y) position in this ship.
	 */
	@Override
	public void fixBounds() {
		if (planetOccupied()) {
			setX1(occupiedPlanet.getX1());
			setY1(occupiedPlanet.getY1());
			super.fixBounds();
		} else {
			super.fixBounds();
		}
	}

	/** Return a string representation of the ship */
	@Override
	public String toString() {
		return "SPACESHIP - " + super.toString();
	}

}