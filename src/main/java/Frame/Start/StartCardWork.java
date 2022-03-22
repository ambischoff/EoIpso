package Frame.Start;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import App.InternalReconstruction;
import Distinctive.DistinctiveLanguage;
import Distributional.PhonotacticLanguage;
import Frame.CardWork;
import Frame.Result;
import Gap.GapLanguage;
import Helper.Language.Language;
import Helper.Language.Method;
import Helper.Log.Logging;
import Helper.Typology.ArticulatoryPhonetics;
import Morphophonemic.MorphophonemicLanguage;
import Semantic.SemanticLanguage;

/**
 * CardWork for the StartCard
 * @author abischoff
 *
 */
public class StartCardWork implements CardWork {

    private Language result = new Language("","");

    /**
     * 
     * @param result2	result of the previous card deck
     */
	public StartCardWork(Result result2) {
		this.setPreviousResult(result2);
	}

    /**
     * sets the previous result
     * @param Result	previous result as Result object
     */
    @Override
    public void setPreviousResult(Result previousResult) {
       this.result = previousResult.getLanguage();
    }

    /**
     * nothing to do
     */
    @Override
    public void runWhenShown() { }
    
    /**
     * getter for the result
     * @return Result
     */
	@Override
    public Result getResult() {
    	Result resObject = new Result();
    	resObject.setResult(this.result);
        return resObject;
    }
    
	/**
	 * setter of the language name
	 * @param value	name of the language
	 * @return true if the argument value is not empty
	 */
    public boolean setLanguage(String value) {
        if (value.matches(".+")) {
            this.result.setLanguage(value);
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * setter for the encoding
     * @param value	encoding as String
     * @return true if the argument value is not empty
     */
    public boolean setEncoding(String value) {
        if (value.matches(".+")) {
            this.result.setEncoding(value);
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * starts the choosen method
     * @param method	Method
     */
	public void start(Method method) {
		
		String lang = result.getLang();
		String trans = result.getTranscription();
		String file = result.getPath();
		if (result.getFormat().contentEquals("corpus")&& !(method == Method.paradigmatic || method == Method.derivational )) {//corpus is converted to a word list
			file = this.makeCorpus2WordList(file);
		}
		if (trans.contentEquals("IPA") && !(method == Method.paradigmatic || method == Method.derivational )) {
			file = this.checkFileForIPA(file);			
		} 
		String encod = result.getEncoding();

		Language myLanguage = result;
		if (method == Method.semantic) {
			myLanguage = new SemanticLanguage(lang,file);
			myLanguage.setEncoding(encod); myLanguage.setTranscription(trans);
		} else if (method == Method.phonotactic) {
			if (trans.contentEquals("untranscribed")) {
				file = this.checkFileForUntranscribed(file);			
			} 
			myLanguage = new PhonotacticLanguage(lang,file);
			myLanguage.setEncoding(encod); myLanguage.setTranscription(trans);			
		} else if (method == Method.distinctive) {
			myLanguage = new DistinctiveLanguage(lang,file);
			myLanguage.setEncoding(encod); myLanguage.setTranscription(trans);
		} else if (method == Method.gap) {
			myLanguage = new GapLanguage(lang,file);
			myLanguage.setEncoding(encod); myLanguage.setTranscription(trans);
		} else if (method == Method.paradigmatic) {
			myLanguage = new MorphophonemicLanguage(lang,file);
			myLanguage.setEncoding(encod); myLanguage.setTranscription(trans);		
			myLanguage.setMethod(method);
		} else if (method == Method.derivational) {
			myLanguage = new MorphophonemicLanguage(lang,file);
			myLanguage.setEncoding(encod); myLanguage.setTranscription(trans);
			myLanguage.setMethod(method);
		}
		

	}


	/**
	 * checks if the file contains untranscribed data
	 * generates a new file and returns its path
	 * 
	 * this method may be irrelevant
	 * @param file	input file
	 * @return	the path of the new file
	 */
	private String checkFileForUntranscribed(String file) {
		try {
			
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), result.getEncoding()));
			String line = br.readLine();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter (new FileOutputStream("data\\transData\\Prepared"+new File(file).getName()),"UTF-8"));

			if (line.toCharArray()[0] == 65279) {
				line = line.substring(1);
			}
			while(line != null){
				line = line.split("\t")[0].replaceAll("[\\*\\?\\-\\(\\)\\!]","").trim();
				StringBuilder sb = new StringBuilder();
				for (char sign : line.toCharArray()) {
					sb.append(" " + Character.toString(sign).toLowerCase());
				} 
				bw.write(sb.toString().trim()+"\r\n");
				line = br.readLine();
			}		
			br.close();
			bw.close();
			file = ".\\data\\transData\\Prepared"+new File(file).getName();

		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
		
		return file;
	}

	/**
	 * converts a corpus to a word list
	 * 
	 * @param file input file
	 * @return path of the new file
	 */
	private String makeCorpus2WordList(String file) {
		try {
			
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), result.getEncoding()));
			String line = br.readLine();

