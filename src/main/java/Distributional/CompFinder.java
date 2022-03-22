package Distributional;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import Helper.Log.Logging;
import Helper.Typology.ArticulatoryPhonetics;

import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;

/**
 * Determination of the optimal complementary sound using six different methods
 * Chafe, Hoenigswald, Dologopolsky, Condition, Brown
 * the purely phonotactical approaches uses the method findSlackComp
 * 
 * for the subtypological method, see the class SubtypologicalModule
 * @author abischoff
 */

public class CompFinder {

	public List<SoundVector> svList = new ArrayList<SoundVector>(); //all Sounds of the language 
	private boolean similarSound = true; //choose the most similar sound among the outliners

	//Result of the methods
	private String chafeComp;
	private String hoenigswaldComp;
	private String dolgopolskyComp;
	private String condition;
	private String brownComp;
	private HashMap<String,HashMap<String,Double>> brownProbs = new HashMap<String,HashMap<String,Double>>(); // a -> {b:0.003}
	private StringBuilder writer = new StringBuilder();
	
	//only true for testing 
	private boolean printSlacks = false;
	private boolean printCandidates = false;
	private boolean printResultFiles = false;
	private int iteration; //which iteration is this?
	
	private String lang = ""; //Language
	
	//Arrays for the determination of the articulatory similarity
	private ArrayList<String> myArtPlace2 = new ArrayList<String>();//bilabial...vorne...mittel
	private ArrayList<String> myArtPlace1Half = new ArrayList<String>(); // minus 0,5
	private ArrayList<String> myArtPlace1 = new ArrayList<String>();
	private ArrayList<String> myArtPlaceHalf = new ArrayList<String>();
	
	/**
	 * Only for tests
	 */
	public static void main(String[] args) {

		/*
		 * Weighting
		 */
		TFIDFText ti = new TFIDFText(2,1,1,"Deutsch",true);//int tfLevel, int idfLevel, int differenceLevel, lang, aroundOn
		CompFinder cf = new CompFinder("Deutsch",ti.getSVList());
		
		cf.svList = ti.getSVList();
			
		if (cf.similarSound) {
			cf.similarSound(0.02, 100000.0, new String[] {"χ", "ç", "t", "p", "k", "r", "ɐ", "s", "z"}); //Threshold 0 = alle positiven Kontexte zählen
		}
	}
	
	/**
	 * @param  lang 	language
	 * @param  svList	list of SoundVectors generated by TFIDFText
	 */
	public CompFinder(String lang, List<SoundVector> svList) {
		this.svList = svList;
		this.lang = lang;
		initBrownProbabilities();
	}

