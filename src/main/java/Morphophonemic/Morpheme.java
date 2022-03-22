package Morphophonemic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Helper.Measures.LevenshteinDistanceMatrix;

/**
 * morpheme objects
 * @author abischoff
 *
 */
public class Morpheme extends AbstractMorpheme implements Serializable {
	
	private static final long serialVersionUID = 7431284242702120476L;
	
	private String name;
	private MorphemeLanguage sprache;
	private Wortart wortart;
	private Genus[] genus = null;
	private String preverb = null;
	private String nomSing = "";
	private String positiv = "";
	private HashMap<String,String> features = new HashMap<String,String>();

	private HashMap<String,Wordform[]> subAllomorphs = new HashMap<String,Wordform[]>();
	private HashMap<String,Wordform[]> adjAllomorphs = new HashMap<String,Wordform[]>();
	private HashMap<String,Wordform[]> verbAllomorphs = new HashMap<String,Wordform[]>();
		
	private ArrayList<String> wortbildungen = new ArrayList<String>();
	private HashMap<String,Wordform[]> allomorphWortbildung = new HashMap<String,Wordform[]>();
	

	
	/**
	 * 
	 * @param sprache	language
	 * @param wortart	part of speech
	 * @param title		name of the morpheme
	 */
	public Morpheme(String sprache, String wortart, String title) {
		this.name = title;
		if (wortart.equals("Verb")) {
			this.wortart = Wortart.Verb;
		} else if (wortart.equals("Substantiv")) {
			this.wortart = Wortart.Substantiv;
		} else if (wortart.equals("Adjektiv")) {
			this.wortart = Wortart.Adjektiv;
		}

		if (sprache.equals("German")) {
			this.sprache = MorphemeLanguage.Deutsch;
		}
		if (sprache.equals("Deutsch")) {
			this.sprache = MorphemeLanguage.Deutsch;
		}
		
	}
	
	/**
	 * Getter for the language
	 * @return the language
	 */
	public MorphemeLanguage getSprache() { 
		return this.sprache; 
	}
	
	/**
	 * Getter for the part of speech
	 * @return the part of speech
	 */
	public String getPOS() { 
		if (this.wortart == null) {
			return "";
		}
		return this.wortart.toString();
	}
	
	/**
	 * Getter for the language
	 * @return the language
	 */
	public Wortart getWortart() { 
		return this.wortart; 
	}
	
	/**
	 * redirect to addFeat(String feature)
	 * @param form	wordform
	 * @param mode	inflectional information
	 */
	@Override
	public String addFeat(String wordform, String feature) {
		return addFeat(feature+"="+wordform);
	}
	
	/**
	 * adds a wordform and its inflectional information to the allomorph list
	 * @param form	wordform
	 * @param mode	inflectional information
	 */
	public String addFeat(String feature) {

		if (feature.split("=").length == 2 ) {
			if (feature.contains("|")) {
				feature = feature.replaceAll("\\|", "");
			}
			String mode = feature.split("=")[0];
			String form = feature.split("=")[1].trim();
			
			if (!form.replaceAll("[^A-Za-z]","").equals("") && !form.contains("&lt;")){ //erroneous forms
				form = form.replaceAll("[^A-Za-zäöüÄÖÜß\\s]", "").toLowerCase().replaceAll("ã","Ã");

				if(this.wortart == Wortart.Verb) {
					addVerbFeat(form, mode);
				} else if (this.wortart == Wortart.Substantiv) {
					if (!form.contains(" "))	//ignore irrelevant 
						addSubstantivFeat(form,mode);
				}else if (this.wortart == Wortart.Adjektiv) {
					addAdjektivFeat(form,mode);
				}
			}
			return form + "\t" + this.getPOS().toString() + "\t" + mode; //necessary for transcription
		}
		return "";
	}
	
