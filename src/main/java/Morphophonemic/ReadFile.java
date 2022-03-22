package Morphophonemic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import Helper.Log.Logging;

/**
 * reads the input file and generates morpheme objects
 * 
 * @author abischoff
 *
 */
public class ReadFile {

	private String language;
	private boolean ipa;
	private ArrayList<AbstractMorpheme> morphemes = new ArrayList<AbstractMorpheme>();
	
	/**
	 * 
	 * @param lang	name of the language
	 * @param ipa	usage of IPA transcription
	 */
	public ReadFile(String lang, boolean ipa) {
		this.language = lang;
		if (lang.contentEquals("Deutsch")) {
			this.language = "German";
		}
		this.ipa = ipa;
	}
	
	/**
	 * reads the input file and generates the morphemes of the language
	 * @param filePath	path of the file
	 * @return	an arraylist with Morpheme objects, the list is empty if the structure of the data are not correct
	 */
	public ArrayList<AbstractMorpheme> read(String filePath) {
		
		if (!new File(".\\data").exists()) {
			new File(".\\data").mkdir();
		} 
		if (!new File(".\\data\\transData").exists()) {
			new File(".\\data\\transData\\").mkdir();
		}
		if (!new File(".\\data\\transData\\"+this.language).exists()) {
			new File(".\\data\\transData\\"+this.language).mkdir();
			Logging.debug("Directory " + ".\\data\\transData\\"+this.language + " created.");
		}
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(filePath) , "UTF-8"));
			String line = br.readLine();
			if (line.startsWith("Lemma")) {
				line = br.readLine();
			}
			
			while (line != null) {
				if (line.matches(".*\\t.*\\t.*")) {
					String[] entry = line.split("\\t");
					String lemma = entry[0];
					String pos = entry[1];
					AbstractMorpheme neu = null; 
					if (this.ipa == true) {
						neu = new IPAMorpheme(this.language, pos, lemma);
					} else {
						neu = new Morpheme(this.language, pos, lemma);
					}
					for (int i = 2 ; i < entry.length ; i++) {
						String[] wordform = entry[i].split("\\(");
						if (wordform.length >= 1) {
							neu.addAllomorph(wordform[0]);
						}
					}
					morphemes.add(neu);
				} else {
					Logging.warn("File needs the structure lemma tab POS tab word forms (tab separated)");;
				}
				
				line = br.readLine();
			}
			
			br.close();
		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
		
		Logging.debug("File read.");
		
		return this.morphemes;
	}
	
	/**
	 * calculates the frequency of the phonotagms and writes the result into a file
	 * @return the path of the output file or null
	 */
	public HashMap<String,Integer> getFrequencies() {
		HashMap<String,Integer> freqs = new HashMap<String,Integer>();
		String outputPath = ".\\data\\transData\\"+this.language+"\\TotalFrequencies.txt";
		   HashMap<String,Integer> phonotaktik = new HashMap<String,Integer>();
		   
		   for (AbstractMorpheme lexem : this.morphemes) {
			   
			   for (String myAllo : lexem.getAllomorphs().keySet()) {
				   
					   if (!this.ipa) {
						   StringBuilder sb = new StringBuilder(myAllo.toCharArray().length);
						   for (char c : myAllo.toCharArray()) {
						     sb.append(Character.toString(c)+" ");
						   }
						   myAllo = sb.toString().trim();
					   }

					   String[] wordformArray = ("# " + myAllo + " #").split("\\s");
					  
						   
					   for (int zeiger = 1, vorzeiger = 0, nachzeiger = 2 ; nachzeiger < wordformArray.length ; zeiger++, vorzeiger++, nachzeiger++) {
							   //single
						   if (phonotaktik.containsKey(wordformArray[zeiger])) {
							   int freq = phonotaktik.get(wordformArray[zeiger]);
							   phonotaktik.remove(wordformArray[zeiger]);
							   phonotaktik.put(wordformArray[zeiger], freq+1);
						  } else {
							   phonotaktik.put(wordformArray[zeiger], 1);
						  }

							   //double
							   
						   String doppel = wordformArray[vorzeiger] + " " + wordformArray[zeiger];
							 	if (phonotaktik.containsKey(doppel)) {
								 int freq = phonotaktik.get(doppel);
								 phonotaktik.remove(doppel);
								   phonotaktik.put(doppel, freq+1);
							} else {
								phonotaktik.put(doppel, 1);
							}

							   //last double
							   
							 	if (nachzeiger == wordformArray.length-1) {
								   String last = wordformArray[zeiger] + " " + wordformArray[nachzeiger];
								   if (phonotaktik.containsKey(last)) {
									   int freq = phonotaktik.get(last);
									   phonotaktik.remove(last);
									   phonotaktik.put(last, freq+1);
								   } else {
									   phonotaktik.put(last, 1);
								   }
							   }
							   
							   
							   //triple
							   
							   String tripel = wordformArray[vorzeiger] + " " + wordformArray[zeiger] + " " + wordformArray[nachzeiger];
							   if (phonotaktik.containsKey(tripel)) {
								   int freq = phonotaktik.get(tripel);
								   phonotaktik.remove(tripel);
								   phonotaktik.put(tripel, freq+1);
							   } else {
								   phonotaktik.put(tripel, 1);
							   }

						   }

					   //Add # as a sound
					   if (phonotaktik.containsKey("#")) {
						   int freq = phonotaktik.get("#");
						   phonotaktik.remove("#");
						   phonotaktik.put("#", freq+2); //before and after
					  } else {
						   phonotaktik.put("#", 2);
					  }
			   }
		   }
		   
			try {
				
				int totalSingle = 0; //sum of all single sounds
				int totalDouble = 0;
				int totalTriple = 0;
				
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter (new FileOutputStream(outputPath),"UTF-8"));
			
				for (String kette : phonotaktik.keySet()) {
					
					if (kette.split("\\s").length == 1) {
						totalSingle += phonotaktik.get(kette);
					} else if (kette.split("\\s").length == 2) {
						totalDouble += phonotaktik.get(kette);
					} else if (kette.split("\\s").length == 3) {
						totalTriple += phonotaktik.get(kette);
					}
					
					bw.write(kette + "\t" + phonotaktik.get(kette)+ "\r\n");
					freqs.put(kette, phonotaktik.get(kette));
					
				}
				
				bw.write("_\t" + totalSingle+ "\r\n");
				freqs.put("_", totalSingle);
				bw.write("__\t" + totalDouble+ "\r\n");
				freqs.put("__", totalDouble);
				bw.write("___\t" + totalTriple + "\r\n");
				freqs.put("___", totalTriple);
				
				bw.close();
			
			} catch (IOException e) {
				Logging.error(e.getLocalizedMessage());
			} 
			
			return freqs;
	   }
	
}
