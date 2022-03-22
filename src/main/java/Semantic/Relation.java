package Semantic;

import java.util.ArrayList;

/**
 * 
 * object for semantic relations of words
 * 
 * @author abischoff
 *
 */

public class Relation {
	
	private Word w1;
	private Word w2;
	private char[] leng1;
	private char[] leng2;
	private boolean hypernym = false;
	private boolean hyponym = false;
	private boolean causative = false;
	private boolean actEntailment = false;
	private boolean passEntailment = false;
	private boolean associative = false;
	
	
	/**
	 * 
	 * @param w1	first word of the relation
	 * @param w2	second word of the relation
	 * @param leng1	alignment of the first word
	 * @param leng2	alignment of the second word
	 */
	public Relation(Word w1, Word w2, char[] leng1, char[] leng2) {
		this.w1 = w1;
		this.w2 = w2;
		this.leng1 = leng1;
		this.leng2 = leng2;
		ArrayList<String> rels = w1.getRelations(w2);
		for (String relation : rels) {
			if (relation.contentEquals("Hypernym")) {
				this.hypernym = true;
			} else if (relation.contentEquals("Hyponym")) {
				this.hyponym = true;
			} else if (relation.contentEquals("Causative")) {
				this.causative = true;
			} else if (relation.contentEquals("Active Entailment")) {
				this.actEntailment = true;
			} else if (relation.contentEquals("Passive Entailment")) {
				this.passEntailment = true;
			} else if (relation.contentEquals("Associative")) {
				this.associative = true;
			} 
		}
	}
	
	/**
	 * returns alternating sounds of the words in questions
	 * it only returns sound pairs that are not identical
	 * 
	 * @return a String[] with alternating sound pairs which are separated by |
	 */
	public String[] getAlternations() {
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0 ; i < leng1.length ; i++) {
			if (leng1[i] != leng2[i]) {
				result.add(Character.toString(leng1[i]) + "|" + Character.toString(leng2[i]));
			}
		}
		
		return result.toArray(new String[result.size()]);
		
	}

	/**
	 * Getter for the status of the relation
	 * @return	true if the first word is a hypernym of the second one
	 */
	public boolean isHypernym() {
		return hypernym;
	}

	/**
	 * Getter for the status of the relation
	 * @return	true if the first word is a hyponym of the second one
	 */
	public boolean isHyponym() {
		return hyponym;
	}

	/**
	 * Getter for the status of the relation
	 * @return	true if the first word is a causative of the second one
	 */
	public boolean isCausative() {
		return causative;
	}

	
	/**
	 * Getter for the status of the relation
	 * @return	true if the first word is an active entailment of the second one
	 */
	public boolean isActEntailment() {
		return actEntailment;
	}

	
	/**
	 * Getter for the status of the relation
	 * @return	true if the first word is a passive entailment of the second one
	 */
	public boolean isPassEntailment() {
		return passEntailment;
	}

	
	/**
	 * Getter for the status of the relation
	 * @return	true if the first word has an associative relation to the second one
	 */
	public boolean isAssociative() {
		return associative;
	}

	/**
	 * Getter for the first word
	 * @return	the name of the first word
	 */
	public String getWordName1() {
		return w1.getName();
	}

	/**
	 * Getter for the second word
	 * @return	the name of the second word
	 */
	public String getWordName2() {
		return w2.getName();
	}
	
	/**
	 * Getter for the first word object
	 * @return	first word as Word
	 */
	public Word getWord1() {
		return w1;
	}
	
	/**
	 * Getter for the second word object
	 * @return	second word as Word
	 */
	public Word getWord2() {
		return w2;
	}
	
}