	/**
	 * adds a wordform and its inflectional information to the verbal allomorph list
	 * the word form will be stemmed if the language is German
	 * @param form	wordform
	 * @param mode	inflectional information
	 */
	public void addVerbFeat(String form, String mode){

				
				if (form.contains(" ")) {
					if (!mode.contains("Partizip")) {
						this.preverb = form.split("\\s")[1];
						form = form.split("\\s")[0];
					} else {
						this.preverb = form.split("\\s")[0];
						form = form.split("\\s")[1];
					}
				}


				mode = mode.replaceAll("Gegenwart", "Präsens").replaceAll("1.Vergangenheit","Präteritum").trim();
			
				features.put(mode, form);
				if (this.wortart == Wortart.Verb && this.sprache == MorphemeLanguage.Deutsch) {

					if (!this.wortart.getFlexion(mode, this.sprache).equals("")) { 


						String allomorph = getStem(form, this.wortart.getFlexion(mode, this.sprache));
						String allomorphNoPreVerb = allomorph;
						if (this.preverb != null) {
							allomorph = allomorph + " " + this.preverb;
						}
					
						//controls the endings
						if (allomorph.split("^ge").length > 1 && verbAllomorphs.containsKey(allomorph.split("^ge")[1])) { //missing participle?
							String fehler = allomorph.split("^ge")[1];
							Wordform[] neu = new Wordform[verbAllomorphs.get(fehler).length+1];
							for (int i = 0 ; i < neu.length-1 ; i++) {
								neu[i] = new Wordform(verbAllomorphs.get(fehler)[i].getName(),verbAllomorphs.get(fehler)[i].getMode(),this.preverb);
							}
							neu[neu.length-1] = new Wordform(form,mode,this.preverb);
							verbAllomorphs.remove(fehler);
							verbAllomorphs.put(allomorph, neu);
						} else if (allomorphNoPreVerb.endsWith("s") && verbAllomorphs.containsKey(allomorph.replaceAll("s "," ")) && verbAllomorphs.get(allomorph.replaceAll("s ", " "))[0].containsKind("du") ) { 
							//System.out.println(allomorph);
							String fehler = allomorph.replaceAll("s "," ");
							Wordform[] neu = new Wordform[verbAllomorphs.get(fehler).length+1];
							for (int i = 0 ; i < neu.length-1 ; i++) {
								neu[i] = new Wordform(verbAllomorphs.get(fehler)[i].getName(),verbAllomorphs.get(fehler)[i].getMode(),this.preverb);
							}
							neu[neu.length-1] = new Wordform(form,mode,this.preverb);
							verbAllomorphs.remove(fehler);
							verbAllomorphs.put(allomorph, neu);
						}else if (allomorphNoPreVerb.endsWith("s") && verbAllomorphs.containsKey(allomorph.replaceAll("s$","")) && verbAllomorphs.get(allomorph.replaceAll("s$", ""))[0].containsKind("du") ) { 
							String fehler = allomorph.replaceAll("s$","");
							Wordform[] neu = new Wordform[verbAllomorphs.get(fehler).length+1];
							for (int i = 0 ; i < neu.length-1 ; i++) {
								neu[i] = new Wordform(verbAllomorphs.get(fehler)[i].getName(),verbAllomorphs.get(fehler)[i].getMode(),this.preverb);
							}
							neu[neu.length-1] = new Wordform(form,mode,this.preverb);
							verbAllomorphs.remove(fehler);
							verbAllomorphs.put(allomorph, neu);
						}else if (allomorphNoPreVerb.endsWith("Ã") && verbAllomorphs.containsKey(allomorph.replaceAll("Ã "," ")) && verbAllomorphs.get(allomorph.replaceAll("Ã ", " "))[0].containsKind("du") ) { 
							//System.out.println(allomorph);
							String fehler = allomorph.replaceAll("Ã "," ");
							Wordform[] neu = new Wordform[verbAllomorphs.get(fehler).length+1];
							for (int i = 0 ; i < neu.length-1 ; i++) {
								neu[i] = new Wordform(verbAllomorphs.get(fehler)[i].getName(),verbAllomorphs.get(fehler)[i].getMode(),this.preverb);
							}
							neu[neu.length-1] = new Wordform(form,mode,this.preverb);
							verbAllomorphs.remove(fehler);
							verbAllomorphs.put(allomorph, neu);
						}else if (allomorphNoPreVerb.endsWith("Ã") && verbAllomorphs.containsKey(allomorph.replaceAll("Ã$","")) && verbAllomorphs.get(allomorph.replaceAll("Ã$", ""))[0].containsKind("du") ) { 
							//System.out.println(allomorphs.get(allomorph.replaceAll("Ã$",""))[0].getName());
							String fehler = allomorph.replaceAll("Ã$","");
							Wordform[] neu = new Wordform[verbAllomorphs.get(fehler).length+1];
							for (int i = 0 ; i < neu.length-1 ; i++) {
								neu[i] = new Wordform(verbAllomorphs.get(fehler)[i].getName(),verbAllomorphs.get(fehler)[i].getMode(),this.preverb);
							}
							neu[neu.length-1] = new Wordform(form,mode,this.preverb);
							verbAllomorphs.remove(fehler);
							verbAllomorphs.put(allomorph, neu);
						} else if (allomorphNoPreVerb.endsWith("t") && verbAllomorphs.containsKey(allomorph.replaceAll("t$","")) && (verbAllomorphs.get(allomorph.replaceAll("t$", ""))[0].containsKind("er,") || verbAllomorphs.get(allomorph.replaceAll("t$", ""))[0].containsKind("man") ))  { 
							String fehler = allomorph.replaceAll("t$","");
							Wordform[] neu = new Wordform[verbAllomorphs.get(fehler).length+1];
							for (int i = 0 ; i < neu.length-1 ; i++) {
								neu[i] = new Wordform(verbAllomorphs.get(fehler)[i].getName(),verbAllomorphs.get(fehler)[i].getMode(),this.preverb);
							}
							neu[neu.length-1] = new Wordform(form,mode,this.preverb);
							verbAllomorphs.remove(fehler);
							verbAllomorphs.put(allomorph, neu);
						} else if (allomorphNoPreVerb.endsWith("t") && verbAllomorphs.containsKey(allomorph.replaceAll("t ","")) && (verbAllomorphs.get(allomorph.replaceAll("t ", ""))[0].containsKind("er,") || verbAllomorphs.get(allomorph.replaceAll("t ", ""))[0].containsKind("man") ))  { 
							String fehler = allomorph.replaceAll("t ","");
							Wordform[] neu = new Wordform[verbAllomorphs.get(fehler).length+1];
							for (int i = 0 ; i < neu.length-1 ; i++) {
								neu[i] = new Wordform(verbAllomorphs.get(fehler)[i].getName(),verbAllomorphs.get(fehler)[i].getMode(),this.preverb);
							}
							neu[neu.length-1] = new Wordform(form,mode,this.preverb);
							verbAllomorphs.remove(fehler);
							verbAllomorphs.put(allomorph, neu);
						} else if (!verbAllomorphs.containsKey(allomorph)) {
							verbAllomorphs.put(allomorph, new Wordform[]{new Wordform(form,mode,this.preverb)});
						} else {				
							Wordform[] neu = new Wordform[verbAllomorphs.get(allomorph).length+1];
							for (int i = 0 ; i < neu.length-1 ; i++) {
								neu[i] = new Wordform(verbAllomorphs.get(allomorph)[i].getName(),verbAllomorphs.get(allomorph)[i].getMode(),this.preverb);
							}
							neu[neu.length-1] = new Wordform(form,mode,this.preverb);
							verbAllomorphs.remove(allomorph);
							verbAllomorphs.put(allomorph, neu);
						}
					} 
				}

			}
	
