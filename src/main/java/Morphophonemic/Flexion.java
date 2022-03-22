package Morphophonemic;

/**
 * interface for the different inflectional types
 * @author abischoff
 *
 */
public interface Flexion {
	String getFlexion(String modus, MorphemeLanguage sprache);
	String getIPAFlexion(String modus, MorphemeLanguage sprache);
}
