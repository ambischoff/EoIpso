package Helper.Language;

import java.util.ArrayList;

import Distributional.SubtypologyModul;

/**
 * object for languages
 * @author abischoff
 *
 */
public class Language {

	protected Method method;
	protected String lang;
	protected String file;
	protected int lineCount;
	protected int iteration;
	protected boolean ipa = true;
	protected String transcription;
	protected String encoding;
	protected String format;
	protected double threshold;
	protected boolean typological;
	protected int currentIteration;
	
	/**
	 * @param lang	name of the lang
	 * @param file	path of the file
	 */
	public Language(String lang, String file) {
		this.lang = lang;
		this.file = file;
	}
	
	/**
	 * @param lang	name of the lang
	 * @param file	path of the file
	 * @param iteration	number of maximal iterations
	 */
	public Language(String lang, String file, int iteration) {
		this.lang = lang;
		this.file = file;
		this.iteration = iteration;
	}

	/**
	 * returns the path of the result file
	 * @return path of the result file
	 */
	public String getResultFile() {
		return null;
	}

	/**
	 * Getter for the transcription
	 * @return	the name of the transcription
	 */
	public String getTranscription() {
		return transcription;
	}

	/**
	 * Setter for the transcription
	 * @param transcription name of the transcription
	 */
	public void setTranscription(String transcription) {
		this.transcription = transcription;
		if (transcription.contentEquals("IPA")) {
			this.ipa = true;
		}else {
			this.ipa = false;
		}
	}

	/**
	 * Getter for the encoding
	 * @return the name of the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Setter for the encoding
	 * @param encoding name of the encoding
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	/**
	 * Getter for the language name
	 * @return name of the language
	 */
	public String getLang()  {
		return this.lang;
	}

	/**
	 * Getter for the filename
	 * @return the path of the file
	 */
	public String getPath() {
		return this.file;
	}
	
	/**
	 * Getter for the method
	 * @return the method
	 */
	public Method getMethod() {
		return this.method;
	}

	/**
	 * Setter for the method
	 * @param method method
	 */
	public void setMethod(Method method) {
		this.method = method;
	}
	
	/**
	 * Setter for the iteration
	 * @param iteration2 number of max. iterations
	 */
	public void setIteration(int iteration2) {
		this.iteration = iteration2;
	}
	
	/**
	 * dummy method
	 * to be overwrite
	 */
	public void start() {
		
	}

	/**
	 * dummy method
	 * to be overwrite
	 */
	public void setSubtypologicalModule(SubtypologyModul modul) {
		
	}

	/**
	 * dummy method
	 * to be overwrite
	 */
	public void setComplementaryFinder(String name) {
		
	}

	/**
	 * Setter for the threshold
	 * @param thr threshold
	 */
	public void setThreshold(double thr) {
		this.threshold = thr;
	} 
	
	/**
	 * Getter for the threshold
	 * @return the threshold
	 */
	public double getThreshold() {
		return this.threshold;
	}

	/**
	 * dummy method
	 * to be overwrite
	 */
	public void setExcludeSounds(String excludeSounds) {
		
	}
	
	/**
	 * Setter for the typology
	 * @param typ is a typological module active?
	 */
	public void setTypological(boolean typ)  {
		this.typological = typ;
	}
	
	/**
	 * Getter for the typology
	 * @return the status of the typological module
	 */
	public boolean getTypological() {
		return this.typological;
	}

	/**
	 * Getter for the iteration number
	 * @return the number of iterations
	 */
	public int getIteration() {
		return iteration;
	}

	/**
	 * Getter for the format
	 * @return the format
	 */
	public String getFormat() {
		return this.format;
	}
	
	/**
	 * Setter for the format
	 * @param format format as string
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * Getter of the current iteration
	 * @return the currenct iteration round
	 */
	public int getCurrentIteration() {
		return this.currentIteration;
	}

	/**
	 * dummy method
	 * to be overwrite
	 * @param	w1	weight 1
	 * @param 	w2	weight 2
	 */
	public void setWeights(Double w1, Double w2) {}

	/**
	 * dummy method
	 * to be overwrite
	 * @param	cluster1	array of sounds
	 * @param 	cluster2	array of sounds
	 */
	public void setClusters(ArrayList<String[]> clusters) {}

	public void setLanguage(String value) {
		this.lang = value;
	}

	public void setPath(String filePath) {
		this.file = filePath;
		
	}

	public int getLines() {
		return this.lineCount;
	}

	public void setLines(int parseInt) {
		this.lineCount = parseInt;
	}
	
}

