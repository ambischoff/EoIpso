package Morphophonemic;

import java.io.Serializable;

/**
 * objects for wordforms of a morpheme
 * @author abischoff
 *
 */
public class Wordform implements Serializable {

	private static final long serialVersionUID = -3309040197980900984L;
	
	private String name;
	private String bestimmung;
	private String preverb;
	//private String ipa;
	private String splittedName;
	
	/**
	 * 
	 * @param a		name of the wordform
	 * @param b		inflectional information 
	 */
	public Wordform(String a, String b) {
		this.name = a;
		generateSpaceString(name);
		this.bestimmung = b;
		
	//	this.ipa = ReadWiktionary.getIPA(a);
		
	}
	
	/**
	 *
	 * @param a		name of the wordform
	 */
	public Wordform(String a) { 
		this.name = a;
		generateSpaceString(name);
	}
	
	/**
	 * 
	 * @param word			name of the wordform
	 * @param bestimmung	inflectional information
	 * @param preverb		preverb
	 */
	public Wordform(String word, String bestimmung, String preverb) {
		this.name = word.replaceAll("-", "");
		generateSpaceString(name);
		this.bestimmung = bestimmung;
		this.setPreverb(preverb);
		
	//	this.ipa = ReadWiktionary.getIPA(word);
	}
	
	/**
	 * generates a string of the name that separates all sounds
	 * the result stored in this.splittedName
	 * @param old	name of the string
	 */
	private void generateSpaceString(String old ) {
		if (!old.contains(" ")) {
			String newName = "";
			for (char letter : name.toCharArray()) {
				
				if (!Character.toString(letter).contentEquals("̯")) {

					newName += Character.toString(letter) + " ";
				}
			}
			this.splittedName = newName.trim();
		} else {
			this.splittedName = old;
		}
	}
	

	/**
	 * Getter of the name
	 * @return	name of the wordform
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Getter for this.splittedName
	 * @return the name with space between each letter/phon
	 */
	public String getSplittedName() {
		if (splittedName.contains(" ̯")) {
			splittedName = splittedName.replace(" ̯", "");
		}
		return this.splittedName;
		
	}
	
	/**
	 * Getter for the inflectional information
	 * @return	the inflection information
	 */
	public String getMode() {
		return this.bestimmung;
	}

	/**
	 * checks if a string such is part of the inflectional information
	 * @param such	element of the inflectional information (e.g., genitive or plural)
	 * @return	true if such is part of the inflectional information
	 */
	public boolean containsKind(String such) {
		if (bestimmung.contains(such)) {
			return true;
		} 
		return false;
	}

	/**
	 * Getter for the preverb
	 * @return	the name of the preverb
	 */
	public String getPreverb() {
		return this.preverb;
	}

	/**
	 * Setter for preverb
	 * @param preverb	name of the prefix
	 */
	public void setPreverb(String preverb) {
		this.preverb = preverb;
	}
	
	/**
	 * Getter for the inflectional information
	 * @return	the inflectional information or - if there is no such information - the name of wordform
	 */
	public String getInfo() {
		
		if (this.bestimmung != null) {
			return this.bestimmung;
		}
		return this.name;
		
	}
	
}
