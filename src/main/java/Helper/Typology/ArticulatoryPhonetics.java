package Helper.Typology;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * integrates information about the distinctive features of sounds
 * 
 * @author abischoff
 *
 */
public class ArticulatoryPhonetics {

	private static HashMap<String,String[]> merkmalsphonetik = new HashMap<String,String[]>();
	private static HashMap<String,String[]> diacritics = new HashMap<String,String[]>();
	private static String standardMethod = "Distinktiv";

	/**
	 * read post-diacritics from Merkmalphonetik_IPA
	 * @return arrayList with diacritics
	 */
	public static ArrayList<String> readDiacritics() {
		ArrayList<String> dia = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader( new InputStreamReader ( new FileInputStream(".\\data\\rawData\\Merkmalsphonetik\\"+standardMethod+"\\Merkmalsphonetik_IPA"), UTF_8));
			boolean readPostDiacritics = false; //when do the post-diacritics occur in the file?
			String line = br.readLine();
			while (line != null) {
				
				if (readPostDiacritics) {
					String[] soundFeat = line.split("\t");
					dia.add(soundFeat[0]);
				}
				if (line.contains("##POST##")) {
					readPostDiacritics = true;
				}
				line = br.readLine();
			}
			
			br.close();
			
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return dia;
	}
	
	/**
	 * reads the file Merkmalsphonetik_IPA
	 * @return a map sound -> [features]
	 */
	public static HashMap<String,String[]> readIPAPhonetics() {

		try {
			BufferedReader br = new BufferedReader( new InputStreamReader ( new FileInputStream(".\\data\\rawData\\Merkmalsphonetik\\"+standardMethod+"\\Merkmalsphonetik_IPA"), UTF_8));
			boolean noPreDiacritics = true;//point when post-diacritics occur in the file
			String line = br.readLine();
			while (line != null) {
				if (noPreDiacritics) {
					merkmalsphonetik.put(line.split("\t")[0],line.replaceFirst("^.*?\t", "").split("\t"));
				} else {
					diacritics.put(line.split("\t")[0],line.replaceFirst("^.*?\t", "").split("\t"));
				}
				if (line.contains("##PRE##")) {
					noPreDiacritics = false;
				}
				line = br.readLine();
			}
			
			br.close();
			
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return merkmalsphonetik;
	}
	
	/**
	 * returns the features of a sound
	 * @param laut	sound
	 * @return an array with the features of the sound
	 */
	public static String[] getFeaturesOf(String laut) {
		if (merkmalsphonetik.containsKey(laut)) {
			return merkmalsphonetik.get(laut);
		} else if (merkmalsphonetik.size() == 0) {
			readIPAPhonetics();
		} else if (laut.contentEquals("#")) {
			return new String[0];
		}
		ArrayList<String> features = new ArrayList<String>();
		char[] supra = laut.toCharArray();
		for (char singl : supra) {
			if (merkmalsphonetik.containsKey(Character.toString(singl))) {
				for (String feat : merkmalsphonetik.get(Character.toString(singl))) {
					features.add(feat);
				}
			} else if (diacritics.containsKey(Character.toString(singl))) {
				for (String feat : diacritics.get(Character.toString(singl))) {
					features.add(feat);
					if (!checkForNonSense(features,feat).contentEquals("")) { 
						features.remove(checkForNonSense(features,feat));
					}
				}

			} else {
				System.err.println("IPA sound not found in list: " + laut + " (" + singl + ") ");
			}
		}
		return features.toArray(new String[features.size()]);
	}
	

	/**
	 * checks for contradictory features, e.g., long and short at the same time
	 * @param features	list of features
	 * @param string	feature in question
	 * @return "" if it is ok, else the name of the correct feature
	 */
	private static String checkForNonSense(ArrayList<String> features, String string) {
		if (string.matches(".*lang") && features.contains("kurz")) {
			return "kurz";
			
		} else if (features.contains("un"+string)) {
			return "un"+string;
		}
		return "";
	}

	/**
	 * Setter for the method
	 * the gap method needs negative pairs and, therefore, its own file
	 * @param gap	name of the method
	 */
	public static void setMethod(String gap) {
		standardMethod = gap;
	}
	
	
}
