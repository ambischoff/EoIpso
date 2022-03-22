package Distributional;

import java.util.Set;
import java.util.TreeMap;

import Helper.Log.Logging;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * implements sound vectors
 * 
 * @author abischoff
 *
 */
public class SoundVector implements Serializable{


	private static final long serialVersionUID = 6526106992003739950L;
	private static String[] allConditions = null; // defines the order of conditions for all sound vectors
	
	public String name;
	public double[] tfidf;

	public TreeMap<String, Integer> conditions; // { _a = 4, b_ = 3, a_b = 7 }
	public double[] vectorArray; //before + after values
	
	/**
	 * 
	 * @param name	sound name
	 */
	public SoundVector(String name) {
		
		if (name.equals(" ")) { this.name = "#";}
		this.name = name;
		this.conditions = new TreeMap<String, Integer>();	
	}
	
	/**
	 * creates a sound vector for the sound
	 */
	public void setVectorArray() {
		try {
			int gross =  allConditions.length;
			double[] res = new double[gross];
			int grossI = 0;
			for (String cond : allConditions) {
				
				
				if (this.conditions.containsKey(cond)) {
					res[grossI] = this.conditions.get(cond).doubleValue();
				} else {
					res[grossI] = 0.0;
				}
				grossI++;
	
			} 
			this.vectorArray = res;
		}catch (OutOfMemoryError e) {
			Logging.error("OutofMemory: encoding have to be UTF-8 and consider transcription!");
		}
	}
	
	/**
	 * Getter for the vector
	 * @return vector
	 */
	public double[] getVectorArray() {
		return this.vectorArray;
	}

	/**
	 * creates a vector only considering outliners
	 * is used for the subtypological method
	 * 
	 * @param korpusSize	number of sounds in the corpus
	 * @param outliners		outliners
	 * @return the outliner vector of the sounds 
	 */
	public double[] getSlackVectorArray(double korpusSize, Set<String> outliners) {
		double[] normVectorArray = new double[this.vectorArray.length];
		for (int i = 0 ; i < this.vectorArray.length ; i++) {
			if (outliners.contains(this.getFeatureName(i))) {
				normVectorArray[i] = this.vectorArray[i] / korpusSize;
			} else {
				normVectorArray[i] = 0.0;
			}
		}
		return normVectorArray;
	}
	
	/**
	 * Setter for the tfidf vector
	 * @param vec	tfidf vector
	 */
	public void setTFIDFVector(double[] vec) {
		this.tfidf = vec;
	}
	
	/**
	 * Getter for the tdidf vector
	 * @return tfidf vector
	 */
	public double[] getTFIDFVector() {
		return this.tfidf;
	}

	/**
	 * Getter for the sound name
	 * @return name of the sound
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * returns the sound environment at a position in the sound vector
	 * @param numb	position of the environment in question
	 * @return the name of the sound environment
	 */
	public String getFeatureName(int numb) {

		if (numb >= SoundVector.allConditions.length) {
			return null;
		}
		String res = (String) SoundVector.allConditions[numb];

		return res;
	}

	/**
	 * Getter for the conditions
	 * @return the conditions as TreeMap: {name of the environment -> occurrence}
	 */
	public TreeMap<String, Integer> getConditions() {
		return this.conditions;
	}
	
	/**
	 * adds a sound to the condition list.
	 * If it already exists, it adds +1 to its occurrence value
	 * @param sound	condition
	 */
	public void add(String sound){

		if (sound.equals(" ")) {sound = "#";}
		if (this.conditions.containsKey(sound)) {
			Integer newValue = this.conditions.get(sound) + 1;
			this.conditions.remove(sound);
			this.conditions.put(sound, newValue);
		} else {
			this.conditions.put(sound, 1);
		}
	}
	
	/**
	 * adds a sound and its occurrence to the condition list.
	 * If it already exists, it adds number to its occurrence value
	 * @param sound		condition
	 * @param number	frequency of this condition
	 */
	public void add(String sound, int number){

		if (sound.equals(" ")) {sound = "#";}
		if (this.conditions.containsKey(sound)) {
			Integer newValue = this.conditions.get(sound) + number;
			this.conditions.remove(sound);
			this.conditions.put(sound, newValue);
		} else {
			this.conditions.put(sound, number);
		}
	}
	
	/**
	 * put a sound and its occurrence to the condition list.
	 * If it already exists, the old value will be overwritten
	 * @param sound
	 * @param number
	 */
	public void put(String sound, Integer number){
		if (sound.equals(" ")) {sound = "#";}
		if (this.conditions.containsKey(sound)) {
			this.conditions.remove(sound);
			this.conditions.put(sound, number);
		} else {
			this.conditions.put(sound, number);
		}
	}
	
	/**
	 * prints all conditions
	 * @return a String with all conditions and its frequencies
	 */
	public String printValues(){
		String out = "";
		for (String name : this.conditions.keySet()) {
			out = out + name + ":" + this.conditions.get(name) + " ";
		}
		return out;
	}
	
	/**
	 * returns the frequency of a sound environment
	 * @param featName	sound context
	 * @return number of occurrence of the environment
	 */
	public double getOccurrence(String featName) {
		double output = 0.0;
	
		if (this.conditions.containsKey(featName)) {
			output = this.conditions.get(featName);
		} else {
			if (!this.name.equals("#")) {
				output = 0.0;
			}
		}
		
		return output;
		
	}

	/**
	 * returns the total frequency of the sound
	 * @return the total frequency of this sound
	 */
	public int getTotalOccurrence() { 
		Integer result = 0;
		for (String position : this.conditions.keySet()) { //before and after are equal
			if (position.startsWith("_")) {
				result += this.conditions.get(position);
			}
		}
		
		return result;
	}
	
	/**
	 * defines the order of all sound environments
	 * static because it should be the same order for all sound vectors
	 * @param list list of sounds
	 */
	public static void setAllConditions(ArrayList<String> list) {
		if (allConditions == null) {
			allConditions = new String[list.size()];
			int count = 0;
			for (String a : list ) { 
				allConditions[count] = a;
				count++;
			}
		}
	}
	
	/**
	 * Getter for the order of all sound environments
	 * @return array with all conditions
	 */
	public static String[] getAllConditions( ) {
		return allConditions;
	}
	
	/**
	 * checks whether a sound vector is identical to this sound vector
	 * Object	SoundVector 
	 */
	@Override
	public boolean equals(Object fp) {
		
	       if (!(fp instanceof SoundVector)) { 
	            return false; 
	        } 
	          
	        SoundVector c = (SoundVector) fp; 
		
	        if (c.getName().contentEquals(this.getName())) {
	        		return true;

	        }
	        return false;
	}
}
