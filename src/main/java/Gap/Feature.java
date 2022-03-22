package Gap;

import java.util.ArrayList;

/**
 * object for features
 * needed for IdealPhonemeSystem
 * @author abischoff
 *
 */
public class Feature {

	private String name;
	private ArrayList<String> cooccur = new ArrayList<String>();
	private ArrayList<String> sounds = new ArrayList<String>();
	
	/**
	 * 
	 * @param name	name of the features
	 */
	public Feature(String name) {
		this.name = name;
	}

	
	/**
	 * adds an feature that occurs with this feature
	 * @param feat feature that occurs with this feature
	 */
	public void addCoOccurrence(String feat) {
		this.cooccur.add(feat);
	}
	
	/**
	 * Getter for name
	 * @return	name of the feature
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * adds an sound that has this feature
	 * @param s	name of the sound
	 */
	public void addSound(String s) {
		this.sounds.add(s);
	}
	
	/**
	 * returns a list of all sounds that have this feature
	 * @return an arraylist of all sounds that have this features as string
	 */
	public ArrayList<String> getSounds() {
		return this.sounds;
	}
	
	/**
	 * compares two features and returns true it they are identic
	 * @param feat	another feature object
	 */
	@Override
	public boolean equals(Object feat) {
		if (!(feat instanceof Feature)) {
			return false;
		}
		
		if (((Feature)feat).getName().contentEquals(this.name)) {
			return true;
		}
		return false;
	}
}