	/**
	 * 
	 * searches for the phonetically most similar sound among candidates 
	 * using for five different methods: Chafe, Hoenigswald, Dolgopolsky, Condition, Brown
	 * 
	 * @param threshold		values more than threshold are considered outliners
	 * @param limit			values more than limit are not considered outliners anymore
	 * @param phones		candidates for being complementary sounds
	 */
	public void similarSound(double threshold, double limit, String[] phones) {
		HashMap<String,String[]> merkmalphonetik = new HashMap<String,String[]>();
		HashMap<String,String[]> feats = new HashMap<String,String[]>();
		/*
		 * Dolgopolsky classes
		 */
		HashMap<Integer,String[]> dolgopolskyClass = new HashMap<Integer,String[]>();
		dolgopolskyClass.put(1,new String[] {"b","p","f","v", "p͡f"}); //labial obstruent
		dolgopolskyClass.put(2,new String[]{"t","d"}); //dental obstruent ohne sibilanten
		dolgopolskyClass.put(3,new String[]{"s","z", "ʃ","ʒ"}); //dental sibilant
		dolgopolskyClass.put(4,new String[]{"χ","x", "ç","g","k","d͡ʒ", "t͡ʃ", "t͡s", "ʦ", "d͡z", "ʁ"}); //velar/postvelar obstruent and affrikates
		dolgopolskyClass.put(5,new String[]{"m","m̩"}); //M
		dolgopolskyClass.put(6,new String[]{"n","n̩","ŋ", "ŋ̩"});//n,ny und non-initial ng
		dolgopolskyClass.put(7,new String[]{"r","l","l̩"});//r,l
		dolgopolskyClass.put(8,new String[]{"u̯","w"});//w and initial u
		dolgopolskyClass.put(9,new String[]{"i̯","j"});//j
		dolgopolskyClass.put(10,new String[]{"h","#"});//laryngals, zero consonant and initial ng

		
		try {
			String langFile = this.lang;
			if (!new File("data\\rawData\\Merkmalsphonetik_"+lang).exists()) {
				langFile = "IPA";
			
				BufferedReader br2 = new BufferedReader(new InputStreamReader( new FileInputStream("data\\rawData\\Merkmalsphonetik\\Distributional\\Merkmalsphonetik_"+langFile) , "UTF-8"));
				String line = br2.readLine();
	
				
				while(line!=null) {
					String[] lineArr = line.split("\\t");
					String phon = lineArr[0];
					String[] feat = new String[lineArr.length-1];
					for (int a = 1, f = 0 ; a < lineArr.length; a++, f++) {
						feat[f] = lineArr[a];
						if (feats.containsKey(lineArr[a])) {
							ArrayList<String> old = new ArrayList<String>();
							old.addAll(Arrays.asList(feats.get(lineArr[a])));
							old.add(phon);
							String[] ne = new String[old.size()];
							feats.remove(lineArr[a]);
							feats.put(lineArr[a],old.toArray(ne));						
						} else {
							String[] ne = new String[] {phon};
							feats.put(lineArr[a],ne);
						}
					}
					merkmalphonetik.put(phon,feat);
					
					line = br2.readLine();
				}
				
				br2.close();
			} else {
				merkmalphonetik = ArticulatoryPhonetics.readIPAPhonetics();
			}
			
			//Is a sound missing?
			for (String phon : phones) {
				if (!merkmalphonetik.containsKey(phon)) {
					merkmalphonetik.put(phon, ArticulatoryPhonetics.getFeaturesOf(phon));
				}
			}
			
			for (String test : phones) {
				writer.append("=== Results for " + test + " ===\r\n");
				String chafe = ""; //result according to Chafe's Principle
				String hoenigswald = ""; //result according to  Hoenigswald
				String dolgopolsky = ""; //result using dolgopolsky classes
				String conditionComp = ""; //result according to condition
				String brown = ""; //result using empirical values
				
				SoundVector testSV = new SoundVector("");
				for (SoundVector s : svList) {
					if(s.getName().contentEquals(test)) {
						testSV = s;
					}
				}
				Map<String,Double> slacks = getSlacks(svList,testSV,threshold,limit);
				if (slacks.size() > 0) {
					TreeMap<Double, String> candidates = new TreeMap<Double,String>();
					
					//Hoenigswalds similarity
					String[] myFeatures = merkmalphonetik.get(testSV.getName());
					makeDistanceTable(myFeatures);
	
					
					//condition approach
					ArrayList<String> dummysFeatures = new ArrayList<String>();
					ArrayList<String> slackFeatures = new ArrayList<String>();
					for (String slack : slacks.keySet()) {
						for (String featu : slack.split("_")) {
							if (featu.contentEquals("#")) {
								slackFeatures.add("stimmlos");
							}
							if (merkmalphonetik.containsKey(featu)) {
								for (String feat : merkmalphonetik.get(featu) ) {
									slackFeatures.add(feat);
								}
							}
						}
					}
					boolean dominantNotInSound = true;
					ArrayList<String> dominantContext = new ArrayList<String>();
					while (dominantNotInSound) { //choose only one relevant feature that occurs in the phone

						dominantContext = getRelevantFeature(slackFeatures);
						if (dominantContext.isEmpty())
							dominantNotInSound = false; 
						for (String feat : dominantContext) {
							if (Arrays.asList(myFeatures).contains(feat)) { //does the feature occurs in the phone
								dominantNotInSound = false; //Ok -> quit loop
							} else {
								if (!slackFeatures.contains(feat) && slackFeatures.contains("vokal")) {
									feat = "vokal"; //because vowels were replecaed by voiced 
								} else if (!slackFeatures.contains(feat) && slackFeatures.contains("hinten")) {
									feat = ("hinten"); //because vowels were replecaed by voiced 
								} else if (!slackFeatures.contains(feat) && slackFeatures.contains("vorne")) {
									feat = ("vorne"); //because vowels were replecaed by voiced 
								}
								while (slackFeatures.contains(feat)) {
									slackFeatures.remove(feat); //remove the feature from the outliner list and repeat the process
	
								}
							}
						}
					}
					for (String feat : myFeatures) { //delete dominant contexts
						if (!dominantContext.contains(feat) && !dummysFeatures.contains(feat))
							dummysFeatures.add(feat);
					}
					
					
					
					for (SoundVector sv2 : svList) {
						double count = 0.0;

						for (int i = 0; i < testSV.getTFIDFVector().length; i++) {
		
							if  (slacks.containsKey(sv2.getFeatureName(i))) {
								count += sv2.getTFIDFVector()[i]; //for calculating the average
							}

						
							
						}
						count = count / (double) slacks.size();
						if (candidates.containsKey(count)) {
							String poly = candidates.get(count) + ", " + sv2.getName();
							candidates.put(count, poly);
						} else {
							candidates.put(count, sv2.getName());
						}
					}
					double hoenigswaldMax = 0.0; //number of common features
					double conditionMax = 0.0; // number of common features of the condition method
					double brownMax = 0.0;
					
					if (printCandidates)
						Logging.debug("===== Candidates list ===== for the threshold " + threshold);
	
					for (Double d : candidates.keySet()) {
						if (printCandidates)
							Logging.debug(d + " " + candidates.get(d));
						for (String soundCandidat : candidates.get(d).split(",\\s")) {
	
							if (merkmalphonetik.containsKey(soundCandidat) && !soundCandidat.contentEquals(test)) {
								
								String[] compare = getSameElements(myFeatures, merkmalphonetik.get(soundCandidat)); //ignore the features vowel and consonant
								
								/*
								 * Calculation of the articulatory similarity
								 */
								double newCompareLength = (double) compare.length;
		
								for (String merkmal : compare) {
									if ((newCompareLength <= ((double) compare.length +1 )) &&myArtPlace2.contains(merkmal)) { //some could contain more elements, so we avoid getting a value more than 1
										newCompareLength += 1; //match: +2 (compareLength is already 1, so +1)
									} else if ((newCompareLength <= (((double) compare.length) +1.5 )) && myArtPlace1Half.contains(merkmal)) {
										newCompareLength += 1.5; //almost match: +1,5
									} else if ((newCompareLength <= ((double) compare.length +1 )) && myArtPlace1.contains(merkmal)) {
										newCompareLength += 1; //bad match: +1.0
									}else if ((newCompareLength <= ((double) compare.length +0.5 )) && myArtPlaceHalf.contains(merkmal)) {
										newCompareLength += 0.5; //very bad match: +0.5
									}
									
								}
								
								// Chafe
								if (compare.length > 0 && chafe.contentEquals("")) {
									
									StringBuilder print = new StringBuilder();
									for (String again : candidates.get(d).split(",\\s")) {
										if (merkmalphonetik.containsKey(again)) {
											int compareAgain = getSameElements(myFeatures, merkmalphonetik.get(again)).length;
											if (compareAgain > 0) {
												print.append(again + " (common: " + compareAgain + ", similar common: " + newCompareLength +"),");
											}
										}
									}
									
									chafe = print.toString().replaceAll(",$", "")+ "(z. B. "+compare[0] +")"+  "\t" + d;
								} 
								
								//Dolgopolsky
								if (dolgopolsky.contentEquals("")) {
									
									for (Integer no : dolgopolskyClass.keySet()) {
										if (Arrays.asList(dolgopolskyClass.get(no)).contains(test) && 
												Arrays.asList(dolgopolskyClass.get(no)).contains(soundCandidat)) {
											
											StringBuilder print = new StringBuilder();
											for (String again : candidates.get(d).split(",\\s")) {
												for (Integer no2 : dolgopolskyClass.keySet()) {
													if (Arrays.asList(dolgopolskyClass.get(no2)).contains(test) && 
															Arrays.asList(dolgopolskyClass.get(no2)).contains(again)) {
														print.append(again + " (similar Common:" + newCompareLength + "),");
													}
												}
											}
											
											dolgopolsky = print.toString().replaceAll(",$", "") + "\tDolgopolsky-Class " + no + ")\t"+d;
										}
									}
									
								}

								//Brown
								
								TreeMap<Double,String> brownies = brownMethod(test,candidates.get(d).split(",\\s"));
								if (brownies != null) {
									Entry<Double,String> brownie = brownies.pollLastEntry();
									
									if (brown.contentEquals("") || brownMax < brownie.getKey()) {
										brown = brownie.getValue() + " (" + brownie.getKey() + "%)";
										brownMax = brownie.getKey();
									} else if (brownMax == brownie.getKey()) {
										brown += "\t" + brownie.getValue() + " (" + brownie.getKey() + "%)";
									}
									
								}

								//Hoenigswald
								if (newCompareLength > hoenigswaldMax) {
									hoenigswaldMax = newCompareLength;
									hoenigswald = soundCandidat + "\t (similar Common: " +   newCompareLength + "("+compare[0] +")"+  "\t" + d;
								} else if (newCompareLength == hoenigswaldMax && (newCompareLength != 0.0)) {
									hoenigswald += "\n" + soundCandidat + "\t" + newCompareLength + "(" + compare[0] + ")\t" +d;
								}
								
								
								//Berechnung Bedingungsmethode
								String[] compareContext = getSameElements(dummysFeatures.toArray(new String[dummysFeatures.size()]),merkmalphonetik.get(soundCandidat));
	
								if (conditionComp.contentEquals("") && compareContext.length > 0) {
									conditionMax = compareContext.length;
									conditionComp = soundCandidat + "\t" + compareContext.length + "("+ compareContext[0] +" und similar Common: "+newCompareLength+", p="+getProbability(candidates,soundCandidat)+"%)\t" +d;							
								}else if (compareContext.length > 0 && compareContext.length == conditionMax) { //restliche mit gleichem Wert
									conditionComp += "\n" + soundCandidat + "\t" + compareContext.length + "(" + compareContext[0]+" und similar Common: "+newCompareLength+", p="+getProbability(candidates,soundCandidat)+"%)\t" +d;
								} else if (compareContext.length > conditionMax) {
									conditionComp = soundCandidat + "\t" + compareContext.length + "(" + compareContext[0]+" und similar Common: "+newCompareLength+", p="+getProbability(candidates,soundCandidat)+"%)\t" +d;
									conditionMax = compareContext.length;
								}
								
							} 
						}
					}
					String dominants = "";
					for (String a :  dominantContext.toArray(new String[ dominantContext.size()])) {dominants += a + " ";}
					String dummy = "";
					for (String a :  dummysFeatures.toArray(new String[ dominantContext.size()])) {dummy += a + " ";}
	
					
					writer.append(test + ": Chafe-complementary " +chafe  +"\r\n");
					writer.append(test + ": Hoenigswald-complementary " + hoenigswald+"\r\n");
					writer.append(test + ": Dolgopolsky-complementary " + dolgopolsky+"\r\n");
					writer.append(test + ": Condition-complementary (dominant: " + dominants +")\n(dummy: " + dummy+")\n" + conditionComp+"\r\n");
					writer.append(test + ": Brown-complementary " + brown+"\r\n");
					
					chafeComp = chafe;
					hoenigswaldComp = hoenigswald;
					dolgopolskyComp = dolgopolsky;
					condition = conditionComp;
					brownComp = brown;
					
				}
			}//Test end
			
			if(this.printResultFiles) { //print the result to a file
				if (!new File(".\\result\\distributional\\"+this.lang).exists()) {
					new File(".\\result\\distributional\\"+this.lang).mkdir();
				}
				BufferedWriter bw2 = new BufferedWriter( new OutputStreamWriter( new FileOutputStream("result\\distributional\\"+ lang + "\\DetailsComplementarySounds"+(this.iteration+1)) , "UTF-8"));
				bw2.write(writer.toString());
				bw2.close();
			}
			this.writer = new StringBuilder();
		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}

		
		
		
	}
	
