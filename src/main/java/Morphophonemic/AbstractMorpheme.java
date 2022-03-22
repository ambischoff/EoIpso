package Morphophonemic;

import java.util.HashMap;

/**
 * abstract class of the morpheme objects Morpheme and IPAMorpheme
 * @author abischoff
 *
 */
public abstract class AbstractMorpheme {
	public abstract String getName();
	
	public abstract HashMap<String,Wordform[]> getAllomorphs();
	
	public abstract HashMap<String,Wordform[]> getAllomorphWortbildungen();
	
	public abstract MorphemeLanguage getSprache();
	
	public abstract String addFeat(String word, String mode);
	
	public abstract void addAllomorph(String allomorph);
}

