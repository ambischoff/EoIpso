package Morphophonemic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import Helper.Language.Language;
import Helper.Log.Logging;
import Helper.Measures.LevenshteinDistanceMatrix;

/**
 * implements the paradigmatic and derivational method
 * 
 * @author abischoff
 *
 */
public class MorphophonemicLanguage extends Language {

	private boolean readWiki;
	private ArrayList<AbstractMorpheme> morphemes;
	public static HashMap<String,SoundPair> soundpairs = new HashMap<String,SoundPair>();
	private ArrayList<String> soundPairtoIgnore = new ArrayList<String>(); //soundPairs that should be ignored (relevant for iterative approach)
	private HashMap<String,Double> countSoundPairs = new HashMap<String,Double>(); //counts the soundPairs for PMI
	private HashMap<String,Double> countSounds = new HashMap<String,Double>(); //counts the sounds for PMI
	private HashMap<String,Integer> countSoundInMorp = new HashMap<String,Integer>();//counts the occurrence of XY in morphs: { XY -> occurrence }
	
	/**
	 * dummy constructor for MethodCard
	 * @param name	language name
	 * @param file	file path
	 */
	public MorphophonemicLanguage(String name, String file) {
		super(name,file);
		super.setIteration(100);
		super.setThreshold(0.01);
	}
	
	/**
	 * starts the process
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void start() {
		
		if (this.readWiki) { 
			ReadWiktionary read = new ReadWiktionary(this.method, this.lang, this.ipa);
			this.morphemes = (ArrayList<AbstractMorpheme>) read.readWiki();
			SoundPair.setCondFreq(read.getFrequencies());
		} else {
			ReadFile readFile = new ReadFile(this.lang, this.ipa);
			this.morphemes = readFile.read(this.file);
			SoundPair.setCondFreq(readFile.getFrequencies());
		}
		Logging.debug("Find sound pairs...");
		this.findPairs();
		Logging.debug("Rank the detected sound pairs...");
		this.evaluatePairs();
	} 
	
		/**
		 * this method applies the method to find complementary sounds among the allomorphs
		 */
	   public void findPairs() {
		  
		  for (AbstractMorpheme morphem : this.morphemes) {
			  for (int i = 0; i < morphem.getAllomorphs().keySet().size() ; i++ ){
				  
				  for (int j = i+1 ; j < morphem.getAllomorphs().keySet().size() ; j++) {

					  String allomorph1 = morphem.getAllomorphs().keySet().toArray()[i].toString().replaceAll("  ", " ");
					  String allomorph2 = morphem.getAllomorphs().keySet().toArray()[j].toString().replaceAll("  ", " ");
					  LevenshteinDistanceMatrix test = new LevenshteinDistanceMatrix();
					  
					  String[] correspondences = null;
					  
					  if (morphem instanceof IPAMorpheme) {
						  correspondences = test.getCorrespondence(allomorph1.split("\\s"), allomorph2.split("\\s"));
					  } else {
						  correspondences = test.getCorrespondence(allomorph1, allomorph2);
					  }
					  
					  for (int s = 0 ; s < correspondences.length ; s++) {

							  String sound1 = correspondences[s].split("\\|")[0];
							  String sound2 = correspondences[s].split("\\|")[1];
							  
						  if (!soundPairtoIgnore.contains(correspondences[s]) && !soundPairtoIgnore.contains(sound2+"|"+sound1)) {

							  /*
							   * neede for pmi-score
							   */
							  
							  if (countSounds.containsKey(sound1)) {
								  double old = countSounds.get(sound1);
								  old = old + 1.0;
								  countSounds.put(sound1, old);
							  } else {
								  countSounds.put(sound1,1.0);
							  }				
							  if (countSounds.containsKey(sound2)) {
								  double old = countSounds.get(sound2);
								  old = old + 1.0;
								  countSounds.put(sound2, old);
							  } else {
								  countSounds.put(sound2,1.0);
							  }
							  if (countSoundPairs.containsKey(correspondences[s])) {
								  double old = countSoundPairs.get(correspondences[s]);
								  old = old + 1.0;
								  countSoundPairs.put(correspondences[s], old);
							  } else {
								  countSoundPairs.put(correspondences[s],1.0);
							  }
	
							  if (!sound1.equals(sound2)) {
								  SoundPair soundPair = new SoundPair(sound1,sound2);
								  
								  if (soundpairs.containsKey(soundPair.getName())) {
									  SoundPair knownSoundPair = soundpairs.get(soundPair.getName());
									  knownSoundPair.setWordforms(sound1, morphem.getAllomorphs().get(allomorph1));
									  knownSoundPair.setWordforms(sound2, morphem.getAllomorphs().get(allomorph2));
									  knownSoundPair.setMorphs(allomorph1, allomorph2);
								  } else {
									  soundPair.setWordforms(sound1, morphem.getAllomorphs().get(allomorph1));
									  soundPair.setWordforms(sound2, morphem.getAllomorphs().get(allomorph2));
									  soundPair.setMorphs(allomorph1, allomorph2);
									  soundpairs.put(soundPair.getName(), soundPair);
								  }
								  
							  }
						  } 
					  }
				  }
				 
			  }
		  }

	   }			
	
