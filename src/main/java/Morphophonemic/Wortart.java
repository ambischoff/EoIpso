package Morphophonemic;

/**
 * enum of part of speechs: verb, nouns, and adjective
 * @author abischoff
 *
 */
public enum Wortart implements Flexion {

	Verb { // verb
		public String getFlexion(String modus, MorphemeLanguage sprache) { return sprache.getVerbFlexion(modus); }
		public String getIPAFlexion(String modus, MorphemeLanguage sprache) { return sprache.getVerbFlexion(modus); } 
		public String toString() { return "Verb";} 
	},
	Substantiv { //noun
		public String getIPAFlexion(String modus, MorphemeLanguage sprache) { return sprache.getSubstantivFlexion(modus); } 
		public String getFlexion(String modus, MorphemeLanguage sprache) { return sprache.getSubstantivFlexion(modus); } 
		
		public String toString() { return "Substantiv";} 
	},
	Adjektiv { //adjective
		public String getFlexion(String modus, MorphemeLanguage sprache) { return sprache.getAdjektivFlexion(modus); }
		public String getIPAFlexion(String modus, MorphemeLanguage sprache) { return sprache.getAdjektivFlexion(modus); } 
		public String toString() { return "Adjektiv";} };

}