	/**
	 * adds a wordform and its inflectional information to the nominal allomorph list
	 * the word form will be stemmed if the language is German
	 * @param form	wordform
	 * @param mode	inflectional information
	 */
	public void addSubstantivFeat(String form, String mode) {
		/*
		 * still unsolved words: Kühlschrankmutter - Kühlschrankmütt/glückshafen-glückshäf, Eltern (Pluraliatanta) 
		 */
		
		mode = mode.replaceAll("['12345\\*]","").trim();
		
		
		if (mode.toLowerCase().equals("singular")) 
			mode = "Nominativ Singular";
		if (mode.toLowerCase().equals("plural")) 
			mode = "Nominativ Plural";
		
		if (mode.equals("Nominativ Singular")) {
			this.nomSing = form;
		}
		
		if (mode.contains("Genus")) {
			if (this.genus == null) {
				this.genus = new Genus[1];
			} else {
				Genus[] neu = new Genus[this.genus.length+1];
				for (int i = 0; i < this.genus.length ; i++)
					neu[i] = this.genus[i];
				this.genus = neu;
			}
			if (form.equals("m")) this.genus[this.genus.length-1] = Genus.m;
			if (form.equals("f")) this.genus[this.genus.length-1] = Genus.f;
			if (form.equals("n")) this.genus[this.genus.length-1] = Genus.n;
		} else if (mode.contains(" ")) {
			if (mode.contains("Singular") || mode.contains("Plural")) {
				String allomorph = getStem(form, this.wortart.getFlexion(mode, this.sprache));

				if (allomorph.endsWith("er") && subAllomorphs.containsKey(allomorph.replaceAll("(er$)","")) && this.nomSing.equals(allomorph + "er")) {
					String fehler = allomorph.replaceAll("er$","");

					Wordform[] neu = new Wordform[subAllomorphs.get(fehler).length+1];
					for (int i = 0 ; i < neu.length-1 ; i++) {
						neu[i] = new Wordform(subAllomorphs.get(fehler)[i].getName(),subAllomorphs.get(fehler)[i].getMode());
					}
					neu[neu.length-1] = new Wordform(form,mode);
					subAllomorphs.remove(fehler);
					subAllomorphs.put(allomorph, neu);
				} else if (allomorph.endsWith("e") && subAllomorphs.containsKey(allomorph.replaceAll("(e$)","")) && this.nomSing.equals(allomorph + "e")) {
					String fehler = allomorph.replaceAll("e$","");

					Wordform[] neu = new Wordform[subAllomorphs.get(fehler).length+1];
					for (int i = 0 ; i < neu.length-1 ; i++) {
						neu[i] = new Wordform(subAllomorphs.get(fehler)[i].getName(),subAllomorphs.get(fehler)[i].getMode());
					}
					neu[neu.length-1] = new Wordform(form,mode);
					subAllomorphs.remove(fehler);
					subAllomorphs.put(allomorph, neu);
				} else if (allomorph.endsWith("en") && subAllomorphs.containsKey(allomorph.replaceAll("(en$)","")) && this.nomSing.equals(allomorph + "en")) {
					String fehler = allomorph.replaceAll("en$","");

					Wordform[] neu = new Wordform[subAllomorphs.get(fehler).length+1];
					for (int i = 0 ; i < neu.length-1 ; i++) {
						neu[i] = new Wordform(subAllomorphs.get(fehler)[i].getName(),subAllomorphs.get(fehler)[i].getMode());
					}
					neu[neu.length-1] = new Wordform(form,mode);
					subAllomorphs.remove(fehler);
					subAllomorphs.put(allomorph, neu);
				}else if (allomorph.endsWith("n") && subAllomorphs.containsKey(allomorph.replaceAll("n$","")) && this.nomSing.equals(allomorph + "n")) {
					String fehler = allomorph.replaceAll("n$","");

					Wordform[] neu = new Wordform[subAllomorphs.get(fehler).length+1];
					for (int i = 0 ; i < neu.length-1 ; i++) {
						neu[i] = new Wordform(subAllomorphs.get(fehler)[i].getName(),subAllomorphs.get(fehler)[i].getMode());
					}
					neu[neu.length-1] = new Wordform(form,mode);
					subAllomorphs.remove(fehler);
					subAllomorphs.put(allomorph, neu);
				}else if (allomorph.endsWith("s") && subAllomorphs.containsKey(allomorph.replaceAll("(s$)","")) && this.nomSing.equals(allomorph + "s")) {
					String fehler = allomorph.replaceAll("s$","");

					Wordform[] neu = new Wordform[subAllomorphs.get(fehler).length+1];
					for (int i = 0 ; i < neu.length-1 ; i++) {
						neu[i] = new Wordform(subAllomorphs.get(fehler)[i].getName(),subAllomorphs.get(fehler)[i].getMode());
					}
					neu[neu.length-1] = new Wordform(form,mode);
					subAllomorphs.remove(fehler);
					subAllomorphs.put(allomorph, neu);
				}
				
				else if (!subAllomorphs.containsKey(allomorph)) {
					subAllomorphs.put(allomorph, new Wordform[]{new Wordform(form,mode)});
				} else {				
					Wordform[] neu = new Wordform[subAllomorphs.get(allomorph).length+1];
					for (int i = 0 ; i < neu.length-1 ; i++) {
						neu[i] = new Wordform(subAllomorphs.get(allomorph)[i].getName(),subAllomorphs.get(allomorph)[i].getMode());
					}
					neu[neu.length-1] = new Wordform(form,mode);
					subAllomorphs.remove(allomorph);
					subAllomorphs.put(allomorph, neu);
				}
			}
		}
		
	}
	
