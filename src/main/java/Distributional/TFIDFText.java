package Distributional;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Helper.Log.Logging;

import java.util.NavigableMap;

/**
 * 
 * implements the tf.idf-calculation for sound vectors
 * 
 * @author abischoff
 *
 */
public class TFIDFText {

	public List<SoundVector> svList = new ArrayList<SoundVector>();
	
	//standard levels
	private int idfLevel = 1; 
	private int tfLevel = 2;
	
	private String lang;
	
	private boolean slacks = false; //outliners according to featureSelection
	private boolean simplyDifference  = false;
	private boolean featureSelectionDiff = false;
	private boolean printForR= true;
	
	/**
	 * 
	 * For tests only
	 * 
	 * generates a comparison of two sounds and prints the relevant contexts
	 * erzeugt einen Vergleich von zwei Lauten
	 * und printet die "relevanten" Kontexte
	 */
	public static void main(String[] args) {

		String sound1 = "r";
		String sound2 = "b";
		
		TFIDFText ti = new TFIDFText();
		ti.readOccurrences("",true);
		/*
		 * Weighting
		 */
		ti.tfidf();
			
			List<SoundVector> soundList = ti.svList;
			
			int soundNumber = 0;
			int compareNumber = 0;
			int count = 0;
			for (SoundVector sou : soundList) {
				if (sou.getName().contentEquals(sound1)) {
					soundNumber = count;
				} else if (sou.getName().contentEquals(sound2)) {
					compareNumber = count;
				}
				count++;
			}
			
			
			Map<Double, String> result = new HashMap<Double, String>();
			

			if (ti.slacks) {
				Map<String, Double> slacks = ti.getSlacks(soundList, soundList.get(soundNumber));
				for (String a : slacks.keySet()) {
					if (result.containsKey(slacks.get(a))){
						result.put(slacks.get(a),result.get(slacks.get(a))+", "+a); 
					} else {
						result.put(slacks.get(a),a);
					}
				}
			}
			
			// compare
			double[] sv1 = soundList.get(soundNumber).getTFIDFVector(); 
			double[] sv2 = soundList.get(compareNumber).getTFIDFVector(); 
			
			if (ti.simplyDifference) {
			//calculation of difference
				for (int i = 0 ; i < sv1.length ; i++) {
					if (result.containsKey(sv1[i] - sv2[i])){
						result.put(sv1[i] - sv2[i] + (1.0 / (100000000000.0 * Math.random() )),soundList.get(soundNumber).getFeatureName(i)); 
					} else {
						result.put(sv1[i] - sv2[i] ,soundList.get(soundNumber).getFeatureName(i)); //+ "(" + ((double) (int) (Math.abs(sv1[i] - sv2[i]) * 1000)) /1000 +") ");
					}
				}
			}
			
			if (ti.featureSelectionDiff) {
			//calculation of difference * Feature Selection Differenz 
				double[] featSelection = ti.tryFeatureSelection(sv1, soundList, false);
				
				for (int i = 0 ; i < sv1.length ; i++) {
					if (result.containsKey(sv1[i] - sv2[i])){
						result.put( featSelection[i] + (1.0 / (100000000000.0 * Math.random() )),soundList.get(soundNumber).getFeatureName(i)); 
					} else {
						result.put(featSelection[i],soundList.get(soundNumber).getFeatureName(i)); //+ "(" + ((double) (int) (Math.abs(sv1[i] - sv2[i]) * 1000)) /1000 +") ");
					}
				}
			}

			NavigableMap<Double,String> result2 = new TreeMap<Double,String>(result);
			printMap(result2.descendingMap());
			
			
			if (ti.printForR) {
				
				HashMap<String,Double> simplDiff = new HashMap<String,Double>();
				for (int i = 0 ; i < sv1.length ; i++) {
					if (!simplDiff.containsKey(soundList.get(soundNumber).getFeatureName(i))){
						simplDiff.put(soundList.get(soundNumber).getFeatureName(i),sv1[i] - sv2[i]); 
					} 
				}
				HashMap<String,Double> festSelect = new HashMap<String,Double>();
				double[] featSelection = ti.tryFeatureSelection(sv1, soundList, false);

				for (int i = 0 ; i < sv1.length ; i++) {
					if (!festSelect.containsKey(soundList.get(soundNumber).getFeatureName(i))){
						festSelect.put(soundList.get(soundNumber).getFeatureName(i),featSelection[i]); 
					} 
				}
				
				try {
				BufferedWriter bw2 = new BufferedWriter( new OutputStreamWriter( new FileOutputStream("./result/distributional/DistributionalDataForR.csv") , "UTF-8"));

				if (simplDiff.size() != festSelect.size()) {
					Logging.warn("Different length of simplDiff and featselect");
				} else {
					bw2.write(sound1+ "\t"+ sound2 + "\r\n");
					bw2.write("Bedingung;Difference;FeatureSelection\r\n");
					for (String kontext : festSelect.keySet()) {
						if (simplDiff.containsKey(kontext)) {
							bw2.write(kontext+";"+simplDiff.get(kontext)+";"+festSelect.get(kontext)+"\r\n");
						} else {
							Logging.warn("Missing context in SimplyDiff: "+ kontext);
						}
					}
				}
				
				bw2.close();
				Logging.debug("R-Data successfully created: result/distributional/DistributionalDataForR.csv");
				
				} catch (IOException e) {
					Logging.error(e.getLocalizedMessage());
				}
				
			}
		

	}
	
