package Gap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import Helper.Log.Logging;

import java.util.TreeMap;

/**
 * implements an ideal phoneme system for the gap approach
 * 
 * @author abischoff
 *
 */
public class IdealPhonemeSystem {

	private HashMap<String,Feature> features = new HashMap<String,Feature>();
	private HashMap<String,String[]> phonetic;
	private HashMap<String,String[]> pairs = new HashMap<String,String[]>(); //eg. { voiceless-voiced -> [p - b, ...]}
	private ArrayList<String> actualSoundsAsList = new ArrayList<String>(); // ["voiceless, palatal, frikative", ...]
	private HashMap<String,IdealSound> idealSoundsAsList = new HashMap<String,IdealSound>(); // ["stl, palatal, frikativ", ...] -> ["changed sound"]
	private HashMap<List<String>,String> allIPA = new HashMap<List<String>,String>();// [bilabial,...] -> p
	private HashMap<List<String>,String> allDiacritica = new HashMap<List<String>,String>();// [labialized,...] -> ^w
	private String[] soundPair = new String[2]; //correspondence of the best as IPA 

	private String result;
	
	/**
	 * starts the methods
	 * @param phonetic	phoneme system of the language in question
	 */
	public IdealPhonemeSystem(HashMap<String,String[]> phonetic) {
		this.readIPA();
		this.setPhonetics(phonetic);
		this.setFeatures();
	}

	/**
	 * reads the file Merkmalsphonetik_IPA.txt
	 * to create IPA symbol of the generated pre-sounds
	 */
	public void readIPA() {
			
			try {
				if (!new File(".\\data\\rawData").exists()) {
					new File(".\\data\\rawData").mkdir();
				}
				if (!new File(".\\data\\rawData\\Merkmalsphonetik").exists()) {
					new File(".\\data\\rawData\\Merkmalsphonetik").mkdir();
				}
				if (!new File(".\\data\\rawData\\Merkmalsphonetik\\Gap").exists()) {
					new File(".\\data\\rawData\\Merkmalsphonetik\\Gap").mkdir();
				}
				BufferedReader ipa = new BufferedReader(new InputStreamReader(new FileInputStream(".\\data\\rawData\\Merkmalsphonetik\\Gap\\Merkmalsphonetik_IPA"),"UTF-8"));
				
				String lineIPA = ipa.readLine();
				boolean diacri = false;
				while (lineIPA != null) {
					if (lineIPA.contains("##POST##")) {
						diacri = true;
					}
					if (diacri) {
						allDiacritica.put(Arrays.asList(lineIPA.replaceFirst("^.*?\\t", "").split("\\t")),lineIPA.split("\\t")[0]);
					} else {
						allIPA.put(getRelevantFeaturesOf(lineIPA.replaceFirst("^.*?\\t", "").split("\\t+")),lineIPA.split("\\t")[0]);
					}
					lineIPA = ipa.readLine();

				}
				ipa.close();
				
			} catch (IOException e) {
				Logging.error(e.getLocalizedMessage());
			}
	}
	

	/**
	 * removes irrelevant features (e.g., consonant and vowel) and returns a new arrayList
	 * 
	 * @param feats	array of features of a sound
	 * @return	an ArrayList with relevant features
	 */
	public ArrayList<String> getRelevantFeaturesOf(String[] feats) {
		ArrayList<String> myFeats = new ArrayList<String>();
		for (String feat : feats) {
			if (feat.contentEquals("halbvokal")) {
				myFeats.add("approximant");
			} else if (!feat.startsWith("un") && !feat.contentEquals("zentralisiert") && !feat.contentEquals("konsonant") && !feat.contentEquals("vokal") && !feat.contentEquals("oral") && !feat.contentEquals("betont") && !feat.contentEquals("kurz")) {
				myFeats.add(feat);
			}
		}
		return myFeats;
	}
	
