package Distinctive;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Helper.Encoding.Encoding;
import Helper.Encoding.EncodingDetector;
import Helper.Language.Language;
import Helper.Language.Method;
import Helper.Log.Logging;
import Helper.Typology.ArticulatoryPhonetics;

/**
 * implements the distinctive method
 * 
 * @author abischoff
 *
 */
public class DistinctiveLanguage extends Language {

	private ArrayList<String> words = new ArrayList<String>();
	private ArrayList<Sound> sounds = new ArrayList<Sound>();
	private ArrayList<String> soundNames = new ArrayList<String>();
	private boolean excludeVowels = false;
	private boolean excludeConsonants = false;
	
	private double g1 = 5.0, g2 = 1.0; //standard weighting
	private HashMap<String,HashMap<String,Integer>> brown;
	private List<String> excludeSounds = new ArrayList<String>();// = Arrays.asList("tʃ", "ʒ", "dʒ");
	private double threshold = 0.01;
	
	private HashMap<String,ArrayList<String>> predefinedClusters = null;

	/**
	 * only for tests
	 * @param args
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		DistinctiveLanguage d = new DistinctiveLanguage("Test",".\\data\\rawData\\distinctive\\MorphemIPA.txt");
		d.setIteration(100);
		d.start();
	}
	
	/**
	 * starts the process
	 * @param name	language name
	 * @param file	file path
	 * @param iter	number of iterations
	 */
	public DistinctiveLanguage(String name,String file) {
		super(name,file);
		super.setThreshold(0.01);
		super.setIteration(30);
	}
	
	/**
	 * Getter for the Method
	 */
	public Method getMethod() {
		return Method.distinctive;
	}
	
	/**
	 * starts the process
	 */
	public void start() {
		Logging.debug("Read file...");
		this.readFile(this.file);
		Logging.debug("Set frequencies...");
		this.setFrequencies();
		this.makeWordCollectionFile();
		Logging.debug("Start distintice approach...");
		this.netWorkFarAllophones();
	}
	

	/**
	 * Setter for sounds that should be excluded
	 * @param sounds	excluded sounds
	 */
	@Override
	public void setExcludeSounds(String sounds) {
		List<String> toExclude = new ArrayList<String>();

		for (String ausnahme : Arrays.asList(sounds.replaceAll("[,\\-]", " ").trim().split("\\s+"))) {

			if (ausnahme.contains("konsonant")) {
				this.excludeConsonants = true;
			} else if (ausnahme.contains("vokal")) {
				this.excludeVowels = true;
			} else {
				if (!toExclude.contains(ausnahme))
					toExclude.add(ausnahme);
			}
		}
		this.excludeSounds = toExclude;
	}

