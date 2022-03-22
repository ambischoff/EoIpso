package Morphophonemic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

/**
 * object for each sound correspondence 
 * it contains all wordforms of this pair and the phonetic environments
 * @author abischoff
 *
 */
public class SoundPair implements Serializable {
	
	private static final long serialVersionUID =  6058105173305639713L;

	private boolean aroundToo = true; // sound environment X_Y
	private boolean nextRight = true; //sound environment  _X
	private boolean nextLeft = true; // sound environment X_
	private boolean single = true;
	
	protected static HashMap<String,Integer> conditionFreq = new HashMap<String,Integer>(); //total frequency of sound sequences
	
	private String name;
	private String sound1;
	private String sound2;
	private double value;
	protected ArrayList<Wordform> wordform1 = new ArrayList<Wordform>();
	protected ArrayList<Wordform> wordform2 = new ArrayList<Wordform>();
	protected HashMap<String,String> morphs = new HashMap<String,String>();
	protected TreeMap<Double,String> conditions = new TreeMap<Double,String>();

	/*
	 * these values are only needed for testing
	 */
	private static boolean evaluate;
	private static String soundEvaluate1;
	private static String soundEvaluate2;
	private static double weight;
	
	/**
	 * 
	 * @param sound1	name of sound 1
	 * @param sound2	name of sound 2
	 */
	public SoundPair(String sound1, String sound2) {

		if (sound1.compareTo(sound2) > 0) {
			this.sound1 = sound1;
			this.sound2 = sound2;
		} else {
			this.sound1 = sound2;
			this.sound2 = sound1;
		}
		
		this.name = this.sound1 + "|" + this.sound2;
		
	}

	/**
	 * Getter for the name
	 * @return	name of the sound pair
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Getter for the first sound
	 * @return	name of the first sound
	 */
	public String getSound1() {
		return this.sound1;
	}
	
	/**
	 * Getter for the second sound
	 * @return	name of the second sound
	 */
	public String getSound2() {
		return this.sound2;
	}
	
	/**
	 * Setter for the wordforms
	 * @param sound	name of sound 1 or 2
	 * @param wordforms	array of Wordforms 
	 */
	public void setWordforms(String sound, Wordform[] wordforms) {
		if (sound.equals(sound1)) {
			for (Wordform wf : wordforms) {
				wordform1.add(wf);
			}
		} else if (sound.equals(sound2)){
			for (Wordform wf : wordforms) {
				wordform2.add(wf);
			}
		}
			
	}

	/**
	 * Getter for the conditions
	 * @return	a treemap of Double -> name of the string
	 */
	public TreeMap<Double,String> getConditons() {
		return this.conditions;
	}