			if (line.toCharArray()[0] == 65279) {
				line = line.substring(1);
			}
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter (new FileOutputStream("data\\transData\\WordList"+new File(file).getName()),"UTF-8"));

			while (line != null) {

					String[] lineWords = line.split("\\s+");
					for (String sign : lineWords) {
						if (!sign.replaceAll("[^\\p{L}]","").replaceAll("(_|-)", " ").contentEquals("")) {
							bw.write(sign.replaceAll("[^\\p{L}]","").replaceAll("(_|-)", " ")+"\r\n");
						}
					} 
					line = br.readLine();
			}
			
			br.close();
			bw.close();
			file = ".\\data\\transData\\WordList"+new File(file).getName();

			
		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
		
		return file;
	}

	/**
	 * checks if the file contains IPA transcriptions
	 * generates a new file and returns its path
	 * 
	 * @param file input file
	 * @return	the path of the new file
	 */
	private String checkFileForIPA(String file) {
		
		try {
			
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), result.getEncoding()));
			String line = br.readLine();
			if (!line.split("\t")[0].contains(" ") && !line.contentEquals("")) {
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter (new FileOutputStream("data\\transData\\IPA"+new File(file).getName()),"UTF-8"));
				String rest = "";
				if (line.split("\t").length > 1) {
					rest = line.replaceFirst("^.*?\\t", "");
				}
				ArrayList<String> diacritics = ArticulatoryPhonetics.readDiacritics();
				System.out.println(line);
				if (line.toCharArray()[0] == 65279) {
					line = line.substring(1);
				}
				while(line != null){
					line = line.split("\t")[0].replaceAll("[\\*\\?\\-\\(\\)\\!]","").trim();
					StringBuilder sb = new StringBuilder();
					for (char sign : line.toCharArray()) {
						String letter = Character.toString(sign);
						if (diacritics.contains(letter)) {
							sb.append(Character.toString(sign));
						} else {
							sb.append(" " + Character.toString(sign));
						}
					} 
					bw.write(sb.toString().trim()+rest+"\r\n");
					line = br.readLine();
				}		
				br.close();
				bw.close();
				file = ".\\data\\transData\\IPA"+new File(file).getName();
			}
			

			
		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
		
		return file;
	}
    

	/**
	 * getter for the file path
	 * @return String 
	 */
	public String getFilePath() {
		return this.result.getPath();
	}

	/**
	 * setter for the file path
	 * @param filePath	new path of the file
	 */
	public void setFilePath(String filePath) {
		this.result.setPath(filePath);
	}
	
	/**
	 * getter for the language name
	 * @return name of the language
	 */
	public String getLanguage() {
		return this.result.getLang();
	}

	/**
	 * getter for the number of lines
	 * @return number of lines of the input file
	 */
	public int getLines() {
		return this.result.getLines();
	}
	
	/**
	 * setter for the number of lines
	 * @param lineNumb number of lines of the input file
	 */
	public void setLines(String lineNumb) {
		if (lineNumb.matches("\\d+")) {
			this.result.setLines(Integer.parseInt(lineNumb));
		}
	}

	/**
	 * getter for the encoding
	 * @return	encoding as String
	 */
	public String getEncoding() {
		return this.result.getEncoding();
	}
	
	/**
	 * getter for the transcription
	 * @return the transcription IPA, XSAMPA, or untranscribed
	 */
	public String getTranscription() {
		return this.result.getTranscription();
	}
	
	/**
	 * setter for the transcription
	 * @param value: IPA, XSAMPA, or untranscribed
	 */
	public void setTranscription(String value) {
		this.result.setTranscription(value);
	}

	/**
	 * resets the variables
	 */
	public void resetData() {
		this.result  =new Language("","");
	}
	
	/**
	 * initializes the next card deck
	 */
	public void close() {
		Logging.debug(result.getLang());
		new InternalReconstruction(this.getResult().getLanguage()).start();
		
	}

	/**
	 * getter for the format
	 * @return the format: word list, corpus, morpheme list
	 */
	public String getFormat() {
		return this.result.getFormat();
	}

	/**
	 * setter for the format
	 * @param format 	word list, corpus, morpheme list
	 */
	public void setFormat(String format) {
		this.result.setFormat(format);
	}
	
}