	/**
	 * adds a wordform and its inflectional information to the adjectival allomorph list
	 * the word form will be stemmed if the language is German
	 * @param form	wordform
	 * @param mode	inflectional information
	 */
	public void addAdjektivFeat(String form, String mode) {
		mode = mode.replaceAll("['12345\\*]","").trim();

		if (mode.equals("Positiv")) {
			this.positiv = form;
		}

		if (mode.contains("Positiv") || mode.contains("Komparativ") || mode.contains("Superlativ")) {
			String allomorph = getStem(form, this.wortart.getFlexion(mode, this.sprache));
			
			if ((!allomorph.endsWith("e")) && this.positiv.equals(allomorph + "e")) {
				allomorph = allomorph + "e";
				
				Wordform[] neu = new Wordform[adjAllomorphs.get(allomorph).length+1];
				for (int i = 0 ; i < neu.length-1 ; i++) {
					neu[i] = new Wordform(adjAllomorphs.get(allomorph)[i].getName(),adjAllomorphs.get(allomorph)[i].getMode());
				}
				neu[neu.length-1] = new Wordform(form,mode);
				adjAllomorphs.remove(allomorph);
				adjAllomorphs.put(allomorph, neu);
			} 	else if (!subAllomorphs.containsKey(allomorph)) {
				adjAllomorphs.put(allomorph, new Wordform[]{new Wordform(form,mode)});
			} else {				
				Wordform[] neu = new Wordform[adjAllomorphs.get(allomorph).length+1];
				for (int i = 0 ; i < neu.length-1 ; i++) {
					neu[i] = new Wordform(adjAllomorphs.get(allomorph)[i].getName(),adjAllomorphs.get(allomorph)[i].getMode());
				}
				neu[neu.length-1] = new Wordform(form,mode);
				adjAllomorphs.remove(allomorph);
				adjAllomorphs.put(allomorph, neu);
			}
		}

	}
	