	/**
	 * generates the conditions as a TreeMap
	 */
	public void setSoundsWF( ) { 
		
		/*
		 * first wordform
		 */
		HashMap<String,Double> soundFreq1 = getSoundFreq(sound1, wordform1);
		
		double freqAllIn1 = 0.0;
		double freqAllIn1_ = 0.0;
		double freqAllIn1__ = 0.0;
		double freqAllIn1___ = 0.0;
		for (String iterSound : soundFreq1.keySet()) {

				freqAllIn1 += soundFreq1.get(iterSound);
				if (!iterSound.contains("_"))
					freqAllIn1_ += soundFreq1.get(iterSound);
				else if (iterSound.split("_").length == 1)
					freqAllIn1__ += soundFreq1.get(iterSound);
				else if (iterSound.split("_").length == 2)
					freqAllIn1___ += soundFreq1.get(iterSound);

		}
		
		/*
		 * second wordform
		 */
		HashMap<String,Double> soundFreq2 = getSoundFreq(sound2, wordform2);

		double freqAllIn2 = 0.0;
		double freqAllIn2_ = 0.0;
		double freqAllIn2__ = 0.0;
		double freqAllIn2___ = 0.0;
		for (String iterSound : soundFreq2.keySet()) {

				freqAllIn2 += soundFreq2.get(iterSound);
				if (iterSound.split("\\s").length == 1)
					freqAllIn2_ += soundFreq2.get(iterSound);
				if (iterSound.split("\\s").length == 2)
					freqAllIn2__ += soundFreq2.get(iterSound);
				if (iterSound.split("\\s").length == 3)
					freqAllIn2___ += soundFreq2.get(iterSound);

		}
		
		ArrayList<String> phons = new ArrayList<String>(soundFreq1.keySet());
		phons.addAll(soundFreq2.keySet()); //contains doublings
		HashMap<String,Double> soundValueMap = new HashMap<String,Double>();

		for (String phon : phons) {

			double conditionPhonFreq = 0.0;
			double myfreqAllIn1 = 0.0;
			double myfreqAllIn2 = 0.0;
			if (phon.startsWith("_") || phon.endsWith("_")) {
				conditionPhonFreq = conditionFreq.get("__");
				myfreqAllIn1 = freqAllIn1__;
				myfreqAllIn2 = freqAllIn2__;
			} else if (phon.contains("_")) {
				conditionPhonFreq = conditionFreq.get("___");
				myfreqAllIn1 = freqAllIn1___;
				myfreqAllIn2 = freqAllIn2___;
			} else {
				conditionPhonFreq = conditionFreq.get("_");
				myfreqAllIn1 = freqAllIn1_;
				myfreqAllIn2 = freqAllIn2_;
			}
			
			if (!soundValueMap.containsKey(phon)) { //due to the doublings in pair
			
				if (soundFreq1.get(phon) == null) {
					soundFreq1.put(phon, 0.0);
				} else if (soundFreq2.get(phon) == null) {
					soundFreq2.put(phon, 0.0);
				}

				boolean useStandardFormula =true; //only false for tests
				double value = 0.0;
				if (useStandardFormula) {
					/*
					 * Formula: 
					 * freqSoundInW1/AllSounds1 * freqSoundInW1/(freqSoundInW1 + freqSoundInW2) 
					 * considers the proportional distribution of c in W1 and W2
					 */
					value = ( (soundFreq1.get(phon) ) / (soundFreq1.get(phon) +soundFreq2.get(phon) +1.0));
				} else {
					/*
					 * Alternative Formula:
					 * freqSoundInW1/AllSounds1 - freqSoundInW2/AllSounds2 
					 */
					value = soundFreq1.get(phon) / freqAllIn1  -  soundFreq2.get(phon) / freqAllIn2 ; 
				}

				if ((evaluate && sound1.equals(soundEvaluate1) && sound2.equals(soundEvaluate2)) || !evaluate) {	
					
					double valueChi = 0.0;
					int useCalculationChi = 0; // standard is 0
					
					if (useCalculationChi == 1) { //only for tests
						//A and B vs. all
						valueChi = get4FieldTest(phon, soundFreq2.get(phon)+soundFreq1.get(phon), myfreqAllIn2+myfreqAllIn2, conditionPhonFreq);						
					} else if (useCalculationChi == 2) { //only for tests
						// A vs. B
						valueChi = get4FieldTest(phon, soundFreq1.get(phon), myfreqAllIn1, soundFreq2.get(phon), myfreqAllIn2);						
					} else {
						//A vs. all -> standard
						valueChi = get4FieldTest(phon, soundFreq1.get(phon), myfreqAllIn1, conditionPhonFreq);
					}

					double binarFnk = 0.0;
					if (weight < 0.0) {
						binarFnk = valueChi; 
					} else if (valueChi > weight) {
						binarFnk = 1.0;
					} 
					
					double newValue = ((value) * binarFnk);
					soundValueMap.put(phon, newValue);		
				} 
				

			}

		}

		for (String phon : soundValueMap.keySet()) {
			if (conditions.containsKey(soundValueMap.get(phon))) {
				String old = conditions.get(soundValueMap.get(phon));
				conditions.remove(soundValueMap.get(phon));
				conditions.put(soundValueMap.get(phon), old + " " + phon);
			} else {
				conditions.put(soundValueMap.get(phon), phon);
			}
		}

	}


