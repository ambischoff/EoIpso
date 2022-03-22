package Morphophonemic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Helper.Measures.LevenshteinDistanceMatrix;

/**
 * morpheme objects with IPA transcriptions
 * @author abischoff
 *
 */
public class IPAMorpheme extends AbstractMorpheme implements Serializable {
	
	private static final long serialVersionUID = 6655358581642997828L;
	
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
	public IPAMorpheme(MorphemeLanguage sprache, Wortart wortart,String title) {
		if (sprache == MorphemeLanguage.Deutsch) {
			this.sprache = MorphemeLanguage.Deutsch_IPA;
		}
		this.wortart = wortart;
		this.name = title;

	}
	
	/**
	 * 
	 * @param sprache
	 * @param wortart
	 * @param title
	 */
	public IPAMorpheme(String sprache, String wortart, String title) {
		this.name = title;
		if (wortart.equals("Verb")) {
			this.wortart = Wortart.Verb;
		} else if (wortart.equals("Substantiv")) {
			this.wortart = Wortart.Substantiv;
		}else if (wortart.equals("Adjektiv")) {
			this.wortart = Wortart.Adjektiv;
		}
		if (sprache.equals("German")) {
			this.sprache = MorphemeLanguage.Deutsch_IPA;
		}
		if (sprache.equals("Deutsch")) {
			this.sprache = MorphemeLanguage.Deutsch_IPA;
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
	 * Getter for the language
	 * @return the language
	 */
	public Wortart getWortart() { 
		return this.wortart; 
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
	 * adds a wordform and its inflectional information to the allomorph list
	 * @param form	wordform
	 * @param mode	inflectional information
	 */
	@Override
	public String addFeat(String form, String mode) {

		if(this.wortart == Wortart.Verb) {
			addVerbFeat(form, mode);
		} else if (this.wortart == Wortart.Substantiv) {
			addSubstantivFeat(form,mode);
		}else if (this.wortart == Wortart.Adjektiv) {
			addAdjektivFeat(form,mode);
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
				if (form.contains("--")) {
					if (!mode.contains("Partizip")) {
						this.preverb = form.split("--")[1];
						form = form.split("--")[0];
					} else {
						this.preverb = form.split("--")[0];
						form = form.split("--")[1];
					}
				} 
								
				features.put(mode, form);

				String gePrefix = "((^g É )|(^ɡ ə ))";
				
				if (this.wortart == Wortart.Verb && this.sprache == MorphemeLanguage.Deutsch_IPA) {

					if (!this.wortart.getFlexion( mode, this.sprache).equals("")) { 

						String allomorph = getStem(form, this.wortart.getFlexion(mode, this.sprache));
						
						String allomorphNoPreVerb = allomorph;
						if (this.preverb != null) {
							allomorph = allomorph + " " + this.preverb;
						}
					
						//controls the endings
						if (allomorph.split(gePrefix).length > 1 && verbAllomorphs.containsKey(allomorph.split(gePrefix)[1])) { //missed participles?

							String fehler = allomorph.split(gePrefix)[1];
							Wordform[] neu = new Wordform[verbAllomorphs.get(fehler).length+1];
							for (int i = 0 ; i < neu.length-1 ; i++) {
								neu[i] = new Wordform(verbAllomorphs.get(fehler)[i].getName(),verbAllomorphs.get(fehler)[i].getMode());
							}
							neu[neu.length-1] = new Wordform(form,mode);
							verbAllomorphs.remove(fehler);
							verbAllomorphs.put(allomorph, neu);
						} else if (allomorphNoPreVerb.endsWith(" s") && verbAllomorphs.containsKey(allomorph.replaceAll(" s "," ")) && verbAllomorphs.get(allomorph.replaceAll(" s ", " "))[0].containsKind("du") ) { 
							String fehler = allomorph.replaceAll(" s "," ");
							Wordform[] neu = new Wordform[verbAllomorphs.get(fehler).length+1];
							for (int i = 0 ; i < neu.length-1 ; i++) {
								neu[i] = new Wordform(verbAllomorphs.get(fehler)[i].getName(),verbAllomorphs.get(fehler)[i].getMode());
							}
							neu[neu.length-1] = new Wordform(form,mode);
							verbAllomorphs.remove(fehler);
							verbAllomorphs.put(allomorph, neu);
						}else if (allomorphNoPreVerb.endsWith(" s") && verbAllomorphs.containsKey(allomorph.replaceAll(" s$","")) && verbAllomorphs.get(allomorph.replaceAll(" s$", ""))[0].containsKind("du") ) { 
							String fehler = allomorph.replaceAll(" s$","");
							Wordform[] neu = new Wordform[verbAllomorphs.get(fehler).length+1];
							for (int i = 0 ; i < neu.length-1 ; i++) {
								neu[i] = new Wordform(verbAllomorphs.get(fehler)[i].getName(),verbAllomorphs.get(fehler)[i].getMode());
							}
							neu[neu.length-1] = new Wordform(form,mode);
							verbAllomorphs.remove(fehler);
							verbAllomorphs.put(allomorph, neu);
						}else if (allomorphNoPreVerb.endsWith(" Ã") && verbAllomorphs.containsKey(allomorph.replaceAll(" Ã "," ")) && verbAllomorphs.get(allomorph.replaceAll(" Ã ", " "))[0].containsKind("du") ) { 
							String fehler = allomorph.replaceAll(" Ã "," ");
							Wordform[] neu = new Wordform[verbAllomorphs.get(fehler).length+1];
							for (int i = 0 ; i < neu.length-1 ; i++) {
								neu[i] = new Wordform(verbAllomorphs.get(fehler)[i].getName(),verbAllomorphs.get(fehler)[i].getMode());
							}
							neu[neu.length-1] = new Wordform(form,mode);
							verbAllomorphs.remove(fehler);
							verbAllomorphs.put(allomorph, neu);
						}else if (allomorphNoPreVerb.endsWith(" Ã") && verbAllomorphs.containsKey(allomorph.replaceAll(" Ã$","")) && verbAllomorphs.get(allomorph.replaceAll(" Ã$", ""))[0].containsKind("du") ) { 
							String fehler = allomorph.replaceAll(" Ã$","");
							Wordform[] neu = new Wordform[verbAllomorphs.get(fehler).length+1];
							for (int i = 0 ; i < neu.length-1 ; i++) {
								neu[i] = new Wordform(verbAllomorphs.get(fehler)[i].getName(),verbAllomorphs.get(fehler)[i].getMode());
							}
							neu[neu.length-1] = new Wordform(form,mode);
							verbAllomorphs.remove(fehler);
							verbAllomorphs.put(allomorph, neu);
						} else if (allomorphNoPreVerb.endsWith(" t") && verbAllomorphs.containsKey(allomorph.replaceAll(" t$","")) && (verbAllomorphs.get(allomorph.replaceAll(" t$", ""))[0].containsKind("er,") || verbAllomorphs.get(allomorph.replaceAll(" t$", ""))[0].containsKind("man") ))  { 
							String fehler = allomorph.replaceAll(" t$","");
							Wordform[] neu = new Wordform[verbAllomorphs.get(fehler).length+1];
							for (int i = 0 ; i < neu.length-1 ; i++) {
								neu[i] = new Wordform(verbAllomorphs.get(fehler)[i].getName(),verbAllomorphs.get(fehler)[i].getMode());
							}
							neu[neu.length-1] = new Wordform(form,mode);
							verbAllomorphs.remove(fehler);
							verbAllomorphs.put(allomorph, neu);
						} else if (allomorphNoPreVerb.endsWith(" t") && verbAllomorphs.containsKey(allomorph.replaceAll(" t ","")) && (verbAllomorphs.get(allomorph.replaceAll(" t ", ""))[0].containsKind("er,") || verbAllomorphs.get(allomorph.replaceAll(" t ", ""))[0].containsKind("man") ))  { 
							String fehler = allomorph.replaceAll(" t ","");
							Wordform[] neu = new Wordform[verbAllomorphs.get(fehler).length+1];
							for (int i = 0 ; i < neu.length-1 ; i++) {
								neu[i] = new Wordform(verbAllomorphs.get(fehler)[i].getName(),verbAllomorphs.get(fehler)[i].getMode());
							}
							neu[neu.length-1] = new Wordform(form,mode);
							verbAllomorphs.remove(fehler);
							verbAllomorphs.put(allomorph, neu);
						} else if (!verbAllomorphs.containsKey(allomorph)) {
							verbAllomorphs.put(allomorph, new Wordform[]{new Wordform(form,mode)});
						} else {				
							Wordform[] neu = new Wordform[verbAllomorphs.get(allomorph).length+1];
							for (int i = 0 ; i < neu.length-1 ; i++) {
								neu[i] = new Wordform(verbAllomorphs.get(allomorph)[i].getName(),verbAllomorphs.get(allomorph)[i].getMode());
							}
							neu[neu.length-1] = new Wordform(form,mode);
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

				if (allomorph.endsWith(" É r") && subAllomorphs.containsKey(allomorph.replaceAll("( É r$)","")) && this.nomSing.equals(allomorph + " É r")) {
					String fehler = allomorph.replaceAll(" É r$","");

					Wordform[] neu = new Wordform[subAllomorphs.get(fehler).length+1];
					for (int i = 0 ; i < neu.length-1 ; i++) {
						neu[i] = new Wordform(subAllomorphs.get(fehler)[i].getName(),subAllomorphs.get(fehler)[i].getMode());
					}
					neu[neu.length-1] = new Wordform(form,mode);
					subAllomorphs.remove(fehler);
					subAllomorphs.put(allomorph, neu);
				} else if (allomorph.endsWith(" ɐ") && subAllomorphs.containsKey(allomorph.replaceAll("( ɐ$)","")) && this.nomSing.equals(allomorph + " ɐ")) {
					String fehler = allomorph.replaceAll("  ɐ$","");

					Wordform[] neu = new Wordform[subAllomorphs.get(fehler).length+1];
					for (int i = 0 ; i < neu.length-1 ; i++) {
						neu[i] = new Wordform(subAllomorphs.get(fehler)[i].getName(),subAllomorphs.get(fehler)[i].getMode());
					}
					neu[neu.length-1] = new Wordform(form,mode);
					subAllomorphs.remove(fehler);
					subAllomorphs.put(allomorph, neu);
				}  else if (allomorph.endsWith(" ə") && subAllomorphs.containsKey(allomorph.replaceAll("( ə$)","")) && this.nomSing.equals(allomorph + " ə")) {
					String fehler = allomorph.replaceAll(" ə$","");

					Wordform[] neu = new Wordform[subAllomorphs.get(fehler).length+1];
					for (int i = 0 ; i < neu.length-1 ; i++) {
						neu[i] = new Wordform(subAllomorphs.get(fehler)[i].getName(),subAllomorphs.get(fehler)[i].getMode());
					}
					neu[neu.length-1] = new Wordform(form,mode);
					subAllomorphs.remove(fehler);
					subAllomorphs.put(allomorph, neu);
				} else if (allomorph.endsWith(" É") && subAllomorphs.containsKey(allomorph.replaceAll("( É$)","")) && this.nomSing.equals(allomorph + " É")) {
					String fehler = allomorph.replaceAll(" É$","");

					Wordform[] neu = new Wordform[subAllomorphs.get(fehler).length+1];
					for (int i = 0 ; i < neu.length-1 ; i++) {
						neu[i] = new Wordform(subAllomorphs.get(fehler)[i].getName(),subAllomorphs.get(fehler)[i].getMode());
					}
					neu[neu.length-1] = new Wordform(form,mode);
					subAllomorphs.remove(fehler);
					subAllomorphs.put(allomorph, neu);
				} else if (allomorph.endsWith(" É n") && subAllomorphs.containsKey(allomorph.replaceAll("( É n$)","")) && this.nomSing.equals(allomorph + " É  n")) {
					String fehler = allomorph.replaceAll(" É n$","");

					Wordform[] neu = new Wordform[subAllomorphs.get(fehler).length+1];
					for (int i = 0 ; i < neu.length-1 ; i++) {
						neu[i] = new Wordform(subAllomorphs.get(fehler)[i].getName(),subAllomorphs.get(fehler)[i].getMode());
					}
					neu[neu.length-1] = new Wordform(form,mode);
					subAllomorphs.remove(fehler);
					subAllomorphs.put(allomorph, neu);
				}else if (allomorph.endsWith(" ə n") && subAllomorphs.containsKey(allomorph.replaceAll("( ə n$)","")) && this.nomSing.equals(allomorph + " ə  n")) {
					String fehler = allomorph.replaceAll(" ə n$","");

					Wordform[] neu = new Wordform[subAllomorphs.get(fehler).length+1];
					for (int i = 0 ; i < neu.length-1 ; i++) {
						neu[i] = new Wordform(subAllomorphs.get(fehler)[i].getName(),subAllomorphs.get(fehler)[i].getMode());
					}
					neu[neu.length-1] = new Wordform(form,mode);
					subAllomorphs.remove(fehler);
					subAllomorphs.put(allomorph, neu);
				}else if (allomorph.endsWith(" n̩") && subAllomorphs.containsKey(allomorph.replaceAll("( n̩$)","")) && this.nomSing.equals(allomorph + " n̩")) {
					String fehler = allomorph.replaceAll(" n̩$","");

					Wordform[] neu = new Wordform[subAllomorphs.get(fehler).length+1];
					for (int i = 0 ; i < neu.length-1 ; i++) {
						neu[i] = new Wordform(subAllomorphs.get(fehler)[i].getName(),subAllomorphs.get(fehler)[i].getMode());
					}
					neu[neu.length-1] = new Wordform(form,mode);
					subAllomorphs.remove(fehler);
					subAllomorphs.put(allomorph, neu);
				} else if (allomorph.endsWith(" n") && subAllomorphs.containsKey(allomorph.replaceAll(" n$","")) && this.nomSing.equals(allomorph + " n")) {
					String fehler = allomorph.replaceAll(" n$","");

					Wordform[] neu = new Wordform[subAllomorphs.get(fehler).length+1];
					for (int i = 0 ; i < neu.length-1 ; i++) {
						neu[i] = new Wordform(subAllomorphs.get(fehler)[i].getName(),subAllomorphs.get(fehler)[i].getMode());
					}
					neu[neu.length-1] = new Wordform(form,mode);
					subAllomorphs.remove(fehler);
					subAllomorphs.put(allomorph, neu);
				}else if (allomorph.endsWith(" s") && subAllomorphs.containsKey(allomorph.replaceAll("( s$)","")) && this.nomSing.equals(allomorph + " s")) {
					String fehler = allomorph.replaceAll(" s$","");

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
				
		} else {
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
			
			if ((!allomorph.endsWith(" É")) && this.positiv.equals(allomorph + " É")) {
				allomorph = allomorph + " É";
				
				Wordform[] neu = new Wordform[adjAllomorphs.get(allomorph).length+1];
				for (int i = 0 ; i < neu.length-1 ; i++) {
					neu[i] = new Wordform(adjAllomorphs.get(allomorph)[i].getName(),adjAllomorphs.get(allomorph)[i].getMode());
				}
				neu[neu.length-1] = new Wordform(form,mode);
				adjAllomorphs.remove(allomorph);
				adjAllomorphs.put(allomorph, neu);
			} else if ((!allomorph.endsWith(" ə")) && this.positiv.equals(allomorph + " ə")) {
				allomorph = allomorph + " ə";
				
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

		if (this.preverb != null && form.contains(preverb) && regex.equals(this.sprache.getVerbFlexion("Partizip II"))) {
			form = form.replaceFirst(preverb, "");
		}
		
		Matcher matcher = Pattern.compile(  regex ).matcher( form );
		StringBuffer sb = new StringBuffer( form.length() );

		while ( matcher.find() )
			matcher.appendReplacement( sb, "$0" );
		
		matcher.appendTail( sb );

		String output = form.replaceAll(regex, "");
	

		if (this.wortart == Wortart.Verb) {
		
			if (regex.equals(this.sprache.getVerbFlexion("Partizip II")) && !this.verbAllomorphs.isEmpty()) { 
				String allo1 = this.verbAllomorphs.keySet().toArray()[0].toString();
				if (allo1.startsWith("g É ") && form.startsWith("g É ")) {
					if (("g É " + output).equals(allo1))
						output = "g É " + output;
				} else if (allo1.startsWith("ɡ ə ") && form.startsWith("ɡ ə ")) {
					if (("ɡ ə " + output).equals(allo1))
						output = "ɡ ə " + output;
				}
			} 
			
			if ( (regex.equals(this.sprache.getVerbFlexion("PrÃ¤sens_er, sie, es")) | regex.equals(this.sprache.getVerbFlexion("Konjunktiv II_ich")) ) && !this.verbAllomorphs.isEmpty()) { 
				for (String allo : this.verbAllomorphs.keySet()) {
					if (allo.endsWith(" t") && (output + " t").equals(allo)) {
						output = output + " t";
					}
				}
			} 
		
		
			if (regex.equals(this.sprache.getVerbFlexion("PrÃ¤sens_du")) && !this.verbAllomorphs.isEmpty()) { 
				String allo1 = this.verbAllomorphs.keySet().toArray()[0].toString();

				if (allo1.endsWith(" s") && (output + " s").equals(allo1)) {
					output = output + "s";
				}
				if (allo1.endsWith(" Ã") && (allo1 + " t").equals(output)) {
					output = allo1;
				}
				if (allo1.endsWith(" z") && (allo1 + " t").equals(output)) {
					output = allo1;
				}
			} 
		
		}
		
		if (this.wortart == Wortart.Substantiv) {
			
			if (regex.equals(this.sprache.getSubstantivFlexion("Nominativ Plural")) && !this.subAllomorphs.isEmpty()) {
				String allo1 = "";
				if (!this.nomSing.equals("")) {
					allo1 = this.nomSing;
				} else {
					allo1 = this.subAllomorphs.keySet().toArray()[0].toString();
				}

				if (allo1.endsWith(" ə") && (output + " ə").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" É") && (output + " É").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" s") && (output + " s").equals(allo1)) {
					output = allo1;
				}       
				if (allo1.endsWith(" n") && (output + " n").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" É s") && (output + " É s").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" ə s") && (output + " ə s").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" É n") && (output + " É n").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" ə n") && (output + " ə n").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" n̩") && (output + " n̩").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" É n") && (output + " É n").replaceAll("ä", "a").replaceAll("ü", "u").replaceAll("ö", "o").equals(allo1)) { 
					output = output + " É n";
				}
				if (allo1.endsWith(" n̩") && (output + " n̩").replaceAll("ɛ", "a").replaceAll("ʏ", "u").replaceAll("œ", "o").equals(allo1)) { 
					output = output + " n̩";
				}
				if (allo1.endsWith(" É r") && (output + " É r").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" ɐ") && (output + " ɐ").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" É r") && (output + " É r").replaceAll("ä", "a").replaceAll("ü", "u").replaceAll("ö", "o").equals(allo1)) { 
					output = output + " É r";
				}
				if (allo1.endsWith(" ɐ") && (output + " ɐ").replaceAll("ɛ", "a").replaceAll("ʏ", "u").replaceAll("œ", "o").equals(allo1)) { 
					output = output + " ɐ";
				}
				if (allo1.endsWith(" i n") && (allo1 + " n É n").equals(output + " É n")) { 
					output = allo1;
				}
				if (allo1.endsWith(" ɪ n") && (allo1 + " n ə n").equals(output + " ə n")) { 
					output = allo1;
				}
			}
			
			if (regex.equals(this.sprache.getSubstantivFlexion("Genitiv Plural")) && !this.subAllomorphs.isEmpty()) { //genitiv
				String allo1 = "";
				if (!this.nomSing.equals("")) {
					allo1 = this.nomSing;
				} else {
					allo1 = this.subAllomorphs.keySet().toArray()[0].toString();
				}

					if (allo1.endsWith(" s") && (output + " s").equals(allo1)) {
						output = allo1;
					}
					if (allo1.endsWith(" É s") && (output + " É s").equals(allo1)) {
						output = allo1;
					}
					if (allo1.endsWith(" ə s") && (output + " ə s").equals(allo1)) {
						output = allo1;
					}
					if (allo1.endsWith(" n") && (output + " n").equals(allo1)) {
						output = allo1;
					}
					if (allo1.endsWith(" É n") && (output + " É n").equals(allo1)) {
						output = allo1;
					}
					if (allo1.endsWith(" n̩") && (output + " n̩").equals(allo1)) {
						output = allo1;
					}
					if (allo1.endsWith(" É") && (output + " É").equals(allo1)) {
						output = allo1;
					}				
					if (allo1.endsWith(" ə") && (output + " ə").equals(allo1)) {
						output = allo1;
					}	
			} 
			if (regex.equals(this.sprache.getSubstantivFlexion("Akkusativ Plural")) && !this.subAllomorphs.isEmpty()) { 
				String allo1 = "";
				if (!this.nomSing.equals("")) {
					allo1 = this.nomSing;
				} else {
					allo1 = this.subAllomorphs.keySet().toArray()[0].toString();
				}
					if (allo1.endsWith(" n") && (output + " n").equals(allo1)) {
						output = allo1;
					}
					if (allo1.endsWith(" É n") && (output + " É n").equals(allo1)) {
						output = allo1;
					}
					if (allo1.endsWith(" É") && (output + " É").equals(allo1)) {
						output = allo1;
					}	
					if (allo1.endsWith(" n̩") && (output + " n̩").equals(allo1)) {
						output = allo1;
					}			
					if (allo1.endsWith(" ə") && (output + " ə").equals(allo1)) {
						output = allo1;
					}	
			} 
			if (regex.equals(this.sprache.getSubstantivFlexion("Dativ Plural")) && !this.subAllomorphs.isEmpty()) { 
				String allo1 = "";
				if (!this.nomSing.equals("")) {
					allo1 = this.nomSing;
				} else {
					allo1 = this.subAllomorphs.keySet().toArray()[0].toString();
				}
					if (allo1.endsWith(" É") && (output + " É").equals(allo1)) {
						output = allo1;
					}	
					if (allo1.endsWith(" É n") && (output + " É n").equals(allo1)) {
						output = allo1;
					}	
					if (allo1.endsWith(" n̩") && (output + " n̩").equals(allo1)) {
						output = allo1;
					}			
					if (allo1.endsWith(" ə") && (output + " ə").equals(allo1)) {
						output = allo1;
					}	
			}
			
			if (regex.equals(this.sprache.getSubstantivFlexion("Dativ Plural")) && !this.subAllomorphs.isEmpty()) { 
				String allo1 = "";
				if (!this.nomSing.equals("")) {
					allo1 = this.nomSing;
				} else {
					allo1 = this.subAllomorphs.keySet().toArray()[0].toString();
				}

				if (allo1.endsWith(" É") && (output + " É").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" ə") && (output + " ə").equals(allo1)) {
					output = allo1;
				}	
				if (allo1.endsWith(" s") && (output + " s").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" n") && (output + " n").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" É s") && (output + " É s").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" ə s") && (output + " ə s").equals(allo1)) {
					output = allo1;
				}	
				if (allo1.endsWith(" É n") && (output + " É n").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" É n") && (output + " É n").replaceAll("ä", "a").replaceAll("ü", "u").replaceAll("ö", "o").equals(allo1)) { 
					output = output + " É n";
				}
				if (allo1.endsWith(" É r") && (output + " É r").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" É r") && (output + " É r").replaceAll("ä", "a").replaceAll("ü", "u").replaceAll("ö", "o").equals(allo1)) { 
					output = output + " É r";
				}
				if (allo1.endsWith(" É r n")&& (output + " É r n").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" in") && (allo1 + " n É n").equals(output + " É n")) { 
					output = allo1;
				}
				if (allo1.endsWith(" n̩") && (output + " n̩").replaceAll("ɛ", "a").replaceAll("ʏ", "u").replaceAll("œ", "o").equals(allo1)) { 
					output = output + " n̩";
				}
				if (allo1.endsWith(" ɐ") && (output + " ɐ").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" ɐ") && (output + " ɐ").replaceAll("ɛ", "a").replaceAll("ʏ", "u").replaceAll("œ", "o").equals(allo1)) { 
					output = output + " ɐ";
				}
				if (allo1.endsWith(" ɐ n") && (output + " ɐ n").replaceAll("ɛ", "a").replaceAll("ʏ", "u").replaceAll("œ", "o").equals(allo1)) { 
					output = output + " ɐ n";
				}
				if (allo1.endsWith(" ɪ n") && (allo1 + " n ə n").equals(output + " ə n")) { 
					output = allo1;
				}
			}
		}
		
		if (this.wortart == Wortart.Adjektiv) {
			if ((regex.equals(this.sprache.getAdjektivFlexion("Komparativ")) || regex.equals(this.sprache.getAdjektivFlexion("Superlativ"))) && !this.subAllomorphs.isEmpty()) { 
				String allo1 = "";
				if (!this.positiv.equals("")) {
					allo1 = this.positiv;
				} else {
					allo1 = this.adjAllomorphs.keySet().toArray()[0].toString();
				}

				if (allo1.endsWith(" É") && (output + " É").equals(allo1)) {
					output = allo1;
				}
				if (allo1.endsWith(" ə") && (output + " ə").equals(allo1)) {
					output = allo1;
				}
			}
		}
		
		return output;
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
	 * stemmer for derivatives (only for German)
	 * @param wortbildung	wordform
	 * @param morphemName	word stem
	 */
	public void derivatStem(String wortbildung, String morphemName) {
		
		this.name = morphemName;
		
		ArrayList<String> praefixe = ReadWiktionary.getIPAPraefixes();
		ArrayList<String> suffixe = ReadWiktionary.getIPASuffixes();
		
			wortbildung = wortbildung.toLowerCase();
			
			String title = this.name.toLowerCase();
			
			if (this.wortart == Wortart.Verb) {
				title = title.replaceAll("(( ə n$)|( n̩$)|( n$))", "");
			}
			
			LevenshteinDistanceMatrix levenshtein = new LevenshteinDistanceMatrix();
			String[] correspondences = levenshtein.getCorrespondence(title.trim().split(" "), wortbildung.trim().split(" "));
			
			String vorsilbe = "";
			String stamm = "";
			String nachsilbe = "";

			for (String corr : correspondences) {

				
				String rectus = corr.split("\\|")[0];
				String derivatum = corr.split("\\|")[1];
				
				if (rectus.equals("0")) {
					if (stamm.equals("")) {
						vorsilbe += derivatum + " ";
					} else {
						nachsilbe += derivatum + " ";
					}
					
				} else {
					if (!derivatum.equals("0"))
						stamm += derivatum + " ";
					nachsilbe = ""; //set to 0 because end of the stem is not reached
				}
				
			}

			stamm = stamm.trim();
	
			
			boolean funna = false;
			
			if ((vorsilbe.equals("") || praefixe.contains(vorsilbe.trim())) &&
				(nachsilbe.equals("") || suffixe.contains(nachsilbe.trim()))	) {
				
				if (allomorphWortbildung.containsKey(stamm)) {
					ArrayList<Wordform> old = new ArrayList<Wordform>(Arrays.asList(allomorphWortbildung.get(stamm.trim())));
					old.add(new Wordform(wortbildung));
					allomorphWortbildung.put(stamm.trim(), old.toArray(new Wordform[old.size()]));
				} else {
					allomorphWortbildung.put(stamm.trim(), new Wordform[]{new Wordform(wortbildung)});
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

				if (wortbildung.replaceAll("ɡ ə ", "").replaceAll(" ɪ k$", "").replaceAll(" ɪ s m ʊ s$", "").contains(title.replaceAll("ɡ ə ", "").replaceAll(" ɪ k$", "").replaceAll(" ɪ s m ʊ s$", "")) && funna == false) { //Partizipien weg
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

	/**
	 * Getter for the name
	 * @return the name of the morpheme as string
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
}



