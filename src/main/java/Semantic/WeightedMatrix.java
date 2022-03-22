package Semantic;

import java.util.HashMap;

import Helper.Log.Logging;

/**
 * calculation of the weighted matrix containing all PMI scores of a round
 * it is re-calculated for each round
 * the PMI scores will be the weights of the next iteration round
 * 
 * @author abischoff
 *
 */
public class WeightedMatrix {
	
	private static float[][] matrix;
	private HashMap<String,Double> countPairs = new HashMap<String,Double>();
	private HashMap<String,Double> countSounds = new HashMap<String,Double>();
	
	/**
	 * 
	 * @param oldMatrix	float[][] matrix of the previous round
	 */
	public WeightedMatrix(float[][] oldMatrix) {
		WeightedMatrix.matrix = new float[oldMatrix.length][oldMatrix.length];
	}
	
	/**
	 * constructor that generates an empty matrix
	 */
	public WeightedMatrix() {
		for (int i = 0; i < matrix.length ; i++) {
			for (int j = 0; j < matrix.length ; j++) {
				matrix[j][i] = 0.0f;
			}
		}
	}

	/**
	 * adds a sound pair to count them
	 * @param sound1	first sound as Character
	 * @param sound2	second sound as Character
	 */
	public void setPair(Character sound1, Character sound2) {
		
		String pair = sound1+"|"+sound2;
		
		if (countPairs.containsKey(pair)) {
			countPairs.put(pair, countPairs.get(pair)+1.0);
		} else {
			countPairs.put(pair, 1.0);
		}
		
		String[] singles = pair.split("\\|");
		for (String single : singles) {
			
			if (countSounds.containsKey(single)) {
				countSounds.put(single, countSounds.get(single)+1.0);
			} else {
				countSounds.put(single, 1.0);
			}
		}
	}
	
	/**
	 * performs the Needleman-Wunsch algorithm
	 * and returns the new matrix 
	 * @return	float[][] matrix
	 */
	public float[][] getMatrix() {
		
		   HashMap<String,Integer> speicherPlatz = new HashMap<String,Integer>();
		      
		   double sumPairs = 0.0; // sum of SoundPairs
		   double sumSounds = 0.0;// sum of all Sounds
		   int maxLaut = 0;

		   
		   for (String pair : countPairs.keySet()) {
			   sumPairs += countPairs.get(pair);
		   }
		   for (String sound : countSounds.keySet()) {
			   sumSounds += countSounds.get(sound);
			   
			   int ss = sound.charAt(0);
			   char[] einzel = sound.toCharArray();
				int value = 0;
				 for (int i = 0; i < einzel.length ; i++) {
				   value += (einzel[i] * (i+1));
				}
				while (speicherPlatz.containsValue(value)) {
					Logging.debug("Two values have the same value: " + sound + " " + value);
					value++;
				}
				speicherPlatz.put(sound,value);

				ss = value;
			  			   
				   if (ss > maxLaut) {
					   maxLaut = ss;
				   }
		   }
		   		   
		   
		  /*
		   * generate a PMI matrix for the Needleman-Wunsch algorithm
		   */
		   String[] names = countSounds.keySet().toArray(new String[countSounds.keySet().size()]);
		   
		   
		   for (int i = 0; i < countSounds.size() ; i++) {
			   
			   String sound1 = names[i];
			   
			   for (int j = 0; j < countSounds.size() ; j ++) {
				   
				   String sound2 = names[j];
				   
				   double pmiZaehler = 0.0; 

				   if (countPairs.containsKey(sound1 + "|" + sound2)) {
					   pmiZaehler = (1.0+countPairs.get(sound1 + "|" + sound2)) / (1.0+sumPairs);
				   } else if (countPairs.containsKey(sound2 + "|" + sound1)) {
					   pmiZaehler = (1.0+countPairs.get(sound2 + "|" + sound1)) / (1.0+sumPairs);
				   }
				   
				   double pmiNenner = 0.0+ ((1.0+(countSounds.get(sound1)) / (1.0+sumSounds) ) * ( (1.0+countSounds.get(sound2)) / (1.0+sumSounds) ));
				   
				   double pmi = Math.log( pmiZaehler / pmiNenner ); 

				   int s1 = sound1.charAt(0);
				   int s2 = sound2.charAt(0);

				   s1 = speicherPlatz.get(sound1);
				   s2 = speicherPlatz.get(sound2);

				   matrix[s1][s2] = (float) pmi;
				   
			   }
			   
		   }
		
		return matrix;
	}
	
}