	/**
	 * 
	 * implements featureSelection to choose the most relevant sound environments for a sounds
	 * it compares the environments of the sound in question with the environments of all other sounds
	 * 
	 * @param list	list of all sound vectors generated by TFIDFText
	 * @param sound	the sound in question as SoundVector
	 * @param threshold		values more than threshold are considered outliners
	 * @param limit			values more than limit are not considered outliners anymore
	 * 
	 * example (the values between 0.2 and 1.0 are choosen):
	 * +1.2 f_, ...
	 * ----- limit
	 * +1.0 d_f, ...
	 * ...
	 * +0.2 s_, ...
	 * ----- threshold
	 * +0.1 a_d, ...
	 * 0.0 a_d, ...
	 * -0.1 d_, ...
	 * 
	 * @return	a map of the most relevant sound environments: { name -> value } 
	 */
	public Map<String, Double> getSlacks(List<SoundVector> list, SoundVector sound, double threshold, double limit) {
		
		HashMap<String, Double> slacks = new HashMap<String, Double>();
		if (printSlacks)
			Logging.debug("===== Relevant environments using the threshold " + threshold + " =====");
		
		for (int i = 0; i < sound.getTFIDFVector().length; i++) {
			double count = 0.0;
			for (SoundVector sv2 : list) {
				if (sv2.getTFIDFVector()[i] != 0.0) { //skip if it is zero
					count += (sv2.getTFIDFVector()[i] * (TFIDFText.cosineSimilarity(sound.getTFIDFVector(), sv2.getTFIDFVector()))); // SimilarityTest.gewichtung1(sound, sv2);
				}
			}
			double abzug = count / (double) list.size();
			
			double featSel = sound.getTFIDFVector()[i] - abzug; 

			if (featSel > threshold && featSel <= limit) { 
				slacks.put(sound.getFeatureName(i), featSel);	
				if (printSlacks)
					Logging.debug(featSel + "\t" + sound.getFeatureName(i));
			}
		
			
		}
		
		return slacks;
		
	}
	
