package dev.android.cbuelter;

import java.util.ArrayList;
import java.util.Random;

import dev.android.cbuelter.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.Gallery;
import android.widget.Spinner;

/**
 * This class connects to the rulesets interface from the options screen. It
 * handles loading and storing of rulesets as well as string creation.
 * <p>
 * 
 * @author Christoph
 * 
 */
public class RuleEditor extends Activity {

	// These are arrays used for editing, they are stored in SharedPrefs and
	// passed to World at the end
	/**
	 * When editing the rulesets using the checkboxes, this array gets changed and in the end passed to "World".
	 */
	static int[] bRulesRed = { 0, 0, 0, 1, 0, 0, 0, 0, 0 };
	/**
	 * When editing the rulesets using the checkboxes, this array gets changed and in the end passed to "World".
	 */
	static int[] sRulesRed = { 0, 0, 1, 1, 0, 0, 0, 0, 0 };
	/**
	 * When editing the rulesets using the checkboxes, this array gets changed and in the end passed to "World".
	 */
	static int[] bRulesBlue = { 0, 0, 0, 1, 0, 0, 0, 0, 0 };
	/**
	 * When editing the rulesets using the checkboxes, this array gets changed and in the end passed to "World".
	 */
	static int[] sRulesBlue = { 0, 0, 1, 1, 0, 0, 0, 0, 0 };

	// WORKAROUND STUFF TO PREVENT SPINNER FIRING ON INITIALIZATION
	/**
	 * Part of the "Prevent-spinner-firing-on-initialization"-workaround.
	 * <p>
	 * Set manually to equal the number of spinners in "editor.xml"
	 */
	private int spinnerCount = 0; // update in onCreate()
	/**
	 * Part of the "Prevent-spinner-firing-on-initialization"-workaround.
	 * <p>
	 * Counts up on any spinner initializing itself.
	 */
	private int spinnerInitializedCount = 0;

	/** Called when the activity is first created. 
	 * Handles preset spinners, to change checkboxes when a new preset is chosen.
	 * 
	 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor);

		// WORKAROUND STUFF TO PREVENT SPINNER FIRING ON INITIALIZATION
		spinnerCount = 2; // red and blue spinner

		// HANDLE Red PRESET SPINNER EVENT
		Spinner spnPresetRed = (Spinner) findViewById(R.id.editor_spnPresetRed);
//		spnPresetRed.setSelection(1);
		spnPresetRed.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int pos, long id) {
				if (spinnerInitializedCount < spinnerCount) {
					spinnerInitializedCount++;
				} else {

					String presetName = parentView.getItemAtPosition(pos)
							.toString();
					bRulesRed = Presets.gollyStringToRuleIntArr(
							Presets.presetHashMap.get(presetName), 0);
					sRulesRed = Presets.gollyStringToRuleIntArr(
							Presets.presetHashMap.get(presetName), 1);
					updateCheckboxesFromRuleIntArrays(0); // only red CBs
					writeSharedPrefsFromCheckboxes();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				return; // do nothing
			}
		});
		
		// HANDLE Blue PRESET SPINNER EVENT
		Spinner spnPresetBlue = (Spinner) findViewById(R.id.editor_spnPresetBlue);
		spnPresetBlue.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int pos, long id) {
				if (spinnerInitializedCount < spinnerCount) {
					spinnerInitializedCount++;
				} else {

					String presetName = parentView.getItemAtPosition(pos)
							.toString();
					bRulesBlue = Presets.gollyStringToRuleIntArr(
							Presets.presetHashMap.get(presetName), 0);
					sRulesBlue = Presets.gollyStringToRuleIntArr(
							Presets.presetHashMap.get(presetName), 1);
					updateCheckboxesFromRuleIntArrays(1); // only Blue CBs
					writeSharedPrefsFromCheckboxes();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				return; // do nothing
			}
		});

		// Starting values for SharedPrefs are created from Start and Game,
		// if this is the first start of the App
		getRulesFromSharedPrefs();
		updateCheckboxesFromRuleIntArrays(2);
	}

	/**
	 * Update checkboxes when onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		getRulesFromSharedPrefs();
		updateCheckboxesFromRuleIntArrays(2);
	}

	/**
	 * Update checkboxes when onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		getRulesFromSharedPrefs();
		updateCheckboxesFromRuleIntArrays(2);
	}

	/**
	 * Store checkbox states to SharedPrefs onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		writeSharedPrefsFromCheckboxes();
		// update instance of World, if it exists
		getRulesFromSharedPrefs();
		updateWorldRules();
	}

	/**
	 * Store checkbox states to SharedPrefs onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		writeSharedPrefsFromCheckboxes();
		// update instance of World, if it exists
		getRulesFromSharedPrefs();
		updateWorldRules();
	}

	/**
	 * Updates the internal arrays, to update checkbox states and pass to
	 * Game.world.
	 */
	public void getRulesFromSharedPrefs() {
		// READ DATA AND UPDATE CHECKBOXES
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		// String loading and internal ruleset update
		String bRulesRedString = prefs.getString("bRulesRedString", "void");
		String sRulesRedString = prefs.getString("sRulesRedString", "void");
		String bRulesBlueString = prefs.getString("bRulesBlueString", "void");
		String sRulesBlueString = prefs.getString("sRulesBlueString", "void");

		bRulesRed = binStringToIntArr(bRulesRedString);
		sRulesRed = binStringToIntArr(sRulesRedString);
		bRulesBlue = binStringToIntArr(bRulesBlueString);
		sRulesBlue = binStringToIntArr(sRulesBlueString);
	}// end fn