	/**
	 * returns a HashMap condition -> frequency in wordform of 1 or 2
	 * @param sound		name of the sound in question
	 * @param wordforms	array list of Wordforms
	 * @return	a HashMap<String,Double> with condition -> frequency
	 */
	public HashMap<String,Double> getSoundFreq(String sound , ArrayList<Wordform> wordforms) {
		HashMap<String,Double> soundFreq = new HashMap<String,Double>();
		
		for (Wordform wf: wordforms) {
				/*
				 * use all sounds
				 */
				if (single) {
					for (int i = 0; i < wf.getSplittedName().split("\\s").length ; i++) {
						String letter = wf.getSplittedName().split("\\s")[i];
						
						if (soundFreq.containsKey(letter)) {
							double old = soundFreq.get(letter);
							soundFreq.remove(letter);
							soundFreq.put(letter, old+1.0);
						} else {
							soundFreq.put(letter, 1.0);
						}
					}
				}
				
				/*
				 * consider environments: X_X, _X, X_
				 */
				
				String surround = " # " + wf.getSplittedName().replaceAll("(\\s|^)" + sound + "(\\s|$)", " _ ").trim() + " # ";
				
				Pattern MY_PATTERN = Pattern.compile("\\s[^\\s]+\\s_\\s[^\\s]+\\s");
				Matcher m = MY_PATTERN.matcher(surround);

				while (m.find()) {
				    String[] surrounds = new String[3];
					
					String around = m.group(0).replaceAll("(^\\s|\\s$)", "");

					surrounds[0] = (nextLeft ? around.split("\\s_")[0] + "_" : null); // X_
				    surrounds[1] = (nextRight ? ("_" + around.split("_\\s")[1]) : null); // _X
				    surrounds[2] = (aroundToo ? around.replaceAll("\\s_\\s","_") : null); //X_X
				    
				    for (int ki = 0 ; ki < surrounds.length; ki++) {
				    	if (surrounds[ki] != null) {
							if (soundFreq.containsKey(surrounds[ki])) {
								double old = soundFreq.get(surrounds[ki]);
								soundFreq.remove(surrounds[ki]);
								soundFreq.put(surrounds[ki], old+1.0);
							} else {
								soundFreq.put(surrounds[ki], 1.0);
							}
				    	}
				    }
				  
				}
			
		}
		
		if (soundFreq.containsKey(sound)) {
			double freqSound = soundFreq.get(sound) - (double) wordforms.size(); //necessary because sound1 occurs in all wordforms (i.e. how often does sound1 occur without SoundPair
		
			soundFreq.remove(sound);
			soundFreq.put(sound, freqSound);
		} else {
			//these are for regional words
			soundFreq.put(sound, 0.0);
		}
		
		return soundFreq;
	}

	/**
	 * calculation of the fourfold test
	 * returns 0.0 if one field is smaller than 5
	 * @param phon				name of the sound
	 * @param soundFreqPhon		frequency of X in s1
	 * @param myfreqAllIn1		frequency of all sounds in s1
	 * @param conditionPhonFreq	frequencies outside of s1
	 * @return	the probability as double
	 */
	public double get4FieldTest(String phon, double soundFreqPhon, double myfreqAllIn1, double conditionPhonFreq) {

		HashMap<String,Integer> frequencies = conditionFreq;

		double Envir1 = soundFreqPhon; //frequency of X in s1
		double Envir2 = 0.0; //frequency of the environment X outside of s1
		
		if (phon.startsWith("_")) { // case _X
			Envir2 = frequencies.get(phon.replaceAll("_\\|?", "")) - Envir1;  
			if (frequencies.get(phon.replaceAll("_\\|?", " #")) != null) {
				Envir2 = Envir2 - frequencies.get(phon.replaceAll("_\\|?", " #")); 
			}
		} else if (phon.endsWith("_")) { // case X_
			Envir2 = frequencies.get(phon.replaceAll("\\|?_", "")) - Envir1;  
			if (frequencies.get(phon.replaceAll("\\|?_", "# ")) != null) {
				Envir2 = Envir2 - frequencies.get(phon.replaceAll("\\|?_", "# ")); 
			}
		} else if (phon.contains("_")) { //case X_X
			String pre = phon.split("_")[0];
			String post = phon.split("_")[1];
			int count = 0;
			for (String kombo : frequencies.keySet()) {
				if (kombo.matches(pre+"\\s.*?\\s"+post)) {
					count += frequencies.get(kombo);
				}
			}
			Envir2 = count - Envir1;
		} else {
			if (!frequencies.containsKey(phon)) {
				Envir2 = 1 - Envir1;
			} else {
				Envir2 = frequencies.get(phon) - Envir1;
			}
		}
		double nichtEnvir1 = myfreqAllIn1 - Envir1;//frequency of other X in s1
		double nichtEnvir2 = (conditionPhonFreq - myfreqAllIn1)-Envir2; //frequency of other X outside of s1
		
		//fourfold chi-square
		double zaehler = conditionPhonFreq*Math.pow((Envir1 * nichtEnvir2) - (Envir2*nichtEnvir1),2 );
		double nenner = (Envir1+nichtEnvir1)*(Envir2+nichtEnvir2)*(Envir1+Envir2)*(nichtEnvir1+nichtEnvir2);
		double chi2 = zaehler/(nenner+0.01);
		
		//expected values should be more than 5.0
		if (Envir1 < 5 || Envir2 < 5 || nichtEnvir1 < 5 || nichtEnvir2 < 5) {
			chi2 = 0.0;
		}
		
		ChiSquaredDistribution c2d = new ChiSquaredDistribution(1);
		return  c2d.cumulativeProbability(chi2);
	}
	
