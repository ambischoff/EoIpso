package Semantic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;

import Helper.Encoding.Encoding;
import Helper.Encoding.EncodingDetector;
import Helper.Language.Language;
import Helper.Language.Method;
import Helper.Log.Logging;
import Helper.Measures.LevenshteinDistanceMatrix;
import Helper.Measures.NeedlemanWunschAlgorithm;
import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import de.tuebingen.uni.sfs.germanet.api.Synset;

/**
 * implements the semantic approach
 * 
 * Proto-Indo-European uses the data from LIV which needs special configurations
 * these are used if the language name is "Proto-Indo-European"
 * 
 * @author abischoff
 *
 */
public class SemanticLanguage extends Language {

	
	private List<String> alphabet = new ArrayList<String>();
	private ArrayList<Word> wordlist = new ArrayList<Word>();
	private GermaNet gnet = null;
	private ArrayList<Relation> relevantRelations = new ArrayList<Relation>(); //all relations with an aggregate PMI more than 0.0
	private TreeMap<Float, String> allWords = new TreeMap<Float,String>(); //aggregatePMI -> cognate pair
	
    private float[][] myMatrix;
	private WeightedMatrix weightedMatrix;
	
	/**
	 * only for tests
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SemanticLanguage L = new SemanticLanguage("Proto-Indo-European",".\\data\\rawData\\Indogermanisch\\LIV_2.txt", 1);
		L.toString();
	}
	
	/**
	 * 
	 * @param lang		name of the language
	 * @param fileName	input file
	 * @param iteration	number of iterations
	 */
	public SemanticLanguage(String lang, String fileName, int iteration) {
		super(lang,fileName, iteration);
		this.start();
	}
	
	/**
	 * 
	 * @param lang		name of the language
	 * @param fileName	input file
	 */
	public SemanticLanguage(String lang, String fileName) {
		super(lang,fileName);
		super.setIteration(5);
	}
	
	/**
	 * Getter for the method
	 * @return 	Method.semantic
	 */
	public Method getMethod() {
		return Method.semantic;
	}
	
	/**
	 * starts the process
	 */
	@Override
	public void start() {
		if (this.lang.contentEquals("Proto-Indo-European")) { //PIE uses digraphs
			weightedMatrix = new WeightedMatrix(new float[7730][7730]);
		}
		Logging.debug("Read file...");
		readFile(this.file);
		generateAlphabet();
		Logging.debug("Read GermaNet...");
		readGermaNet();
		initMapAndMatrix();


		gnet= null;
		int round = this.iteration;

		while (round >= 0) {
		
			relevantRelations.clear(); //choose only the values of the last round
			
			for (Word a : wordlist) {
	
				TreeMap<Float, String> table4Word = new TreeMap<Float, String>();
				for (String wordform : a.getWordForms()) {
					TreeMap<Float, String> table4WordForm = startNeedlemanWunsch(a, wordform, getRelatedWordsFor(a),5.5f);
					for (Float pmi : table4WordForm.keySet()) {
						if (table4Word.containsKey(pmi)) {
							String old = table4Word.get(pmi);
							table4Word.remove(pmi);
							table4Word.put(pmi, old+"\t"+table4WordForm.get(pmi));
						} else {
							table4Word.put(pmi,table4WordForm.get(pmi));
						}
					}
				}
				a.setResultWordNetPMI(table4Word);
			}
			Logging.debug("Round: " + round);
			renewMyMatrix(weightedMatrix.getMatrix());
			weightedMatrix = null;
			if (this.lang.contentEquals("Proto-Indo-European")) { //PIE uses digraphs
				weightedMatrix = new WeightedMatrix(new float[7730][7730]);
			} else {
				weightedMatrix = new WeightedMatrix();
			}			
		  	round--;
		}

		printResult();
		startRelevantRelations();
		
	}

