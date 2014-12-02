package dev.android.cbuelter;

import dev.android.cbuelter.R;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

/**
 * Opens up the options screen to adjust gameplay and visual preferences. These
 * settings are stored in the applications SharedPreferences.
 * 
 * @author Christoph
 * 
 */
public class Options extends PreferenceActivity {
	// this is just for the strings!
	/**
	 * Used to calculate the cell resolutions, first values stores width, the second stores height.
	 */
	int[] hig = new int[2];
	/**
	 * Used to calculate the cell resolutions, first values stores width, the second stores height.
	 */
	int[] med = new int[2];
	/**
	 * Used to calculate the cell resolutions, first values stores width, the second stores height.
	 */
	int[] low = new int[2];
	/**
	 * Used to calculate the cell resolutions, first values stores width, the second stores height.
	 */
	int[] vlo = new int[2];
	/**
	 * Used to calculate the cell resolutions, first values stores width, the second stores height.
	 */
	int[] slo = new int[2];

	/**
	 * Called when the activity is first created.
	 * <p>
	 * Installs a OnPreferenceChangedHandler for the Cell Resolution option. It is handled separately to force
	 * and update of the grid in "Game".
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.options);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		calcRes(); // calculate resolutions
		
		// HANDLE CELL RESOLUTION CHANGE TO FORCE UPDATE
		ListPreference resPref = (ListPreference) findPreference("res");

		CharSequence[] entries = {
				((Integer.toString(hig[0])) + " x "
						+ (Integer.toString(hig[1])) + " cells"),
				((Integer.toString(med[0])) + " x "
						+ (Integer.toString(med[1])) + " cells"),
				((Integer.toString(low[0])) + " x "
						+ (Integer.toString(low[1])) + " cells"),
				((Integer.toString(vlo[0])) + " x "
						+ (Integer.toString(vlo[1])) + " cells"),
				((Integer.toString(slo[0])) + " x "
						+ (Integer.toString(slo[1])) + " cells"),

		};
		CharSequence[] entryValues = { "2", "4", "8", "16", "32" };
		resPref.setEntries(entries);
		resPref.setDefaultValue("16");
		resPref.setEntryValues(entryValues);
		
		// NEEDED FOR RULE UPDATE OF NEW WORLD 
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);		

		// ON PREFERENCE VALUE CHANGED
		// If resPref is changed, make sure to create a new grid in "Game"
		resPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {

				// Creates a new world / grid.
				// this is pretty much equivalent to "createWorld()" in "Game"
				int val = Integer.parseInt((String) newValue);
				Game.dimX = Start.dmWidth / val; // update vals for loops
				Game.dimY = Start.dmHeight / val;
				Game.world = new World(Game.dimX, Game.dimY);
				Game.world.setTorus(Game.torus);
				Game.world.fillGrid(Game.dens);
				Game.myGrid = Game.world.getGrid();
				Game.z = val; // proper scaling
				// MAKE SURE TO UPDATE THE NEW WORLDS RULESETS!
				Game.world.setbRedBufferArr(RuleEditor.binStringToIntArr(prefs.getString(
						"bRulesRedString", "void")));
				Game.world.setsRedBufferArr(RuleEditor.binStringToIntArr(prefs.getString(
						"sRulesRedString", "void")));
				Game.world.setbBlueBufferArr(RuleEditor.binStringToIntArr(prefs.getString(
						"bRulesBlueString", "void")));
				Game.world.setsBlueBufferArr(RuleEditor.binStringToIntArr(prefs.getString(
						"sRulesBlueString", "void")));				
				return true;
			}

		});

	}

	/**
	 * Calculates the cell resolutions available to the user, dependent on the
	 * devices display. Will provide 5 different resolutions.
	 * <p>
	 * The resolutions cannot be saved in the SharedPreferences as they consist
	 * of two values and prefs can only store primite data types like integers.
	 * <p>
	 * However, using their division value as the entryValues, setting the
	 * resolution with the help of the metrics object is easy.
	 * <p>
	 * E.g.: Picked 3rd entry, division value = 8, so divide display width and
	 * height by 8 to get the new cell resolution.
	 * <p>
	 * Pixel factor "z" is set accordingly, so "dimX", "dimY" and "z" are passed
	 * to "Game".
	 */
	public void calcRes() {
		int w = Start.dmWidth;
		int h = Start.dmHeight;
		hig[0] = w / 2;
		hig[1] = h / 2;
		med[0] = w / 4;
		med[1] = h / 4;
		low[0] = w / 8;
		low[1] = h / 8;
		vlo[0] = w / 16;
		vlo[1] = h / 16;
		slo[0] = w / 32;
		slo[1] = h / 32;
	}// end fn

}
