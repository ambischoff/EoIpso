package Frame;

import Distinctive.DistinctiveLanguage;
import Distributional.PhonotacticLanguage;
import Helper.Language.Language;
import Helper.Language.Method;
import Helper.Log.Logging;
import Morphophonemic.MorphophonemicLanguage;
import Semantic.SemanticLanguage;

/**
 * objects of the result of a CardWork
 * the result of one CardWork is the input of the next CardWork
 * it contains one Language object
 */
public class Result {

	private Language myLanguage;
	
    public Result() {
    	this.myLanguage = new Language("","");
    }

	/**
	 * Getter for the Language object
	 * @return Language
	 */
    public Language getLanguage() {
    	return this.myLanguage;
    }

    /**
     * setter for the Language object
     * @param inputMethod	Method of the language
     * @param language		name of the language
     * @param filePath		file path of the input file
     * @param encoding		encoding of the input file
     * @param transcription	IPA, SAMPA, or untranscribed
     * @param format		word list, morpheme list, or corpus
     * @param linesCount	number of lines
     */
    public void setLanguage(Method inputMethod, String language, String filePath, String encoding, String transcription, String format, int linesCount) {

		if (inputMethod == Method.derivational || inputMethod == Method.paradigmatic) {
			this.myLanguage = new MorphophonemicLanguage(language, filePath);
		} else if (inputMethod == Method.phonotactic) {
			this.myLanguage = new PhonotacticLanguage(language, filePath);
		} else if (inputMethod == Method.semantic) {
			this.myLanguage = new SemanticLanguage(language, filePath);
		} else if (inputMethod == Method.distinctive) {
			this.myLanguage = new DistinctiveLanguage(language, filePath);
		} else {
			Logging.error("Type of Language not found!");
		}
    	this.myLanguage.setTranscription(transcription);
    	this.myLanguage.setEncoding(encoding);
    	this.myLanguage.setFormat(format);
    	this.myLanguage.setMethod(inputMethod);
    }

    /**
     * setter for the Language object
     * @param la	language object
     */
    public void setResult(Language la) {
    	this.myLanguage = la;
    }

    /**
     * getter for the Method
     * @return	Method of the Language
     */
    public Method getMethod() {
    	return this.myLanguage.getMethod();
    }

    /**
     * Setter for the method
     * @param myMethod	Method object
     */
	public void setMethod(Method myMethod) {
		this.myLanguage.setMethod(myMethod);
	}
    
}