	/**
	 * calculation of the fourfold test
	 * returns 0.0 if one field is smaller than 5
	 * @param phon				name of the sound
	 * @param soundFreqPhon1	frequency of X in s1
	 * @param myfreqAllIn1		frequency of all sounds in s1
	 * @param soundFreqPhon2	frequency of X in s2
	 * @param myfreqAllIn2		frequency of all sounds in s2
	 * @return	the probability as double
	 */
	public double get4FieldTest(String phon, double soundFreqPhon1, double myfreqAllIn1, double soundFreqPhon2, double myfreqAllIn2) {

		double Envir1 = soundFreqPhon1; //frequency of X in s1
		double Envir2 = soundFreqPhon2; //frequency of environment X outside of s1 (i.e., s2)
		
		double nichtEnvir1 = myfreqAllIn1 - Envir1;//frequency of other X in s1
		double nichtEnvir2 = myfreqAllIn2 -Envir2; //frequency of other X outside of s1 (i.e., in s2)

		double n = myfreqAllIn1 + myfreqAllIn2;
		
		//fourfold chi-square
		double zaehler = n*Math.pow((Envir1 * nichtEnvir2) - (Envir2*nichtEnvir1),2 );
		double nenner = (Envir1+nichtEnvir1)*(Envir2+nichtEnvir2)*(Envir1+Envir2)*(nichtEnvir1+nichtEnvir2);
		double chi2 = zaehler/(nenner+0.01);
		
		//expected values should be more than 5.0
		if (Envir1 < 5 || Envir2 < 5 || nichtEnvir1 < 5 || nichtEnvir2 < 5) {
			chi2 = 0.0;
		}
		
		ChiSquaredDistribution c2d = new ChiSquaredDistribution(1);
		return  c2d.cumulativeProbability(chi2);
	}	

	/**
	 * Getter for morphs
	 * @return HashMap<String,String> with morphs
	 */
	public HashMap<String,String> getMorphs() {
		return morphs;
	}

	/**
	 * Setter for this.morphs
	 * @param morphem1	first morpheme
	 * @param morphem2	second morpheme
	 */
	public void setMorphs(String morphem1,String morphem2) {
		this.morphs.put(morphem1,morphem2);
	}
	
	/**
	 * Setter for value
	 * @param value	probability value
	 */
	public void setValue(double value) {
		this.value = value;
	} 
	
	/**
	 * Getter for this.value
	 * @return	chi-square probability
	 */
	public double getValue() {
		return this.value;
	}
	
	/**
	 * Setter for this.conditionFreq
	 * @param freq	HashMap condition -> frequency
	 */
	public static void setCondFreq(HashMap<String,Integer> freq) {
		conditionFreq = freq;
	}
	
	/**
	 * overrides equals
	 * @return	true if both sounds are equal
	 */
	@Override
	public boolean equals(Object fp) {
		
	       if (!(fp instanceof SoundPair)) { 
	            return false; 
	        } 
	          
	        SoundPair c = (SoundPair) fp; 
		
	        if (c.getSound1().equals(this.sound1) || c.getSound2().equals(this.sound1)) {
	        	if (c.getSound1().equals(this.sound2) || c.getSound2().equals(this.sound2)) {
	        		return true;
	        	}
	        }
	        return false;
	}
	
}