	/**
	 * chooses candidates of complementary sounds for a sound in question using the purely phonotactical way
	 * it compares the outliners of the sound in question with the environments of all the other sounds
	 * 
	 * @param outliners		environments that are regarded as outliners according to the method getSlacks()
	 * @param sound			the sound in question 
	 * 
	 * @return 	a TreeMap of sounds and their calculated value: {value -> "sound name(s)"}
	 */
	public TreeMap<Double,String> findSlackComp(Map<String,Double> outliners, SoundVector sound) {
		
		TreeMap<Double,String> res = new TreeMap<Double,String>();
		
		for (SoundVector sv2 : svList) {		
			if (!sv2.getName().contentEquals(sound.getName())) {
				double count = 0.0;
				// eine Art durchschnitts Wert berechnen
				
				for (int i = 0; i < sound.getTFIDFVector().length; i++) {
					if (outliners.containsKey(sound.getFeatureName(i))) {
						count += (sv2.getTFIDFVector()[i] * (-1.0*outliners.get(sound.getFeatureName(i)))); 
					}
				}
				
				count = count / (double) outliners.size();
				
				double value = (1-count)* TFIDFText.cosineSimilarity(sv2.getTFIDFVector(),sound.getTFIDFVector());
			
				if (res.containsKey(value)) {
					String old = res.get(value);
					res.put(value, old + " "+sv2.getName()+">"+sound.getName());
				} else {
					res.put(value, sv2.getName()+">"+sound.getName());
				}
			}
		}
		
		return res;
		
	}
	
