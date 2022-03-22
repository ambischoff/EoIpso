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
import java.util.Arrays;
import java.util.HashMap;

import Helper.Language.Method;
import Helper.Log.Logging;

/**
 * 
 * reads Wiktionary and extracts paradigmatic or derivational data
 * seralizes the extracted data
 * 
 * @author abischoff
 *
 */
public class ReadWiktionary {

	private String filePath;
	private boolean ipaWiki;

	private boolean paradigmatisch = false;
	private boolean derivational = false;
	
	private String language;

	private ArrayList<Morpheme> morphems = new ArrayList<Morpheme>();
	private ArrayList<Morpheme> relevantMorphems = new ArrayList<Morpheme>();
	private ArrayList<IPAMorpheme> IPAmorphems = new ArrayList<IPAMorpheme>();
	
	private static HashMap<String,String> IPAConvert = new HashMap<String,String>(); 
	private static ArrayList<String> suffix = new ArrayList<String>();
	private static ArrayList<String> praefix = new ArrayList<String>();
	private static ArrayList<String> IPAsuffix = new ArrayList<String>();
	private static ArrayList<String> IPApraefix = new ArrayList<String>();
	
	/**
	 * 
	 * @param method	ParadigmaticMethod or DerivationalMethod
	 * @param lang		name of the language (should be the name that is stored in Wiktionary)
	 * @param ipa		should the IPA transcription of Wiktionary be used?
	 */
	public ReadWiktionary(Method method, String lang, boolean ipa) {
	
		if (Method.paradigmatic == method) {
			paradigmatisch = true;
		} else if (Method.derivational == method) {
			derivational = true;
		}
		this.ipaWiki = ipa;
		
		if (lang.contentEquals("German") || lang.contains("Deutsch")) {
			this.language = "Deutsch";
		} else {
			this.language = lang;
			Logging.warn("Lemmatization only exists for German");
		}

	}
	
	/**
	 * reads Wiktionary and initializes this.filePath and this.frequency
	 * @return	an arrayList with Morpheme objects
	 */
	public ArrayList<?> readWiki() {
		Logging.debug("Read Wiktionary ...");
		this.filePath = this.readCorpusOrDefault(true); 
		if (this.ipaWiki) {
			this.filePath = this.getIpaEntriesOrDefault();
		}
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
		if (ipaWiki) {
			return this.IPAmorphems;
		}

		return this.morphems;
	}
	