	/**
	 * searches for significantly relevant sound pairs among the semantic relations of the ontology
	 * prints the result into the result file
	 */
	public void startRelevantRelations() {

		HashMap<String,ArrayList<Relation>> hypernym = new HashMap<String,ArrayList<Relation>>();
		HashMap<String,ArrayList<Relation>> hyponym = new HashMap<String,ArrayList<Relation>>();
		
		Logging.debug("Identify significant relations...");
		for (Relation rel : relevantRelations) {
			if (rel.isHypernym()) {
			
				for (String alternation : rel.getAlternations()) {
					if (hypernym.containsKey(alternation)) {
						ArrayList<Relation> old = hypernym.get(alternation);
						boolean lemmaPaarMissing = true; // is the cognate pair already inside?
						for (Relation oRel : old) {
							if (oRel.getWordName1().contentEquals(rel.getWordName1()) && oRel.getWordName2().contentEquals(rel.getWordName2())) {
								lemmaPaarMissing = false;
							}
						}
						if (lemmaPaarMissing) {
							old.add(rel);
						}
						hypernym.remove(alternation);
						hypernym.put(alternation, old);
					} else {
						ArrayList<Relation> neu = new ArrayList<Relation>();
						neu.add(rel);
						hypernym.put(alternation, neu);
					}
					
				}
			}
			if (rel.isHyponym()) {
				
				for (String alternation : rel.getAlternations()) {
					
					if (hyponym.containsKey(alternation)) {
						ArrayList<Relation> old = hyponym.get(alternation);
						boolean lemmaPaarMissing = true;
						for (Relation oRel : old) {
							if (oRel.getWordName1().contentEquals(rel.getWordName1()) && oRel.getWordName2().contentEquals(rel.getWordName2())) {
								lemmaPaarMissing = false;
							}
						}
						if (lemmaPaarMissing) {
							old.add(rel);
						}
						hyponym.remove(alternation);
						hyponym.put(alternation, old);
					} else {
						ArrayList<Relation> neu = new ArrayList<Relation>();
						neu.add(rel);
						hyponym.put(alternation, neu);
					}
					
				}
			}
			
		}
		int hypernymRelations = 0;
		ArrayList<String> allAlternations = new ArrayList<String>();
		HashMap<String,ArrayList<String>> resultHypernym = new HashMap<String,ArrayList<String>>();
		for (String al: hypernym.keySet()) {
			if (!allAlternations.contains(al)) {
				allAlternations.add(al);
			}
			hypernymRelations += hypernym.get(al).size();
			resultHypernym.put(al, new ArrayList<String>());
				for (Relation r : hypernym.get(al)) {
					String wort1 = r.getWordName1() + r.getWord1().getOriginalMeaning();
					String wort2 = r.getWordName2() + r.getWord2().getOriginalMeaning();
					float aggrPMI = 1111110.0f;
					for (Float pmi : allWords.keySet()) {
						if (allWords.get(pmi).contains(wort1+" : "+wort2)|| allWords.get(pmi).contains(wort2+" : "+wort1)) {
							LevenshteinDistanceMatrix dist = new LevenshteinDistanceMatrix();
							aggrPMI = dist.getLevenshteinDistance(r.getWordName1().replaceAll("\\?", ""), r.getWordName2().replaceAll("\\?", ""));
						}
					}
					
					resultHypernym.get(al).add(wort1 + " " + wort2 +  "\t" + aggrPMI);
				}

		}
		int hyponymRelations = 0;
		HashMap<String,ArrayList<String>> resultHyponym = new HashMap<String,ArrayList<String>>();
		for (String al: hyponym.keySet()) {
			hyponymRelations += hyponym.get(al).size();
			if (!allAlternations.contains(al)) {
				allAlternations.add(al);
			}
			resultHyponym.put(al, new ArrayList<String>());
				for (Relation r : hyponym.get(al)) {
					String wort1 = r.getWordName1() + r.getWord1().getOriginalMeaning();
					String wort2 = r.getWordName2() + r.getWord2().getOriginalMeaning();
					float aggrPMI = 1111110.0f;
					for (Float pmi : allWords.keySet()) {
						if (allWords.get(pmi).contains(wort1+" : "+wort2)|| allWords.get(pmi).contains(wort2+" : "+wort1)) {
							LevenshteinDistanceMatrix dist = new LevenshteinDistanceMatrix();
							aggrPMI = dist.getLevenshteinDistance(r.getWordName1().replaceAll("\\?", ""), r.getWordName2().replaceAll("\\?", ""));
						}
					}
					resultHyponym.get(al).add(wort1 + " " + wort2 +  "\t" + aggrPMI);
				}
			
		}
		
		/*
		 * Chi-Square Test
		 */
		TreeMap<Double,String> chiTest = new TreeMap<Double,String>();
		
		for (String alternation : allAlternations) {
			double a = 0.0;
			double b = 0.0;
			if (hypernym.containsKey(alternation)) {
				a = hypernym.get(alternation).size();
			}
			if (hyponym.containsKey(alternation)) {
				b = hyponym.get(alternation).size();
			}
			double c = hypernymRelations - a;
			double d = hyponymRelations - b;
			double n = hypernymRelations + hyponymRelations;
			
			double zaehler = n * ( (a*d)-(c*b) ) * ( (a*d)-(c*b) );
			double nenner = (a+c)* (b+d)*(a+b)*(c+d);
			
			double quantil = zaehler/nenner;
			if (chiTest.containsKey(quantil)) {
				String old = chiTest.get(quantil);
				old = old + " " + alternation;
				chiTest.put(quantil, old);
			} else {
				chiTest.put(quantil, alternation);
			}
		}
		
		/*
		 * Print result
		 */
		try {
			if (!new File(".\\result").exists()) {
				new File(".\\result").mkdir();
			} 
			if (!new File(".\\result\\"+this.getMethod().toString()).exists()) {
				new File(".\\result\\"+this.getMethod().toString()).mkdir();
			}
			if (!new File(".\\result\\"+this.getMethod().toString()+"\\"+this.lang).exists()) {
				new File(".\\result\\"+this.getMethod().toString()+"\\"+this.lang).mkdir();
			}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter (new FileOutputStream(".\\result\\"+this.getMethod().toString()+"\\"+this.lang+"\\ResultSemantic.txt"),"UTF-8"));
		bw.write("Language: " + this.lang + " Iterations: " + iteration+"\r\n");
		
			for (Double d : chiTest.descendingKeySet()) {
				for (String pair : chiTest.get(d).split("\s")) {
					int hypernymCount = (hypernym.get(pair) == null ? 0 : hypernym.get(pair).size());
					int hyponymCount = (hyponym.get(pair) == null ? 0 : hyponym.get(pair).size());
					bw.write("=== " + onePhonemOneSign(pair,false) + " ===\r\n"+ "Chi-Square: " + d + "\t" + "Hypernym: " +
						 hypernymCount + "\tHyponym: " + hyponymCount + "\r\n"
						);
					if (hypernymCount > hyponymCount) {
						for (String cognate : resultHypernym.get(pair)) {
							bw.write(cognate+"\r\n");
						}
					} else {
						for (String cognate : resultHyponym.get(pair)) {
							bw.write(cognate+"\r\n");
						}
					}
				}
			}

			bw.close();
			
		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
		
	}
	
