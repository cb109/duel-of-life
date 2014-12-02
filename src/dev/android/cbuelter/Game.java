package dev.android.cbuelter;

import java.io.File;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PFont;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
//import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Holds the gameplay mechanics and draws everything on the screen. Generations
 * calculated in "World" are sent to this class and visualized.
 * <p>
 * Note: Processing sketches run as an activity in android, so all activity
 * methods like for e.g. for menus, are available. But: Resources will NOT be found. No icons, no strings...
 * 
 * @author Christoph
 */
public class Game extends PApplet {
	final String TAG = "gol";

	// static boolean firstStartCreateInitialRulesets = false;

	// declare here, so we can change their titles later
	/**
	 * Menu item that is created programmatically.
	 */
	MenuItem m1;
	/**
	 * Menu item that is created programmatically.
	 */
	MenuItem m2;
	/**
	 * Menu item that is created programmatically.
	 */
	MenuItem m3;
	/**
	 * Menu item that is created programmatically.
	 */
	MenuItem m4;
	/**
	 * Menu item that is created programmatically.
	 */
	MenuItem m5;
	/**
	 * Menu item that is created programmatically.
	 */
	MenuItem m6;
	/**
	 * Menu item that is created programmatically.
	 */
	MenuItem m7;
	/**
	 * Menu item that is created programmatically.
	 */
	MenuItem m8;
	/**
	 * Menu item that is created programmatically.
	 */
	MenuItem m9;

	// screen width and height from "Start"
	/**
	 * Display width in pixels.
	 */
	int dmWidth = Start.dmWidth;
	/**
	 * Display height in pixels.
	 */
	int dmHeight = Start.dmHeight;

	/**
	 * Holds the current grid of Game.world.
	 */
	static Cell[][] myGrid;
	/**
	 * Holds the current instance of "World", that stores the current and next grid generation.
	 */
	static World world = null;

	// todo: make all that stuff update using onPrefChange handlers instead of
	// looking it up every frame...

	// These are starting values, but they are meant to be overwritten by
	// "updateVars()", which means the values set inside the SharedPreferences
	/**
	 * Grid X-dimension, updated from "Options"
	 */
	static int dimX = 20;
	/**
	 * Grid Y-dimension, updated from "Options"
	 */
	static int dimY = 30;
	/**
	 * Pixel factor, determines how many pixels are used to draw a cell.
	 */
	static int z = 16; // originally Zoom, now Pixels
	/**
	 * Brush size, as used in the menu tools. b=5 will mean: 1 px + (5*2) px as the width and height of the brush.
	 */
	static int b = 1; // Brush size (e.g. 0 = 1x1 px, 1 = 3x3 px, 2 = 5x5 px
						// ...)
	/**
	 * Frames per second, internal processing value. Changed from "Options".
	 */
	static int fps = 12;
	/**
	 * Initial density of living cells on a newly created grid. Changed from "Options".
	 */
	static float dens = 0.15f;
	/**
	 * X Offset for drawing things on the screen. Not used in the current version.
	 */
	int offX = 0; // Offset for panning
	/**
	 * Y Offset for drawing things on the screen. Not used in the current version.
	 */
	int offY = 0; // Offset for panning
	/**
	 * The color of the background. Not changeable in this version.
	 */
	int clrBG = 0; // bg color as a gray shade value. If changed, make sure to
					// change dead cells color as well
	/**
	 * Holds the current color of the cells that are drawn with the brush. Changed in the menu.
	 */
	int currentPaintFaction = 0; // 0 = red, 1 = blue
	/**
	 * Used to play/pause the grid updates. Changed in the menu.
	 */
	boolean halt = false; // Play/pause calculations
	/**
	 * Used to determine the scale of each drawn cell.
	 * <p>
	 * By reducing it by 1 px, a grid-like effect is created.
	 */
	static boolean grid = true; // draw raster
	/**
	 * Used to either draw or not draw the statistics to screen.
	 */
	static boolean stats = true; // show statistics
	/**
	 * Same as in "World", tells it to either behave like a torus or not.
	 */
	static boolean torus = true; // make sure to pass this to World
	/**
	 * Used to 
	 */
	boolean paint = true; // paint/erase on click
	/**
	 * Determines how quickly the color fading effect dissolves.
	 */
	static int fadeStep = 1; // how fast cell color fades (invoked from World)
	/**
	 * Part of the statistics: The current ruleset in Golly/RLE notation. Set from "RuleEditor"
	 */
	static String redRuleSetString = "void"; // set from RuleEditor
	/**
	 * Part of the statistics: The current ruleset in Golly/RLE notation. Set from "RuleEditor"
	 */
	static String blueRuleSetString = "void";

