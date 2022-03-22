package Semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import de.tuebingen.uni.sfs.germanet.api.ConRel;
import de.tuebingen.uni.sfs.germanet.api.Synset;

/**
 * object for words of the semantic approach
 * 
 * @author abischoff
 *
 */
public class Word {
	
	public static HashMap<String,ArrayList<Word>> dictDE2PIE = new HashMap<String,ArrayList<Word>>(); //dictionary German2PIE from synset-entry:Ing
	public static HashMap<String,String> dictPIE2DE = new HashMap<String,String>(); //dictionary PIE2German
	
	private String[] wordForms;
	private String name;
	private String[] meaning;
	private String originalMeaning;
	private ArrayList<Synset> synsets = new ArrayList<Synset>();
	private TreeMap<Float, String> resultWordNetPMI = null; //result of startNeedlemanWunsch
	private Float highestPMI;
	
	/**
	 * 
	 * @param name		name of the word
	 * @param wordForms	wordforms as String[]
	 * @param meanings	meanings as String[]
	 */
	public Word(String name, String[] wordForms, String[] meanings) {
		
		this.setName(name.trim());
		this.setWordForms(wordForms);
		this.setMeaning(meanings);
		if (!dictPIE2DE.containsKey(name)) {
			dictPIE2DE.put(name, meanings.toString());
		} else {
			int count = 2;
			while (dictPIE2DE.containsKey(name)) {
				name = name+count;
				count++;
			}
		}
		
	}

	/**
	 * returns the relation of this word to another word
	 * @param pie	the other word
	 * @return	an ArrayList with all relations as strings
	 */
	public ArrayList<String> getRelations(Word pie) {
		ArrayList<String> relations = new ArrayList<String>();
		for (Synset syn : this.synsets) {
			List<Synset> hypernyms = syn.getRelatedSynsets(ConRel.has_hypernym);
			for (Synset otherSyn : pie.synsets) {

				if (hypernyms.contains(otherSyn) && !relations.contains("Hyponym")) { 
					//i.e., the word has the second word as hypernym -> is is a hyponym of the second one
					relations.add("Hyponym");
				}
			}
			List<Synset> hyponyms = syn.getRelatedSynsets(ConRel.has_hyponym);
			for (Synset otherSyn : pie.synsets) {
				if (hyponyms.contains(otherSyn) && !relations.contains("Hypernym")) {
					relations.add("Hypernym");
				}
			}
			List<Synset> causative = syn.getRelatedSynsets(ConRel.causes);
			for (Synset otherSyn : pie.synsets) {
				if (causative.contains(otherSyn) && !relations.contains("Causative")) {
					relations.add("Causative");
				}
			}
			List<Synset> actEntailment = syn.getRelatedSynsets(ConRel.entails);
			for (Synset otherSyn : pie.synsets) {
				if (actEntailment.contains(otherSyn) && !relations.contains("Active Entailment")) {
					relations.add("Active Entailment");
				}
			}
			List<Synset> passEntailment = syn.getRelatedSynsets(ConRel.is_entailed_by);
			for (Synset otherSyn : pie.synsets) {
				if (passEntailment.contains(otherSyn) && !relations.contains("Passive Entailment")) {
					relations.add("Passive Entailment");
				}
			}			
			List<Synset> associative = syn.getRelatedSynsets(ConRel.is_related_to);
			for (Synset otherSyn : pie.synsets) {
				if (associative.contains(otherSyn) && !relations.contains("Associative")) {
	 				relations.add("Associative");
				}
			}
			
			
		}
		return relations;
	}
	
	/**
	 * Getter for the wordforms
	 * @return	wordforms as String[]
	 */
	public String[] getWordForms() {
		return wordForms;
	}

	/**
	 * Setter for wordforms
	 * @param wordForms	String[] of wordforms
	 */
	public void setWordForms(String[] wordForms) {
		this.wordForms = wordForms;
	}

	/**
	 * Getter for the name of the word
	 * @return	name of the word as string
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for the name of the word	
	 * @param name	name of the word as string
	 */
	public void setName(String name) {
		this.name = name.trim();
	}

	
	/**
	 * Getter for the meaning of the word
	 * @return	String[] of meanings
	 */
	public String[] getMeaning() {
		return meaning;
	}

	/**
	 * Setter for the meanings
	 * @param meaning	String[] of meanings
	 */
	public void setMeaning(String[] meaning) {
		this.meaning = meaning;
	}

	/**
	 * Getter for the synsets
	 * @return	an ArrayList of GermaNet synsets
	 */
	public ArrayList<Synset> getSynsets() {
		return synsets;
	}

	/**
	 * Adds an synset to the synsets of the word
	 * @param synset	GermaNet synset
	 */
	public void addSynsets(Synset synset) {
		if (!synsets.contains(synset)) {
			this.synsets.add(synset);
			
			for (String lemma : synset.getAllOrthForms()) {
				
				if (Word.dictDE2PIE.containsKey(lemma)) {
					ArrayList<Word> pies = dictDE2PIE.get(lemma);
					pies.add(this);
					dictDE2PIE.remove(lemma);
					dictDE2PIE.put(lemma, pies);
				} else {
					ArrayList<Word> pies = new ArrayList<Word>();
					pies.add(this);
					dictDE2PIE.put(lemma, pies);
				}
				
			}
		}
	}

	/**
	 * Getter for the result
	 * @return	TreeMap: PMI score -> pair
	 */
	public TreeMap<Float, String> getResultWordNetPMI() {
		return resultWordNetPMI;
	}

	/**
	 * Setter for the result
	 * @param resultWordNetPMI	TreeMap: PMI score -> pair
	 */
	public void setResultWordNetPMI(TreeMap<Float, String> resultWordNetPMI) {
		if (resultWordNetPMI.size() != 0) {
			this.highestPMI = resultWordNetPMI.lastKey();
		} else {
			this.highestPMI = 0.0f;
		}
		this.resultWordNetPMI = resultWordNetPMI;
	}
	
	/**
	 * Getter for the highest PMI score
	 * @return	float of the highest PMI score
	 */
	public Float getHighestPMI() {
		return this.highestPMI;
	}
	
	/**
	 * Getter for the meanings as a single string
	 * @return	string of meanings that are separated by comma
	 */
	public String getStringMeaning() {
		String str = "";
		for (String mean : meaning) {
			str += mean + ", ";
		}
		return "'"+str.replaceAll(",\\s$", "")+"'";
	}

	/**
	 * Getter for the original meaning
	 * necessary if digraphs were replaced
	 * @return 	string of the original word meaning
	 */
	public String getOriginalMeaning() {
		return originalMeaning;
	}

	/**
	 * Setter for the original meaning
	 * necessary if digraphs were replaced 
	 * @param originalMeaning	string of the original meanings
	 */
	public void setOriginalMeaning(String originalMeaning) {
		this.originalMeaning = originalMeaning;
	}
	
}