	/**
	 * method for deserialize data 
	 * @param 	lang	name of the language
	 * @return list of soundVectors
	 */
	@SuppressWarnings("unchecked")
	public static List<SoundVector> deSerializeData(String lang) {
		
		InputStream fis = null;
		List<SoundVector> out = new ArrayList<SoundVector>();
		try
		{
		  fis = new FileInputStream( ".\\data\\transData\\soundVectors"+lang+".ser" );

		  ObjectInputStream o = new ObjectInputStream( fis );
		  out = (List<SoundVector>) o.readObject();
		  o.close();

		}
		catch ( IOException e ) {
			Logging.error(e.getLocalizedMessage());
		}	catch ( ClassNotFoundException e ) { 
			Logging.error(e.getLocalizedMessage());
		} finally { 
			try { 
				fis.close();  
			} catch ( Exception e ) { 		
				Logging.error(e.getLocalizedMessage());
			} 
		}
		
		return out;
		
	}
	
	/**
	 * supporting method
	 * returns an array with common elements of two arrays a and ab
	 * ignores the features consonant and vowel
	 * 
	 * @param	a	first array
	 * @param	b	second array
	 * @return	array of common elements
	 */
	public static String[] getSameElements(String[] a, String[] b) {
		
		ArrayList<String> out = new ArrayList<String>();
		
		for (String alfa : a) {
			
			for (String beta : b) {
				if (alfa.contentEquals(beta) && !alfa.contentEquals("konsonant") && !alfa.contentEquals("vokal")) {
					out.add(alfa);
				}
			}
			
		}
		
		return out.toArray(new String[out.size()]);
		
	}
	
	
	/**
	 * supporting method
	 * returns from a list of features the dominant feature(s)
	 * replaces vowel by voiced, front by palatal, and back by velar
	 * 
	 * @param 	a	list of distinctive features: { nasal, bilabial, ....}
	 * @return	an arrayList with the dominant features
	 */
	public static ArrayList<String> getRelevantFeature(ArrayList<String> a) {
		
		HashMap<String,Integer> out = new HashMap<String,Integer>();
		ArrayList<String> result = new ArrayList<String>();
		for (String alfa : a) {
			
			if (alfa.contentEquals("vokal")) {
				alfa = "stimmhaft";
			}else if (alfa.contentEquals("vorne")) {
				alfa = "palatal";
			}else if (alfa.contentEquals("hinten")) {
				alfa = "velar";
			}

				if (out.containsKey(alfa)) {
					Integer neu = out.get(alfa);
					out.remove(alfa);
					out.put(alfa, 1+neu);
				} else {
					out.put(alfa, 1);
				}
			
			
		}
		//what is the best value
		int max = 0;
		for (String feat : out.keySet()) {
			if (out.get(feat) > max) {
				max = out.get(feat);
			}
		}
		//which has the best value
		for (String feat : out.keySet() ) {
			if (out.get(feat) >= max) {
				result.add(feat);
			}
		}
		
		return result;
		
	}

	
	/**
	 * 
	 * implements the method for calculating the articulartorily most similar sounds
	 * initialize the values for this.myArtPlaceHalf, ...
	 * 
	 * @param 	myFeatures	array of features
	 */
	public void makeDistanceTable(String[] myFeatures) {
		
		String[] articulationCon = {"bilabial","labiodental","dental","alveolar","postalveolar","retroflex","alveolo-palatal","palatal", "velar", "uvular", "pharyngal", "glottal"};
		String[] articulationVow1 = {"vorne", "zentral","hinten"};
		String[] articulationVow2 = {"geschlossen", "fast-geschlossen","halbgeschlossen","mittel","halboffen","fast-offen","offen"};

		
		for (String myFeat : myFeatures) {
			
			for (int i = 0; i < articulationCon.length ; i++) {
				
				//Points for Consonants
				if (myFeat.contentEquals(articulationCon[i])) {
					this.myArtPlace2.add(articulationCon[i]);
					if (i > 0) {
						this.myArtPlace1Half.add(articulationCon[i-1]);
						if (i > 1) {
							this.myArtPlace1.add(articulationCon[i-2]);
							if (i > 2) {
								this.myArtPlaceHalf.add(articulationCon[i-3]);
							}
						}
					}
					
					if (i < (articulationCon.length-1)) {
						this.myArtPlace1Half.add(articulationCon[i+1]);
						if (i < (articulationCon.length-2)) {
							this.myArtPlace1.add(articulationCon[i+2]);
							if (i < (articulationCon.length-3)) {
								this.myArtPlaceHalf.add(articulationCon[i+3]);
							}
						}
					}
					
				}
				

				
			}
			
			for (int i = 0; i < articulationVow1.length ; i++) {

			
				//Points for Vowel 1
				if (myFeat.contentEquals(articulationVow1[i])) {
					this.myArtPlace2.add(articulationVow1[i]);
					if (i > 0) {
						this.myArtPlace1Half.add(articulationVow1[i-1]);
						if (i > 1) {
							this.myArtPlace1.add(articulationVow1[i-2]);
							if (i > 2) {
								this.myArtPlaceHalf.add(articulationVow1[i-3]);
							}
						}
					}
					
					if (i < (articulationVow1.length-1)) {
						this.myArtPlace1Half.add(articulationVow1[i+1]);
						if (i < (articulationVow1.length-2)) {
							this.myArtPlace1.add(articulationVow1[i+2]);
							if (i < (articulationVow1.length-3)) {
								this.myArtPlaceHalf.add(articulationVow1[i+3]);
							}
						}
					}
					
				}	
			}
			
			for (int i = 0; i < articulationVow2.length ; i++) {

				
				//Points for Vowel 1
				if (myFeat.contentEquals(articulationVow2[i])) {
					this.myArtPlace2.add(articulationVow2[i]);
					if (i > 0) {
						this.myArtPlace1Half.add(articulationVow2[i-1]);
						if (i > 1) {
							this.myArtPlace1.add(articulationVow2[i-2]);
							if (i > 2) {
								this.myArtPlaceHalf.add(articulationVow2[i-3]);
							}
						}
					}
					
					if (i < (articulationVow2.length-1)) {
						this.myArtPlace1Half.add(articulationVow2[i+1]);
						if (i < (articulationVow2.length-2)) {
							this.myArtPlace1.add(articulationVow2[i+2]);
							if (i < (articulationVow2.length-3)) {
								this.myArtPlaceHalf.add(articulationVow2[i+3]);
							}
						}
					}
					
				}	
			}
			
		}
	}
	