	/**
	 * dummy constructor for main for tests
	 */
	private TFIDFText() {}
	
	/**
	 * starts the process
	 * @param tfLevel
	 * 	  	tf 2: frequency in all sound vectors
	 * 		tf 3: addition of all features of the corresponding sound vector 
	 * @param idfLevel
	 *	 	Idf 0: original idf: log (number of phones/phones with the context more than zero)
	 * 		Idf 1: sounds / how many sounds have a higher value
	 * 		Idf 2: number of phones / how many phones have a higher frequency than the average of term
	 * 		Idf 3: as weighting: log(frequency of all sounds / frequency of sw-sound +1)
	 * @param differenceLevel
	 * 		level 1: uses outliners
	 * 		level 2: uses simple Difference
	 * 		level 3: uses featureSelection difference
	 * @param lang		name of the language
	 * @param around	considers the context X_Y
	 */
	public TFIDFText( int tfLevel, int idfLevel, int differenceLevel, String lang, boolean around) {
		this.tfLevel = tfLevel;
		this.idfLevel = idfLevel;
		if (differenceLevel == 1) {
			slacks = true; simplyDifference = false; featureSelectionDiff = false;
		} else if (differenceLevel == 2) {
			slacks = false; simplyDifference = true; featureSelectionDiff = false;
		} else if (differenceLevel == 3) {
			slacks = false; simplyDifference = false; featureSelectionDiff = true;
		}
		this.lang = lang;
		this.readOccurrences(lang, around);
		this.tfidf();
	}
	
	/**
	 * reads the file Occurrence
	 * @param lang		name of the language
	 * @param aroundOn	considers the context X_Y
	 */
	public void readOccurrences(String lang, boolean aroundOn) {
		try {
			
				BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(".\\data\\transData\\Occurrences"+lang) , "UTF-8") );
				String vector = br.readLine();
				ArrayList<String> soundVectorNames = new ArrayList<String>();
				
				while (vector != null) {
					String[] nameCut = vector.split("= ");
					soundVectorNames.add(nameCut[0]);
					
					vector = br.readLine();
				}
				
				br.close();		
				
