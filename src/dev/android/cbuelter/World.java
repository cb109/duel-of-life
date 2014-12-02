package dev.android.cbuelter;

import java.util.Random;

public class World {
	/**
	 * X-dimension of the grid.
	 */
	private int dimX;
	/**
	 * Y-dimension of the grid.
	 */
	private int dimY;

	/**
	 * Sets the world to be a torus or not.
	 */
	private boolean torus = true; // starting value, will be updated
									// from "Game", which gets it from
									// SharedPreferences
	/**
	 * Stores the current grid generation and all its cells.
	 */
	private Cell[][] grid;
	/**
	 * Stores the next grid generation and all its cells.
	 */
	private Cell[][] grid_nextGen;
	
	/**
	 * Counts up on generation change, used for the statistics.
	 */
	public int genCounter = 0; // counts gens, passed to Game to show as stat

	// buffers are updated from RuleEditor and Start
	/**
	 * A buffer used to store a ruleset. 
	 */
	private int[] bRedBufferArr = { 0, 0, 0, 1, 0, 0, 0, 0, 0 }; // buffered red birth ruleset
	/**
	 * A buffer used to store a ruleset. 
	 */
	private int[] sRedBufferArr = { 0, 0, 1, 1, 0, 0, 0, 0, 0 };; // buffered red survival ruleset
	/**
	 * A buffer used to store a ruleset. 
	 */
	private int[] bBlueBufferArr = { 0, 0, 0, 1, 0, 0, 0, 0, 0 }; // buffered blue birth ruleset
	/**
	 * A buffer used to store a ruleset. 
	 */
	private int[] sBlueBufferArr = { 0, 0, 1, 1, 0, 0, 0, 0, 0 };; // buffered blue survival ruleset
	
	// the current arrays are used to switch between red/blue rulesets inside calcNextGen()
	/**
	 * Holds the current birth ruleset used in "calcNextGen()".
	 * <p>
	 * Will change depending on cell faction/color and load new values from the buffer accordingly.
	 */
	private int[] bArr = { 0, 0, 0, 1, 0, 0, 0, 0, 0 }; // current birth ruleset
	/**
	 * Holds the current survival ruleset used in "calcNextGen()".
	 * <p>
	 * Will change depending on cell faction/color and load new values from the buffer accordingly.
	 */
	private int[] sArr = { 0, 0, 1, 1, 0, 0, 0, 0, 0 };; // current survival ruleset

	public World(int dimX, int dimY) {
		super();
		this.dimX = dimX;
		this.dimY = dimY;
	}
	
	public int[] getbRedBufferArr() {
		return bRedBufferArr;
	}

	public void setbRedBufferArr(int[] bRedBufferArr) {
		this.bRedBufferArr = bRedBufferArr;
	}

	public int[] getsRedBufferArr() {
		return sRedBufferArr;
	}

	public void setsRedBufferArr(int[] sRedBufferArr) {
		this.sRedBufferArr = sRedBufferArr;
	}

	public int[] getbBlueBufferArr() {
		return bBlueBufferArr;
	}

	public void setbBlueBufferArr(int[] bBlueBufferArr) {
		this.bBlueBufferArr = bBlueBufferArr;
	}

	public int[] getsBlueBufferArr() {
		return sBlueBufferArr;
	}

	public void setsBlueBufferArr(int[] sBlueBufferArr) {
		this.sBlueBufferArr = sBlueBufferArr;
	}

	public int getDimX() {
		return dimX;
	}

	public boolean isTorus() {
		return torus;
	}

	public void setTorus(boolean torus) {
		this.torus = torus;
	}

	public boolean getTorus() {
		return torus;
	}

	public void setDimX(int dimX) {
		this.dimX = dimX;
	}

	public int getDimY() {
		return dimY;
	}

	public void setDimY(int dimY) {
		this.dimY = dimY;
	}

	// initialize world
	/**
	 * Initializes both current and next grid by filling it with cells.
	 * <p>
	 * The dens parameter determines, how many cells will be set alive randomly. 
	 * @param dens
	 */
	public void fillGrid(double dens) {
		// create and fill the original grid randomly by density
		grid = new Cell[dimX][dimY];
		Random r = new Random();

		for (int i = 0; i < dimX; i++) {
			for (int j = 0; j < dimY; j++) {
				double ranD = r.nextDouble();
				int ranF = r.nextInt(2);

				if (ranD <= dens) {
					grid[i][j] = new Cell(i, j, true);
					grid[i][j].setFaction(ranF);
				} else {
					grid[i][j] = new Cell(i, j, false);
					grid[i][j].setFaction(ranF);
				}
			}
		}

		// intitialize grid for next generation
		grid_nextGen = new Cell[dimX][dimY];
	}// end fn