	/**
	 * initializes the matrices for the Needleman-Wunsch algorithm
	 * uses the transition probabilities from the German paradigmatic approach
	 * this makes it necessary to assign sounds from the language in question to German
	 * (for Proto-Indo-European, this is done by this method)
	 */
   @SuppressWarnings("unchecked")
   public void initMapAndMatrix( ){
	   
	 HashMap<String,Double> countPairs = new HashMap<String,Double>();
	 HashMap<String,Double> countSounds = new HashMap<String,Double>();

		try {
		  ObjectInputStream o = new ObjectInputStream( new FileInputStream( "data\\rawData\\Deutsch\\transitionsParadigmaticPairs.ser" ));
		  countPairs = (HashMap<String,Double>) o.readObject();
		  o.close();
			 
		  ObjectInputStream o2 = new ObjectInputStream( new FileInputStream( "data\\rawData\\Deutsch\\transitionsParadigmaticSounds.ser" ));
		  countSounds = (HashMap<String,Double>) o2.readObject();
		  o2.close();
			
		}
		catch ( IOException e ) { 
			Logging.error(e.getLocalizedMessage());
		}
		catch ( ClassNotFoundException e ) { 
			Logging.error(e.getLocalizedMessage());
		}
	 
		/*
		 * add the new sounds of PIE to the sound pairs of German
		 */
		if (this.lang.contentEquals("Proto-Indo-European")) {
		      for (String correspond : ((HashMap<String,Double>)countPairs.clone()).keySet()) {
		    	  if (correspond.contains("x") || correspond.contains("h")) {
		    		  countPairs.put(correspond.replaceAll("[hx]", "ħ"),countPairs.get(correspond));
		    		  countPairs.put(correspond.replaceAll("[hx]", "ḫ"),countPairs.get(correspond));
		    		  countPairs.put(correspond.replaceAll("[hx]", "ḥ"),countPairs.get(correspond));
		    	  }
		    	  if (correspond.contains("ɡ")) {
		    		  countPairs.put(correspond.replaceAll("ɡ", "ģ"),countPairs.get(correspond));
		    		  countPairs.put(correspond.replaceAll("ɡ", "ǧ"),countPairs.get(correspond));
		    		  countPairs.put(correspond.replaceAll("ɡ", "ζ"),countPairs.get(correspond));
		    		  countPairs.put(correspond.replaceAll("ɡ", "γ"),countPairs.get(correspond));
		    		  countPairs.put(correspond.replaceAll("ɡ", "ω"),countPairs.get(correspond));
		    		  
		    	  }
		    	  if (correspond.contains("k")) {
		    		  countPairs.put(correspond.replaceAll("k", "ḱ"),countPairs.get(correspond));
		    		  countPairs.put(correspond.replaceAll("k", "ǩ"),countPairs.get(correspond));
		    		  countPairs.put(correspond.replaceAll("k", "χ"),countPairs.get(correspond));
		    	  }
		    	  if (correspond.contains("d")) {
		    		  countPairs.put(correspond.replaceAll("d", "δ"),countPairs.get(correspond));
		    	  }
		    	  if (correspond.contains("t")) {
		    		  countPairs.put(correspond.replaceAll("t", "θ"),countPairs.get(correspond));
		    	  }
		    	  if (correspond.contains("b")) {
		    		  countPairs.put(correspond.replaceAll("b", "β"),countPairs.get(correspond));
		    	  }
		    	  if (correspond.contains("p")) {
		    		  countPairs.put(correspond.replaceAll("p", "φ"),countPairs.get(correspond));
		    	  }
		    	  if (correspond.contains("v")) {
		    		  countPairs.put(correspond.replaceAll("v", "w"),countPairs.get(correspond));
		    	  }
		    	  if (correspond.contains("j")) {
		    		  countPairs.remove(correspond.replaceAll("j", "y")); 
		    		  countPairs.put(correspond.replaceAll("j", "y"),countPairs.get(correspond));
		    	  }
		    	  if ( correspond.contains("ʁ")) { //a+R
		    		  
		    		  double complementar = 0;
		    		  if (countPairs.containsKey(correspond.replaceAll("ʁ", "ɐ̯")))
		    			  countPairs.get(correspond.replaceAll("ʁ", "ɐ̯")); 
		    		  
		    		  countPairs.put(correspond.replaceAll("ʁ", "r"), countPairs.get(correspond)+complementar); 
		    	  }
		    	  if ( correspond.contains("ɐ̯")) { //a+R
		    		  
		    		  double complementar = 0;
		    		  if (countPairs.containsKey(correspond.replaceAll("ɐ̯", "ʁ")))
		    			  countPairs.get(correspond.replaceAll("ɐ̯", "ʁ")); 
		    		  
		    		  countPairs.put(correspond.replaceAll("ʁ", "r"), countPairs.get(correspond)+complementar); 
		    	  }

		      }
		      
		      for (String correspond : ((HashMap<String,Double>) countSounds.clone()).keySet()) {
		    	  if (correspond.contains("x") || correspond.contains("h")) {
		    		  countSounds.put(correspond.replaceAll("[hx]", "ħ"),countSounds.get(correspond));
		    		  countSounds.put(correspond.replaceAll("[hx]", "ḫ"),countSounds.get(correspond));
		    		  countSounds.put(correspond.replaceAll("[hx]", "ḥ"),countSounds.get(correspond));
		    	  }
		    	  if (correspond.contains("ɡ")) {
		    		  countSounds.put(correspond.replaceAll("ɡ", "ģ"),countSounds.get(correspond));
		    		  countSounds.put(correspond.replaceAll("ɡ", "ζ"),countSounds.get(correspond));
		    		  countSounds.put(correspond.replaceAll("ɡ", "ǧ"),countSounds.get(correspond));
		    		  countSounds.put(correspond.replaceAll("ɡ", "γ"),countSounds.get(correspond));
		    		  countSounds.put(correspond.replaceAll("ɡ", "ω"),countSounds.get(correspond));
		    	  }
		    	  if (correspond.contains("k")) {
		    		  countSounds.put(correspond.replaceAll("k", "ḱ"),countSounds.get(correspond));
		    		  countSounds.put(correspond.replaceAll("k", "ǩ"),countSounds.get(correspond));
		    		  countSounds.put(correspond.replaceAll("k", "χ"),countSounds.get(correspond));
		    	  }
		    	  if (correspond.contains("d")) {
		    		  countSounds.put(correspond.replaceAll("d", "δ"),countSounds.get(correspond));
		    	  }
		    	  if (correspond.contains("t")) {
		    		  countSounds.put(correspond.replaceAll("t", "θ"),countSounds.get(correspond));
		    	  }
		    	  if (correspond.contains("b")) {
		    		  countSounds.put(correspond.replaceAll("b", "β"),countSounds.get(correspond));
		    	  }
		    	  if (correspond.contains("p")) {
		    		  countSounds.put(correspond.replaceAll("p", "φ"),countSounds.get(correspond));
		    	  }
		    	  if (correspond.contains("j")) {
		    		  countSounds.remove("y");
		    		  countSounds.put(correspond.replaceAll("j", "y"),countSounds.get(correspond));
		    	  }
		    	  
		    	  if ( correspond.contains("ɐ̯")) { //a+R
		    		  countSounds.put(correspond.replaceAll("ʁ", "r"), countSounds.get(correspond)+countSounds.get("ʁ")); 
		    	  }
		    	  
		      }

		} else {
			/*
			 * ask for the assignment of missing sounds
			 */
			for (String pieSound : this.alphabet) {
				if (!countSounds.containsKey(pieSound)) {
					String input = JOptionPane.showInputDialog("There is no starting probability for the sound " + pieSound + ". Please assign it to one of the following sounds whose probability value should be used.");
				     for (String correspond : ((HashMap<String,Double>)countPairs.clone()).keySet()) {
				    	  if (correspond.contains(input)) {
				    		  countPairs.put(correspond.replaceAll(input, pieSound),countPairs.get(correspond));
				    	  }
				     }
				     for (String correspond : ((HashMap<String,Double>) countSounds.clone()).keySet()) {
				    	  if (correspond.contains(input)) {
				    		  countSounds.put(correspond.replaceAll(input, pieSound),countSounds.get(correspond));
				    	  }
				     }
				}
			}
		}

      for (String letter : this.alphabet) {
	      if (!countSounds.containsKey(letter)) {
	    	 Logging.warn("One letter in SoundPairs is missing in the alphabet: " + letter);
	      }
      }
      /*
       * delete all sounds you do not need
       */

      for (String correspond : ((HashMap<String,Double>)countPairs.clone()).keySet()) {
    	  correspond = correspond.replaceAll("0","-");
    	  if (!alphabet.contains(correspond.split("\\|")[0]) || !alphabet.contains(correspond.split("\\|")[1])) {
    		  countPairs.remove(correspond);
    	  }
      }

      for (String correspond : ((HashMap<String,Double>)countSounds.clone()).keySet()) {
    	  correspond = correspond.replaceAll("0","-");
    	  if (!alphabet.contains(correspond)) {
    		  countSounds.remove(correspond);
    	  }
      }


      /*
       * matrix for Needleman-Wunsch
       * for IPA: each IPA sign gets an own integer value
       */
		
	   HashMap<String,Integer> speicherPlatz = new HashMap<String,Integer>();
      
	   double sumPairs = 0.0; // sum of all freq soundPairs
	   double sumSounds = 0.0;// sum of all freq sounds
	   int maxLaut = 0;
	   
	   for (String pair : countPairs.keySet()) {
		   sumPairs += countPairs.get(pair);
	   }
	   for (String sound : countSounds.keySet()) {
		   sumSounds += countSounds.get(sound);
		   
		   int ss = sound.charAt(0);
		   if (this.ipa) {
			   char[] einzel = sound.toCharArray();
			   int value = 0;
			   for (int i = 0; i < einzel.length ; i++) {
				   value += (einzel[i] * (i+1));
			   }
			   while (speicherPlatz.containsValue(value)) {
				   Logging.warn("Two sounds have the same value: " + sound + " " + value);
				   value++;
			   }
			   speicherPlatz.put(sound,value);

			   ss = value;
		   }
		   
		   if (ss > maxLaut) {
			   maxLaut = ss;
		   }
	   }
	   if (this.lang.contentEquals("Proto-Indo-European")) { //LIV uses digraphs
		   maxLaut = 7729;
	   }
	   this.weightedMatrix = new WeightedMatrix(new float[maxLaut+1][maxLaut+1]);
	   /*
	    * Generate the PMI matrix for Needleman-Wunsch
	    */
	   
	   float[][] matrix = new float[maxLaut+1][maxLaut+1]; //the matrix identifies the sound in question using the memory 
	   String[] names = countSounds.keySet().toArray(new String[countSounds.keySet().size()]);
	   
	   for (int i = 0; i < countSounds.size() ; i++) {
		   
		   String sound1 = names[i];
		   
		   for (int j = 0; j < countSounds.size() ; j ++) {
			   
			   String sound2 = names[j];
			   
			   double pmiZaehler = 0.0;
			   
			   if (countPairs.containsKey(sound1 + "|" + sound2)) {
				   pmiZaehler = (1.0 +countPairs.get(sound1 + "|" + sound2)) / (1.0+sumPairs);
			   } else if (countPairs.containsKey(sound2 + "|" + sound1)) {
				   pmiZaehler = (1.0+countPairs.get(sound2 + "|" + sound1)) / (1.0+sumPairs);
			   }
			   
			   double pmiNenner = 0.0 + (((1.0+countSounds.get(sound1)) / (1.0+sumSounds) ) * ( (1.0+countSounds.get(sound2)) / (1.0+sumSounds) ));
			   
			   double pmi = Math.log( pmiZaehler / pmiNenner );
			   
			   int s1 = sound1.charAt(0);
			   int s2 = sound2.charAt(0);

			   if (this.ipa) {
				   s1 = speicherPlatz.get(sound1);
				   s2 = speicherPlatz.get(sound2);
			   }
			   
			   matrix[s1][s2] = (float) pmi;
			   
		   }
		   
	   }
	   
	   myMatrix = matrix;
	   
	   Logging.debug("Matrix created...");
      
     
   }

