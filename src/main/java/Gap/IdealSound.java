package Gap;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * object for an ideal sound
 * needed for IdealPhonemeSystem
 * @author abischoff
 *
 */
public class IdealSound {
	
	private String name;
	private HashMap<String,String> changed = new HashMap<String,String>(); //{ new sound -> original sound }
	
	/**
	 * 
	 * @param name	name of the ideal sound
	 */
	public IdealSound(String name) {
		this.setName(name);
	}

	/**
	 * Getter for the name
	 * @return	the name of the ideal sound
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for the name
	 * @param name	sets the name of the ideal sound
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for this.changd
	 * @return a map {new sound -> original sound}
	 */
	public HashMap<String,String> getChanged() {
		return this.changed;
	}

	/**
	 * adds a pair of sounds that should be replaced
	 * @param a	original sound
	 * @param b	new sound
	 */
	public void addChanged(String a, String b) {
		if (!this.changed.containsKey(b))
			this.changed.put(b, a);
	}

	/**
	 * turns [nasal, voiced, consonant] to [voiced, consonant] -> [nasal]
	 * @return a map {[nasal, voiced, consonant] -> [voiced, consonant]>[nasal]}
	 */
	public HashMap<String,String[]> getStrip() {
		ArrayList<String> keySet = new ArrayList<String>(this.getChanged().keySet());
		ArrayList<String> valueSet = new ArrayList<String>(this.getChanged().values());
		HashMap<String,String[]> res = new HashMap<String,String[]>();
		for (int i = 0 ; i < this.getChanged().keySet().size() ; i++) {
			String key = keySet.get(i);
			String value = valueSet.get(i);
			String ohneChange = name.replaceAll(key +", ", "").replaceAll(", "+key+"\\]", "]");
			if (!res.containsKey(ohneChange)) {
				String[] array = new String[this.getChanged().size()];
				array[0] = key+":"+value;
				res.put(ohneChange, array);
			} else {
				String[] array = res.get(ohneChange);
				array[i] = key+":"+value;
				res.put(ohneChange, array);
			}
		}
		return res;
	}
	
	
}