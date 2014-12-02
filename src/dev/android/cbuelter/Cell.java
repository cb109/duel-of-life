package dev.android.cbuelter;

/**
 * Defines a cell that exists on a grid defined by <code>World</code>.
 * @author Christoph
 *
 */
public class Cell {
	/**
	 * X position of the cell on the grid.
	 */
	private int x;
	/**
	 * Y position of the cell on the grid.
	 */
	private int y;
	/**
	 * State of the cell (true = alive, false = dead).
	 */
	private boolean alive;
	/**
	 * This integer value defines how long the cell has been dead.
	 * <p>
	 * The value can be used to create a color fading effect.
	 * <p> 
	 * Living cells will have a value of 255.
	 */
	private int deadSince = 0;
	/**
	 * Defines the faction and thereby color of the cell (0 = red, 1 = blue).
	 */
	private int faction = 0;

	/**
	 * Constructor of the cell. Will set a value for <code>deadSince</code> according to its state.
	 * @param x
	 * @param y
	 * @param alive
	 */
	public Cell(int x, int y, boolean alive) {
		super();
		this.x = x;
		this.y = y;
		this.alive = alive;
		if (alive) {
			deadSince = 255;
		}
	}
//	public Cell(int x, int y, boolean alive, int faction) {
//		super();
//		this.x = x;
//		this.y = y;
//		this.alive = alive;
//		if (alive) {
//			deadSince = 255;
//		}
//		this.faction = faction;
//	}

	public int getFaction() {
		return faction;
	}

	public void setFaction(int faction) {
		this.faction = faction;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public int getDeadSince() {
		return deadSince;
	}

	public void setDeadSince(int deadSince) {
		this.deadSince = deadSince;
	}

}
