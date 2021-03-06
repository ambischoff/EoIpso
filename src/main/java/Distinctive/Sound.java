package Distinctive;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;

/**
 * objects for sounds for the distinctive approach
 * @author abischoff
 *
 */
public class Sound implements Serializable {

	private static final long serialVersionUID = 4294473391683031355L;
	private static final String[] vowels = new String[]{"a", "ɐ", "ɑ", "ɒ", "æ", "​ɑ̃​", "ʌ", "e", "ə", "ɘ", "ɛ", "​ɛ̃", "ɜ", "i", "ĩ", "ɨ", "ɪ", "ɯ", "o", "õ", "ɵ", "ø", "ɞ", "œ", "​œ̃", "ɶ", "ɔ", "​ɔ̃", "ɤ", "u", "ũ", "ʉ", "ʊ", "y", "ʏ"};
	private static final String[] consonants = new String[]{"b", "ɓ", "ʙ", "β", "c", "ç", "ɕ", "d", "ɗ", "ɖ", "ð", "​d͡z", "​d͡ʒ", "ʤ", "​d̠͡ʑ", "​ɖ͡ʐ", "f", "ɸ", "g", "ɡ", "ɠ", "ɢ", "ʛ", "ɣ", "h", "ħ", "ɦ", "ɧ", "ʜ", "j", "ʝ", "ɟ", "ʄ", "k", "​k͡x", "l", "ɫ", "ɬ", "ɭ", "ʟ", "ɮ", "ʎ", "m", "ɱ", "n", "ɲ", "ŋ", "ɳ", "ɴ", "p", "​p͡f", "q", "r", "ɾ", "ɺ", "ɽ", "ɹ", "ɻ", "ʀ", "ʁ", "​r̝​", "s", "ʂ", "ʃ", "​s͡f", "t", "ʈ", "θ", "​t͡s", "ʦ", "​t͡ʃ", "ʧ", "​t̠͡ɕ", "​ʈ͡ʂ", "v", "​v̊", "ʋ", "ѵ", "w", "​w̃", "ʍ", "ɰ", "x", "χ", "ɥ", "z", "ʑ", "ʐ", "ʒ", "​z͡v", "ʔ", "ʡ", "ʕ", "ʢ", "ʘ", "ǀ", "ǂ", "ǁ", "ǃ"};
	
	private String name;
	private int frequency;
	private HashMap<String, Integer> distinctivePairs;
	private ArrayList<String> wordCollection;
	
	/**
	 * @param name	name of the sound
	 */
	public Sound(String name) {
		this.name = name;
		this.frequency = 0;
		this.distinctivePairs = new HashMap<String,Integer>();
		this.wordCollection = new ArrayList<String>();
	}
	
	/**
	 * Getter for the name 
	 * @return the name of the sound
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the name of the sound
	 */
	@Override
	public String toString() {
		return this.name;
	}
	
	/**
	 * Getter for the frequency
	 * @return	frequency of the sound
	 */
	public int getFrequency() {
		return this.frequency;
	}
	
	/**
	 * Getter for the distinctive pairs
	 * @return HashMap {distinctive pair -> frequency}
	 */
	public HashMap<String,Integer> getDistinctivePairs() {
		return this.distinctivePairs;
	}
	
	/**
	 * adds one to the frequency of this sound
	 */
	public void plusFrequency() {
		this.frequency = this.frequency +1;
	}
	
	/**
	 * adds a sound to the distinctive pairs and adds one to its frequency if it already exists
	 * @param sound name of the sound
	 */
	public void setDistinctives(String sound) {
		
		if (this.distinctivePairs.containsKey(sound)) {
			
			int old = this.distinctivePairs.get(sound);
			int now = old + 1;
			this.distinctivePairs.remove(sound);
			this.distinctivePairs.put(sound, now);
			
		} else {
			this.distinctivePairs.put(sound, 1);
		}
		
	}
	
	/**
	 * Getter for the word list
	 * @return arraylist with all words that contain this sound
	 */
	public ArrayList<String> getWordCollection() {
		return this.wordCollection;
	}
	

	/**
	 * initializes this.distinctivePairs
	 * i.e.,
	 * put the value zero for all sounds
	 * @param sounds	arraylist of all sounds
	 */
	public void initDistinctives(ArrayList<String> sounds) { 
		for (String soundName : sounds){
			this.distinctivePairs.put(soundName, 0);
		}
	}
	
	/**
	 * return true if the IPA sound is a vowel
	 * @return true if the sound is a vowel
	 */
	public boolean isVowel() {
		for (String vowel : vowels) {
			if (this.name.contains(vowel)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * return true if the IPA sound is a consonant
	 * @return true if the sound is a consonant
	 */
	public boolean isConsonant() {
		for (String conso : consonants) {
			if (this.name.contains(conso)) {
				return true;
			}
		}
		
		return false;
	} 
}