	/**
	 * 
	 * reads the cross-linguistic data from Brown et al.
	 * calculates the probabilities and store them in this.brownProbs
	 */
	public void initBrownProbabilities() {

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(".\\data\\rawData\\Universal\\WichmannsListe.txt") , "UTF-8") );
			String line = br.readLine();
			
			while (line != null) {
				
				String sound1 = line.split("\\t")[0];
				HashMap<String,Double> s2Prob = new HashMap<String,Double>();
				double summe = 0.0;
				for (int i = 1; i < line.split("\\t").length ; i++) {
					
					String unit = line.split("\\t")[i];
					String sound2 = unit.split("\\s")[0];
					Double cp = Double.parseDouble(unit.split("\\s")[2]);
					summe += cp;
					if (cp > 100.0) {
						Logging.warn("Wrong double in " + sound1 + " " + sound2);
					}
					s2Prob.put(sound2, cp);
				}
				
				for (String s2 : s2Prob.keySet()) {
					double d = s2Prob.get(s2);
					s2Prob.put(s2, d/summe);
				}
				
				this.brownProbs.put(sound1, s2Prob);
				
				line =br.readLine();
			}
			
			br.close();
		} catch (IOException e) {
			
		}
		
	}
	
	/**
	 * 
	 * implements the empirical-probab. method using the data from Brown et al.
	 * 
	 * @param sound	the sound in question
	 * @param cands	candidates generated by the method getSlacks()
	 * @return	a map of the most probable complementary sounds: { value -> name of sound(s) }
	 */
	public TreeMap<Double,String> brownMethod(String sound, String[] cands) {
		HashMap<String,String> asjpDict = new HashMap<String,String>();
		asjpDict.put("p","p");asjpDict.put("k", "k"); asjpDict.put("ɸ","p"); asjpDict.put("b","b"); asjpDict.put("β","b"); asjpDict.put("f","f"); asjpDict.put("v","v"); asjpDict.put("m","m"); asjpDict.put("w","w"); asjpDict.put("θ","8"); asjpDict.put("ð","8"); asjpDict.put("n̪","4"); asjpDict.put("t","t"); asjpDict.put("d","d"); asjpDict.put("s","s"); asjpDict.put("z","z"); asjpDict.put("ʦ","c"); asjpDict.put("ʣ","c"); asjpDict.put("n","n"); asjpDict.put("ɾ","r"); asjpDict.put("r","r"); asjpDict.put("ʀ","r"); asjpDict.put("ɽ","r"); asjpDict.put("l","l"); asjpDict.put("ʃ","S"); asjpDict.put("ʒ","Z"); asjpDict.put("ʧ","C"); asjpDict.put("ʤ","j"); asjpDict.put("c","T"); asjpDict.put("ɟ","T"); asjpDict.put("ɲ","5"); asjpDict.put("j","y"); asjpDict.put("ɡ","g"); asjpDict.put("x","x"); asjpDict.put("ɣ","x"); asjpDict.put("ŋ","N"); asjpDict.put("q","q"); asjpDict.put("ɢ","G"); asjpDict.put("χ","X"); asjpDict.put("ʁ","X"); asjpDict.put("ħ","X"); asjpDict.put("ʕ","X"); asjpDict.put("h","h"); asjpDict.put("ɦ","h"); asjpDict.put("Ɂ","7"); asjpDict.put("ʔ", "7"); asjpDict.put("ʟ","L"); asjpDict.put("ɭ","L"); asjpDict.put("ʎ","L"); asjpDict.put("!","!"); asjpDict.put("ǀ","!"); asjpDict.put("ǁ","!"); asjpDict.put("ǂ","!"); asjpDict.put("i","i"); asjpDict.put("ɪ","i"); asjpDict.put("y","i"); asjpDict.put("ʏ","i"); asjpDict.put("e","e"); asjpDict.put("ø","e"); asjpDict.put("æ","E"); asjpDict.put("ɛ","E"); asjpDict.put("ɶ","E"); asjpDict.put("œ","E"); asjpDict.put("ɨ","3"); asjpDict.put("ɘ","3"); asjpDict.put("ǝ","3"); asjpDict.put("ɜ","3"); asjpDict.put("ʉ","3"); asjpDict.put("ɵ","3"); asjpDict.put("ʚ","3"); asjpDict.put("a","a"); asjpDict.put("ɐ","a"); asjpDict.put("ɯ","u"); asjpDict.put("u","u"); asjpDict.put("ɤ","o"); asjpDict.put("ʌ","o"); asjpDict.put("ɑ","o"); asjpDict.put("o","o"); asjpDict.put("ɔ","o"); asjpDict.put("ɒ","o");
		asjpDict.put("ç", "C"); asjpDict.put("ʊ", "u"); asjpDict.put("ə", "3"); asjpDict.put("#", "Ø"); //fehlt
		if (!asjpDict.containsKey(sound.replaceAll("ː", ""))) {
			Logging.warn(sound + " is missing in the ASJP sound list!");
			return null;
		}
		TreeMap<Double,String> probs = new TreeMap<Double,String>();
		HashMap<String,Double> probs4Sound1 = brownProbs.get(asjpDict.get(sound.replaceAll("ː", "")));
		if (probs4Sound1 == null) {
			Logging.warn(sound + " is missing in the ASJP sound list");
			return null;
		}
		for (String slack : cands) {
			String cand = slack.replaceAll("ː", "");
			if (asjpDict.containsKey(cand) && probs4Sound1.containsKey(asjpDict.get(cand))) {
				if (probs.containsKey(probs4Sound1.get(asjpDict.get(cand)))) {
					String old = probs.get(probs4Sound1.get(asjpDict.get(cand)));
					probs.put(probs4Sound1.get(asjpDict.get(cand)), old + ", " +slack);
				} else {
					probs.put(probs4Sound1.get(asjpDict.get(cand)), slack);
				}
			} else {
				if (probs.containsKey(0.0)) {
					String old = probs.get(0.0);
					probs.put(0.0, old + ", " +slack);
				} else {
					probs.put(0.0, slack);
				}
			}
		}
	

		return (probs.size()!=0 ? probs : null);
	}
	

	/**
	 * supporting method
	 * returns the probability of a sound 
	 * 
	 * formula: 
	 * 1 - featSelection (values more or equal to 1 are removed -> these are usually the sound in question)
	 * @param	values	values
	 * @param	sound 	the sound in question
	 * @return 	probability of the sound in question
	 */
	public static double getProbability(TreeMap<Double,String> values, String sound) {
		
		HashMap<String,Double> einzel = new HashMap<String,Double>();
		double total = 0.0;
		for (Double val : values.keySet()) {
			if (val < 1.0) {
				for (String laut : values.get(val).split(",\\s+")) {
					einzel.put(laut, 1.0-val); 
					total += (1.0-val);
				}
			}
		}

		return ((einzel.get(sound) != null ? einzel.get(sound) : 0.0 )/total)*100.0;
		
	}

	public String getChafe() {
		if (chafeComp != null)
			return chafeComp.split("\s")[0];
		return null;
	}

	public String getHoenigswald() {
		if (hoenigswaldComp != null)
			return hoenigswaldComp.split("\t")[0];
		return null;
	}
	
	public String getBrown() {
		if (brownComp != null)
			return brownComp.split("\t")[0];
		return null;
	}

	public String getDolgopolsky() {
		if (dolgopolskyComp != null)
			return dolgopolskyComp.split("\t")[0];
		return null;
	}

	public String getCondition() {
		if (condition != null)
			return condition.split("\t")[0];
		return null;
	}

	/**
	 * sets the current number of iterations
	 * @param iteration
	 */ 
	public void setIteration(int iteration) {
		this.iteration = iteration;
	}
	
}