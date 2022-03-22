package Distributional;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import Helper.Log.Logging;

/**
 * implement the subtypological method
 * see also Helper.Typology
 * 
 * @author abischoff
 *
 */
public class SubtypologyModul {

	private HashMap<String,String> soundReplacements = new HashMap<String,String>(); // { sound in the universal corpus -> its correspondence in the language in question }
	private HashMap<String,String> languageCodes = new HashMap<String,String>(); // {language name -> language id }
	private TreeMap<Double,String> mostSimilarLangs = new TreeMap<Double,String>();
	private String[] chosenLangs; // Top 5 most similar Langs [nld, nob, pdt, cat, dan]
	private String lang = "myLanguage";
	private String file;
	private HashMap<String,TreeMap<Double, String>> complementaryCandidates = new HashMap<String,TreeMap<Double,String>>(); //candidates for the comparison

	//to compare
	private List<SoundVector> svListDeutsch = new ArrayList<SoundVector>();
	private List<SoundVector> svListUniversal = new ArrayList<SoundVector>();
	private TFIDFText ti; //of the language in question
	
	/**
	 * only for tests
	 * @param ma
	 */
	public static void main(String[] ma) {
		SubtypologyModul sub = new SubtypologyModul("Deutsch",".\\Test.txt");
		sub.findSubtypologicalLanguages();
		Logging.debug("5 most similar languages : " +Arrays.asList(sub.getMostSimilarLangs(5)));
		sub.compareCorpusWithSubTypology(sub.getPreparedCorpusOrDefault(),sub.getUniversalCorpusOrDefault());
		sub.getComplementarySound("z");
	}
	
	/**
	 * 
	 * @param language	language name
	 * @param file		file path
	 */
	public SubtypologyModul(String language, String file) {
		this.file = file;
		this.readUniversalSounds();
		if (this.languageCodes.containsValue(language)) {
			this.lang = this.languageCodes.get(language);
		} else if (this.languageCodes.containsKey(language)) {
			this.lang = this.languageCodes.get(language);
		} else if (lang.contentEquals("Deutsch")) {
			this.lang = "deu";
		} else {
			Logging.warn("Language "+language + " not found in LanguageCodes.txt");
		}
		this.applyReplacementsOnLanguages();
	}
	
	/**
	 * applies the file myLanguageCorpus.txt to the method getCorpusOrDefault()
	 * @return	the path of the result file
	 */
	public String getPreparedCorpusOrDefault() {
		return getCorpusOrDefault("myLanguageCorpus.txt");
	}
	
	/**
	 * applies the file myUniversalCorpus.txt to the method getCorpusOrDefault()
	 * @return	the path of the result file
	 */
	public String getUniversalCorpusOrDefault() {
		return getCorpusOrDefault("myUniversalCorpus.txt");
	}
	

