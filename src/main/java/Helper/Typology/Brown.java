package Helper.Typology;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * integrates information about empirical data from Brown et al.
 * 
 * @author abischoff
 *
 */
public class Brown {

	private static HashMap<String, HashMap<String, Integer>> brown = new HashMap<String,HashMap<String, Integer>>();//p -> {m -> 22, d -> 34, ...}
	private static HashMap<String,String> IPA2ASJP = new HashMap<String,String>();
	
	/**
	 * reads the file WichmannsListe.txt
	 * @return a map of the type: p -> {m -> 22, d -> 34, ...}
	 */
	public static HashMap<String,HashMap<String,Integer>> readBrown() {

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(".\\data\\rawData\\Universal\\WichmannsListe.txt"), "UTF-8"));
			String line = br.readLine();
			while (line != null) {
				String mySound = line.split("\\t")[0];
				String[] correspondences = line.replaceFirst("^.*?\\t", "").split("\\t");
				HashMap<String,Integer> corrs = new HashMap<String,Integer>();
				int total = 0;
				for (String corr : correspondences) {
					String[] spl = corr.split("\\s");
					String corrSound = spl[0];
					Integer freq = Integer.parseInt(spl[1]);
					total += freq;
					corrs.put(corrSound, freq);
				}
				corrs.put("TOTAL", total);
				brown.put(mySound, corrs);
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		IPA2ASJP.put("p","p"); IPA2ASJP.put("ɸ","p"); IPA2ASJP.put("b","b"); IPA2ASJP.put("β","b"); IPA2ASJP.put("f","f"); IPA2ASJP.put("v","v"); IPA2ASJP.put("m","m"); IPA2ASJP.put("w","w"); IPA2ASJP.put("θ","8"); IPA2ASJP.put("ð","8"); IPA2ASJP.put("n̪","4"); IPA2ASJP.put("t","t"); IPA2ASJP.put("d","d"); IPA2ASJP.put("s","s"); IPA2ASJP.put("z","z"); IPA2ASJP.put("ʦ","c"); IPA2ASJP.put("ʣ","c"); IPA2ASJP.put("n","n"); IPA2ASJP.put("ɾ","r"); IPA2ASJP.put("r","r"); IPA2ASJP.put("ʀ","r"); IPA2ASJP.put("ɽ","r"); IPA2ASJP.put("l","l"); IPA2ASJP.put("ʃ","S"); IPA2ASJP.put("ʒ","Z"); IPA2ASJP.put("ʧ","C"); IPA2ASJP.put("ʤ","j"); IPA2ASJP.put("c","T"); IPA2ASJP.put("ɟ","T"); IPA2ASJP.put("ɲ","5"); IPA2ASJP.put("j","y"); IPA2ASJP.put("ɡ","g"); IPA2ASJP.put("x","x"); IPA2ASJP.put("ɣ","x"); IPA2ASJP.put("ŋ","N"); IPA2ASJP.put("q","q"); IPA2ASJP.put("ɢ","G"); IPA2ASJP.put("χ","X"); IPA2ASJP.put("ʁ","X "); IPA2ASJP.put("ħ","X"); IPA2ASJP.put("ʕ","X"); IPA2ASJP.put("h","h"); IPA2ASJP.put("ɦ","h"); IPA2ASJP.put("Ɂ","7"); IPA2ASJP.put("ʔ", "7"); IPA2ASJP.put("ʟ","L"); IPA2ASJP.put("ɭ","L"); IPA2ASJP.put("ʎ","L"); IPA2ASJP.put("!","!"); IPA2ASJP.put("ǀ","!"); IPA2ASJP.put("ǁ","!"); IPA2ASJP.put("ǂ","!");
		IPA2ASJP.put("i","i"); IPA2ASJP.put("ɪ","i"); IPA2ASJP.put("y","i"); IPA2ASJP.put("ʏ","i"); IPA2ASJP.put("e","e"); IPA2ASJP.put("ø","e"); IPA2ASJP.put("æ","E"); IPA2ASJP.put("ɛ","E"); IPA2ASJP.put("ɶ","E"); IPA2ASJP.put("œ","E"); IPA2ASJP.put("ɨ","3"); IPA2ASJP.put("ɘ","3"); IPA2ASJP.put("ǝ","3"); IPA2ASJP.put("ɜ","3"); IPA2ASJP.put("ʉ","3"); IPA2ASJP.put("ɵ","3"); IPA2ASJP.put("ʚ","3"); IPA2ASJP.put("a","a"); IPA2ASJP.put("ɐ","a"); IPA2ASJP.put("ɯ","u"); IPA2ASJP.put("u","u"); IPA2ASJP.put("ɤ","o"); IPA2ASJP.put("ʌ","o"); IPA2ASJP.put("ɑ","o"); IPA2ASJP.put("o","o"); IPA2ASJP.put("ɔ","o"); IPA2ASJP.put("ɒ","o");			
		IPA2ASJP.put("ʰ","h"); IPA2ASJP.put("ʲ","j"); IPA2ASJP.put("ʷ","w"); IPA2ASJP.put("ᵂ","w"); IPA2ASJP.put("#", "Ø");

		return brown;

			
	}
	
	/**
	 * calculates the probability of two sounds based on the data from Brown et al.
	 * @param s0	sound1
	 * @param s1	sound2
	 * @return probability
	 */
	public static double getTypoProbablity(String s0, String s1) {

		if (brown.size() == 0) {
			readBrown();

		}
		
		String sound1 = s0.replaceAll("[:ːˑ̆ʼ|​||​​‿​͜͡​ ̥​​ ̊​​ ̬​​ ̻​​ ̪​​ ̺​]","");
		String sound2 = s1.replaceAll("[:ːˑ̆ʼ|​||​​‿​͜͡​ ̥​​ ̊​​ ̬​​ ̻​​ ̪​​ ̺​]","");
		String[] sounds =  new String[] {sound1,sound2};
		
		for (int i = 0 ; i < 2 ; i++ ) {
			
			String sound = sounds[i];
			
			//corrections for affricates
			sound = sound.replaceAll("ts","c").replaceAll("tʃ","C").replaceAll("dʒ","j").replaceAll("dz","j");
			
			for (char part : sound.toCharArray()) {
				if (IPA2ASJP.containsKey(Character.toString(part))) {

					sounds[i] = sound.replaceAll(Character.toString(part),IPA2ASJP.get(Character.toString(part)));
	
				}
				
			}
		}
		
		double value = 0.0;
		
		boolean missingSoundInASJP = true;
		
		for (String sound : sounds) {
			
			
			for (String key : brown.keySet()) {
				if (key.contentEquals(sound)) {
					missingSoundInASJP = false;
				}
			} 
			if (missingSoundInASJP && !sound.contentEquals("?")) {
				System.err.println("Missing sound in ASJP list: "+sound);
				missingSoundInASJP = true;
			}
		}
		
		if (sounds[0].contentEquals(sounds[1]) && !sounds[0].contentEquals("?")) {
			value = 0.25;
		} else if (!sounds[0].contentEquals("?") && !sounds[1].contentEquals("?") 
				&&brown.containsKey(sounds[0])
				&&brown.containsKey(sounds[1])) {
			//Formula: Freq /TotalSound1 + TotalSound2
			value = ((double) (brown.get(sounds[1]).get(sounds[0]) == null ? 0.0 : brown.get(sounds[1]).get(sounds[0])) ) 
					/ ((double) brown.get(sounds[0]).get("TOTAL") + (double) brown.get(sounds[1]).get("TOTAL"));
		}
		
		return value;
	}
}