	// win message
	/**
	 * Red win message, shown if statistics is on and there are only red and no blue cells.
	 */
	String redWinMsg = "RED WINS";
	/**
	 * Blue win message, shown if statistics is on and there are only blue and no red cells.
	 */
	String blueWinMsg = "BLUE WINS";
	/**
	 * Draw message, shown if statistics is on and there are an equal number of red and blue cells.
	 */
	String evenWinMsg = "DRAW";

	/**
	 * Number of red living cells on the grid.
	 */
	int reds = 0;
	/**
	 * Number of blue living cells on the grid.
	 */
	int blues = 0;

	// Image series is controlled by these vars ONLY#
	/**
	 * Part of the image-series screenshot tool.
	 * <p>
	 * Triggers screenshot creation if true.
	 */
	boolean seriesPermission = false;
	/**
	 * Part of the image-series screenshot tool.
	 * <p>
	 * The base name for the images, as typied into the user prompt.
	 */
	String seriesBaseName = "";
	/**
	 * Part of the image-series screenshot tool.
	 * <p>
	 * Number of images to capture, as chosen in the user prompt.
	 */
	int seriesMaxImg = 0;
	/**
	 * Part of the image-series screenshot tool.
	 * <p>
	 * Current image number in the series.
	 */
	int seriesCurrImg = 1;

	// updateVars whenever necessary to reflect options
	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// updateVars();
	// }

	/**
	 * Update variables and ruleset strings on startup.
	 */
	@Override
	public void onStart() {
		super.onStart();
		updateVars();
		// create intial rulestrings from SharedPrefs on app start
		createInitialRulesetStrings();
	}// end fn

	/**
	 * Update variables and ruleset strings on resume.
	 */
	@Override
	public void onResume() {
		super.onResume();
		updateVars();
	}// end fn

	/**
	 * Update variables and ruleset strings on restart.
	 */
	@Override
	public void onRestart() {
		super.onRestart();
		updateVars();
	}// end fn

	/**
	 * On the start of the app, this method creates the ruleset strings for the
	 * statistics.
	 */
	public void createInitialRulesetStrings() {
		// READ DATA AND UPDATE CHECKBOXES
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		// String loading (should not be void, cause set by Start)
		String bRulesRedString = prefs.getString("bRulesRedString", "void");
		String sRulesRedString = prefs.getString("sRulesRedString", "void");
		String bRulesBlueString = prefs.getString("bRulesBlueString", "void");
		String sRulesBlueString = prefs.getString("sRulesBlueString", "void");
		// String conversion to intArr
		int[] bRulesRed = RuleEditor.binStringToIntArr(bRulesRedString);
		int[] sRulesRed = RuleEditor.binStringToIntArr(sRulesRedString);
		int[] bRulesBlue = RuleEditor.binStringToIntArr(bRulesBlueString);
		int[] sRulesBlue = RuleEditor.binStringToIntArr(sRulesBlueString);
		// IntArr conversion to RuleString
		redRuleSetString = RuleEditor.ruleIntArrsToRuleString(bRulesRed,
				sRulesRed);
		blueRuleSetString = RuleEditor.ruleIntArrsToRuleString(bRulesBlue,
				sRulesBlue);
	}// end fn