	/**
	 * calculates the frequency of the phonotagms and writes the result into a file
	 * @return the path of the output file or null
	 */
	public HashMap<String,Integer> getFrequencies() {
		HashMap<String,Integer> freqs = new HashMap<String,Integer>();
		String outputPath = ".\\data\\transData\\"+this.language+"\\TotalFrequencies.txt";
		   HashMap<String,Integer> phonotaktik = new HashMap<String,Integer>();
		   
		   ArrayList<AbstractMorpheme> myMorphems = new ArrayList<AbstractMorpheme>();
		   if (ipaWiki) {
			   myMorphems.addAll(IPAmorphems);
		   } else {
			   myMorphems.addAll(morphems);
		   }
		   
		   for (AbstractMorpheme lexem : myMorphems) {
			   
			   for (String myAllo : lexem.getAllomorphs().keySet()) {
				   
				   for (Wordform wordform : lexem.getAllomorphs().get(myAllo)) {
					   
					   String wordformString = wordform.getName();
					   if (!ipaWiki) {
						   StringBuilder sb = new StringBuilder(wordform.getName().toCharArray().length);
						   for (char c : wordform.getName().toCharArray()) {
						     sb.append(Character.toString(c)+" ");
						   }
						   wordformString = sb.toString().trim();
					   }

					   String[] wordformArray = ("# " + wordformString + " #").split("\\s");
					  
						   
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
	
	/**
	 * 
	 * reads German Wiktionary
	 * 
	 * @param allMorphems all morphems or only those with allomorphs
	 * @return the file path of the txt with all Wikionary entries or null
	 */
	public String readCorpusOrDefault(boolean allMorphems){ 
		String outputPath = ".\\data\\transData\\"+this.language+"\\WiktionaryEntries.txt";
		try {
			BufferedReader br2 = new BufferedReader(new InputStreamReader( new FileInputStream(".\\data\\rawData\\Wiktionary\\dewiktionary-latest-pages-articles.xml") , "UTF-8"));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter (new FileOutputStream(outputPath),"UTF-8"));

			String line2 = br2.readLine();
			Morpheme actualPara = null;
			Morpheme actualDeriv = null;
			
			boolean paradigma = false;
			boolean wortbildungen = false;
			String lastLang = "";
			String lastWortArt = "";
			String title = "";
			String ipaString = "";

			while (line2 != null) {
				
				if (line2.contains("</title>")) {
					if (line2.split(">").length == 2) {
						title = line2.split(">")[1].split("<")[0];
					} 

				}
				
				if (line2.startsWith(":{{IPA}}") && this.ipaWiki && lastLang.equals(this.language)) {
					if (line2.split("Lautschrift\\|").length >= 2) {
						String preIPA = line2.split("Lautschrift\\|")[1];			
						if (!preIPA.startsWith("}")) {
							ipaString = preIPA.split("}")[0];
							IPAConvert.put(title, makeIPA(ipaString).trim());
							if (lastWortArt.equals("Suffix")) {
								IPAsuffix.add(makeIPA(ipaString).replaceAll("(^- )", "").trim()); 
							}
							if (lastWortArt.equals("Präfix")) {
								IPApraefix.add(makeIPA(ipaString).replaceAll("(^- )", "").trim());
							}
							if (lastWortArt.equals("Affix")) {
								if (title.startsWith("-")) {
									IPAsuffix.add(makeIPA(ipaString).replaceAll("(^- )", "").trim());
								} else {
									IPApraefix.add(makeIPA(ipaString).replaceAll("(^- )", "").trim());
								}
							}
							
						}
						
					}
				}
				
				if (title.contains("Flexion:") || !title.contains(":")) { //ignores all helping pages

					/*
					 * Determination of language and POS
					 */

					if (line2.contains("{{Wortart|")){

						lastLang = "";
						lastWortArt = "";
						
						if (line2.split("\\|").length == 3) {
							lastWortArt = line2.split("\\|")[1].trim();
							if (lastWortArt.contains("rzung")) {
								lastWortArt = "Substantiv";
							}
							
							lastLang = line2.split("\\|")[2].split("}")[0];
							
							if (lastWortArt.equals("Suffix") && lastLang.equals(language)) {
								suffix.add(title);
							}
							if (lastWortArt.equals("Präfix") && lastLang.equals(language)) {
								praefix.add(title);
							}
							if (lastWortArt.equals("Affix") && lastLang.equals(language)) {
								if (title.startsWith("-")) {
									suffix.add(title);
								} else {
									praefix.add(title);
								}
							}
							
							if (!lastWortArt.equals("Verb") && !lastWortArt.equals("Adjektiv") && !lastWortArt.equals("Substantiv")) {
								
								lastWortArt = lastWortArt.replaceAll("Eigenname", "Substantiv").replaceAll("Straßenname", "Substantiv").replaceAll("Toponym","Substantiv").replaceAll("Vollverb", "Verb").replaceAll("Hilfsverb", "Verb");

							}
							
						} else if (line2.split("\\|").length > 3) { //the author hat more than one form, choose noun, verb,adjective,German
							
							for (int i = 0 ; i < line2.split("\\|").length ; i++) {
								String split = line2.split("\\|")[i];
								if (split.equals("Verb")) {
									lastWortArt = "Verb";
									lastLang = line2.split("\\|")[i+1].split("}")[0].trim();
								} else if (split.equals("Substantiv")) {
									lastWortArt = "Substantiv";
									lastLang = line2.split("\\|")[i+1].split("}")[0].trim();
								} else if (split.equals("Adjektiv")) {
									lastWortArt = "Adjektiv";
									lastLang = line2.split("\\|")[i+1].split("}")[0].trim();
								} 
							}
							
							if (lastWortArt.equals("")) { //for auxiliary verbs etc.
								lastWortArt = line2.split("\\|")[1];
								lastLang = line2.split("\\|")[2].split("}")[0];
								
								if (lastWortArt.contains("rzung")) {
									lastWortArt = "Substantiv";
								}
								
								if (lastWortArt.equals("Suffix") && lastLang.equals(language)) {
									suffix.add(title);
								}
								if (lastWortArt.equals("Präfix") && lastLang.equals(language)) {
									praefix.add(title);
								}
							
								if (!lastWortArt.equals("Verb") && !lastWortArt.equals("Adjektiv") && !lastWortArt.equals("Substantiv")) {
									
									lastWortArt = lastWortArt.replaceAll("Eigenname", "Substantiv").replaceAll("Stra�enname", "Substantiv").replaceAll("Toponym","Substantiv").replaceAll("Vollverb", "Verb").replaceAll("Hilfsverb", "Verb");
								}
							}
						}
					}
					
					if (line2.contains("{{Verbkonjugation")) {
						lastWortArt = "Verb";
						lastLang = line2.split("\\|")[1].split("}")[0];
					}
					
					
					/*
					 * Paradigmatic 
					 */
					if (paradigmatisch) {
						if (line2.contains("}}") || line2.contains("|Bild") || line2.contains("| Bild") ) {
							paradigma = false;
							actualPara = null;							
						}
						if (paradigma) {
							if (actualPara != null) {
								String feature = actualPara.addFeat(line2);
								feature.toCharArray();
							}
						}
						if (line2.contains("{{Deutsch") && line2.contains("Verb") && !line2.contains("}}")) {
							paradigma = true;
	

							Morpheme neu = new Morpheme("German", "Verb", title);
							morphems.add(neu);
							actualPara = neu;
						}
						if (line2.contains("{{Deutsch") && line2.contains("Substantiv") && !line2.contains("}}")) {
							paradigma = true;
						
							
							Morpheme neu = new Morpheme("German", "Substantiv", title);
							morphems.add(neu);
							actualPara = neu;
						}
						if (line2.contains("{{Deutsch") && line2.contains("Adjektiv") && !line2.contains("}}")) {
							paradigma = true;
					

							Morpheme neu = new Morpheme("German", "Adjektiv", title);
							morphems.add(neu);
							actualPara = neu;
						}
					}
					
					/*
					 * Derivational
					 */
					if (derivational) {
						
						if (lastLang.equals(language)) { 
						
							if (!line2.equals("") && !line2.startsWith(":")) {
								wortbildungen = false;
								actualDeriv = null;
							}	
							
							if (wortbildungen) {
								if (actualDeriv != null) {
									actualDeriv.addDerivat(line2);
								}
							}
							if (line2.contains("{{Wortbildung")) {
								wortbildungen = true;

								Morpheme neu = new Morpheme(lastLang, lastWortArt, title);
								morphems.add(neu);
								actualDeriv = neu;
							}
						}
					}
					
				}
				
				line2 = br2.readLine();
				
				
			}
			
			for (Morpheme morphem : morphems) {
				
				/*
				 *  check forms for suffixes and generate word stems
				 */
				
				if (derivational) {
					
					if (morphem.getWortbildungen().size() != 0) {
						morphem.derivatStem();
					}
					
				}
				
				boolean relevant = false;
				if (morphem.getSprache() == MorphemeLanguage.Deutsch) {
						for (String allo : morphem.getAllomorphs().keySet()) { //change allomorph here
							if (allMorphems || morphem.getAllomorphs().size() > 1) {
								bw.write(allo + "(" + morphem.getAllomorphs().get(allo)[0].getInfo() + ") ");
								relevant = true;
							}
						}
						if (allMorphems || morphem.getAllomorphs().size() > 1)
							bw.write("\r\n");
						
						if(relevant) {
							relevantMorphems.add(morphem);
							relevant = false;
						}
				}
			}
			Logging.debug("Reading Wiktionary finished with " + morphems.size() + " morphemes." );
			br2.close();
			bw.close();

		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
		
		if (new File(outputPath).exists()) {
			return outputPath;
		}
		return null;
		
	}
	
	
	/**
	 * 
	 * turns an ipa string to a space separated ipa string
	 * puts the stress symbol to the next vowel
	 * 
	 * @param ipa	ipa string 
	 * @return	IPA string with space between the symbols
	 */
	public String makeIPA(String ipa) {

		//the stress symbol should be with the next vowel
		ArrayList<String> vokale = new ArrayList<String>(Arrays.asList("a", "o", "ɔ", "i", "ʊ", "e", "y",
				"u", "ɐ", "͡ɛ", "ə", "ɪ", "ʉ", "ɑ", "æ", "̃œ" ,"œ", "ʏ", "ø", "ã", "í", "ɒ", "ɜ", 
				"ε", "ɛ", "è", "ô", "ĩ", "î", "ẽ", "ó", "à", "ǎ", "á", "ù", "́ì", "ú", "ı", "ɝ", "ĭ", "ā", 
				"ǐ", "û", "â", "ǔ", "ò", "ê" ,"ŏ" ,"ī" ,"ŭ", "ǝ", 
				"ɘ", "ɶ" , "å" ,"ο", "ƴ", "ᵻ", 
				"ě", "ē", "é", "A", "ō", "ü", "ạ", "ĕ", "ȯ", "ǒ", "ǣ" ,"ï" ));
		
		String out = "";
		boolean accent = false;
		
		for (Character Char : ipa.toCharArray()) {
			String sign = Character.toString(Char);
			
			if (sign.equals("ˈ") || sign.equals("ˌ")) {
				accent = true;
			} else {
				if (accent && vokale.contains(sign)) {
					out += "ˈ";
					accent = false;
				}
				out += sign + " ";
			}
		}
		
		//Diacritic symbols
		out = out.replaceAll(" ͡ ", "͡").replaceAll(" ː", "ː").replaceAll(" ̯ ", "̯ ").replaceAll(" ̩ ", "̩ ").replaceAll(" ̃ ", "̃ ").replaceAll("a ɪ", "aɪ").replaceAll("a ʊ", "aʊ").replaceAll("ɔ ɪ", "ɔɪ").replaceAll("ɔ ʊ̯", "ɔʊ̯").replaceAll(" ʰ", "ʰ").replaceAll(" ̃ː", "̃ː").replaceAll("   ", "--");
		
		return out;
	}
	
	/**
	 * converts the entries from Wiktionary to IPA
	 * @return the file path of the txt with all Wikionary entries or null
	 */
	public String getIpaEntriesOrDefault() {
		
		String outputPath = ".\\data\\transData\\"+this.language+"\\WiktionaryEntriesIPA.txt";;
		
		for (Morpheme morphem : morphems) {
			
			ArrayList<Wordform> IPAwordforms = new ArrayList<Wordform>();
			String preverbIPA = null;	
			for (String allomorph : morphem.getAllomorphs().keySet()) {
				
				Wordform dummie = null;
				
				for (Wordform wordform : morphem.getAllomorphs().get(allomorph)) {
					
					String wf = wordform.getName();

					if (wordform.getPreverb() != null && !wordform.getMode().contains("Partizip")) {
						wf = wf + " " + wordform.getPreverb();
					
						if (IPAConvert.containsKey(wf) && IPAConvert.get(wf).contains("--")) {
							preverbIPA = IPAConvert.get(wf).split("--")[1].replaceAll("  ", " ").trim();
							
							if (dummie != null) {
								String dummieIPA = this.participleHelpOrDefault(IPAConvert.get(dummie.getName()).replaceAll("  " , " "), preverbIPA);
								if (dummieIPA != null) {
									IPAwordforms.add(new Wordform(dummieIPA, dummie.getMode()));
								}
								dummie = null;
							}
						}
					}
					
					if (IPAConvert.containsKey(wf)) {
						String ipaName = IPAConvert.get(wf).replaceAll("  ", " ");
					
						if (preverbIPA != null && wordform.getMode().contains("Partizip")) {
							ipaName = this.participleHelpOrDefault(ipaName,preverbIPA);
						} else if (wordform.getPreverb() != null && wordform.getMode().contains("Partizip")) {
							dummie = wordform;
						}

						if (ipaName != null && dummie == null) {
							IPAwordforms.add(new Wordform(ipaName, wordform.getMode()));
						}
					}
				}
				
			}
			
			/*
			 * stemming
			 */

			if (IPAwordforms.size() > 0) {
				
				IPAMorpheme ipaM = new IPAMorpheme(morphem.getSprache(),morphem.getWortart(),morphem.getName());
				for (Wordform wortform : IPAwordforms) {
					String mode = wortform.getMode();
					if (mode == null) {
						mode = "";
					}
					
					if (paradigmatisch) {
						ipaM.addFeat(wortform.getName().replaceAll("  ", " "), mode); //necessary because of transcription errors that could cause problems					}
					}
					if (derivational) {
						if (IPAConvert.containsKey(morphem.getName())) {
							ipaM.derivatStem(wortform.getName(),IPAConvert.get(morphem.getName()));
						}
					}

				}
				
				this.IPAmorphems.add(ipaM);
			}
			
		}
			/*
			 * write the result into a file
			 */
			try {
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter (new FileOutputStream(outputPath),"UTF-8"));
				for (IPAMorpheme mor : this.IPAmorphems ) {
					if (mor.getAllomorphs().size() != 0) {
						bw.write(mor.getName() + "\t" + mor.getPOS()+":");
						for (String ipa : mor.getAllomorphs().keySet()) {
							bw.write("\t"+ipa + "(" + mor.getAllomorphs().get(ipa)[0].getMode() + ")");
						}
						bw.write("\r\n");
					}
				}
				bw.close();
			} catch (IOException e) {
				Logging.error(e.getLocalizedMessage());
			}
			
		if (new File(outputPath).exists()) {
			return outputPath;
		}
		return null;
	
	}
	
	/**
	 * method to support the lemmatization of German participles
	 * @param ipaName	lemma name as IPA 
	 * @param preverbIPA	preverb as IPA transcription
	 * @return	the stem as IPA or null
	 */
	public String participleHelpOrDefault(String ipaName, String preverbIPA) {
		if (ipaName.startsWith(preverbIPA)) {
			ipaName = preverbIPA + "--" + ipaName.replaceAll(preverbIPA, "").trim();
		} else if (ipaName.replaceAll("Å","n").startsWith(preverbIPA.replaceAll("Å", "n"))) { //transcription errors
			String partizip = ipaName.replaceAll(preverbIPA.replaceAll("n", "Å"), "").substring(1).trim(); 
			if (partizip.startsWith("É")) {
				partizip = "g " + partizip;
			}
			ipaName = preverbIPA + "--" + partizip;
		}
		else { 
			
			if (preverbIPA.contains(":")) { //long vowel
				String tmpPV = preverbIPA.replaceAll("a:","a").replaceAll("e:", "É").replaceAll(" o: ", " É ").replaceAll("u:", "Ê").replaceAll("i:", "Éª");
				if (ipaName.startsWith(tmpPV)) {
					ipaName = preverbIPA + "--" + ipaName.replaceAll(tmpPV, "").substring(1).trim();
	
				}

			} else {	//short vowel
		
				String tmpPV2 = preverbIPA.replaceAll("a","a:").replaceAll("É","e:").replaceAll(" É "," o: ").replaceAll("Ê","u:").replaceAll("Éª", "i:");
				if (ipaName.startsWith(tmpPV2)) {
					ipaName = preverbIPA + "--" + ipaName.replaceAll(tmpPV2, "").substring(1).trim();
		
				}
			}

			if (!ipaName.contains("--")) { //return null if there is something wrong
				ipaName = null;
			}
		}
		
		return ipaName;
	}

	public static String getIPA(String wortform) {
		return IPAConvert.get(wortform);
	}
	
	public static ArrayList<String> getPraefixes() {
		return praefix;
	}
	
	public static ArrayList<String> getSuffixes() {
		return suffix;
	}
	
	public static ArrayList<String> getIPAPraefixes() {
		return IPApraefix;
	}
	
	public static ArrayList<String> getIPASuffixes() {
		return IPAsuffix;
	}
	
	public String getFilePath() {
		return this.filePath;
	}
	
}