   /**
    * returns a list with all related words for myPIE
    * @param myPIE	word in question
    * @return	an ArrayList of Word objects that are semantic related
    */
   public ArrayList<Word> getRelatedWordsFor(Word myPIE) {
		   
		   ArrayList<Word> relationPIEWords = new ArrayList<Word>(); //all relation entries in PIE		   
		   for( Synset subSynset : myPIE.getSynsets() ) { //iterates over each synset of "gehen"
			  for (Synset obj : subSynset.getRelatedSynsets()) { 


				  /*
				   * fill relationPIEWords
				   */
				  for (String lemma : obj.getAllOrthForms()) {
					  if (Word.dictDE2PIE.containsKey(lemma)) {
						  for (Word idgWord : Word.dictDE2PIE.get(lemma)) {
							  
							  if (!idgWord.getName().contentEquals(myPIE.getName())) { //exclude the same synset
							  
								  if (!relationPIEWords.contains(idgWord)) {
									  relationPIEWords.add(idgWord);
								  }
								  
							  }
						  }
					  }
				  }
			  }
		   }
		   
		   return relationPIEWords;
	   }
	   

	   /**
	    * starts the Needleman-Wunsch algorithm for a word and all its related words
	    * 
	    * @param myPIEWord	first word for the algorithm (as Word object)
	    * @param myPIE		second word for the algorithm (as string)
	    * @param myRelatedWords	ArrayList of related words of myPIEWord
	    * @param gap		open gap penalty
	    * @return	TreeMap: pmi score -> word pair (the words are tab separated)
	    */
	   public TreeMap<Float, String> startNeedlemanWunsch(Word myPIEWord, String myPIE, ArrayList<Word> myRelatedWords, float gap) {
		    
		   TreeMap<Float,String> allTable = new TreeMap<Float,String>();	 

		   for (Word relatedPIEWord : myRelatedWords) {

			   /*
			    * list all hypernyms that was already taken -> this should avoid repetitions
			    */
			   String[] myRelatedWordForms = relatedPIEWord.getWordForms();
			   
				for (String relatedWord : myRelatedWordForms) {

					String pair = myPIE + "-" + relatedWord+"<"+relatedPIEWord.getName()+(this.lang.contentEquals("Proto-Indo-European") ? relatedPIEWord.getStringMeaning() : relatedPIEWord.getOriginalMeaning());
					NeedlemanWunschAlgorithm nw = new NeedlemanWunschAlgorithm(onePhonemOneSign(myPIE, true), onePhonemOneSign(relatedWord, true));
					nw.start(myMatrix, gap);
					float value = nw.getScore();

					for (int i = 0; i < nw.getAlignedSequence1().length ; i ++) {
						weightedMatrix.setPair(nw.getAlignedSequence1()[i],nw.getAlignedSequence2()[i]);
					}
					
					if (value > 0.0) { //for relation
						relevantRelations.add(new Relation(myPIEWord, relatedPIEWord, nw.getAlignedSequence1(),nw.getAlignedSequence2()));
					}
				
						if (!allTable.keySet().contains(value)) {
			  				allTable.put(value, pair);
			  			} else {
			  				String name = pair + "\t" + allTable.get(value); 
			  				allTable.remove(value);
			  				allTable.put(value, name);
			  			}
			  			
				}
		   }

		   return allTable;
								  	
	   }
	   