	/**
	 * takes features as string and returns the corresponding IPA sign
	 * @param feats	distinctive features as a string
	 * @return IPA symbol
	 */
	public String getIPA(String feats) {
		String output = "?";
		ArrayList<String> myFeats = getRelevantFeaturesOf(feats.replace("]:[", ", ").replace("[", "").replace("]","").split(", "));
		for (List<String> ipas : allIPA.keySet())  {
			if(ipas.containsAll(myFeats) && myFeats.containsAll(ipas)) {
				output = allIPA.get(ipas);
			}
		}
		
		if (output.contentEquals("?")) { 
			List<String> clone = new ArrayList<String>();
			clone.addAll(myFeats);
			String thisDia = "";
			for (List<String> dias : allDiacritica.keySet())  {
				
				if(myFeats.containsAll(dias)) {
					thisDia = allDiacritica.get(dias);
					clone.removeAll(dias);
					for (List<String> ipas : allIPA.keySet())  {
						
						if(ipas.containsAll(clone) && clone.containsAll(ipas)) {
							output = allIPA.get(ipas) + thisDia;
						}
					}
				}
			}
		}
		
		return output;
	}
	

	/**
	 * generates for each distinctive pair an fictive sound
	 * e.g.,
	 * affricate : plosive
	 * -> each plosive sound replaces affricate
	 * -> each affricate sound replaces plosive
	 * 
	 * add to the result list if missing
	 * 
	 * @return a map of sound with removed "unideal" sounds
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String,String[]> makeIdeal() {
		
		ArrayList<ArrayList<String>> chains = new ArrayList<ArrayList<String>>();
		
		for (String pair : pairs.keySet()) {
			String feat1 = pair.split(":")[0];
			String feat2 = pair.split(":")[1];
 
			/*
			 * for chain
			 */
			
			boolean filled = false;
			for (ArrayList<String> chain : chains) {
				
				if (chain.contains(feat1)) {
					if (!chain.contains(feat2))
						chain.add(feat2);
					filled = true;
				} 
				if (chain.contains(feat2)) {
					if (!chain.contains(feat1))
						chain.add(feat1);
					filled  =true;
				} 

			}
			if (!filled) {
				ArrayList<String> neu = new ArrayList<String>();
				
				neu.add(feat1);
				neu.add(feat2);
				chains.add(neu);
			}

			/*
			 * initialize IdealSoundSystem
			 */
			
