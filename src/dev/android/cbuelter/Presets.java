package dev.android.cbuelter;

import java.util.HashMap;

import android.util.Log;

public class Presets {

	/**
	 * Used to store ruleset presets. A hash map is used to easily access a preset by its name instead of an index.
	 */
	public static HashMap<String, String> presetHashMap = new HashMap<String, String>();

	/**
	 * Creates the preset hash map. The keys have to be exactly the same as in
	 * the string array in <code>strings.xml</code>
	 */
	public static void initializePresetHashMap() {

		presetHashMap.put("None", "B/S");
		presetHashMap.put("Life (Default)", "B3/S23");
		presetHashMap.put("2x2", "B36/S125");
		presetHashMap.put("34 Life", "B34/S34");
		presetHashMap.put("Amoeba", "B357/S1358");
		presetHashMap.put("Assimilation", "B345/S4567");
		presetHashMap.put("Coagulations", "B378/S235678");
		presetHashMap.put("Coral", "B3/S45678");
		presetHashMap.put("Day and Night", "B34678/S3678");
		presetHashMap.put("Diamoeba", "B35678/S5678");
		presetHashMap.put("Flakes", "B3/S012345678");
		presetHashMap.put("Gnarl", "B1/S1");
		presetHashMap.put("High Life", "B36/S23");
		presetHashMap.put("Long Life", "B345/S5");
		presetHashMap.put("Inverse Life", "B0123478/S01234678");
		presetHashMap.put("Maze", "B3/S12345");
		presetHashMap.put("Maze with Mice", "B37/S12345");
		presetHashMap.put("Mazectric", "B3/S1234");
		presetHashMap.put("Corrosion of Conformity", "B3/S124");
		presetHashMap.put("Move", "B368/S245");
		presetHashMap.put("Pseudo Life", "B357/S238");
		presetHashMap.put("Replicator", "B1357/S1357");
		presetHashMap.put("Seeds", "B2/S");
		presetHashMap.put("Serviettes", "B234/S");
		presetHashMap.put("Stains", "B3678/S235678");
		presetHashMap.put("Walled Cities", "B45678/S2345");
		presetHashMap.put("Life without Death", "B3/S012345678");

	}// end fn

	/**
	 * Converts the Golly/RLE String to either a birth or survival rule int
	 * array.
	 * <p>
	 * Called twice with different modes to get the whole rule that is then
	 * applied to the checkboxes.
	 * 
	 * @param gollyPartStr
	 * @param mode
	 * @return
	 */
	public static int[] gollyStringToRuleIntArr(String gollyPartStr, int mode) {
		// mode: 0 = convert to birth ruleIntArr, 1 = convert to survival
		// ruleIntArr
		int[] xIntArr = { 0, 0, 0, 0, 0, 0, 0, 0, 0 }; // initialize result

		// split into parts using delimiters
		String[] temp;
		String[] xTemp;
		temp = gollyPartStr.split("/");
		if (mode == 0) {
			xTemp = temp[0].split("B");
		} else {
			xTemp = temp[1].split("S");
		}

		
		if (xTemp.length > 0) { // handle "B/S" cases with no numbers

			int[] xNumArr = new int[xTemp[1].length()]; // initialize
			char[] xCharArr = xTemp[1].toCharArray();
			// get single numbers
			for (int i = 0; i < xCharArr.length; i++) {
				String numStr = String.valueOf(xCharArr[i]);
				xNumArr[i] = Integer.parseInt(numStr);
			}
			// use numbers to construct the binary string
			int n = 0; // starting index for numArr
			for (int i = 0; i < 9; i++) {
				if (i == xNumArr[n]) {
					xIntArr[i] = 1;
					if (xNumArr.length > (n + 1)) {
						n++;
					}
				} else {
					xIntArr[i] = 0;
				}
			}
		}
		return xIntArr;
	}// end fn
}
