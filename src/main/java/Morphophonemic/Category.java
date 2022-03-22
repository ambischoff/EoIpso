package Morphophonemic;

import java.util.HashMap;

/**
 * interface for the language objects
 * these methods have to be implemented
 * @author abischoff
 *
 */
public interface Category {
	
	HashMap<String,String> verb = new HashMap<String,String>();
	HashMap<String,String> substantiv = new HashMap<String,String>();
	HashMap<String,String> adjektiv = new HashMap<String,String>();
	
	String getVerbFlexion(String modus);
	String getSubstantivFlexion(String modus);
	String getAdjektivFlexion(String modus);
	
}