	/**
	 * If the RuleEditor is left, update Game.world RuleSets.
	 */
	public void updateWorldRules() {
		if (Game.world != null) {
			Game.world.setbRedBufferArr(bRulesRed);
			Game.world.setsRedBufferArr(sRulesRed);
			Game.world.setbBlueBufferArr(bRulesBlue);
			Game.world.setsBlueBufferArr(sRulesBlue);

			// // Update strings in Game
			Game.redRuleSetString = ruleIntArrsToRuleString(bRulesRed,
					sRulesRed);
			Game.blueRuleSetString = ruleIntArrsToRuleString(bRulesBlue,
					sRulesBlue);
		}
	}// end fn

	/**
	 * Converts two int arrays (= one ruleset) to a string in Golly/RLE
	 * notation.
	 * <p>
	 * E.g.: "000100000" + "001100000" = "B3/S23"
	 * 
	 * @param bArr
	 * @param sArr
	 * @return
	 */
	public static String ruleIntArrsToRuleString(int[] bArr, int[] sArr) {
		ArrayList<Integer> bList = new ArrayList<Integer>();
		ArrayList<Integer> sList = new ArrayList<Integer>();
		StringBuffer sb = new StringBuffer();
		// convert from intArr to ArrayList
		for (int i = 0; i < bArr.length; i++) {
			if (bArr[i] != 0) {
				bList.add(i);
			}
		}
		for (int j = 0; j < sArr.length; j++) {
			if (sArr[j] != 0) {
				sList.add(j);
			}
		}
		// create stringBuffer from lists
		sb.append("B");
		for (int k = 0; k < bList.size(); k++) {
			sb.append(Integer.toString(bList.get(k)));
		}
		sb.append("/S");
		for (int l = 0; l < sList.size(); l++) {
			sb.append(Integer.toString(sList.get(l)));
		}
		// return string
		String gollyString = sb.toString();
		return gollyString;
	}// end fn