			for (String s1 : features.get(feat1).getSounds()) {

				ArrayList<String> sFeats = new ArrayList<String>(Arrays.asList(phonetic.get(s1)));
				sFeats.remove(feat1);
				sFeats.add(feat2);
				Collections.sort(sFeats);
				String corresponding = sFeats.toString(); // "voiced palatal plosive" becomes "voiced palatal fricative"
				if (!idealSoundsAsList.containsKey(corresponding)) {
					IdealSound myIdeal = new IdealSound(corresponding);
					myIdeal.addChanged(feat1,feat2);
					idealSoundsAsList.put(corresponding,myIdeal);
				} else {
					IdealSound myChanges = idealSoundsAsList.get(corresponding);
					myChanges.addChanged(feat1,feat2);
					idealSoundsAsList.put(corresponding,myChanges);
				}
			}
			for (String s1 : features.get(feat2).getSounds()) {
				ArrayList<String> sFeats = new ArrayList<String>(Arrays.asList(phonetic.get(s1)));
				sFeats.remove(feat2);
				sFeats.add(feat1);
				Collections.sort(sFeats);
				String corresponding = sFeats.toString(); // "voiced palatal fricative" becomes "voiced palatal plosive"
				if (!idealSoundsAsList.containsKey(corresponding)) {
					IdealSound myIdeal = new IdealSound(corresponding);
					myIdeal.addChanged(feat2,feat1);
					idealSoundsAsList.put(corresponding,myIdeal);
				} else {
					IdealSound myChanges = idealSoundsAsList.get(corresponding);
					myChanges.addChanged(feat2,feat1);
					idealSoundsAsList.put(corresponding,myChanges);
				}
			}
		}
		
		HashMap<String,ArrayList<IdealSound>> missed = new HashMap<String,ArrayList<IdealSound>>();// contains only missing sounds
		TreeMap<Integer,ArrayList<String>> countMiss2 = new TreeMap<Integer,ArrayList<String>>(); // 6 for [consonant, labiodental,voiceless]
		
		for (String b : idealSoundsAsList.keySet()) {
			if (!actualSoundsAsList.contains(b)) {
				/*
				 * [consonant, labiodental, nasal, voiceless] becomes [consonant, labiodental, voiceless] -> {nasal}
				 */
				HashMap<String,String[]> formissed = idealSoundsAsList.get(b).getStrip();
				for (String key : formissed.keySet()) {


					if (missed.containsKey(key)) {
						ArrayList<IdealSound> changed = missed.get(key);
						changed.add(idealSoundsAsList.get(b));
						missed.put(key, changed);
						ArrayList<String> old = new ArrayList<String>();
						if (countMiss2.containsKey(getMissings(key,missed.get(key)).size())) {
							old = countMiss2.get(getMissings(key,missed.get(key)).size());
						} 
						old.add(key);
						countMiss2.put(getMissings(key,missed.get(key)).size(), old);

					} else {
						ArrayList<IdealSound> changed = new ArrayList<IdealSound>();
						changed.add(idealSoundsAsList.get(b));
						missed.put(key, changed);

						ArrayList<String> old = new ArrayList<String>();
						if (countMiss2.containsKey(getMissings(key,missed.get(key)).size())) {
							old = countMiss2.get(getMissings(key,missed.get(key)).size());
						} 
						old.add(key);
						countMiss2.put(getMissings(key,missed.get(key)).size(), old);
					}
					
					
					
				}
			}
		}
		TreeMap<Double,String[]> countMiss = new TreeMap<Double,String[]>(); 

		for (String missOne : missed.keySet()) {

			
			ArrayList<String> missOneFeats = getMissings(missOne, missed.get(missOne));
			
			for (String missTwo : missed.keySet()) {
				
				ArrayList<String> missTwoFeats = getMissings(missTwo, missed.get(missTwo));
				for (ArrayList<String> chain : chains) {

					if (chain.containsAll(missOneFeats) && chain.containsAll(missTwoFeats)) { //both belong to the same group

						ArrayList<String> existent1Feats = getNonMissing(missOne,missed.get(missOne));
						ArrayList<String> existent2Feats = getNonMissing(missTwo,missed.get(missTwo));
						String miss1 = "";
						String miss2 = "";
						ArrayList<String> miss1Feats = new ArrayList<String>();
						ArrayList<String> miss2Feats = new ArrayList<String>();
						
						if (existent1Feats.size() < existent2Feats.size()) { //the small one is younger
							miss2 = missOne;
							miss1 = missTwo;
							miss2Feats = missOneFeats;
							miss1Feats = missTwoFeats;
						} else {
							miss1 = missOne;
							miss2 = missTwo;
							miss1Feats = missOneFeats;
							miss2Feats = missTwoFeats;
						}
						
						boolean noDouble = true; //only if there is no intersection  and they complete each other
						for (String m : existent1Feats) {
							if (existent2Feats.contains(m)) {
								noDouble = false;
							}
						}

						if (noDouble) {

							double zahl = ((double) (miss1Feats.size()+miss2Feats.size()))/(double)chain.size(); //formula
							boolean overwrite = false; //this pre-sound is better than the previous defined pre-sound
							
							for (Double zahlOld : ( new ArrayList<Double>(countMiss.keySet()))) {
							
								if (countMiss.get(zahlOld)[1].contentEquals(miss2 + ":"+ getNonMissing(miss2, missed.get(miss2)))) { //the same post-sound but another pre-sound -> is this pre-sound the better one?
									String[] old = countMiss.get(zahlOld)[0].split(":")[0].replaceAll("[\\[\\]]", "").split(", ");
									String[] neu = miss1.replaceAll("[\\[\\]]", "").split(", ");
									ArrayList<String> postSound = new ArrayList<String>(Arrays.asList(countMiss.get(zahlOld)[1].split(":")[0].replaceAll("[\\[\\]]", "").split(", ")));
									
									int schnitt1 = 0;
									int schnitt2 = 0; //intersection new and post-Sound
									for (String oldie : old) {
										if (postSound.contains(oldie))
											schnitt1++;
									}
									for (String neuie : neu) {
										if (postSound.contains(neuie))
											schnitt2++;
									}
									
	
									if (schnitt2 > schnitt1) {//the new one has a bigger intersection than the old one
										overwrite = true;
										countMiss.remove(zahlOld);
									}
									
								}
							}
							
							
							if (!countMiss.containsKey(zahl) || overwrite) {//overwrites only if overwrite is true
								String[] a = new String[2];

								ArrayList<String> potentialPreFeatures = getMissings(miss1, missed.get(miss1));
								String potentialPreFeature = "";

								for (String pot : getNonMissing(miss2, missed.get(miss2))) {
									if (potentialPreFeatures.contains(pot)) {
										potentialPreFeature = potentialPreFeature.replaceAll("(\\[|\\])", "").replaceAll("(.*)", "[$1, " +pot+ "]").replaceAll("\\[, ", "[");
										potentialPreFeatures.remove(pot);
									}
								}
								if (potentialPreFeature.contentEquals("")) {
									potentialPreFeature = potentialPreFeatures.toString();
								}
								a[0] = miss1 + ":"+ potentialPreFeature;
								a[1] = miss2 + ":"+ getNonMissing(miss2, missed.get(miss2)).subList(0, 1); //if possible only choose one 
								
								countMiss.put(zahl, a);
							}
						}

					}
				}
			}
		}
		
		Entry<Double, String[]> firstOut = countMiss.pollLastEntry();
		if (firstOut == null)
			return null;

		this.result = "Lautwandel: " + firstOut.getValue()[0] + " > " + firstOut.getValue()[1] + "\t" + getIPA(firstOut.getValue()[0]) + " > " + getIPA(firstOut.getValue()[1]);
		this.soundPair = new String[] {getIPA(firstOut.getValue()[0]),getIPA(firstOut.getValue()[1])};
		
		for (String letter : ((HashMap<String,String[]>) phonetic.clone()).keySet()) {
			ArrayList<String> featOfLetter = new ArrayList<String>(Arrays.asList(phonetic.get(letter)));
			ArrayList<String> myFeature = new ArrayList<String>(Arrays.asList(firstOut.getValue()[1].split(":")[0].replaceAll("[\\]\\[]", "").split(", ")));
			if (featOfLetter.containsAll(myFeature)){
				phonetic.remove(letter); //remove each sound of the chain with the most gaps 
			}
		}
		
		return phonetic;
	}
	

	/**
	 * returns a list with all features that are missing for "name"
	 * e.g.,
	 * [consonant, lateral, voiced] -> [labiodental, glottal, palatal, bilabial, postalveolar, velar]
	 * @param name	sound
	 * @param array	all ideal sounds
	 * @return an arraylist with features that are missing for "name"
	 */
	public ArrayList<String> getMissings(String name,ArrayList<IdealSound> array) {
		ArrayList<String> res = new ArrayList<String>();
		ArrayList<String> nameFeat = new ArrayList<String>(Arrays.asList(name.replaceAll("[\\[\\]]", "").split(", ")));
		for (IdealSound sound : array) {
			for (String missFeat : sound.getChanged().keySet()) {
				if (!nameFeat.contains(missFeat) && !res.contains(missFeat)) {
					res.add(missFeat);
				}
			}
		}
		Collections.sort(res);
		return res;
	} 
	

	/**
	 * returns a list with all features that exist for "name"
	 *
	 * @param name	sound
	 * @param array	all ideal sounds
	 * @return an arraylist with sounds that contais for "name"
	 */
	public ArrayList<String> getNonMissing(String name, ArrayList<IdealSound> array) {
		ArrayList<String> res = new ArrayList<String>();
		

		ArrayList<String> nameFeats = new ArrayList<String>(Arrays.asList(name.replaceAll("[\\[\\]]", "").split(", ")));
		ArrayList<IdealSound> s = array;

		for (IdealSound is : s) {

			ArrayList<String> soundFeats = new ArrayList<String>(Arrays.asList(is.getName().replaceAll("[\\[\\]]", "").split(", ")));
			for (String missFeat : getMissings(name, s)) {
				if (soundFeats.contains(missFeat) && !nameFeats.contains(missFeat) && !res.contains(is.getChanged().get(missFeat))) {
					res.add(is.getChanged().get(missFeat));
				}
			}
		}


		return res;
	}

	/**
	 * fills the map this.pairs { voiceless-voiced -> [p - b, ...]}
	 * @param merkmal1	feature 1
	 * @param merkmal2	feature 2
	 * @param letter1	sound 1
	 * @param letter2	sound 2
	 */
	public void put(String merkmal1, String merkmal2, String letter1, String letter2) {

		ArrayList<String> merkmale = new ArrayList<String>(); merkmale.add(merkmal1); merkmale.add(merkmal2); //sort alphabet.
		Collections.sort(merkmale);
		String pair = merkmale.get(0) + ":" + merkmale.get(1);
		String sounds = letter1 + "\t"+ letter2;
		if (merkmale.get(0).contentEquals(merkmal1)) {
			sounds = letter2 + "\t"+ letter1;
		}
		
		if (this.pairs.containsKey(pair)) {
			if (!Arrays.asList(this.pairs.get(pair)).contains(sounds)) {
				ArrayList<String> old = new ArrayList<String>(Arrays.asList(this.pairs.get(pair)));
				old.add(sounds);
				this.pairs.remove(pair);
				this.pairs.put(pair, old.toArray(new String[old.size()]));
			}
		} else {
			this.pairs.put(pair, new String[] {sounds});
		}

	}
	
	/**
	 * Setter for this.phonetic
	 * @param pairs
	 */
	public void setPhonetics(HashMap<String,String[]> pairs) {
		this.phonetic = pairs;
		for (String letter : this.phonetic.keySet()) {
			List<String> feats = Arrays.asList(this.phonetic.get(letter));
			Collections.sort(feats); //alphabet.
			actualSoundsAsList.add(feats.toString());
		}
		
	}
	
	/**
	 * fills the map this.features
	 */
	public void setFeatures() {
		for (String letter : this.phonetic.keySet()) {

			for (String feat : this.phonetic.get(letter)) {

				if (this.features.containsKey(feat)) {
					Feature f = this.features.get(feat);
					f.addSound(letter);
					for (String myFeat : this.phonetic.get(letter)) {
						if (!feat.contentEquals(myFeat)) {
							f.addCoOccurrence(myFeat);
						}
					}
					this.features.put(feat, f);
				} else {
					
					Feature f = new Feature(feat);
					f.addSound(letter);
					for (String myFeat : this.phonetic.get(letter)) {
						if (!feat.contentEquals(myFeat)) {
							f.addCoOccurrence(myFeat);
						}
					}
					this.features.put(feat, f);
				}
			}
		}
		
	}

	/**
	 * Getter for result file
	 * @return	path of the result file
	 */
	public String getResult() {
		return this.result;
	}
	
	/**
	 * Getter for this.soundPair
	 * @return an array with sound pairs
	 */
	public String[] getSoundPair() {
		return this.soundPair;
	}
}