	/**
	 * stemmer for German
	 * @param form	wordform
	 * @param mode	regular expression from class Language
	 */
	public String getStem(String form, String regex) {

		if (this.preverb != null && form.contains(preverb) && regex.equals("((^ge)|(et$)|(t$)|(en$))")) {
			form = form.replaceFirst(preverb, "");
		}
		
		boolean doppelEe = false;	//dient der grafischen Schreibung ee, wie Tee und see
		if (form.endsWith("ee")) {
			form = form.replaceAll("ee$", "123e");
			doppelEe = true;
		}
		
		Matcher matcher = Pattern.compile(  regex ).matcher( form );
		StringBuffer sb = new StringBuffer( form.length() );

		while ( matcher.find() )
			matcher.appendReplacement( sb, "$0" );
		
		matcher.appendTail( sb );

		String output = form.replaceAll(regex, "");

		if (this.wortart == Wortart.Verb) {
		
			if (regex.equals("((^ge)|(t$)|(en$))") && !this.verbAllomorphs.isEmpty()) { //ge-Präfix überprüfen
				String allo1 = this.verbAllomorphs.keySet().toArray()[0].toString();
				if (allo1.startsWith("ge") && form.startsWith("ge")) {
					if (("ge" + output).equals(allo1))
						output = "ge" + output;
				}
			} 
		
			if ( (regex.equals("((et$)|(t$))") | regex.equals("((ete$)|(te$)|(e$))") ) && !this.verbAllomorphs.isEmpty()) { //
				for (String allo : this.verbAllomorphs.keySet()) {
					if (allo.endsWith("t") && (output + "t").equals(allo)) {
						output = output + "t";
					}
				}
			} 
		
		
			if (regex.equals("((est$)|(st$)|(Ãt$))") && !this.verbAllomorphs.isEmpty()) { //st-Präfix überprüfen
				String allo1 = this.verbAllomorphs.keySet().toArray()[0].toString();

				if (allo1.endsWith("s") && (output + "s").equals(allo1)) {
					output = output + "s";
				}
				if (allo1.endsWith("Ã") && (allo1 + "t").equals(output)) {
					output = allo1;
				}
				if (allo1.endsWith("z") && (allo1 + "t").equals(output)) {
					output = allo1;
				}
			} 
		
		}
		
		if (this.wortart == Wortart.Substantiv) {


			
			if (regex.equals("((es$)|(s$)|(en$)|(n$)|(e$)|(er$))?") && !this.subAllomorphs.isEmpty()) { //plural
				String allo1 = "";
				if (!this.nomSing.equals("")) {
					allo1 = this.nomSing;
				} else {
					allo1 = this.subAllomorphs.keySet().toArray()[0].toString();
				}

				if (allo1.endsWith("e") && (output + "e").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith("s") && (output + "s").equals(allo1)) {
					output = allo1;
				}       
				if (allo1.endsWith("n") && (output + "n").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith("es") && (output + "es").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith("en") && (output + "en").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith("en") && (output + "en").replaceAll("Ã¤", "a").replaceAll("Ã¼", "u").replaceAll("Ã¶", "o").equals(allo1)) { //mutter mütter
					output = output + "en";
				}
				if (allo1.endsWith("er") && (output + "er").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith("er") && (output + "er").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith("er") && (output + "er").replaceAll("Ã¤", "a").replaceAll("Ã¼", "u").replaceAll("Ã¶", "o").equals(allo1)) { //mutter mütter
					output = output + "er";
				}
				if (allo1.endsWith("er") && (output + "er").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith("in") && (allo1 + "nen").equals(output + "en")) { //fuer den fall -innen?
					output = allo1;
				}
			}
			if (regex.equals("((es$)|(s$)|(n$)|(en$))?") && !this.subAllomorphs.isEmpty()) { //genitiv
				String allo1 = "";
				if (!this.nomSing.equals("")) {
					allo1 = this.nomSing;
				} else {
					allo1 = this.subAllomorphs.keySet().toArray()[0].toString();
				}

					if (allo1.endsWith("s") && (output + "s").equals(allo1)) {
						output = allo1;
					}
					if (allo1.endsWith("es") && (output + "es").equals(allo1)) {
						output = allo1;
					}
					if (allo1.endsWith("n") && (output + "n").equals(allo1)) {
						output = allo1;
					}
					if (allo1.endsWith("en") && (output + "en").equals(allo1)) {
						output = allo1;
					}
					if (allo1.endsWith("e") && (output + "e").equals(allo1)) {
						output = allo1;
					}				
			} 
			if (regex.equals("((en$)|(n$))?") && !this.subAllomorphs.isEmpty()) { 
				String allo1 = "";
				if (!this.nomSing.equals("")) {
					allo1 = this.nomSing;
				} else {
					allo1 = this.subAllomorphs.keySet().toArray()[0].toString();
				}
					if (allo1.endsWith("n") && (output + "n").equals(allo1)) {
						output = allo1;
					}
					if (allo1.endsWith("en") && (output + "en").equals(allo1)) {
						output = allo1;
					}
					if (allo1.endsWith("e") && (output + "e").equals(allo1)) {
						output = allo1;
					}	
				
			} 
			if (regex.equals("((e$)|(en$))?") && !this.subAllomorphs.isEmpty()) { 
				String allo1 = "";
				if (!this.nomSing.equals("")) {
					allo1 = this.nomSing;
				} else {
					allo1 = this.subAllomorphs.keySet().toArray()[0].toString();
				}
					if (allo1.endsWith("e") && (output + "e").equals(allo1)) {
						output = allo1;
					}	
					if (allo1.endsWith("en") && (output + "en").equals(allo1)) {
						output = allo1;
					}	
			}
			
			if (regex.equals("((es$)|(s$)|(en$)|(n$)|(e$)|(ern$))?") && !this.subAllomorphs.isEmpty()) { 
				String allo1 = "";
				if (!this.nomSing.equals("")) {
					allo1 = this.nomSing;
				} else {
					allo1 = this.subAllomorphs.keySet().toArray()[0].toString();
				}

				if (allo1.endsWith("e") && (output + "e").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith("s") && (output + "s").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith("n") && (output + "n").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith("es") && (output + "es").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith("en") && (output + "en").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith("en") && (output + "en").replaceAll("Ã¤", "a").replaceAll("Ã¼", "u").replaceAll("Ã¶", "o").equals(allo1)) { //mutter mütter
					output = output + "en";
				}
				if (allo1.endsWith("er") && (output + "er").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith("er") && (output + "er").replaceAll("Ã¤", "a").replaceAll("Ã¼", "u").replaceAll("Ã¶", "o").equals(allo1)) { //mutter mütter
					output = output + "er";
				}
				if (allo1.endsWith("ern")&& (output + "ern").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith("in") && (allo1 + "nen").equals(output + "en")) { //fuer den fall -innen?
					output = allo1;
				}
			}
		}
		
		if (this.wortart == Wortart.Adjektiv) {
			if ((regex.equals("\"((er$)|(r$))\");\r\n") || regex.equals("((^am\\\\s)|(esten$)|(sten$))\"")) && !this.subAllomorphs.isEmpty()) { 
				String allo1 = "";
				if (!this.positiv.equals("")) {
					allo1 = this.positiv;
				} else {
					allo1 = this.adjAllomorphs.keySet().toArray()[0].toString();
				}

				if (allo1.endsWith("e") && (output + "e").equals(allo1)) {
					output = allo1;
				}
			}
		}
		
		if (doppelEe) {
			output = output.replaceAll("123e", "ee").replaceAll("123", "ee");
		}
		
		return output;
	}
	