	   /**
	    * this method ranks the found sound pairs
	    */
		public void evaluatePairs() {
			
			TreeMap<Double,ArrayList<String>> wortform = new TreeMap<Double,ArrayList<String>>();

			for (AbstractMorpheme stem : this.morphemes) {
				
				ArrayList<String> soundInMorp = new ArrayList<String>();
				
				for (String allomorph : stem.getAllomorphs().keySet()) {
					if (stem instanceof Morpheme) {
						
						for (int i = 0 ; i < allomorph.toCharArray().length ; i++) {
							if (!soundInMorp.contains(Character.toString(allomorph.charAt(i)))) {
								String sound = Character.toString(allomorph.charAt(i));
								if (countSoundInMorp.containsKey(sound)) {							
									Integer old = countSoundInMorp.get(sound);
									countSoundInMorp.remove(sound);
									countSoundInMorp.put(sound, 1+old);
								} else {
									countSoundInMorp.put(sound, 1);
								}
								soundInMorp.add(Character.toString(allomorph.charAt(i)));
							}
						}
					} else if (stem instanceof IPAMorpheme) {
						ipa = true;
						for (int i = 0 ; i < allomorph.split("\\s").length ; i++) {

							if (!soundInMorp.contains(allomorph.split("\\s")[i])) {
								String sound = allomorph.split("\\s")[i];

								if (countSoundInMorp.containsKey(sound)) {
								
									Integer old = countSoundInMorp.get(sound);
									countSoundInMorp.remove(sound);
									countSoundInMorp.put(sound, 1+old);
								} else {
									countSoundInMorp.put(sound, 1);
								}
								soundInMorp.add(allomorph.split("\\s")[i]);
							}
						}
					}
				}
				
			}
			
			for (String pairName : soundpairs.keySet()) {
				
				SoundPair sp = soundpairs.get(pairName);
				
				if(!sp.getSound2().equals("0") &&!sp.getSound2().equals(" ")) {
		
					/*
					 * formula 
					 * freq�a / (freq� + freqa - freq�a) mit logs
					 * 
					 * alternative formulae would be
					 * wfC = (double) (soundpairs.get(pairName).getWordform1().size() + soundpairs.get(pairName).getWordform2().size()); (counts wordforms)
					 * wfC = (double) soundpairs.get(pairName).getMorphs().size(); (counts common morphemes)
					 */
								
					double wfC = (double) Math.log(sp.getMorphs().size()) / (double) Math.log(countSoundInMorp.get(sp.getSound1()) + countSoundInMorp.get(sp.getSound2()) - (double) sp.getMorphs().size()); 
					sp.setValue(wfC); 

					if (wortform.containsKey(wfC)) {
						ArrayList<String> old = wortform.get(wfC);
						old.add(pairName);
						wortform.put(wfC, old);		
					} else {
						ArrayList<String> neu = new ArrayList<String>();
						neu.add(pairName);
						wortform.put(wfC, neu);	
					}
					
					soundpairs.get(pairName).setSoundsWF();//counts sounds among the wordforms

				}
			}
			
			//print
			try { 
				if (!new File(".\\result").exists()) {
					new File(".\\result").mkdir();
				} 
				if (!new File(".\\result\\"+this.method.toString()).exists()) {
					new File(".\\result\\"+this.method.toString()).mkdir();
				}
				if (!new File(".\\result\\"+this.method.toString()+"\\"+this.lang).exists()) {
					new File(".\\result\\"+this.method.toString()+"\\"+this.lang).mkdir();
				}
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter (new FileOutputStream(".\\result\\"+this.method.toString()+"\\"+this.lang+"\\ResultMorphological.txt"),"UTF-8"));
			
				int count2Max = 1;
				for (Double i : wortform.descendingKeySet()) {
					if (count2Max > this.iteration) {
						break;
					}
					for (String s : wortform.get(i)) {
						if (!Double.isNaN(i)) {
							bw.write(count2Max +".\t"+ s + "\t" + i + "\r\n");
							for (Double condition : soundpairs.get(s).getConditons().keySet()){
								bw.write("\t-- " + soundpairs.get(s).getConditons().get(condition) + " = " + condition + "\r\n");
							
							}
							count2Max++;
						}
					}
					
				}
				
				bw.close();
				Logging.debug("Result file generated.");

			} catch (IOException e) {
				Logging.error(e.getLocalizedMessage());
			}
			
		}
	   
	/**
	 * handles the information about the usage of Wiktionary
	 * @param excludeSounds	is ", vokal" if the Wiktionary IPA should be use, ", konsonant" if Wiktionary should be use, "" else
	 */
	@Override
	public void setExcludeSounds(String excludeSounds) {
		if (excludeSounds.contentEquals(", vokal")) {
			this.readWiki = true;
			this.ipa = true;
		} else if (excludeSounds.contentEquals(", konsonant")) {
			this.readWiki = true;
			this.ipa = false;
		} else {
			this.readWiki = false;
		}
	}
	
	/**
	 * returns the path of the result file
	 * @return the path of the result file
	 */
	@Override
	public String getResultFile() {
		return ".\\result\\"+this.method.toString()+"\\"+this.lang+"\\ResultMorphological.txt";
	}
	

}