	/**
	 * generates from the chosenLangs a corpus file and stores it in the dir \\data\\transData\\Universal\\subtypology
	 * @param name	file name
	 * @return the path of the resul file
	 */
	private String getCorpusOrDefault(String name) {
		try {

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter (new FileOutputStream(".\\data\\transData\\Universal\\subtypology\\"+name),"UTF-8"));
			if (name.contentEquals("myUniversalCorpus.txt")) {
				for (String lc : this.chosenLangs) {
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(".\\data\\transData\\Universal\\subtypology\\languages\\"+lc),"UTF-8"));
					String line = br.readLine();
					
					while (line != null) {
						bw.write(line+"\r\n");
						line = br.readLine();
					}
					br.close();
				}
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.file),"UTF-8"));
				String line = br.readLine();
				while (line != null) {
					bw.write(getWordform(line)+"\r\n");
					line = br.readLine();
				}
				br.close();
			}
			bw.close();
			if (new File(".\\data\\transData\\Universal\\subtypology\\"+name).exists())
				return ".\\data\\transData\\Universal\\subtypology\\"+name;
			
		}catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
		
		return null;
	}

	/**
	 * replaces all sounds in the universal corpus by a sound that exists in the phoneme list of the language in question
	 */
	private void applyReplacementsOnLanguages() {

		try {
			File direc = new File(".\\data\\rawData\\Universal\\subtypology\\languages");
			File[] fileArray = direc.listFiles();	
			ArrayList<File> fileList = new ArrayList<File>();
			fileList.addAll(Arrays.asList(fileArray));
			if (!this.languageCodes.containsValue(this.lang))
				fileList.add(new File(this.file));
			
			for (File file : fileList) {
				String fileName = file.getName();
				if (file.getAbsolutePath().contentEquals(this.file)) {
					fileName = "myLanguage";
				}
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file.getCanonicalPath()),"UTF-8"));
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter (new FileOutputStream(".\\data\\transData\\Universal\\subtypology\\languages\\"+fileName),"UTF-8"));

				String line = br.readLine();

				bw.write(getWordform(line));
				line = br.readLine();
				while (line!=null ) {
					bw.write("\r\n"+getWordform(line));
					line = br.readLine();
				}
				
				br.close();
				bw.close();
			}
		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
		
	}
	
	/**
	 * reads the universal replacement file and declares the variables soundReplacements and languageCodes
	 */
	public void readUniversalSounds() {
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(".\\data\\rawData\\Universal\\subtypology\\UniversalSoundCorrespondences.txt"), "UTF-8"));
			String line = br.readLine();
			line = br.readLine();
			
			while (line != null) {
				this.soundReplacements.put(line.split("\t")[0], line.split("\t")[1]);
				line = br.readLine();
			}
			br.close();
			
			BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(".\\data\\rawData\\Universal\\subtypology\\LanguageCodes.txt"), "UTF-8"));
			String line2 = br2.readLine();
			line2 = br2.readLine();
			
			while (line2 != null) {
				this.languageCodes.put(line2.split("\t")[0], line2.split("\t")[1]);
				line2 = br2.readLine();
			}
			br2.close();
			
		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
		
	}
	

	/**
	 * determines the most similar languages for this langauge and stores the result (degree of similarity and name) in this.mostSimilarLangs
	 */
	public void findSubtypologicalLanguages() {
		
		TreeMap<Double, String> result = new TreeMap<Double,String>();
		HashMap<String,double[]> langVector = new HashMap<String,double[]>();
		HashMap<String,Integer> phonotagms = new HashMap<String,Integer>(); //phonotagm -> position in vector
		
		try {		
			//1. read alphabet
			ArrayList<String> alphabet = new ArrayList<String>();
			alphabet.add("#");
			for (String value : this.soundReplacements.values()) {
				if (!alphabet.contains(value)) {
					alphabet.add(value);
				}
			}

			// create phonotagms
			int count = 0;
			for (String letter1 : alphabet) {
				for (String letter2 : alphabet) {
					phonotagms.put(letter1+" "+letter2, count);
					count++;
				}

			}
			
			
			File direc = new File(".\\data\\transData\\Universal\\subtypology\\languages");
			File[] fileArray = direc.listFiles();	

			for (File lang : fileArray) {

				HashMap<String,Integer> countPhonotagm = new HashMap<String,Integer>();
				int totalPhonotagms = 0;
				
				BufferedReader brL = new BufferedReader(new InputStreamReader( new FileInputStream(lang) , "UTF-8"));
				String wordLine = brL.readLine();
				while (wordLine != null) {
					wordLine = wordLine.replaceAll("[ː]", ""); //ignore diacritics
					String[] word = ("# "+wordLine.trim()+" #").split("\\s+");
					for (int i = 0, j = 1; j < word.length; i++,j++) {
						String phonotagm = word[i]+ " " + word[j];
						if (countPhonotagm.containsKey(phonotagm)) {
							int old = countPhonotagm.get(phonotagm);
							countPhonotagm.put(phonotagm, old+1);
 						} else {
							countPhonotagm.put(phonotagm, 1);
						}
						totalPhonotagms++;
					}
					
					
					wordLine = brL.readLine();
				}
				
				brL.close();
				
				double[] myVector = new double[phonotagms.size()];
				for (String phonotagm : countPhonotagm.keySet()) {
					if (phonotagms.containsKey(phonotagm)) {
						int position = phonotagms.get(phonotagm);
						myVector[position] = (((double) countPhonotagm.get(phonotagm)) / ((double) totalPhonotagms))*10000.0;
					} else {
						Logging.debug("Missing phonotagm: " + phonotagm);
					}
				}
				langVector.put(lang.getName(), myVector);
			}

			double[] myLanguage = langVector.get(this.lang);
			
			for (String lang : langVector.keySet()) {
				if (!lang.contentEquals(this.lang) && !lang.contentEquals(this.lang)) {
					double cosSim = TFIDFText.cosineSimilarity(langVector.get(lang), myLanguage);
					
					if (!Double.isNaN(cosSim)) {
						if (result.containsKey(cosSim)) {
							String old = result.get(cosSim);
							result.put(cosSim, old + " " + lang);
						} else {
							result.put(cosSim, lang);
						}
					}
				}
			}
		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}

		this.mostSimilarLangs =  result;
	}
	
	
	/**
	 * turns a string "Hello" to "H e l: o"
	 * @param str	string that should be converted
	 * @return	the converted string
	 */
	private String getWordform(String str) {
		String line = "";
		for (char charac : str.toCharArray()) {
			line += " "+Character.toString(charac);
		}
		String[] words = line.trim().split("\\s+");
		String out = "";
		for (String word : words) {

			for (Character letterChar : word.toCharArray()) {
				if (letterChar.equals('ː')) {
					String lastone = out.split("\\s")[out.split("\\s").length-1];
					if (lastone.contentEquals("ɛ") || lastone.contentEquals("a") || lastone.contentEquals("ɪ") || lastone.contentEquals("u") || lastone.contentEquals("i") ||
							lastone.contentEquals("o") ||lastone.contentEquals("ɔ") ||lastone.contentEquals("œ") || lastone.contentEquals("ʊ") || lastone.contentEquals("e") ||
							lastone.contentEquals("ʏ") ||lastone.contentEquals("y") ||lastone.contentEquals("ø")) {
						out += "ː";
					}
				} else if (letterChar.equals('ʒ')) {
					String lastone = out.split("\\s")[out.split("\\s").length-1];
					if (lastone.contentEquals("d")) {
						out = out.replaceAll("d$", "ʤ");
					} else {
						out +=" "+ this.soundReplacements.get(Character.toString(letterChar));
					}
				} else if (letterChar.equals('ʃ')) {
					String lastone = out.split("\\s")[out.split("\\s").length-1];
					if (lastone.contentEquals("t")) {
						out = out.replaceAll("t$", "ʧ");
					}else {
						out +=" "+ this.soundReplacements.get(Character.toString(letterChar));
					}
				} else if (letterChar.equals('s')) {
					String lastone = out.split("\\s")[out.split("\\s").length-1];
					if (lastone.contentEquals("t")) {
						out = out.replaceAll("t$", "ʦ");
					}else {
						out +=" "+ this.soundReplacements.get(Character.toString(letterChar));
					}
				} else if (this.soundReplacements.containsKey(Character.toString(letterChar))) {
					out +=" "+ this.soundReplacements.get(Character.toString(letterChar));
				}
			}
		}

		return out.trim();
	}


	/**
	 * 
	 * defines the x phonotactically most similar language in this.mostSimilarLangs
	 * 
	 * @param rank	how many languages should be determined
	 * @return an array of x language names
	 */
	public String[] getMostSimilarLangs(int rank) {
		String[] result = new String[rank];
		Collection<String> langs = this.mostSimilarLangs.descendingMap().values();
		int pos = 0;
		for (int i = 0 ; i < rank; i++) {
			for (int j = 0; j < ((String)langs.toArray()[i]).split(" ").length; j++) {
				String lang = ((String)langs.toArray()[i]).split(" ")[j];
				if (pos < rank) {
					result[pos] = lang;
					pos++;
				} else {
					break;
				}
				
			}
		}
		
		this.chosenLangs = result.clone();
		
		int d = 0;
		for (String chosenLang : this.chosenLangs) {
			String longName = null;
			for (String langName : this.languageCodes.keySet()) {
				if (this.languageCodes.get(langName).contentEquals(chosenLang) && 
						(longName == null || longName.toCharArray().length < langName.toCharArray().length)) {
					longName = langName;
				}
			}
			result[d] = longName;
			d++;
		}
		
		return result;
	} 
	
	/**
	 * Getter for the candidates
	 * @return complementary candidates
	 */
	public HashMap<String,TreeMap<Double, String>> getCandidates() {
		return this.complementaryCandidates;
	}
	
	/**
	 * compares the phonotactical similarity of the language in question and its subtypologically related languages
	 * @param file1	file of the language in question
	 * @param file2	file of the subtypological related languages
	 */
	@SuppressWarnings("unused")
	public void compareCorpusWithSubTypology(String file1, String file2) {
		// ReadCorpus reads the corpus and generates the file occurrences.txt		
		boolean aroundOn = false; //bad for comparisons
		boolean ipa = true;
		ReadCorpus rc = new ReadCorpus(file1,this.lang,"UTF-8",aroundOn,ipa); 
		ReadCorpus rc2 = new ReadCorpus(file2, "Subtypology","UTF-8", aroundOn, ipa);
		this.initAllCondition(file1, file2);//this makes sure that both have the same vectors

		this.ti = new TFIDFText(2,1,1,this.lang, aroundOn);

		this.svListDeutsch = ti.getSVList();
				
		TFIDFText ti2 = new TFIDFText(2,1,1,"Subtypology",aroundOn);
		this.svListUniversal = ti2.getSVList();
	}
	

	/**
	 * determines the complementary sound of a sound according to the subtypological method
	 * call compareCorpusWithSubTypology() before
	 * 
	 * @param laut	sound
	 * @return the complementary sound according to the subtypological method
	 */
	public String getComplementarySound(String laut) {

		double bestValue = 100.0;
		String compSound = "";
		SoundVector laut1 = null;
		double corpus1Size = 0;
		
		for (SoundVector sv : this.svListDeutsch) {

			if (sv.getName().contentEquals(laut)) {
				laut1 = sv;
			}
			corpus1Size += sv.getTotalOccurrence();
		}

		if (laut1 == null) {
			return null;
		}
		
		HashMap<String,Double> slacks = (HashMap<String, Double>)  new CompFinder(this.lang, ti.getSVList()).getSlacks(this.svListDeutsch, laut1, 0.1, 100.0);

		SoundVector laut2 = null;
		double corpus2Size = 0; //number of sounds in the corpus
		
		for (SoundVector sv : this.svListUniversal) {
			if (sv.getName().contentEquals(laut)) {
				laut2 = sv;
			}
			corpus2Size += sv.getTotalOccurrence();
		}
		if (laut2 == null) {
			Logging.warn("Sound " +laut + " not found in the universal corpus!");
			return "";
		}
		
		TreeMap<Double,String> features = new TreeMap<Double,String>();
		HashMap<String,Double> feature1 = new HashMap<String,Double>();
		for (int i = 0 ; i < laut1.getTFIDFVector().length ; i++ ) {
			if (!laut1.getFeatureName(i).contentEquals(laut2.getFeatureName(i))) {
				Logging.warn("WARNING: Vectors are not identical!");
			}
			feature1.put(laut1.getFeatureName(i),laut1.getTFIDFVector()[i]);
		}

		HashMap<String,Double> feature2 = new HashMap<String,Double>();
		for (int i = 0 ; i < laut2.getTFIDFVector().length ; i++ ) {

			feature2.put(laut2.getFeatureName(i),laut2.getTFIDFVector()[i]);
		}
		
		for (String feat1 : feature1.keySet()) {
			if (feature2.containsKey(feat1)) {
				double value = feature1.get(feat1) - feature2.get(feat1);
				if (features.containsKey(value)) {
					
					String d = features.get(value);
					features.remove(value);
					features.put(value, d + ", " + feat1);
				} else {
					features.put(value, feat1);
				}
			} else {
				Logging.debug("Missing feature: " + feat1);
			}
		} 
		
		//CompFinder
		
		SoundVector b1 = null;
		TreeMap<Double, String> candidates = new TreeMap<Double,String>();
		for (SoundVector svD : this.svListDeutsch) {
			
			b1 = svD;
			SoundVector b2 = null;
			
			String nameSvD = svD.getName();
			
			for (SoundVector svU : this.svListUniversal) {

				if (svU.getName().contentEquals(nameSvD)) {
					b2 = svU;
					
					double[] deutschAB = TFIDFText.addVectorValues(laut1.getSlackVectorArray(corpus1Size,slacks.keySet()),b1.getSlackVectorArray(corpus1Size,slacks.keySet()));
					double[] universalAB = TFIDFText.addVectorValues(laut2.getSlackVectorArray(corpus2Size,slacks.keySet()),b2.getSlackVectorArray(corpus2Size,slacks.keySet()));
					
					double value = TFIDFText.euDistance(deutschAB, universalAB);
					if (candidates.containsKey(value)) {
						String old = candidates.get(value);
						candidates.put(value, old + " " + nameSvD);
					} else {
						candidates.put(value, nameSvD);
					}
					
					if (value < bestValue) {
						bestValue = value;
						compSound = nameSvD;
					}
				}
			}
			
		}
		if (!this.complementaryCandidates.containsKey(laut)) {
			this.complementaryCandidates.put(laut, candidates);
		}
		return compSound;			

	}
	
	/**
	 * makes sure that both files uses the same vectors
	 * @param file1	file of the language in question
	 * @param file2 file of the subtypological related languages
	 */
	public void initAllCondition(String file1, String file2) {		
		
		try {
			
			BufferedReader br1 = new BufferedReader(new InputStreamReader( new FileInputStream(".\\data\\transData\\Occurrences"+this.lang) , "UTF-8") );
			BufferedReader br2 = new BufferedReader(new InputStreamReader( new FileInputStream(".\\data\\transData\\OccurrencesSubtypology") , "UTF-8") );

			ArrayList<String> soundVectorNames = new ArrayList<String>();
			String vector = br1.readLine();
			while (vector != null) {
				String[] nameCut = vector.split("= ");
				if (!soundVectorNames.contains(nameCut[0]))
					soundVectorNames.add(nameCut[0]);
				
				vector = br1.readLine();
			}
			
			br1.close();	
			String vector2 = br2.readLine();
			while (vector2 != null) {
				String[] nameCut2 = vector2.split("= ");
				if (!soundVectorNames.contains(nameCut2[0]))
					soundVectorNames.add(nameCut2[0]);
				
				vector2 = br2.readLine();
			}
			
			br2.close();		
			
			/*
			 * initialize sound vectors (first check because they are already initialized for inter-lingual comparisons
			 */

				boolean aroundOn = true;
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

		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());

		}
	}
	
}