	/**
	 * Getter for the allomorphs
	 * @return a HashMap <String,Wordform[]> of the allomorphs and their wordforms
	 */
	public HashMap<String,Wordform[]> getAllomorphs() {
		HashMap<String,Wordform[]> output = new HashMap<String,Wordform[]>();
		output.putAll(allomorphWortbildung);
		if (this.wortart == Wortart.Adjektiv) {
			output.putAll(this.adjAllomorphs);
		} else if (this.wortart == Wortart.Verb) {
			output.putAll(this.verbAllomorphs);
		} else {
			output.putAll(this.subAllomorphs);
		}

		return output;
		
	}
	
	/**
	 * Getter for the derivatives
	 * @return an arraylist of the derivatives
	 */
	public ArrayList<String> getWortbildungen() {
		return this.wortbildungen;
	}
	
	/**
	 * Getter for the allomorphs and their derivatives
	 * @return a HashMap <String,Wordform[]> of the allomorphs and their derivatives
	 */
	public HashMap<String,Wordform[]> getAllomorphWortbildungen() {
		return this.allomorphWortbildung;
	}
	
	/**
	 * adds an derivative
	 * @param line	line of a Wiktionary entry
	 */
	public void addDerivat(String line)  {
		if (!line.equals("") && !line.toLowerCase().contains("siehe auch")) {

			if (line.split("\\:\\'\\'").length == 2) { //excludes links of nouns etc. 
					line = line.split("\\:\\'\\'")[1];
			} else if (line.split("\\:\\'\\'").length > 2){ 
				line = line.split("\\:\\'\\'")[2];
			} 
			
			for (int i = 1 ; i < line.split("\\[\\[").length; i++)  {
				String wortbildung = line.split("\\[\\[")[i].split("\\]\\]")[0];
				if (wortbildung.contains("|")) {
					wortbildung = wortbildung.split("|")[1];
				}
				
				this.wortbildungen.add(wortbildung);

			}
			
		}
	}