	/**
	 * Gets values from the SharedPreferences of the app. Called whenever Game
	 * is created, started, resumed or restarted.
	 * <p>
	 * Used to reflect changes done in the options screen in the game.
	 */
	public void updateVars() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		// DIMENSIONS UPDATE:
		// dimX and dimY are set when cell resolution is changed
		// and then a new world is created from "Options"

		grid = prefs.getBoolean("grid", true); // 2nd value = default
		stats = prefs.getBoolean("stats", true);
		torus = prefs.getBoolean("torus", true);
		fadeStep = Integer.parseInt(prefs.getString("fade", "1"));
		b = Integer.parseInt(prefs.getString("brush", "1"));
		fps = Integer.parseInt(prefs.getString("fps", "12"));
		dens = Float.parseFloat(prefs.getString("fill", "0.15f"));

		// update torus, but make sure the world exists
		if (world != null) {
			world.setTorus(torus);
		}

		// update ruleset strings
		// redRuleSetString = RuleEditor.createRedRuleSetString();
		// blueRuleSetString = RuleEditor.createBlueRuleSetString();
	}// end fn

	/**
	 * Called when the menu button is pressed.
	 * <p>
	 * Pops up a menu with tools like paint / erase / pause and a link to the
	 * options screen.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// When accessing resources from the PApplet, we HAVE to use the
		// constant 32bit ID, otherwise it will not find it (R has no field).
		// But here we only have to deal with the menu.xml (and not every
		// string and icon, which would be really annoying)
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(0x7f070000, menu);

		// For some damn reason the inflater wont find the menu resource, so
		// we populate the menu with code instead, but then are stuck with
		// default ugly icon-less text buttons
		m1 = menu.add(Menu.NONE, 1, 1, "Use Eraser");
		m2 = menu.add(Menu.NONE, 2, 2, "Choose Blue");
		m3 = menu.add(Menu.NONE, 3, 3, "Clear Grid");
		m4 = menu.add(Menu.NONE, 4, 4, "Pause");
		m5 = menu.add(Menu.NONE, 5, 5, "Options");
		m6 = menu.add(Menu.NONE, 6, 6, "Restart Grid");
		m7 = menu.add(Menu.NONE, 7, 7, "Reset Time");
		m8 = menu.add(Menu.NONE, 8, 8, "Save single Image");
		m9 = menu.add(Menu.NONE, 9, 9, "Save Image series");

		// disable
		m3.setEnabled(false);
		m6.setEnabled(false);

		// ADDING RESOURCES HARD-CODED USING HEX ID WORKS!
		return super.onCreateOptionsMenu(menu);
	}

	// we will get localization problems with this workaround -__-
	/**
	 * Called when a menu item is selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// We cannot use resource item ids, so we use numbers
		switch (item.getItemId()) {
		case 1:
			if (m1.getTitle() == "Use Brush") {
				m1.setTitle("Use Eraser");
				paint = true;
			} else {
				m1.setTitle("Use Brush");
				paint = false;
			}
			return true;
		case 2:
			if (m2.getTitle() == "Choose Red") {
				m2.setTitle("Choose Blue");
				currentPaintFaction = 0;
			} else {
				m2.setTitle("Choose Red");
				currentPaintFaction = 1;
			}
			return true;
		case 3:
			// clear grid
			for (int i = 0; i < dimX; i++) {
				for (int j = 0; j < dimY; j++) {
					myGrid[i][j].setAlive(false);
				}
			}
			world.genCounter = 0;
			return true;
		case 4:
			// play / pause: make sure to enable/disable grid tools
			if (m4.getTitle() == "Play") {
				m4.setTitle("Pause");
				halt = false;
				m3.setEnabled(false);
				m6.setEnabled(false);
			} else {
				m4.setTitle("Play");
				halt = true;
				m3.setEnabled(true);
				m6.setEnabled(true);
			}
			return true;
		case 5:
			startActivity(new Intent(this, Options.class));
			return true;
		case 6:
			// restart grid
			Random r = new Random();
			for (int i = 0; i < dimX; i++) {
				for (int j = 0; j < dimY; j++) {
					double ranD = r.nextDouble();
					int ranF = r.nextInt(2);
					if (ranD <= dens) {
						myGrid[i][j] = new Cell(i, j, true);
						myGrid[i][j].setFaction(ranF);
					} else {
						myGrid[i][j] = new Cell(i, j, false);
						myGrid[i][j].setFaction(ranF);
					}
				}
			}
			world.genCounter = 0;
			return true;
		case 7:
			// reset time
			world.genCounter = 0;
			return true;
		case 8:
			// save screen as single image to device
			takeScreenShotWithPrompt();
			return true;
		case 9:
			// save image series
			getSeriesMaxImgWithPrompt();
			getSeriesBaseNameWithPrompt();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * A prompt opens for the user to type a filename into, which can be used to
	 * save a file.
	 */
	public void takeScreenShotWithPrompt() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Save single Image");
		alert.setMessage("The image will be saved in sdcard/DuelOfLife. Please enter a file name:");
		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String fileName = input.getText().toString();
				if (fileName.equals("")) { // if no filename specified...
					Toast.makeText(getApplicationContext(), "Wrong file name!",
							Toast.LENGTH_SHORT).show();
				} else { // if correct filename...
					File externalStorage = Environment
							.getExternalStorageDirectory();
					String sdPath = externalStorage.getAbsolutePath();
					String imgType = ".png";
					String filePath = sdPath + "/DuelOfLife/" + fileName
							+ imgType;
					save(filePath);
					Toast.makeText(getApplicationContext(), "Image saved",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});
		alert.show();
	}// end fn

	
	/**
	 * Lets the user type in a string used to name the images taken for the series.
	 */
	public void getSeriesBaseNameWithPrompt() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Capture Image Series");
		alert.setMessage("The image series will be saved in sdcard/DuelOfLife. Please enter a base name:");
		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String baseName = input.getText().toString();
				if (baseName.equals("")) { // if no filename specified...
					Toast.makeText(getApplicationContext(), "Wrong base name!",
							Toast.LENGTH_SHORT).show();
				} else { // if correct filename...
					seriesBaseName = baseName;
				}
			}
		});
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});
		alert.show();
	}// end fn

	/**
	 * Lets the user choose a predefined number of images to capture in the series.
	 */
	public void getSeriesMaxImgWithPrompt() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Capture Image Series");
		alert.setMessage("Please select how many images to capture:");
		// Set an EditText view to get user input
		// final EditText input = new EditText(this);
		final Spinner spn = new Spinner(this);
		String[] spnArray = { "5", "10", "25", "50", "75", "100", "150", "200",
				"300", "400", "500", "600", "700", "800", "900", "1000" };
		ArrayAdapter<String> spnArrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, spnArray);
		spn.setAdapter(spnArrayAdapter);
		alert.setView(spn);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// IMAGE SERIES IS ENABLED HERE
				String maxImgStr = spn.getSelectedItem().toString();
				seriesMaxImg = Integer.parseInt(maxImgStr);
				seriesCurrImg = 0;
				seriesPermission = true;
				halt = false; // important, might capture still image a 100
								// times
				m4.setTitle("Pause");
				m3.setEnabled(false);
				m6.setEnabled(false);
				Toast.makeText(
						getApplicationContext(),
						("Series of , " + seriesMaxImg + " images will be captured..."),
						Toast.LENGTH_SHORT).show();
			}
		});
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});
		alert.show();
	}// end fn

	/**
	 * Saves the screen to SDcard using a base name and suffix.
	 * 
	 * @param fileName
	 * @param suffix
	 */
	public void takeScreenShotForSeries(String baseName, int suffix) {
		File externalStorage = Environment.getExternalStorageDirectory();
		String sdPath = externalStorage.getAbsolutePath();
		String imgType = ".png";

		// padding
		String imgSuffix = Integer.toString(suffix);
		if (suffix < 10) {
			imgSuffix = "000" + Integer.toString(suffix);
		}
		if (suffix > 9 && suffix < 100) {
			imgSuffix = "00" + Integer.toString(suffix);
		}
		if (suffix > 99 && suffix < 1000) {
			imgSuffix = "0" + Integer.toString(suffix);
		}
		if (suffix > 999) {
			imgSuffix = Integer.toString(suffix);
		}

		String filePath = sdPath + "/DuelOfLife/" + baseName + imgSuffix
				+ imgType;

		save(filePath);
	}// end fn

	/**
	 * Creates a world with a grid and fills it with cells.
	 */
	public void createWorld() {
		world = new World(dimX, dimY);
		world.setTorus(torus);
		world.fillGrid(dens);
		myGrid = world.getGrid();

		// Create initial rulesets from SharedPrefs
		// (cant be void, because if they were, they have been filled by Start)
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		world.setbRedBufferArr(RuleEditor.binStringToIntArr(prefs.getString(
				"bRulesRedString", "void")));
		world.setsRedBufferArr(RuleEditor.binStringToIntArr(prefs.getString(
				"sRulesRedString", "void")));
		world.setbBlueBufferArr(RuleEditor.binStringToIntArr(prefs.getString(
				"bRulesBlueString", "void")));
		world.setsBlueBufferArr(RuleEditor.binStringToIntArr(prefs.getString(
				"sRulesBlueString", "void")));
	}// end fn

	/**
	 * Prepares the processing environment within the app. If "size()" is not
	 * used, processing will use the whole display to create the sketch.
	 */
	@Override
	public void setup() {

		createWorld();

		// size(dimX * z, dimY * z);
		frameRate(fps);
		background(0);
		noStroke();
		orientation(PORTRAIT); // the hamburger way
		// orientation(LANDSCAPE); // the hot dog way
		PFont font = loadFont("Arial-BoldMT-16.vlw");
		textFont(font);

	}// end setup

	/**
	 * Is fired every frame, used for calculations, updates and drawing.
	 * <p>
	 * Responsible for taking screenshots for the image series, depending on the state of seriesPermission.
	 */
	@Override
	public void draw() {
		// todo: dont do this every frame?
		// updateVars(); // reflect option changes instantly

		background(clrBG); // not needed until not every cell is drawn anew
		frameRate(fps);
		myGrid = world.getGrid();

		// ON MOUSE PRESSED
		if (mousePressed) {
			int x = (mouseX - offX) / z;
			int y = (mouseY - offY) / z;

			// set cells alive that are within the rectangle
			for (int i = (x - b); i < (x + 1 + b); i++) {
				for (int j = (y - b); j < (y + 1 + b); j++) {
					try {
						if (paint) {
							// myGrid[i][j].setAlive(false); // erase
							myGrid[i][j].setAlive(true); // paint color
							myGrid[i][j].setFaction(currentPaintFaction);
						} else {
							myGrid[i][j].setAlive(false); // erase
						}
					} catch (IndexOutOfBoundsException e) {
					}
				}
			}
		}

		// DRAW CELLS (make sure changes above are reflected)
		reds = 0;
		blues = 0;

		for (int i = 0; i < dimX; i++) {
			for (int j = 0; j < dimY; j++) {
				// determine color
				if (myGrid[i][j].isAlive()) { // if alive...

					if (myGrid[i][j].getFaction() == 0) {
						reds++;
						fill(255, 0, 0);
					} else {
						blues++;
						fill(0, 0, 255);
					}

				} else { // if dead...
					if (myGrid[i][j].getFaction() == 0) {
						fill(myGrid[i][j].getDeadSince() / 2,
								myGrid[i][j].getDeadSince() / 3,
								myGrid[i][j].getDeadSince() / 3);
					} else {
						fill(myGrid[i][j].getDeadSince() / 3,
								myGrid[i][j].getDeadSince() / 3,
								myGrid[i][j].getDeadSince() / 2);
					}

				}
				// draw cell & grid
				if (grid) {
					// A grid does not have to be defined with new lines, we can
					// just make the rectangles smaller by one pixel:
					rect(i * z + offX, j * z + offY, z - 1, z - 1);
				} else {
					rect(i * z + offX, j * z + offY, z, z);
				}
				// draw eraser cell overlay
				if (!paint && mousePressed) {
					fill(255);
					// using rectMode(CENTER) lags alot, so write it like
					// this instead;
					int brushWidth = (b * 2 + 1) * z;
					rect(mouseX - brushWidth / 2, mouseY - brushWidth / 2,
							brushWidth, brushWidth);
				}
			}
		}

		if (!halt) {
			world.calcNextGen();
		}

		// draw text: cell count
		if (stats) {
			// rulesets
			fill(170, 170, 255);
			text(blueRuleSetString, 10 + offX, 25 + offY);
			fill(255, 170, 170);
			text(redRuleSetString, 10 + offX, 45 + offY);

			// Red/Blue counters
			if (blues >= reds) {
				fill(255);
				text(blues, (dimX / 2) * z + offX + 40, 25 + offY);
				fill(170, 170, 255);
				text("+", (dimX / 2) * z + offX + 27, 25 + offY);
			} else {
				fill(195);
				text(blues, (dimX / 2) * z + offX + 40, 25 + offY);
			}

			if (reds >= blues) {
				fill(255);
				text(reds, (dimX / 2) * z + offX + 40, 45 + offY);
				fill(255, 170, 170);
				text("+", (dimX / 2) * z + offX + 27, 45 + offY);
			} else {
				fill(195);
				text(reds, (dimX / 2) * z + offX + 40, 45 + offY);
			}

			// win messages
			if (reds == 0 && blues != 0) {
				fill(170, 170, 255);
				text(blueWinMsg, 10 + offX, dimY * z - 30 + offY);
			} else if (blues == 0 && reds != 0) {
				fill(255, 170, 170);
				text(redWinMsg, 10 + offX, dimY * z - 30 + offY);
			} else if (blues == reds || (blues == 0 && reds == 0)) {
				fill(255);
				text(evenWinMsg, 10 + offX, dimY * z - 30 + offY);
			}

			// generation counter
			fill(195);
			text("t=" + world.genCounter, 10 + offX, dimY * z - 10 + offY);

			// pause symbol
			if (halt) {
				fill(195);
				rect(dimX * z - 20  + offX, dimY * z - 40 + offY, 8, 30);
				rect(dimX * z - 40  + offX, dimY * z - 40 + offY, 8, 30);
			}
		}

		// HANDLE IMAGE SERIES automatically
		if (seriesPermission) {
			if (!seriesBaseName.equals("")) {
				fill(255);
				if (seriesCurrImg != seriesMaxImg) {
					takeScreenShotForSeries(seriesBaseName, seriesCurrImg);
					seriesCurrImg++;
					// feedback
					text(Integer.toString(seriesCurrImg) + " / "
							+ Integer.toString(seriesMaxImg), (dimX / 2) * z
							+ offX, dimY * z - 10 + offY);
				} else { // if maxImg reached, reset everything
					seriesPermission = false;
					seriesCurrImg = 1;
					seriesMaxImg = 0;
					// feedback
					text("Done", (dimX / 2) * z + offX, dimY * z - 10 + offY);
				}
			}
		}
	} // end draw fn
} // end class