	// count neighbours
	/**
	 * Receives a cell and counts all its living neighbour cells. 
	 * <p>
	 * Will return an integer array with two values, the first being the neighbour count, the second being the faction of the cell.
	 * @param c
	 * @return
	 */
	public int[] countNeighbours(Cell c) {
		int x = c.getX();
		int y = c.getY();

		int[] res = new int[2];
		res[0] = 0; // neighbour count
		res[1] = 0; // major faction
		int fc1 = 0; // faction1 count
		int fc2 = 0; // faction2 count

		for (int i = (x - 1); i < (x + 2); i++) {
			for (int j = (y - 1); j < (y + 2); j++) {

				int a = i;
				int b = j;

				// torus field check
				if (torus) {
					if (i > dimX - 1) {
						a = i % dimX;
					}
					if (i < 0) {
						a = dimX - 1;
					}
					if (j > dimY - 1) {
						b = j % dimY;
					}
					if (j < 0) {
						b = dimY - 1;
					}
				}

				try {
					if (this.grid[a][b].isAlive()) {
						res[0]++;
						if (this.grid[a][b].getFaction() == 0) {
							fc1++;
						} else {
							fc2++;
						}
					}
				} catch (IndexOutOfBoundsException e) {
					// if we end up here, some neighbours dont exist
				}
			}// end for
		}// end for
		if (this.grid[x][y].isAlive()) // the cell itself
			res[0]--;
		if (fc1 > fc2) { // maybe this is reds advantage?
			res[1] = 0;
		} else if (fc2 > fc1) {
			res[1] = 1;
		} else { // if red & blue neighbour counts are the same, random...
			Random ran = new Random();
			int temp = ran.nextInt(2);
			if (temp == 0) {
				res[1] = 0;
			} else {
				res[1] = 1;
			}
		}
		return res;
	}// end fn

	// calc next generation
	/**
	 * Analyzes the current grid by looking at each cell and its neighbours. 
	 * <p>
	 * Used to create the next grid generation.
	 * <p>
	 * Is also responsible for the color fading value changes. 
	 * 
	 */
	public void calcNextGen() {
		for (int i = 0; i < dimX; i++) {
			for (int j = 0; j < dimY; j++) {

				// neighbours count
				int n = (countNeighbours(this.grid[i][j]))[0];
				// old faction of current cell
				int fOld = this.grid[i][j].getFaction();
				// average faction of neighbours
				int f = (countNeighbours(this.grid[i][j]))[1];
				// buffer deadSince of current cell
				int cellDeadSince = grid[i][j].getDeadSince();

				// Apply ruleset depending on faction
				if (grid[i][j].getFaction() == 0) {
					// Get and use red rulesets
					bArr = bRedBufferArr;
					sArr = sRedBufferArr;
				} else {
					// Get and use blue rulesets
					bArr = bBlueBufferArr;
					sArr = sBlueBufferArr;
				}
				boolean doSurvive = false;
				boolean beBorn = false;

				if (grid[i][j].isAlive()) { // cell lives...

					// check using Bit Array...
//					for (int k = 0; k < sArr.length; k++) {
//						if (n == k && sArr[k] == 1) {
//							doSurvive = true;
//							break;
//						}
//					}
					for (int k = 1; k < sArr.length; k++) {
						if (n == k && sArr[k] == 1) {
							doSurvive = true;
							break;
						}
					}

					if (doSurvive) {
						grid_nextGen[i][j] = new Cell(i, j, true);
						// grid_nextGen[i][j].setDeadSince(255);

						grid_nextGen[i][j].setFaction(f);

					} else {
						grid_nextGen[i][j] = new Cell(i, j, false);
						// grid_nextGen[i][j].setAlive(false);
						// grid_nextGen[i][j].setDeadSince(0);

						grid_nextGen[i][j].setFaction(fOld);
					}
				} else { // cell is dead...

					// check using Bit Array...
//					for (int k = 0; k < bArr.length; k++) {
//						if (n == k && bArr[k] == 1) {
//							beBorn = true;
//							break;
//						}
//					}
					for (int k = 1; k < bArr.length; k++) {
						if (n == k && bArr[k] == 1) {
							beBorn = true;
							break;
						}
					}

					if (beBorn) {
						grid_nextGen[i][j] = new Cell(i, j, true);
						// grid_nextGen[i][j].setAlive(true);
						// grid_nextGen[i][j].setDeadSince(255);

						grid_nextGen[i][j].setFaction(f);

					} else {
						grid_nextGen[i][j] = new Cell(i, j, false);
						// grid_nextGen[i][j].setAlive(false);
						// grid_nextGen[i][j].setDeadSince(0);

						grid_nextGen[i][j].setFaction(fOld);
					}
				}
				// darken dead cells, set living to "white"
				if (grid_nextGen[i][j].isAlive() == false) {
					if (cellDeadSince > Game.fadeStep)
						grid_nextGen[i][j].setDeadSince(cellDeadSince
								- Game.fadeStep);
				} else {
					grid_nextGen[i][j].setDeadSince(255);
				}

			}// end for
		}// end for

		for (int i = 0; i < dimX; i++) {
			for (int j = 0; j < dimY; j++) {
				grid[i][j] = grid_nextGen[i][j];
			}
		}
		genCounter++; // iterate
	}// end fn

	// send grid to processing for graphical visualisation
	public Cell[][] getGrid() {
		return grid;
	}
}