	/**
	 * Getter for the name
	 * @return the name of the morpheme as string
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/**
	 * adds an allomorph to the list
	 * this method is necessary if the data from Wiktionary is not used
	 * @param allomorph	allomorph
	 */
	public void addAllomorph(String allomorph) {
		if (this.wortart == Wortart.Adjektiv) {
			
			this.adjAllomorphs.put(allomorph, new Wordform[] {new Wordform(allomorph)});
		} else if (this.wortart == Wortart.Verb) {
			this.verbAllomorphs.put(allomorph, new Wordform[] {new Wordform(allomorph)});
		} else {
			this.subAllomorphs.put(allomorph, new Wordform[] {new Wordform(allomorph)});
		}
	}
	
	/**
	 * stemmer for derivatives (only for German)
	 * @param wortbildung	wordform
	 * @param morphemName	word stem
	 */
	public void derivatStem() {
		
		ArrayList<String> praefixe = ReadWiktionary.getPraefixes();
		ArrayList<String> suffixe = ReadWiktionary.getSuffixes();
		
		for (String wortbildung : wortbildungen) {
			wortbildung = wortbildung.toLowerCase().replaceAll("[^A-Za-zäöüÄÖÜß]", "");
			
			String title = this.name.toLowerCase().replaceAll("[^A-Za-zäöüÄÖÜß]", "");
			
			if (this.wortart == Wortart.Verb) {
				title = title.replaceAll("(n$|en$)", "");
			}
			
			LevenshteinDistanceMatrix levenshtein = new LevenshteinDistanceMatrix();
			String[] correspondences = levenshtein.getCorrespondence(title.trim(), wortbildung.trim());
			
			String vorsilbe = "";
			String stamm = "";
			String nachsilbe = "";

			for (String corr : correspondences) {
				
				String rectus = corr.split("\\|")[0];
				String derivatum = corr.split("\\|")[1];
				
				if (rectus.equals("0")) {
					if (stamm.equals("")) {
						vorsilbe += derivatum;
					} else {
						nachsilbe += derivatum;
					}
					
				} else {
					if (!derivatum.equals("0"))
						stamm += derivatum;
					nachsilbe = ""; //set to 0 because end of the stem is not reached
				}
				
			}
			
			boolean funna = false;
			
			if ((vorsilbe.equals("") || praefixe.contains(vorsilbe+"-")) &&
				(nachsilbe.equals("") || suffixe.contains("-"+nachsilbe))	) {
				
				if (allomorphWortbildung.containsKey(stamm)) {
					ArrayList<Wordform> old = new ArrayList<Wordform>(Arrays.asList(allomorphWortbildung.get(stamm)));
					old.add(new Wordform(wortbildung));
					allomorphWortbildung.put(stamm, old.toArray(new Wordform[old.size()]));
				} else {
					allomorphWortbildung.put(stamm, new Wordform[]{new Wordform(wortbildung)});
				}
				funna = true;
			}
			
			if (funna == false) { //for compounds
				
				for (String stramm : allomorphWortbildung.keySet()) { //does the stem occurs in the word?
					if (wortbildung.contains(stramm)) {
						ArrayList<Wordform> old = new ArrayList<Wordform>(Arrays.asList(allomorphWortbildung.get(stramm)));
						old.add(new Wordform(wortbildung));
						allomorphWortbildung.put(stramm, old.toArray(new Wordform[old.size()]));
						funna = true;
					}
					
				}
				
				if (wortbildung.contains(title) && funna == false && !allomorphWortbildung.containsKey(title)) {
					allomorphWortbildung.put(title, new Wordform[]{new Wordform(wortbildung)});		
					funna = true;
				}

				if (wortbildung.replaceAll("ge", "").replaceAll("ik$", "").replaceAll("imus$", "").contains(title.replaceAll("ge", "").replaceAll("ik$", "").replaceAll("ismus$", "")) && funna == false) { //Partizipien weg
					if (allomorphWortbildung.containsKey(title)) {
						ArrayList<Wordform> old = new ArrayList<Wordform>(Arrays.asList(allomorphWortbildung.get(title)));
						old.add(new Wordform(wortbildung));
						allomorphWortbildung.put(title, old.toArray(new Wordform[old.size()]));
					} else {
						allomorphWortbildung.put(title, new Wordform[]{new Wordform(wortbildung)});
					}
					funna = true;
				}
				
				if (!funna) {
					if (allomorphWortbildung.containsKey(stamm)) {
						ArrayList<Wordform> old = new ArrayList<Wordform>(Arrays.asList(allomorphWortbildung.get(stamm)));
						old.add(new Wordform(wortbildung));
						allomorphWortbildung.put(stamm, old.toArray(new Wordform[old.size()]));
					} else {
						allomorphWortbildung.put(stamm, new Wordform[]{new Wordform(wortbildung)});
					}
					funna = true;					
				
				}
			}
		}
	}
}