	   /**
	    * updates the matrix using a weight
	    * @param weight	weight that will be added
	    */
	   public void renewMyMatrix(float[][] weight) {
		   for (int i = 0 ; i < this.myMatrix.length ; i++) {
			   for (int j = 0 ; j < this.myMatrix[i].length ; j++) {
				   if (this.myMatrix[i][j] == Double.NEGATIVE_INFINITY) //avoid too-low-value errors
					   this.myMatrix[i][j] = Float.MIN_VALUE;				  	
				   this.myMatrix[i][j] = this.myMatrix[i][j] + (1.0f* weight[i][j]);
			   }
		   }
		   
	   }
	
	   /**
	    * reads the input file
	    * it needs the structure word + tab + meaning
	    * 
	    * @param file	path of the file
	    */
	public void readFile(String file) {
		try {
			EncodingDetector e = new EncodingDetector(new FileInputStream(new File(file)), Encoding.UTF8);
			String encoding = e.getEncoding().toString();
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
			String line = br.readLine();
			//Remove BOM if exists
			if (line.toCharArray()[0] == 65279) {
				line = line.substring(1);
			}
			if (line.split("\t")[0].contains(" ")) { //spaces are used for IPA transcriptions
				this.ipa = true;
			}
			while (line != null) {
				if (line.split("\t").length > 1) {
					String word = line.split("\t")[0];
					String[] words = prepareLemma(word);
					String[] meanings = prepareMeanings(line.split("\t")[1]);
					Word myPIE = new Word(word,words,meanings);
					myPIE.setOriginalMeaning(line.split("\t")[1]);
					wordlist.add(myPIE);
					
				} else {
					Logging.warn("File needs the structure \"word + tab + meaning\". Error at line\r\n" + line );
				}
				
				line = br.readLine();
			}

			br.close();
			
		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
	}

	/**
	 * preprocess of the meanings 
	 * the meanings should be German and should be comparable with the meanings of GermaNet
	 * 
	 * @param angabe
	 * @return
	 */
	private String[] prepareMeanings(String angabe) {

		String[] meanings = null;
		ArrayList<String> result = new ArrayList<String>();
		if (this.lang.contentEquals("Proto-Indo-European")) { //use exceptions for LIV
			meanings = angabe.replaceAll(";", ",").replaceAll("('|\\(\\?\\)|\\?|\\(i?n?tr\\.\\)|\\(unpers\\.\\)|\\(auch als Gestus\\)|\\(bei/an/in\\)|\\(Fort\\-\\)|\\(als Erlös\\)|\\(ohne Ortsveränderung\\)|\\(in feindlicher Absicht\\)|\\(von der Frau\\)|\\(vor Angst\\)|\\(vor Schreck\\)|\\(richtig\\)|seelisch)", "").trim().split("\\s*,\\s*");
		} else {
			angabe = angabe.replaceAll("\\(sich\\)", "sich/#_#");
			meanings = angabe.replaceAll(";", ",").replaceAll("('|\\?)", "").replaceAll("\\s+", " ").replaceAll("\\(.*?\\)(\\W|$)", "").trim().split("\\s*,\\s*");

		}

			
			for (String meaning : meanings) {

				meaning = meaning.replaceAll("\\s(über|nach|auf|mit|um|in)$","");

				if (meaning.contains("(")) {
					String mitKlammer = meaning.replaceAll("[\\(\\)]", "").trim();
					String ohneKlammer = meaning.replaceAll("\\(.*?\\)", "").replaceAll("\\s+", " ").trim();
					meaning = mitKlammer + "|" + ohneKlammer;
				}

				for (String submean : meaning.split("\\|")) {

					if (submean.contains("/")) {
						

						String slashWords = submean.replaceAll("\\s[A-Za-zäöüÄÖÜß]*\\s", " ").replaceAll("(^|\\s)[A-Za-zäöüÄÖÜß]*(\\s|$)", "");
						
						
						for (String opt : slashWords.split("/")) {
							result.add(submean.replace(slashWords, opt).replaceAll("#_#", ""));
						}

					} else {
						result.add(submean);
					}
			}
		}
		
		return result.toArray(new String[result.size()]);
	}
	
	/**
	 * preprocess of the lemma
	 * 
	 * 1. replace all *, ?, -, ...
	 * 2. brackets indicates optional letters
	 * 
	 * @param word	word entry of the word list
	 * @return a String[] with all possible wordforms
	 */
	public String[] prepareLemma(String word) {
	
		ArrayList<String> resultKlammer = new ArrayList<String>();
		if (this.lang.contentEquals("Proto-Indo-European")) {
			word = word.replaceAll("[\\*\\?-]", "");
			word = word.replaceAll("\\(ģ\\)", "G").replaceAll("\\(ḱ\\)", "C"); //palatals are problematic
		} else {
					word = word.replaceAll("[\\*\\?\\-,.!\"\']", "");	
		}
		
		if (word.split("\\(").length == 1) {
			resultKlammer.add(word);
		} else if (word.split("\\(").length == 2) {
			String mitKlammer = word.replaceAll("[\\)\\(]", "");
			String ohneKlammer = word.replaceAll("\\(.*?\\)", "");
			resultKlammer.add(mitKlammer);
			resultKlammer.add(ohneKlammer);
		} else if (word.split("\\(").length == 3) {
			String mitKlammer = word.replaceAll("[\\)\\(]", "");
			String ohneKlammer = word.replaceAll("\\(.*?\\)", "");
			String mit1Klammer = word.replaceFirst("\\(.*?\\)", "").replaceAll("[\\)\\(]", "");
			String mit2Klammer = word.replaceFirst("\\(", "").replaceFirst("\\)", "").replaceAll("\\(.*?\\)", "");;
			resultKlammer.add(mitKlammer);
			resultKlammer.add(ohneKlammer);
			resultKlammer.add(mit1Klammer);
			resultKlammer.add(mit2Klammer);
		} else if (word.split("\\(").length > 3) {
			Logging.debug("Lemma contains more than one (:\t" + word);
		}
		
		if (this.lang.contentEquals("Proto-Indo-European")) {
			String lexemes = "";
			for (String lexem : resultKlammer) {
				while (lexem.matches(".*[A-Z].*")) {
						if (lexem.contains("H")) {
							String[] kette = lexem.split("\\|");
							lexem = "";
							for (String perle : kette) {
								lexem += perle.replaceFirst("H", "ħ") + "|" + perle.replaceFirst("H", "ḫ") + "|" + perle.replaceFirst("H", "ḥ") + "|";
							}
						} else if (lexem.contains("G")) {
							String[] kette = lexem.split("\\|");
							lexem = "";
							for (String perle : kette) {
								lexem += perle.replaceFirst("G", "ģ") + "|" + perle.replaceFirst("G", "g") + "|";
							}
						} else if (lexem.contains("R")) {
							String[] kette = lexem.split("\\|");
							lexem = "";
							for (String perle : kette) {
								lexem += perle.replaceFirst("R", "r") + "|" + perle.replaceFirst("R", "l") + "|";
							}
						} else if (lexem.contains("K")) {
							String[] kette = lexem.split("\\|");
							lexem = "";
							for (String perle : kette) {
								lexem += perle.replaceFirst("K", "ḱ") + "|" + perle.replaceFirst("K", "k") + "|"+ perle.replaceFirst("K", "kᵂ") + "|" +perle.replaceFirst("K", "ģ") + "|" + perle.replaceFirst("K", "g")  + "|";
							}
						}else if (lexem.contains("C")) {
							String[] kette = lexem.split("\\|");
							lexem = "";
							for (String perle : kette) {
								lexem += perle.replaceFirst("C", "ḱ") + "|" + perle.replaceFirst("C", "k")  + "|";
							}

						} else if (lexem.contains("T")) {
							String[] kette = lexem.split("\\|");
							lexem = "";
							for (String perle : kette) {
								lexem += perle.replaceFirst("T", "t") + "|" + perle.replaceFirst("T", "d") + "|"+ perle.replaceFirst("T", "dʰ")  + "|";
							}

						} else if (lexem.contains("N")) {
							String[] kette = lexem.split("\\|");
							lexem = "";
							for (String perle : kette) {
								lexem += perle.replaceFirst("N", "n") + "|" + perle.replaceFirst("N", "m") + "|";
							}
						}
				}
				lexemes = lexem;
			}
			
			return lexemes.replaceAll("\\|$", "").split("\\|");
		}
		
		return resultKlammer.toArray(new String[resultKlammer.size()]);
		
	}
	
	/**
	 * reads GermaNet and prepares the data:
	 * 1. GermaNet contains the word
	 * 2. the meaning of the word is part of a paraphrase
	 * 3. take a substring of the word if the substring exists
	 * 
	 */
	public void readGermaNet() {

		try {
			gnet = new GermaNet(new File(".\\data\\rawData\\GN_V90\\GN_V90_XML"), true);
			int countVorhanden = 0;
			int countInParaphrase = 0;
			int countTeilwort = 0;
			int countKompositateil = 0;
			int countumgeleitet = 0;
			for (Word pie : wordlist) {

				for (String myPIEmeaning : pie.getMeaning()) {
					

					
					if (gnet.getSynsets(myPIEmeaning).size() > 0 ) { // 1. GermaNet contains the word
						for (Synset synset : gnet.getSynsets(myPIEmeaning)) {
							pie.addSynsets(synset);
						}
						countVorhanden++;
					}else {
						boolean foundSynset = false;
							//2. the meaning of the word is part of a paraphrase
							for (Synset synset : gnet.getSynsets()) {
								if (synset.getParaphrase().matches(".*\\b"+myPIEmeaning+"\\b.*")) {
									pie.addSynsets(synset);
									foundSynset = true;
								}
							}
					
							
							if (!foundSynset) {
								
								String[] einzel = myPIEmeaning.replaceAll("\\b(sich|werden|sein|geraten|machen|kommen|tun|lassen|oder|ins|etw\\.|jmdn\\.|als|jmdm\\.|etwas|und|in|die|den|das|eine|einen|einer|durch|im|nach|mit|ans|an|wieder|zum|zu|für|von)\\b", "").replaceAll("\\s+", " ").trim().split("\\s");

								for (String myNewPIEmeaning : einzel) {
									//3. take a substring of the word if the substring exists
									// manual assignments:
									if (myNewPIEmeaning.contentEquals("Schwebe")) {
										myNewPIEmeaning = "schweben";
									} else if (myNewPIEmeaning.contentEquals("herunterbiegen")) {
										myNewPIEmeaning = "biegen";
									}
									for (Synset synset : gnet.getSynsets(myNewPIEmeaning)) {
			
										pie.addSynsets(synset);
										foundSynset = true;
									}
								}
								
								if (!foundSynset) {
									
									String splitKompo = myPIEmeaning.replaceAll("^(heraus|hinaus|hinein|davon|zusammen|auf|über|dahin|hin|nieder|an|um|los|zu|brand|aus|hoch|ab|ein|fehl|durch|hervor|glatt|sich nieder|her)", "").replaceAll("\\s+", " ");
									for (Synset synset : gnet.getSynsets(splitKompo)) {
										
										pie.addSynsets(synset);
										foundSynset = true;
									}
									
									if (!foundSynset) {
										countumgeleitet++;
										
										/*
										 * Exclude problematic meanings
										 */
										if (myPIEmeaning.contentEquals("anfeinden")) {
											myPIEmeaning = "angreifen";
										} else if (myPIEmeaning.contentEquals("sich davonmachen")) {
											myPIEmeaning = "davongehen";
										}else if (myPIEmeaning.contentEquals("aufgewühlt sein")) {
											myPIEmeaning = "aufwühlen";
										}else if (myPIEmeaning.contentEquals("irreführen")) {
											myPIEmeaning = "irren";
										}else if (myPIEmeaning.contentEquals("aufgewühlt werden")) {
											myPIEmeaning = "aufwühlen";
										}else if (myPIEmeaning.contentEquals("abmagern")) {
											myPIEmeaning = "abnehmen";
										}else if (myPIEmeaning.contentEquals("wohin geraten")) {
											myPIEmeaning = "geraten";
										}else if (myPIEmeaning.contentEquals("nach unten geraten")) {
											myPIEmeaning = "geraten";
										}else if (myPIEmeaning.contentEquals("gar machen")) {
											myPIEmeaning = "garen";
										}else if (myPIEmeaning.contentEquals("fisten")) {
											myPIEmeaning = "furzen";
										}else if (myPIEmeaning.contentEquals("angekommen sein")) {
											myPIEmeaning = "ankommen";
										}else if (myPIEmeaning.contentEquals("sich losschnellen")) {
											myPIEmeaning = "schnellen";
										}else if (myPIEmeaning.contentEquals("hervorquellen lassen")) {
											myPIEmeaning = "quellen";
										}else if (myPIEmeaning.contentEquals("libieren")) {
											myPIEmeaning = "opfern";
										}else if (myPIEmeaning.contentEquals("beraubt sein")) {
											myPIEmeaning = "berauben";
										}else if (myPIEmeaning.contentEquals("liebgewinnen")) {
											myPIEmeaning = "lieben";
										}
										for (Synset synset : gnet.getSynsets(myPIEmeaning)) {
											pie.addSynsets(synset);
											foundSynset = true;
										}
										
										if (!foundSynset) {
											Logging.warn("The following meaning was not found in GermaNet! Please replace it by a similar meaning: " + myPIEmeaning);
										}
									} else {
										countKompositateil++;
									}

								} else {
									countTeilwort++;
								}

							} else {
								countInParaphrase++;
							}
					} 
					
				}

			}
			Logging.debug("Assignment of the wordlist's entries to the entries of GermaNet");
			Logging.debug("In GermaNet available:\t" +countVorhanden);
			Logging.debug("In paraphrases of GermaNet available:\t" +countInParaphrase);
			Logging.debug("Only one word of the phrase is available in GermaNet:\t" +countTeilwort);
			Logging.debug("Only a part of a compound is available in GermaNet:\t" +countKompositateil);
			Logging.debug("Missing in GermaNet and manually assigned:\t" +countumgeleitet);

			
		} catch (NullPointerException e) {
			Logging.error(e.getLocalizedMessage());
		} catch (FileNotFoundException e) {
			Logging.error(e.getLocalizedMessage());
		} catch (XMLStreamException e) {
			Logging.error(e.getLocalizedMessage());
		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
	}


	/**
	 * generates the result files
	 */
	public void printResult() {


		TreeMap<Float, ArrayList<Word>> myWords = new TreeMap<Float, ArrayList<Word>>();
		ArrayList<String> pairs = new ArrayList<String>(); //is the pair already in allWords?
		
		for (Word w : wordlist) {
			
			if (w.getHighestPMI() != null) {

				if (!myWords.containsKey(w.getHighestPMI())) {
					ArrayList<Word> al = new ArrayList<Word>();
					al.add(w);
					myWords.put(w.getHighestPMI(), al);
				} else {
					ArrayList<Word> al = myWords.get(w.getHighestPMI());
					al.add(w);
					myWords.put(w.getHighestPMI(), al);
				}
			} else {
				if (!myWords.containsKey(0.0f)) {
					ArrayList<Word> al = new ArrayList<Word>();
					al.add(w);
					myWords.put(0.0f, al);
				} else {
					ArrayList<Word> al = myWords.get(0.0f);
					al.add(w);
					myWords.put(0.0f, al);
				}
			} 
			
			boolean abstrahiert = true;

			for (Float pmi : w.getResultWordNetPMI().keySet()) {
				for (String pairM : w.getResultWordNetPMI().get(pmi).split("\\t")) {
					String pair = pairM.replaceFirst("-", "<" + w.getName() + (this.lang.contentEquals("Proto-Indo-European") ? w.getStringMeaning() : w.getOriginalMeaning()) +" : ");
					if (abstrahiert) {
						pair = pair.replaceAll("^.*?<", "").replaceAll(": .*?<",": ");
					}					
					if (!pairs.contains(pair) && !pairs.contains(pair.split(" : ")[1] + " : " + pair.split(" : ")[0])) {

						if (allWords.containsKey(pmi)) {
							String old = allWords.get(pmi);
							allWords.remove(pmi);
							allWords.put(pmi, old + "\r\n\t"+ pair);
						} else {
							allWords.put(pmi, pair);
						}
					}
					pairs.add(pair);
				}
			}
			
		}
		
		try {
			
			if (!new File(".\\result").exists()) {
				new File(".\\result").mkdir();
			} 
			if (!new File(".\\result\\"+this.getMethod().toString()).exists()) {
				new File(".\\result\\"+this.getMethod().toString()).mkdir();
			}
			if (!new File(".\\result\\"+this.getMethod().toString()+"\\"+this.lang).exists()) {
				new File(".\\result\\"+this.getMethod().toString()+"\\"+this.lang).mkdir();
			}
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter (new FileOutputStream(".\\result\\"+this.getMethod().toString()+"\\"+this.lang+"\\ResultWordNetPMI_Einzel.txt"),"UTF-8"));
			
			for (Float val : myWords.descendingKeySet()) {
				for (Word pie : myWords.get(val)) {
					bw.write("============ "+ pie.getName() + " (" + (this.lang.contentEquals("Proto-Indo-European") ? pie.getStringMeaning() : pie.getOriginalMeaning())+") ============\r\n");

					for (Float flo : pie.getResultWordNetPMI().keySet()) {
						for (String pair : pie.getResultWordNetPMI().get(flo).split("\t"))
							bw.write(flo + "\t" + pair + "\r\n");
					}
				}
			}
			
			bw.close();
			Logging.debug("Result file generated.");
			
			BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter (new FileOutputStream(".\\result\\"+this.getMethod().toString()+"\\"+this.lang+"\\ResultWordNetPMI_All.txt"),"UTF-8"));
			for (Float val : allWords.descendingKeySet()) {
				bw2.write(val + "\t" + allWords.get(val) + "\r\n");
			}
			
			bw2.close();
			
		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
		
		
		
	}
	
	/**
	 * reads the entries in the word list and generates an alphabet with all sounds
	 */
	public void generateAlphabet() {
		for (Word word : this.wordlist) {
			for (String wordform : word.getWordForms()) {
				for (char sound : wordform.toCharArray()) {
					if (sound != '﻿' && !this.alphabet.contains(Character.toString(sound))) {
						this.alphabet.add(Character.toString(sound));
					}
				}
			}
		}
		if (this.lang.contentEquals("Proto-Indo-European")) {
			this.alphabet = Arrays.asList(new String[] { "0" ,"β", "a", "ɡ", "e", "δ", "ḫ", "ǧ", "ħ", "ζ", "ḥ", "y", "d", "l", "k", "n", "r", "γ", "w", "s", "ģ", "ḱ", "m", "p", "t", "ω", "b", "ǩ", "i", "χ", "θ", "φ"});
		}
	}
	

	/**
	 * replaces all digraphs 
	 * 
	 * @param idg		word
	 * @param encode	true if all digraphs should be replaced, false if the process should be undone
	 * @return a string of the word with the replaced digraphs (or the result of its undoing)
	 */
		public String onePhonemOneSign(String idg, boolean encode) {
			if (!encode) { //decode 
				if (this.lang.contentEquals("Proto-Indo-European")) {
					return idg.replaceAll("χ", "ḱʰ").replaceAll("φ", "pʰ").replaceAll("θ", "tʰ").replaceAll("β", "bʰ").replaceAll("γ", "gʰ").replaceAll("ζ", "ģʰ").replaceAll("δ", "dʰ").replaceAll("ω", "gᵂʰ").replaceAll("ǩ", "kᵂ").replaceAll("ǧ", "gᵂ").replaceAll("ɡ", "g").replaceAll("ḥ", "h1").replaceAll("ḫ", "h2").replaceAll("ħ", "h3");
				}
				return idg.replaceAll("ɡ","g");			
			}
			if (this.lang.contentEquals("Proto-Indo-European")) {
				return idg.replaceAll("ḱʰ", "χ").replaceAll("pʰ", "φ").replaceAll("tʰ", "θ").replaceAll("bʰ", "β").replaceAll("gʰ", "γ").replaceAll("ģʰ", "ζ").replaceAll("dʰ", "δ").replaceAll("gᵂʰ", "ω").replaceAll("kᵂ", "ǩ").replaceAll("gᵂ", "ǧ").replaceAll("g", "ɡ");

			}
			return idg.replaceAll("g", "ɡ");
		}
	
		/**
		 * Getter for the file path
		 * @return the path of the file as string
		 */
		@Override
		public String getResultFile() {
			if (new File(".\\result\\"+this.getMethod().toString()+"\\"+this.lang+"\\ResultSemantic.txt").exists()) {
				return ".\\result\\"+this.getMethod().toString()+"\\"+this.lang+"\\ResultSemantic.txt";
			}
			return "";
		}
}