				/*
				 * initialize sound vectors (first check because they are already initialized for inter-lingual comparisons
				 */
				if (SoundVector.getAllConditions() == null || SoundVector.getAllConditions().length == 0) {
					ArrayList<String> allConditons = new ArrayList<String>();
					for (String name : soundVectorNames) {
						allConditons.add(name + "_");
						allConditons.add("_" + name);
						if (aroundOn) {
							for (String name2 : soundVectorNames) {
								allConditons.add(name+"_"+name2);
							}
						}
						
					}

					SoundVector.setAllConditions(allConditons);
				}
			svList = CompFinder.deSerializeData(lang);
			for (SoundVector sv : svList) {

				sv.setVectorArray();
			}
		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
	}
	
	/**
	 * implements the featureSelectionDifference, only for testing
	 * @param v1	vector
	 * @param list	sound vectors
	 * @param print	should the result be printed
	 * @return return a new vector for v1
	 */
	public double[] tryFeatureSelection(double[] v1, List<SoundVector> list, boolean print) {
		
		Map<Double, String> result = new HashMap<Double, String>();
		double[] resVec = new double[v1.length];
		for (int i = 0 ; i < v1.length ; i++) {
			double featSum = 0.0;
			for (SoundVector sv : list) {
				featSum += sv.getTFIDFVector()[i] * TFIDFText.cosineSimilarity(v1, sv.getTFIDFVector());
			}
			resVec[i] = v1[i] - (featSum/list.size());
		}
		
		for (int i = 0 ; i < resVec.length ; i++) {
			if (result.containsKey(resVec[i])){
				result.put(resVec[i] + (1.0 / (100000000000.0 * Math.random() )),list.get(0).getFeatureName(i)); 
			} else {
				result.put(resVec[i] , list.get(0).getFeatureName(i));
			}
		}
		NavigableMap<Double,String> result2 = new TreeMap<Double,String>(result);
		if (print)
			printMap(result2.descendingMap());
		
		return resVec;
	}
	
	
	/**
	 * supporting method for printing a map
	 * @param <K>	key
	 * @param <V> 	values
	 * @param map	
	 */
    public static <K, V> void printMap(Map<K, V> map) {
        	int count = 0;
            for (Map.Entry<K, V> entry : map.entrySet()) {
                Logging.debug(count + " " + entry.getValue()+ "" + entry.getKey());
                count++;
            }
      
    }
	
    /**
     * returns the Euklidean distance of two vectors
     * @param a		vector 1 (double)
     * @param b		vector 2 (double)
     * @return the Euklidean distance
     */
	public static double euDistance(double[] a, double[] b) {
        double diff = 0; 

        for(int i = 0; i < a.length; i++)
        {
        	diff += (a[i] - b[i]) * (a[i] - b[i]);
        }
        return Math.sqrt(diff);
    }
	

	/**
	 * addition of two vectors
	 * @param a	vector 1
	 * @param b	vector 2
	 * @return	sum of the vectors
	 */
	public static double[] addVectorValues(double[] a, double[] b) {
		
		double[] result = new double[a.length];
		
		for (int i = 0; i < a.length ; i++) {
			result[i] = a[i] + b[i];
		}
		
		return result;
	}
	
	/**
	 * division of a vector by a number
	 * @param a	vector 1
	 * @param b	dividend
	 * @return the result of the division
	 */
	public static double[] divideVectorValues(double[] a, double b) {
		
		double[] result = new double[a.length];
		
		for (int i = 0; i < a.length ; i++) {
			result[i] = a[i] / b;
		}
		
		return result;
	}
	
	/**
	 * multiplication of a vector and a number
	 * @param a	vector 1
	 * @param b	factor
	 * @return the result of the multiplication
	 */
	public static double[] multiplyVectorValues(double[] a, double b) {
		
		double[] result = new double[a.length];
		
		for (int i = 0; i < a.length ; i++) {
			result[i] = a[i] * b;
		}
		
		return result;
	}
	
	/**
	 * multiplication of two vectors
	 * @param a	vector 1
	 * @param b	vector 2
	 * @return the result of the multiplication
	 */
	public static double[] multiplyTwoVectorValues(double[] a, double[] b) {
		
		double[] result = new double[a.length];
		
		for (int i = 0; i < a.length ; i++) {
			result[i] = a[i] * b[i];
		}
		
		return result;
	}
	
	/**
	 * division of two vectors
	 * @param a	vector 1
	 * @param b	vector 2
	 * @return the result of the division
	 */
	public static double[] divideTwoVectorValues(double[] a, double[] b) {
		
		double[] result = new double[a.length];
		
		for (int i = 0; i < a.length ; i++) {
			result[i] = a[i] / b[i];
		}
		
		return result;
	}

	public void tfidf() {
		
		/*
		 * calculation tf
		 */
		double[] sigmaFreqs = new double[svList.get(0).getVectorArray().length];
		double[] dtelementdocs = new double[sigmaFreqs.length];
		
		for (int o = 0; o < sigmaFreqs.length ; o++) {
			double countDF = 0.0;
			for (SoundVector sw : svList) { //only by assumption 2
				sigmaFreqs[o] = sigmaFreqs[o] + sw.getVectorArray()[o];
				if (sw.getVectorArray()[o] > 0) {
					countDF++;
				}
			}
			dtelementdocs[o] = countDF;
		}
		
		/*
		 * calculation of idf
		 * 
		 * |D| is the sum of _x, _y, _z, i.e., the size of svList
		 * {d|t element d}: number of sounds containing the position _y
		 * log(|D| / |d|)
		 */

		
		for (SoundVector sw : svList) {
			
			// Position = term and laut = doc
			
			//freq: how often does the position _y occurs for sound x -> this is already counted in VectorArray()
			//sigmaFreq: how often does the position occurs in total -> sum of the vectors at position _y
			
			//tf = freq/sigmaFreq
			
			double[] idf = null;
			
			switch (idfLevel) {
			case 0:
				idf = versuch0(sw);
				break;
			case 1 :
				idf = versuch1(sw);
				break;
			case 2:
				idf = versuch2(sw);
				break;
			case 3:
				idf = versuch3(sw);
				break;
			}

			double[] tf = null;
			
			switch (tfLevel) {
			case 2:
				tf = tf2(sw);
				break;
			case 3:
				tf = tf3(sw);
				break;
			}
			
			//tf * idf
			
			double[] tfidfVector = multiplyTwoVectorValues(tf, idf);
			

			sw.setTFIDFVector(tfidfVector); 
		}
		
		
	}
	
	
	/**
	 * uses original idf: log (number of phones/phones with the context more than zero)
	 * @param sw	sound vector
	 * @return	new sound vector
	 */
	public double[] versuch0(SoundVector sw) {
		
		double[] res = new double[sw.getVectorArray().length]; 
		
		for (int i = 0; i < sw.getVectorArray().length ; i++) {
			int countNenner = 0;
			for (SoundVector compSound : svList) {
				if (compSound.getVectorArray()[i] > 0) {
					countNenner++;
				}
			}
			
			if (countNenner == 0) {
				res[i] = Math.log((double) svList.size() / (double) 1.0 ); //for the cases that no value is higher
			} else {
				res[i] = Math.log((double) svList.size() / (double) countNenner );
			}
		}
		return res;
	}
	
	/**
	 * uses idf 1: sounds / how many sounds have a higher value
	 * @param sw	sound vector	
	 * @return new sound vector
	 */
	public double[] versuch1(SoundVector sw) {
		
		double[] res = new double[sw.getVectorArray().length]; 
		
		for (int i = 0; i < sw.getVectorArray().length ; i++) {
			int countHigher = 0;
			for (SoundVector compSound : svList) {
				if (compSound.getVectorArray()[i] > sw.getVectorArray()[i]) {
					countHigher++;
				}
			}
			
			if (countHigher == 0) {
				res[i] = Math.log((double) svList.size() / (double) 1.0 ); //for the case that no value is higher
			} else {
				res[i] = Math.log((double) svList.size() / (double) countHigher );
			}
		}
		return res;
	}
	
	/**
	 * uses Idf 2: number of phones / how many phones have a higher frequency than the average of term
	 * @param sw	sound vector
	 * @return	a new sound vector
	 */
	public double[] versuch2(SoundVector sw) {
		
		double[] res = new double[sw.getVectorArray().length]; 
		
		for (int i = 0; i < sw.getVectorArray().length ; i++) {
			double mittel = 0.0;
			for (SoundVector compSound : svList) { //determine the average
				mittel += compSound.getVectorArray()[i];
			}
			mittel = mittel / (double) svList.size();
			int countHigher = 0;
			for (SoundVector compSound : svList) {
				if (compSound.getVectorArray()[i] > mittel) {
					countHigher++;
				}
			}
			res[i] = Math.log((double) svList.size() / ((double) countHigher + 1.0) );

		}

		return res;
	}
	
	/**
	 * uses Idf 3: as weighting: log(frequency of all sounds / frequency of sw-sound +1)
	 * @param sw
	 * @return
	 */
	public double[] versuch3(SoundVector sw) { 
		double[] res = new double[sw.getVectorArray().length]; 
		
		// calculation of total occurrecne of all sounds of the class
		int allTotalOcc = 0;
		for (SoundVector svec : svList) {
			allTotalOcc += svec.getTotalOccurrence();
 		}
		
		for (int i = 0; i < sw.getVectorArray().length ; i++) {

			res[i] = Math.log( (double) allTotalOcc / ((double) sw.getTotalOccurrence() + 1.0) ); 

		}
		return res;
	}


	/**
	 * uses tf 2: frequency in all sound vectors
	 * @param sw	sound vector
	 * @return a new sound vector
	 */
	public double[] tf2(SoundVector sw) {
		
		double[] res = new double[sw.getVectorArray().length]; 
		
		for (int o = 0; o < res.length ; o++) {
			res[o] = 1.0;
			for (SoundVector sv : svList) { 
				res[o] = res[o] + sv.getVectorArray()[o];
			}
	
		}
		return divideTwoVectorValues(sw.getVectorArray(), res);
	}
	
	
	/**
	 * uses tf 3: addition of all features of the corresponding sound vector 
	 * @param sw	sound vector
	 * @return a new vector
	 */
	public double[] tf3(SoundVector sw) { //tf
		
		double[] res = new double[sw.getVectorArray().length]; 
		double[] smooth = new double[sw.getVectorArray().length];  //smooth vector
		
		for (int o = 0; o < res.length ; o++) {
			res[o] = 1.0; //Add-One-Smoothing
			for (double sv : sw.getVectorArray()) { 
				res[o] = res[o] + sv; 
			}
			smooth[o] = 1.0;
		}
		return divideTwoVectorValues(addVectorValues(sw.getVectorArray(),smooth), res);
	}
	


	/**
	 * implements featureSelection
	 * a problem using a median: the median is in most cases around 0.000001 what generates to many outliners
	 * @param list	list of all sound vectors
	 * @param sound	sound vector
	 * @return a map with relevant features, i.e., which positions occur siginificantly frequent with this sound
	 */
	public Map<String, Double> getSlacks(List<SoundVector> list, SoundVector sound) { 
		
		TreeMap<Double, String> order = new TreeMap<Double, String>();
		HashMap<String, Double> slacks = new HashMap<String, Double>();
		
		for (int i = 0; i < sound.getTFIDFVector().length; i++) {
			double count = 0.0;
			for (SoundVector sv2 : list) {
				count += (sv2.getTFIDFVector()[i] * (TFIDFText.cosineSimilarity(sound.getTFIDFVector(), sv2.getTFIDFVector()))); // SimilarityTest.gewichtung1(sound, sv2);
			}
			
			double abzug = count / (double) list.size();
			
			double featSel = sound.getTFIDFVector()[i] - abzug; 
			
			while (order.containsKey(featSel)) { //for the determination of the median, all need their own position
				featSel = featSel + (1.0 / (100000000000.0 * Math.random()) );
				
			}
			order.put(featSel, sound.getFeatureName(i));
		
			
		}
		/*
		* determination of the median
		*/
		double median = (Double) order.keySet().toArray()[order.size()/2];
		double threshold = median *0.2;
		
		threshold = 0.5;
		
		for (Double value : order.keySet()) {
			if (value > threshold) {
				slacks.put(order.get(value), value);
			}
		}
		
		
		return slacks;
		
	}
	
	/**
	 * Getter for the sound vector list
	 * @return list with all sound vectors
	 */
	public List<SoundVector> getSVList() {
		return this.svList;
	}

	/**
	 * Getter for the language
	 * @return name of the language
	 */
	public String getLang() {
		return lang;
	}
	
	/**
	 * calculates the cosine similarity of two vectors
	 * @param vectorA	vector 1
	 * @param vectorB	vector 2
	 * @return the cosine similarity
	 */
	public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
	    double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < vectorA.length; i++) {
	        dotProduct += vectorA[i] * vectorB[i];
	        normA += Math.pow(vectorA[i], 2);
	        normB += Math.pow(vectorB[i], 2);
	    }   
	    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}
	
}