	/**
	 * reads the input file
	 * @param file	file name
	 */
	public void readFile(String file) {
		try {

			EncodingDetector e = new EncodingDetector(new FileInputStream(new File(file)), Encoding.UTF8);
			String encoding = e.getEncoding().toString();
			e.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
			String line = br.readLine();
			ArrayList<String> diacritics = ArticulatoryPhonetics.readDiacritics();
			//Remove BOM if it exists
			if (!line.contentEquals("") && line.toCharArray()[0] == 65279) {
				line = line.substring(1);
			}
			if (line.split("\t")[0].contains(" ")) {
				this.ipa = true;
			}
			while(line != null){
				line = line.split("\t")[0].replaceAll("[\\*\\?\\-\\(\\)\\!]","").trim();
				StringBuilder sb = new StringBuilder();
				if (this.ipa) {
					for (String sign : line.split("\\s+")) {
						if (diacritics.contains(sign)) {
							sb.append(sign);
						} else {
							sb.append(" " + sign);
						}
					} 
				} else {
					for (char sign : line.toCharArray()) {
						String letter = Character.toString(sign);
						if (diacritics.contains(letter)) {
							sb.append(Character.toString(sign));
						} else {
							sb.append(" " + Character.toString(sign));
						}
					} 
				}
				if (!words.contains(sb.toString().trim())) {
					words.add(sb.toString().trim());	
				}
				line = br.readLine();
			}		
			br.close();

		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
	}
	
	/**
	 * determines the frequencies of the sounds within the corpus
	 */
	public void setFrequencies() {
		for (String word : words) {
			for (String letter : word.split("\\s")) {
				
				if (!letter.contentEquals("") && this.soundNames.contains(letter)) {
					
					for (Sound laut : this.sounds) {
						if (laut.getName().equals(letter)) {
							laut.plusFrequency();
						}
					}
				} else if (!letter.contentEquals("")) {
					this.soundNames.add(letter);
					Sound mySound =new Sound(letter);
					if (!(this.excludeVowels && mySound.isVowel()) && !(this.excludeConsonants && mySound.isConsonant())) {
						this.sounds.add(mySound);
					}
				}
			}
			
		}
		
	}
	
	/**
	 * generates a list with words containing the phoneme in question: abaaXaa, dddaXaa
	 * the sound in question is marked with _
	 * if the sound occurs x-times, the word is counted x-times: aXad and adaX,
	 * for counting the minimal pairs, the identic words have to be counted
	 */
	public void makeWordCollectionFile() { 

		for (String word : this.words) {
			
			String[] phones = word.split("\\s");
			for (int i = 0 ; i < phones.length ; i++) {
				if (!phones[i].contentEquals("")) {
					Sound mainSound = null;
					String altLaut = phones[i];
					
					for (Sound sound : this.sounds) { //determination of the sound
						if (sound.getName().equals(altLaut))
							mainSound = sound;
					}
					
					if (mainSound != null) {
				        StringBuilder builder = new StringBuilder();
				        for(int j = 0 ; j < phones.length ; j++) {
				        	if (j == i)
				        		builder.append("_");
				        	else {
				        		builder.append(phones[j]); //without separator
				        	}
				        }
						
						mainSound.getWordCollection().add(builder.toString());
					}
				}
			}

		}

	}
	
	/**
	 * implements the distinctive method for sounds of different clusters
	 */
	public void netWorkFarAllophones() {

		Map<String,Double> pairCosines = jaccardCosine(threshold); 
		Map<Double,String> table = new TreeMap<Double,String>();
		
		@SuppressWarnings("unchecked")
		ArrayList<Sound> soundsWithoutExcluded = (ArrayList<Sound>) this.sounds.clone();
		for (Sound sound : this.sounds) {
			if (excludeSounds.contains(sound.getName())) {
				soundsWithoutExcluded.remove(sound);
			}
		}
		
		Clustering cluster = new Clustering(soundsWithoutExcluded);
		HashMap<String,ArrayList<String>> klassen = cluster.soundClasses(); 
		
		printClusters(klassen,"SoundClasses");
		HashMap<String,ArrayList<String>> clusters = this.predefinedClusters;
		if (clusters == null) {
			 clusters = cluster.clustering(0);
		}
		printClusters(clusters,"Clusters");
		

		TreeMap<Double,String> tableTest = new TreeMap<Double,String>();
		TreeMap<Double,String> tableTypo = new TreeMap<Double,String>(); 

		Map<String, String> jkoeffs = new HashMap<String, String>();
		
		HashMap<String,double[]> jaccVectors = new HashMap<String,double[]>(); 

		for (Sound suchSound1 : soundsWithoutExcluded) {

				double[] jaccVector = new double[soundsWithoutExcluded.size()]; 
				int i = 0; //test
					
				for (Sound suchSound2 : soundsWithoutExcluded) {
				if (suchSound1 == null || suchSound2 == null ) { continue;}
					double vereinigung = (double) suchSound1.getWordCollection().size() + (double) suchSound2.getWordCollection().size() - (double) suchSound1.getDistinctivePairs().get(suchSound2.getName());
					double jkoeff = (double) suchSound1.getDistinctivePairs().get(suchSound2.getName()) / vereinigung;
					
					jaccVector[i] = jkoeff;
					i++;
					
					DecimalFormat df = new DecimalFormat("0.0000000000"); 
					
					if (! jkoeffs.containsKey(suchSound2.getName()+","+suchSound1.getName())) {
						jkoeffs.put(suchSound1.getName() + "," + suchSound2.getName(), df.format(jkoeff).replaceAll(",", "."));
					}
					
	
				}
				jaccVectors.put(suchSound1.getName(), jaccVector);
			
		}
		

		HashMap<String,Double> potentialPairs = new HashMap<String,Double>();
		
		for (String pair : pairCosines.keySet()) {
			String sound1 = pair.split("\\s")[0];
			String sound2 = pair.split("\\s")[1];
			for (int j = 0 ; j < clusters.size() ; j++) {
				for (int k = 0; k < clusters.size() ; k++) {
					if (j != k && ((clusters.get(Integer.toString(j)).contains(sound1) && clusters.get(Integer.toString(k)).contains(sound2)) || 
							(clusters.get(Integer.toString(j)).contains(sound2) && clusters.get(Integer.toString(k)).contains(sound1)) ) ) {
						for (ArrayList<String> klasse : klassen.values()) {//they should belong to the same class
							if (klasse.contains(sound1) && klasse.contains(sound2)) {
								potentialPairs.put(pair, pairCosines.get(pair));
							}
						}
					}
				}
			}
		}
		

		for (String pair : potentialPairs.keySet()) {
			String sound1 = pair.split("\\s")[0];
			String sound2 = pair.split("\\s")[1];
			Sound mySound1 = null;
			Sound mySound2 = null;
			
			for (Sound s : this.sounds) {
				if (s.getName().contentEquals(sound1)) {
					mySound1 = s;
				}
				if (s.getName().contentEquals(sound2)) {
					mySound2 = s;
				}
			}

			Map<Double,String> table1 = conditionalSyllable(mySound1.getWordCollection(), mySound2.getWordCollection());

			for (Double d : table1.keySet()){
				
				double bewertungsmass = 0.0;
				double bewertungsmass2 = 0.0;
				
					bewertungsmass = (this.g1 * d) + (this.g2 * potentialPairs.get(pair));
					bewertungsmass2 = (this.g1 * d) + (this.g2*typologicalProb(pair.split("\s")[0],pair.split("\s")[1]));
				
				if (tableTest.containsKey(d)) { // addition??
					tableTest.put(bewertungsmass, table.get(d) + "\r\n" + pair + " mit " + table1.get(d) + "\t" + d + "\t CosSim:" + potentialPairs.get(pair) + "\t");
				} else { //(1/distanceToCentroiistad)
					tableTest.put(bewertungsmass, pair + " mit " + table1.get(d) + "\t" + d + "\t CosSim:" + potentialPairs.get(pair) + "\t");
					tableTypo.put(bewertungsmass2, pair + " mit " + table1.get(d) + "\t" + d + "\t TypoFrequency: " + typologicalProb(pair.split("\s")[0],pair.split("\s")[1]) +"\t");
				}
			}
		}
	
	try {

		ArrayList<String> soundAlreadyIn = new ArrayList<String>(); //ignore pairs if one of its element already is used
		ArrayList<String> pairsForEval = new ArrayList<String>();
		if (!new File(".\\result").exists()) {
			new File(".\\result").mkdir();
		}
		if (!new File(".\\result\\distinctive").exists()) {
			new File(".\\result\\distinctive").mkdir();
		}
		if (!new File(".\\result\\distinctive\\"+this.lang).exists()) {
			new File(".\\result\\distinctive\\"+this.lang).mkdir();
			Logging.debug("Generated directory \\result\\distinctive\\"+this.lang );
		}
		BufferedWriter bw2 = new BufferedWriter( new OutputStreamWriter( new FileOutputStream("./result/distinctive/"+this.lang+"/BestResultsDiffClusters.txt"), "UTF-16"));
		bw2.write("== JaccCos ==\r\n");
		bw2.write("pair\tconDiff\tcosSim\tCentroid\tCosSim+conDiffxcosSim\r\n");
		String nan = "";
		for (Double tableKey : tableTest.descendingKeySet()) {
			if (!tableKey.isNaN()) {
				String pair = tableTest.get(tableKey).split(" mit")[0];
				String s1 = pair.split("\\s")[0];
				String s2 = pair.split("\\s")[1];
				if (!soundAlreadyIn.contains(s1) && !soundAlreadyIn.contains(s2)) {
					bw2.write(tableTest.get(tableKey)+"\t"+tableKey+"\r\n");
					soundAlreadyIn.add(s1); soundAlreadyIn.add(s2);
					pairsForEval.add(s1+"-"+s2);
				}
			} else {
				nan += tableTest.get(tableKey)+"\t"+tableKey+"\r\n";
			}
		}
		
		soundAlreadyIn.clear();
		if (this.getTypological()) {
			bw2.write("== Typological ==\r\n");
			bw2.write("pair\tconDiff\tcosSim\ttypological\tJaccTypo\r\n");
	
			for (Double tableKey : tableTypo.descendingKeySet()) {
				if (!tableKey.isNaN()) {
					String pair = tableTypo.get(tableKey).split(" mit")[0];
					String s1 = pair.split("\\s")[0];
					String s2 = pair.split("\\s")[1];
					if (!soundAlreadyIn.contains(s1) && !soundAlreadyIn.contains(s2)) {
						bw2.write(tableTypo.get(tableKey)+"\t"+tableKey+"\r\n");
						soundAlreadyIn.add(s1); soundAlreadyIn.add(s2);
						pairsForEval.add(s1+"-"+s2);
					}
				} 
			}
		}
		bw2.write(nan.trim());
		bw2.close();
		
		Logging.debug("Result file generated.");

	} catch (IOException e) {
		Logging.error(e.getLocalizedMessage());
	}
	
	}
	
	/**
	 * tries to consider the relevant sound environment of sets
	 * this method takes the environments #C_, #CC_, _CC#, _C#
	 * 
	 * @param menge1	set 1
	 * @param menge2	set 2
	 * @return	a map: { relevance value -> sound environment }
	 */
	public static Map<Double,String> conditionalSyllable(ArrayList<String> menge1, ArrayList<String> menge2) {
		/*
		 * sub-classes
		 */

		Map<Double,String> table = new TreeMap<Double,String>(); 
		double zielwoerterAnlaut1 = 0.0 ;
		double zielwoerterZweitlautPostC1 = 0.0;
		double zielwoerterVorAuslautPraeC1 = 0.0;
		double zielwoerterAuslaut1 = 0.0;
		for (String word: menge1) {
			if (word.startsWith("_")) {
				zielwoerterAnlaut1++;
			}
			if (word.indexOf("_") == 1 ) {
				zielwoerterZweitlautPostC1++;
			}
			if (word.indexOf("_") == word.length() -2) {
				zielwoerterVorAuslautPraeC1++;
			}
			if (word.endsWith("_")){
				zielwoerterAuslaut1++;
			}
		}
		double zielwoerterAnlaut2 = 0.0 ;
		double zielwoerterZweitlautPostC2 = 0.0;
		double zielwoerterVorAuslautPraeC2 = 0.0;
		double zielwoerterAuslaut2 = 0.0;
		for (String word: menge2) {
			if (word.startsWith("_")) {
				zielwoerterAnlaut2++;
			}
			if (word.indexOf("_") == 1 ) {
				zielwoerterZweitlautPostC2++;
			}
			if (word.indexOf("_") == word.length() -2 ) {
				zielwoerterVorAuslautPraeC2++;
			}
			if (word.endsWith("_")){
				zielwoerterAuslaut2++;
			}
		}
		
		
		double valueAnlaut = ( zielwoerterAnlaut1 / (double) menge1.size() ) - ( zielwoerterAnlaut2 / (double) menge2.size() );  
		double valueZweitlaut = (zielwoerterZweitlautPostC1 / (double) menge1.size()) - (zielwoerterZweitlautPostC2 / (double) menge2.size());
		double valueVorAuslaut = (zielwoerterVorAuslautPraeC1 / (double) menge1.size()) - (zielwoerterVorAuslautPraeC2 / (double) menge2.size());
		double valueAuslaut = ( zielwoerterAuslaut1 / (double) menge1.size() ) - ( zielwoerterAuslaut2 / (double) menge2.size() );  
	
		if (table.containsKey(valueAnlaut)) {
			table.put(valueAnlaut, table.get(valueAnlaut) + "\r\n" + " at initial position: ");
		} else {
			table.put(valueAnlaut," at initial position: ");
		}	
		
		if (table.containsKey(valueZweitlaut)) {
			table.put(valueZweitlaut, table.get(valueZweitlaut) + "\r\n" + " at second position after consonant: ");
		} else {
			table.put(valueZweitlaut," at second position after consonant: ");
		}	
		if (table.containsKey(valueVorAuslaut)) {
			table.put(valueVorAuslaut, table.get(valueVorAuslaut) + "\r\n" + " at penultimate position before consonant: ");
		} else {
			table.put(valueVorAuslaut," at penultimate position before consonant: ");
		}	
		
		if (table.containsKey(valueAuslaut)) {
			table.put(valueAuslaut, table.get(valueAuslaut) + "\r\n" + " at final position: ");
		} else {
			table.put(valueAuslaut," at final position: ");
		}
	
		return table;
	}

	/**
	 * calculation of the relevance
	 * @param threshold	threshold
	 * @return a map { sound pair -> relevance measure }
	 */
	public Map<String,Double> jaccardCosine(double threshold) { 
		
		/*
		 * initialize the distinctive pairs
		 */
		for (Sound soundi : this.sounds) {
			soundi.initDistinctives(this.soundNames);
		}
		
		for (Sound mainSound : this.sounds) {
			Logging.debug("Identified sound: "+ mainSound.getName());
			for (Sound laut : this.sounds) { //look at each sounds and look for doublet words
				for (String word : mainSound.getWordCollection()) {
					if (laut.getWordCollection().contains(word) && !laut.getName().equals(mainSound.getName())) {
						mainSound.setDistinctives(laut.getName());
					}
				}
			}
		}
		
		HashMap<String,double[]> jaccVectors = new HashMap<String,double[]>(); 
		HashMap<String,Double> jaccVectorsSoundPair = new HashMap<String,Double>();
		
		for (Sound suchSound1 : this.sounds) { 
				
			double[] jaccVector = new double[this.sounds.size()]; 
			int i = 0; 
						
			for (Sound suchSound2 : this.sounds) {
				
				double vereinigung = (double) suchSound1.getWordCollection().size() + (double) suchSound2.getWordCollection().size() - (double) suchSound1.getDistinctivePairs().get(suchSound2.getName());
				double jkoeff = (double) suchSound1.getDistinctivePairs().get(suchSound2.getName()) / vereinigung;
				jaccVectorsSoundPair.put(suchSound1.getName()+"-"+suchSound2.getName(), jkoeff);
				
				jaccVector[i] = jkoeff;
				i++;
				

			}
			jaccVectors.put(suchSound1.getName(), jaccVector);

		}
		
		for (String sound : jaccVectors.keySet()) {
			double length = 0.0;
			for (double d : jaccVectors.get(sound)) {
				length += (d*d);
			}
			length = Math.sqrt(length);
			
			double[] newOne = new double[jaccVectors.get(sound).length];
			for (int i = 0 ; i < newOne.length ; i++) {
				newOne[i] = jaccVectors.get(sound)[i] / length;
			}
			 
			jaccVectors.put(sound, newOne);
		}
		
		Map<String,Double> result = new TreeMap<String,Double>();
		
		for (String sound1 : jaccVectors.keySet()) { 
			
			for (String sound2 : jaccVectors.keySet()) {
				
				if (!sound1.equals(sound2) && jaccVectorsSoundPair.get(sound1+"-"+sound2) <= threshold) {
					
					double cosSim = cosineSimilarity(jaccVectors.get(sound2),jaccVectors.get(sound1));
					if (cosSim == Double.NaN) {
						Logging.warn("Error NaN: " + sound1 + " " +sound2);
						continue;
					}

					result.put(sound1 + " " + sound2, cosSim);

				} 
			}

		}

		return result;
	}


	/**
	 * calculates the cosine similarity for two vectors
	 * @param vectorA	vector 1
	 * @param vectorB	vector 2
	 * @return	cosine similarity
	 */
	public double cosineSimilarity(double[] vectorA, double[] vectorB) {
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
	
	/**
	 * generates a file containing the generated clusters
	 * @param clusters	clusters (result of the class Clusterin)
	 * @param fileName	name for the generated file
	 */
	public void printClusters(HashMap<String,ArrayList<String>> clusters, String fileName) {
		try {
			if (!new File("./result/distinctive/"+this.lang).exists()) {
				new File("./result/distinctive/"+this.lang).mkdir();
			}
			BufferedWriter bw2 = new BufferedWriter( new OutputStreamWriter( new FileOutputStream("./result/distinctive/"+this.lang+"/"+fileName+".txt"), "UTF-16"));
			for (String arr : clusters.keySet()) {
				bw2.write(arr+"\t"+clusters.get(arr)+"\r\n");
			}
			bw2.close();

		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
	}

	/**
	 * reads the probability values from Brown et al. and returns the value of a sound pair
	 * see Helper.Typology
	 * @param s1	sound 1 of the pair
	 * @param s2	sound 2 of the pair
	 * @return probability of the sound pair
	 */
	public double typologicalProb(String s1, String s2) {

		if (this.brown == null) {
			this.brown = Helper.Typology.Brown.readBrown(); //p -> {m -> 22, d -> 34, ...}
		}
		HashMap<String,String> IPA2ASJP = new HashMap<String,String>();
			
			/*
			 * IPA2ASJP
			 * needed because the list does not use IPA
			 */			

			String sound1 = s1.replaceAll("ḱ", "c").replaceAll("ģ", "g").replaceAll("[ːˑ̆ʼ|​||​​‿​͜͡​ ̥​​ ̊​​ ̬​​ ̻​​ ̪​​ ̺​]","");;
			String sound2 = s2.replaceAll("ḱ", "c").replaceAll("ģ", "g").replaceAll("[ːˑ̆ʼ|​||​​‿​͜͡​ ̥​​ ̊​​ ̬​​ ̻​​ ̪​​ ̺​]","");;
				for (char part : sound1.toCharArray()) {
					if (IPA2ASJP.containsKey(Character.toString(part))) {
						sound1 = sound1.replaceAll(Character.toString(part),IPA2ASJP.get(Character.toString(part)));
					}
					
				}
				for (char part : sound2.toCharArray()) {
					if (IPA2ASJP.containsKey(Character.toString(part))) {
						sound2 = sound2.replaceAll(Character.toString(part),IPA2ASJP.get(Character.toString(part)));}
				}
				
				double value = 0.0;
			
				if (sound1.contentEquals(sound2) && !sound1.contentEquals("?")) {
					value = 0.25;
				} else if (!sound1.contentEquals("?") && !sound2.contentEquals("?") 
						&&brown.containsKey(sound1)&&brown.containsKey(sound2)) {
					//Formula Freq /TotalSound1 + TotalSound2
					value = ((double) (brown.get(sound2).get(sound1) == null ? 0.0 : brown.get(sound2).get(sound1)) ) 
							/ ((double) brown.get(sound1).get("TOTAL") + (double) brown.get(sound2).get("TOTAL"));
				}

				return value;

		
	}
	
	/**
	 * Setter for weights
	 * @param	w1	weight 1
	 * @param	w2 	weight 2
	 */
	@Override
	public void setWeights(Double w1, Double w2) {		
		if (w1 != null) {
			this.g1 = w1;
		}
		if (w2 != null) {
			this.g2 = w2;
		}

	}
	
	/**
	 * Setter for Clusters
	 * if one sound is in both clusters, it will be removed from the second cluster
	 * @param	cluster1	array of sounds
	 * @param 	cluster2	array of sounds
	 */
	@Override
	public void setClusters(ArrayList<String[]> clusters) {
		
		for (int i = 0; i < clusters.size(); i++) {
			String[] cluster = clusters.get(i);
			List<String> array1 = Arrays.asList(cluster);

			Logging.debug("Set clusters: " + array1.toString());
			if (array1.size()!=0) {
				if (this.predefinedClusters == null) {
					this.predefinedClusters = new HashMap<String,ArrayList<String>>();
				}
				this.predefinedClusters.put(Integer.toString(i),new ArrayList<String>(array1));
			}
		}

	}

	/**
	 * returns the path of the result file
	 */
	@Override
	public String getResultFile() {
		return ".\\result\\distinctive\\"+this.lang+"\\BestResultsDiffClusters.txt";
	}
	
	
}
