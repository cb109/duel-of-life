package dev.android.cbuelter;

import dev.android.cbuelter.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Shows the starting screen of the app with the logo, and three buttons:
 * <code>Start</code>, <code>How To</code> and <code>Options</code>.
 * <p>
 * 
 * @author Christoph
 */
public class Start extends Activity {

	
	/**
	 * Used to get width and height of the device.
	 */
	DisplayMetrics displaymetrics; // stores width and height of screen, and is
									// referenced from withing Game and Options
									// to calculate and set cell resolutions
	/**
	 * Used to calculate and send initial x-dimensions to <code>Game</code>.
	 */
	static int dmWidth;
	/**
	 * Used to calculate and send initial y-dimensions to <code>Game</code>.
	 */
	static int dmHeight;

	/**
	 * Called when the activity is first created.
	 * <p>
	 * Tries to load initial rulesets from SharedPreferences. Creates some if
	 * needed.
	 * <p>
	 * Also provides initial dimensions for the game, based on the width and
	 * height of the device.
	 * <p>
	 * Sets a fixed orientation (portrait).
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// create hyperlink
		// TextView textview = (TextView) findViewById(R.id.start_txtHyperlink);
		// Linkify.addLinks(textview, Linkify.WEB_URLS);

		// Get sharedPrefs
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		// Create the preset hash map
		Presets.initializePresetHashMap(); 
		
		// Create starting ruleSetStrings in SharedPrefs, if this is the first
		// start
		// these starting values are then used in RuleEditor to update
		// checkboxes
		String bRulesRedString = prefs.getString("bRulesRedString", "void");
		if (bRulesRedString == "void") {
			// no need to check the other strings, if void then first start:
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("bRulesRedString", "000100000");
			editor.putString("sRulesRedString", "001100000");
			editor.putString("bRulesBlueString", "000100000");
			editor.putString("sRulesBlueString", "001100000");
			editor.commit();
		}
		// The starting ruleset strings are then created in Game.onStart() !

		// Get screen width and height
		displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		dmWidth = displaymetrics.widthPixels;
		dmHeight = displaymetrics.heightPixels;

		// Update dimX, dimY and z in "Game" to show the grid correctly on the
		// first start (otherwise it will use default values instead of the ones
		// saved inside the SharedPreferences):
		int val = Integer.parseInt(prefs.getString("res", "16"));
		Game.dimX = dmWidth / val;
		Game.dimY = dmHeight / val;
		Game.z = val;
	}

	/**
	 * Called when the "Start"-button is pressed on the start screen.
	 * <p>
	 * Starts the game.
	 */
	public void startGame(View view) {
		startActivity(new Intent(this, Game.class));
	}

	/**
	 * Called when the "Options"-button is pressed on the start screen.
	 * <p>
	 * Switches to the options screen.
	 */
	public void goToOptions(View view) {
		startActivity(new Intent(this, Options.class));
	}

	/**
	 * Called when the "How To"-button is pressed on the start screen.
	 * <p>
	 * Shows the "How To" dialog.
	 */
	public void goToHowTo(View view) {
		String msg = "1. Under Options, define rules for both red and blue cells.\n\n"
				+ "2. Adjust performance, gameplay and visual options.\n\n"
				+ "3. Hit start. The menu button offers additional tools. Create new cells by drawing on the screen.\n\n"
				+ "4. There is no goal, just play around.";
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Duel of Life - How To");
		alert.setMessage(msg);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Clicked
			}
		});
		alert.show();
	}
}