	/**
	 * Converts an integer array (e.g. { 0, 0, 1, 0 } to a string.
	 * <p>
	 * Used for storage of rulesets in SharedPrefs.
	 * 
	 * @param intArr
	 * @return
	 */
	public static String intArrToBinString(int[] intArr) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < intArr.length; i++) {
			sb.append(intArr[i]);
		}
		String str = sb.toString();
		return str;
	}// end fn

	/**
	 * Converts a "binary string" (e.g. "001100000") to an integer array.
	 * <p>
	 * Used for loading ruleSets from SharedPrefs.
	 * 
	 * @param args
	 */
	public static int[] binStringToIntArr(String binStr) {
		int[] intArr = new int[binStr.length()]; // initialize
		char[] charArr = binStr.toCharArray();
		// for (int i = 0; i < charArr.length; i++) {
		// String tempStr = String.valueOf(charArr[i]);
		// intArr[i] = Integer.parseInt(tempStr);
		// }
		for (int i = 1; i < charArr.length; i++) {
			String tempStr = String.valueOf(charArr[i]);
			intArr[i] = Integer.parseInt(tempStr);
		}
		return (intArr);
	}// end fn

	// /**
	// * Resets all checkboxes to "Life".
	// */
	// public void resetAllCheckboxes(View v) {
	// // change buffers and reuse red for blue
	// int[] newbRulesRed = { 0, 0, 0, 1, 0, 0, 0, 0, 0 };
	// int[] newsRulesRed = { 0, 0, 1, 1, 0, 0, 0, 0, 0 };
	// bRulesRed = newbRulesRed;
	// sRulesRed = newsRulesRed;
	// bRulesBlue = newbRulesRed;
	// sRulesBlue = newsRulesRed;
	// // and then update all checkboxes from them
	// updateCheckboxesFromRuleIntArrays(2);
	// // to make sure blueToRed works everytime correct:
	// writeSharedPrefsFromCheckboxes();
	// getRulesFromSharedPrefs();
	// }// end fn

	// /**
	// * Resets red checkboxes to "Life".
	// */
	// public void resetRedCheckboxes(View v) {
	// // change buffers
	// int[] newbRulesRed = { 0, 0, 0, 1, 0, 0, 0, 0, 0 };
	// int[] newsRulesRed = { 0, 0, 1, 1, 0, 0, 0, 0, 0 };
	// bRulesRed = newbRulesRed;
	// sRulesRed = newsRulesRed;
	// // bRulesBlue = newbRulesRed;
	// // sRulesBlue = newsRulesRed;
	// // and then update red checkboxes from them
	// updateCheckboxesFromRuleIntArrays(0);
	// }// end fn

	// /**
	// * Resets blue checkboxes to "Life".
	// */
	// public void resetBlueCheckboxes(View v) {
	// // change buffers
	// int[] newbRulesBlue = { 0, 0, 0, 1, 0, 0, 0, 0, 0 };
	// int[] newsRulesBlue = { 0, 0, 1, 1, 0, 0, 0, 0, 0 };
	// bRulesBlue = newbRulesBlue;
	// sRulesBlue = newsRulesBlue;
	// // and then update blue checkboxes from them
	// updateCheckboxesFromRuleIntArrays(1);
	// }// end fn
	//

	// /**
	// * Sets blue checkboxes exactly like the red ones.
	// * <p>
	// * Costly way to do it, but it was easy to implement.
	// */
	// public void blueToRedCheckboxes(View v) {
	// // write checkboxes (may have changed since start)
	// writeSharedPrefsFromCheckboxes();
	// // update buffers
	// getRulesFromSharedPrefs();
	// // synchronize buffers
	// for (int i = 0; i < bRulesRed.length; i++) {
	// bRulesBlue[i] = bRulesRed[i];
	// sRulesBlue[i] = sRulesRed[i];
	// }
	// // and then update blue checkboxes from them
	// updateCheckboxesFromRuleIntArrays(1);
	// }// end fn

	// /**
	// * Randomizes red checkboxes.
	// */
	// public void randomizeRedCheckboxes(View v) {
	// // create a random number generator
	// Random ran = new Random();
	// // change buffers
	// int[] newbRulesRed = { ran.nextInt(2), ran.nextInt(2), ran.nextInt(2),
	// ran.nextInt(2), ran.nextInt(2), ran.nextInt(2), ran.nextInt(2),
	// ran.nextInt(2), ran.nextInt(2) };
	// int[] newsRulesRed = { ran.nextInt(2), ran.nextInt(2), ran.nextInt(2),
	// ran.nextInt(2), ran.nextInt(2), ran.nextInt(2), ran.nextInt(2),
	// ran.nextInt(2), ran.nextInt(2) };
	// bRulesRed = newbRulesRed;
	// sRulesRed = newsRulesRed;
	// // and then update red checkboxes from them
	// updateCheckboxesFromRuleIntArrays(0);
	// }// end fn
	//
	// /**
	// * Randomizes Blue checkboxes.
	// */
	// public void randomizeBlueCheckboxes(View v) {
	// // create a random number generator
	// Random ran = new Random();
	// // change buffers
	// int[] newbRulesBlue = { ran.nextInt(2), ran.nextInt(2), ran.nextInt(2),
	// ran.nextInt(2), ran.nextInt(2), ran.nextInt(2), ran.nextInt(2),
	// ran.nextInt(2), ran.nextInt(2) };
	// int[] newsRulesBlue = { ran.nextInt(2), ran.nextInt(2), ran.nextInt(2),
	// ran.nextInt(2), ran.nextInt(2), ran.nextInt(2), ran.nextInt(2),
	// ran.nextInt(2), ran.nextInt(2) };
	// bRulesBlue = newbRulesBlue;
	// sRulesBlue = newsRulesBlue;
	// // and then update blue checkboxes from them
	// updateCheckboxesFromRuleIntArrays(1);
	// }// end fn

	/**
	 * When the activity opens, the checkboxes are updated to reflect the stored
	 * rulesets.
	 */
	public void updateCheckboxesFromRuleIntArrays(int mode) { // mode: 0=red,
																// 1=blue,
																// 2=both
		// getRulesFromSharedPrefs(); // not needed here anymore

		if (mode == 0 || mode == 2) {

			// RED BIRTH CB
			// final CheckBox ckbRedB0 = (CheckBox)
			// findViewById(R.id.editor_ckbRedB0);
			// if (bRulesRed[0] == 1) {
			// ckbRedB0.setChecked(true);
			// } else {
			// ckbRedB0.setChecked(false);
			// }
			final CheckBox ckbRedB1 = (CheckBox) findViewById(R.id.editor_ckbRedB1);
			if (bRulesRed[1] == 1) {
				ckbRedB1.setChecked(true);
			} else {
				ckbRedB1.setChecked(false);
			}
			final CheckBox ckbRedB2 = (CheckBox) findViewById(R.id.editor_ckbRedB2);
			if (bRulesRed[2] == 1) {
				ckbRedB2.setChecked(true);
			} else {
				ckbRedB2.setChecked(false);
			}
			final CheckBox ckbRedB3 = (CheckBox) findViewById(R.id.editor_ckbRedB3);
			if (bRulesRed[3] == 1) {
				ckbRedB3.setChecked(true);
			} else {
				ckbRedB3.setChecked(false);
			}
			final CheckBox ckbRedB4 = (CheckBox) findViewById(R.id.editor_ckbRedB4);
			if (bRulesRed[4] == 1) {
				ckbRedB4.setChecked(true);
			} else {
				ckbRedB4.setChecked(false);
			}
			final CheckBox ckbRedB5 = (CheckBox) findViewById(R.id.editor_ckbRedB5);
			if (bRulesRed[5] == 1) {
				ckbRedB5.setChecked(true);
			} else {
				ckbRedB5.setChecked(false);
			}
			final CheckBox ckbRedB6 = (CheckBox) findViewById(R.id.editor_ckbRedB6);
			if (bRulesRed[6] == 1) {
				ckbRedB6.setChecked(true);
			} else {
				ckbRedB6.setChecked(false);
			}
			final CheckBox ckbRedB7 = (CheckBox) findViewById(R.id.editor_ckbRedB7);
			if (bRulesRed[7] == 1) {
				ckbRedB7.setChecked(true);
			} else {
				ckbRedB7.setChecked(false);
			}
			final CheckBox ckbRedB8 = (CheckBox) findViewById(R.id.editor_ckbRedB8);
			if (bRulesRed[8] == 1) {
				ckbRedB8.setChecked(true);
			} else {
				ckbRedB8.setChecked(false);
			}

			// RED SURVIVAL CB
			// final CheckBox ckbRedS0 = (CheckBox)
			// findViewById(R.id.editor_ckbRedS0);
			// if (sRulesRed[0] == 1) {
			// ckbRedS0.setChecked(true);
			// } else {
			// ckbRedS0.setChecked(false);
			// }
			final CheckBox ckbRedS1 = (CheckBox) findViewById(R.id.editor_ckbRedS1);
			if (sRulesRed[1] == 1) {
				ckbRedS1.setChecked(true);
			} else {
				ckbRedS1.setChecked(false);
			}
			final CheckBox ckbRedS2 = (CheckBox) findViewById(R.id.editor_ckbRedS2);
			if (sRulesRed[2] == 1) {
				ckbRedS2.setChecked(true);
			} else {
				ckbRedS2.setChecked(false);
			}
			final CheckBox ckbRedS3 = (CheckBox) findViewById(R.id.editor_ckbRedS3);
			if (sRulesRed[3] == 1) {
				ckbRedS3.setChecked(true);
			} else {
				ckbRedS3.setChecked(false);
			}
			final CheckBox ckbRedS4 = (CheckBox) findViewById(R.id.editor_ckbRedS4);
			if (sRulesRed[4] == 1) {
				ckbRedS4.setChecked(true);
			} else {
				ckbRedS4.setChecked(false);
			}
			final CheckBox ckbRedS5 = (CheckBox) findViewById(R.id.editor_ckbRedS5);
			if (sRulesRed[5] == 1) {
				ckbRedS5.setChecked(true);
			} else {
				ckbRedS5.setChecked(false);
			}
			final CheckBox ckbRedS6 = (CheckBox) findViewById(R.id.editor_ckbRedS6);
			if (sRulesRed[6] == 1) {
				ckbRedS6.setChecked(true);
			} else {
				ckbRedS6.setChecked(false);
			}
			final CheckBox ckbRedS7 = (CheckBox) findViewById(R.id.editor_ckbRedS7);
			if (sRulesRed[7] == 1) {
				ckbRedS7.setChecked(true);
			} else {
				ckbRedS7.setChecked(false);
			}
			final CheckBox ckbRedS8 = (CheckBox) findViewById(R.id.editor_ckbRedS8);
			if (sRulesRed[8] == 1) {
				ckbRedS8.setChecked(true);
			} else {
				ckbRedS8.setChecked(false);
			}
		}
		//
		//
		//
		//

		if (mode == 1 || mode == 2) {

			// BLUE BIRTH CB
			// final CheckBox ckbBlueB0 = (CheckBox)
			// findViewById(R.id.editor_ckbBlueB0);
			// if (bRulesBlue[0] == 1) {
			// ckbBlueB0.setChecked(true);
			// } else {
			// ckbBlueB0.setChecked(false);
			// }
			final CheckBox ckbBlueB1 = (CheckBox) findViewById(R.id.editor_ckbBlueB1);
			if (bRulesBlue[1] == 1) {
				ckbBlueB1.setChecked(true);
			} else {
				ckbBlueB1.setChecked(false);
			}
			final CheckBox ckbBlueB2 = (CheckBox) findViewById(R.id.editor_ckbBlueB2);
			if (bRulesBlue[2] == 1) {
				ckbBlueB2.setChecked(true);
			} else {
				ckbBlueB2.setChecked(false);
			}
			final CheckBox ckbBlueB3 = (CheckBox) findViewById(R.id.editor_ckbBlueB3);
			if (bRulesBlue[3] == 1) {
				ckbBlueB3.setChecked(true);
			} else {
				ckbBlueB3.setChecked(false);
			}
			final CheckBox ckbBlueB4 = (CheckBox) findViewById(R.id.editor_ckbBlueB4);
			if (bRulesBlue[4] == 1) {
				ckbBlueB4.setChecked(true);
			} else {
				ckbBlueB4.setChecked(false);
			}
			final CheckBox ckbBlueB5 = (CheckBox) findViewById(R.id.editor_ckbBlueB5);
			if (bRulesBlue[5] == 1) {
				ckbBlueB5.setChecked(true);
			} else {
				ckbBlueB5.setChecked(false);
			}
			final CheckBox ckbBlueB6 = (CheckBox) findViewById(R.id.editor_ckbBlueB6);
			if (bRulesBlue[6] == 1) {
				ckbBlueB6.setChecked(true);
			} else {
				ckbBlueB6.setChecked(false);
			}
			final CheckBox ckbBlueB7 = (CheckBox) findViewById(R.id.editor_ckbBlueB7);
			if (bRulesBlue[7] == 1) {
				ckbBlueB7.setChecked(true);
			} else {
				ckbBlueB7.setChecked(false);
			}
			final CheckBox ckbBlueB8 = (CheckBox) findViewById(R.id.editor_ckbBlueB8);
			if (bRulesBlue[8] == 1) {
				ckbBlueB8.setChecked(true);
			} else {
				ckbBlueB8.setChecked(false);
			}

			// BLUE SURVIVAL CB
			// final CheckBox ckbBlueS0 = (CheckBox)
			// findViewById(R.id.editor_ckbBlueS0);
			// if (sRulesBlue[0] == 1) {
			// ckbBlueS0.setChecked(true);
			// } else {
			// ckbBlueS0.setChecked(false);
			// }
			final CheckBox ckbBlueS1 = (CheckBox) findViewById(R.id.editor_ckbBlueS1);
			if (sRulesBlue[1] == 1) {
				ckbBlueS1.setChecked(true);
			} else {
				ckbBlueS1.setChecked(false);
			}
			final CheckBox ckbBlueS2 = (CheckBox) findViewById(R.id.editor_ckbBlueS2);
			if (sRulesBlue[2] == 1) {
				ckbBlueS2.setChecked(true);
			} else {
				ckbBlueS2.setChecked(false);
			}
			final CheckBox ckbBlueS3 = (CheckBox) findViewById(R.id.editor_ckbBlueS3);
			if (sRulesBlue[3] == 1) {
				ckbBlueS3.setChecked(true);
			} else {
				ckbBlueS3.setChecked(false);
			}
			final CheckBox ckbBlueS4 = (CheckBox) findViewById(R.id.editor_ckbBlueS4);
			if (sRulesBlue[4] == 1) {
				ckbBlueS4.setChecked(true);
			} else {
				ckbBlueS4.setChecked(false);
			}
			final CheckBox ckbBlueS5 = (CheckBox) findViewById(R.id.editor_ckbBlueS5);
			if (sRulesBlue[5] == 1) {
				ckbBlueS5.setChecked(true);
			} else {
				ckbBlueS5.setChecked(false);
			}
			final CheckBox ckbBlueS6 = (CheckBox) findViewById(R.id.editor_ckbBlueS6);
			if (sRulesBlue[6] == 1) {
				ckbBlueS6.setChecked(true);
			} else {
				ckbBlueS6.setChecked(false);
			}
			final CheckBox ckbBlueS7 = (CheckBox) findViewById(R.id.editor_ckbBlueS7);
			if (sRulesBlue[7] == 1) {
				ckbBlueS7.setChecked(true);
			} else {
				ckbBlueS7.setChecked(false);
			}
			final CheckBox ckbBlueS8 = (CheckBox) findViewById(R.id.editor_ckbBlueS8);
			if (sRulesBlue[8] == 1) {
				ckbBlueS8.setChecked(true);
			} else {
				ckbBlueS8.setChecked(false);
			}
		}
	}// end fn

	/**
	 * Stores the current rulesets (as read from the checkbox states) to SharedPrefs.
	 */
	public void writeSharedPrefsFromCheckboxes() {
		// First: READ CHECKBOX VALUES AND UPDATE RULESET

		// Red
		// BIRTH
		// final CheckBox ckbRedB0 = (CheckBox)
		// findViewById(R.id.editor_ckbRedB0);
		// if (ckbRedB0.isChecked()) {
		// bRulesRed[0] = 1;
		// } else {
		// bRulesRed[0] = 0;
		// }
		final CheckBox ckbRedB1 = (CheckBox) findViewById(R.id.editor_ckbRedB1);
		if (ckbRedB1.isChecked()) {
			bRulesRed[1] = 1;
		} else {
			bRulesRed[1] = 0;
		}
		final CheckBox ckbRedB2 = (CheckBox) findViewById(R.id.editor_ckbRedB2);
		if (ckbRedB2.isChecked()) {
			bRulesRed[2] = 1;
		} else {
			bRulesRed[2] = 0;
		}
		final CheckBox ckbRedB3 = (CheckBox) findViewById(R.id.editor_ckbRedB3);
		if (ckbRedB3.isChecked()) {
			bRulesRed[3] = 1;
		} else {
			bRulesRed[3] = 0;
		}
		final CheckBox ckbRedB4 = (CheckBox) findViewById(R.id.editor_ckbRedB4);
		if (ckbRedB4.isChecked()) {
			bRulesRed[4] = 1;
		} else {
			bRulesRed[4] = 0;
			;
		}
		final CheckBox ckbRedB5 = (CheckBox) findViewById(R.id.editor_ckbRedB5);
		if (ckbRedB5.isChecked()) {
			bRulesRed[5] = 1;
		} else {
			bRulesRed[5] = 0;
		}
		final CheckBox ckbRedB6 = (CheckBox) findViewById(R.id.editor_ckbRedB6);
		if (ckbRedB6.isChecked()) {
			bRulesRed[6] = 1;
		} else {
			bRulesRed[6] = 0;
		}
		final CheckBox ckbRedB7 = (CheckBox) findViewById(R.id.editor_ckbRedB7);
		if (ckbRedB7.isChecked()) {
			bRulesRed[7] = 1;
		} else {
			bRulesRed[7] = 0;
		}
		final CheckBox ckbRedB8 = (CheckBox) findViewById(R.id.editor_ckbRedB8);
		if (ckbRedB8.isChecked()) {
			bRulesRed[8] = 1;
		} else {
			bRulesRed[8] = 0;
		}

		// Red
		// SURVIVAL
		// final CheckBox ckbRedS0 = (CheckBox)
		// findViewById(R.id.editor_ckbRedS0);
		// if (ckbRedS0.isChecked()) {
		// sRulesRed[0] = 1;
		// } else {
		// sRulesRed[0] = 0;
		// }
		final CheckBox ckbRedS1 = (CheckBox) findViewById(R.id.editor_ckbRedS1);
		if (ckbRedS1.isChecked()) {
			sRulesRed[1] = 1;
		} else {
			sRulesRed[1] = 0;
		}
		final CheckBox ckbRedS2 = (CheckBox) findViewById(R.id.editor_ckbRedS2);
		if (ckbRedS2.isChecked()) {
			sRulesRed[2] = 1;
		} else {
			sRulesRed[2] = 0;
		}
		final CheckBox ckbRedS3 = (CheckBox) findViewById(R.id.editor_ckbRedS3);
		if (ckbRedS3.isChecked()) {
			sRulesRed[3] = 1;
		} else {
			sRulesRed[3] = 0;
		}
		final CheckBox ckbRedS4 = (CheckBox) findViewById(R.id.editor_ckbRedS4);
		if (ckbRedS4.isChecked()) {
			sRulesRed[4] = 1;
		} else {
			sRulesRed[4] = 0;
		}
		final CheckBox ckbRedS5 = (CheckBox) findViewById(R.id.editor_ckbRedS5);
		if (ckbRedS5.isChecked()) {
			sRulesRed[5] = 1;
		} else {
			sRulesRed[5] = 0;
		}
		final CheckBox ckbRedS6 = (CheckBox) findViewById(R.id.editor_ckbRedS6);
		if (ckbRedS6.isChecked()) {
			sRulesRed[6] = 1;
		} else {
			sRulesRed[6] = 0;
		}
		final CheckBox ckbRedS7 = (CheckBox) findViewById(R.id.editor_ckbRedS7);
		if (ckbRedS7.isChecked()) {
			sRulesRed[7] = 1;
		} else {
			sRulesRed[7] = 0;
		}
		final CheckBox ckbRedS8 = (CheckBox) findViewById(R.id.editor_ckbRedS8);
		if (ckbRedS8.isChecked()) {
			sRulesRed[8] = 1;
		} else {
			sRulesRed[8] = 0;
		}

		// Blue
		// BIRTH
		// final CheckBox ckbBlueB0 = (CheckBox)
		// findViewById(R.id.editor_ckbBlueB0);
		// if (ckbBlueB0.isChecked()) {
		// bRulesBlue[0] = 1;
		// } else {
		// bRulesBlue[0] = 0;
		// }
		final CheckBox ckbBlueB1 = (CheckBox) findViewById(R.id.editor_ckbBlueB1);
		if (ckbBlueB1.isChecked()) {
			bRulesBlue[1] = 1;
		} else {
			bRulesBlue[1] = 0;
		}
		final CheckBox ckbBlueB2 = (CheckBox) findViewById(R.id.editor_ckbBlueB2);
		if (ckbBlueB2.isChecked()) {
			bRulesBlue[2] = 1;
			;
		} else {
			bRulesBlue[2] = 0;
		}
		final CheckBox ckbBlueB3 = (CheckBox) findViewById(R.id.editor_ckbBlueB3);
		if (ckbBlueB3.isChecked()) {
			bRulesBlue[3] = 1;
		} else {
			bRulesBlue[3] = 0;
		}
		final CheckBox ckbBlueB4 = (CheckBox) findViewById(R.id.editor_ckbBlueB4);
		if (ckbBlueB4.isChecked()) {
			bRulesBlue[4] = 1;
		} else {
			bRulesBlue[4] = 0;
		}
		final CheckBox ckbBlueB5 = (CheckBox) findViewById(R.id.editor_ckbBlueB5);
		if (ckbBlueB5.isChecked()) {
			bRulesBlue[5] = 1;
		} else {
			bRulesBlue[5] = 0;
		}
		final CheckBox ckbBlueB6 = (CheckBox) findViewById(R.id.editor_ckbBlueB6);
		if (ckbBlueB6.isChecked()) {
			bRulesBlue[6] = 1;
		} else {
			bRulesBlue[6] = 0;
		}
		final CheckBox ckbBlueB7 = (CheckBox) findViewById(R.id.editor_ckbBlueB7);
		if (ckbBlueB7.isChecked()) {
			bRulesBlue[7] = 1;
		} else {
			bRulesBlue[7] = 0;
		}
		final CheckBox ckbBlueB8 = (CheckBox) findViewById(R.id.editor_ckbBlueB8);
		if (ckbBlueB8.isChecked()) {
			bRulesBlue[8] = 1;
		} else {
			bRulesBlue[8] = 0;
		}

		// Blue
		// SURVIVAL
		// final CheckBox ckbBlueS0 = (CheckBox)
		// findViewById(R.id.editor_ckbBlueS0);
		// if (ckbBlueS0.isChecked()) {
		// sRulesBlue[0] = 1;
		// } else {
		// sRulesBlue[0] = 0;
		// }
		final CheckBox ckbBlueS1 = (CheckBox) findViewById(R.id.editor_ckbBlueS1);
		if (ckbBlueS1.isChecked()) {
			sRulesBlue[1] = 1;
		} else {
			sRulesBlue[1] = 0;
		}
		final CheckBox ckbBlueS2 = (CheckBox) findViewById(R.id.editor_ckbBlueS2);
		if (ckbBlueS2.isChecked()) {
			sRulesBlue[2] = 1;
		} else {
			sRulesBlue[2] = 0;
		}
		final CheckBox ckbBlueS3 = (CheckBox) findViewById(R.id.editor_ckbBlueS3);
		if (ckbBlueS3.isChecked()) {
			sRulesBlue[3] = 1;
		} else {
			sRulesBlue[3] = 0;
		}
		final CheckBox ckbBlueS4 = (CheckBox) findViewById(R.id.editor_ckbBlueS4);
		if (ckbBlueS4.isChecked()) {
			sRulesBlue[4] = 1;
		} else {
			sRulesBlue[4] = 0;
		}
		final CheckBox ckbBlueS5 = (CheckBox) findViewById(R.id.editor_ckbBlueS5);
		if (ckbBlueS5.isChecked()) {
			sRulesBlue[5] = 1;
		} else {
			sRulesBlue[5] = 0;
		}
		final CheckBox ckbBlueS6 = (CheckBox) findViewById(R.id.editor_ckbBlueS6);
		if (ckbBlueS6.isChecked()) {
			sRulesBlue[6] = 1;
		} else {
			sRulesBlue[6] = 0;
		}
		final CheckBox ckbBlueS7 = (CheckBox) findViewById(R.id.editor_ckbBlueS7);
		if (ckbBlueS7.isChecked()) {
			sRulesBlue[7] = 1;
		} else {
			sRulesBlue[7] = 0;
		}
		final CheckBox ckbBlueS8 = (CheckBox) findViewById(R.id.editor_ckbBlueS8);
		if (ckbBlueS8.isChecked()) {
			sRulesBlue[8] = 1;
		} else {
			sRulesBlue[8] = 0;
		}

		// Second: WRITE CHECKBOXES AS BIN STRING TO SHAREDPREFS
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();

		// Third: STRING CREATION AND WRITING
		String str = intArrToBinString(bRulesRed);
		editor.putString("bRulesRedString", str);

		str = intArrToBinString(sRulesRed);
		editor.putString("sRulesRedString", str);

		str = intArrToBinString(bRulesBlue);
		editor.putString("bRulesBlueString", str);

		str = intArrToBinString(sRulesBlue);
		editor.putString("sRulesBlueString", str);

		editor.commit();
	}// end fn
}
