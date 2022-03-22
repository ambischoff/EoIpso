package Frame.Drop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import App.InternalReconstruction;
import Frame.Result;
import Frame.CardWork;
import Helper.Encoding.Encoding;
import Helper.Encoding.EncodingDetector;
import Helper.Language.Language;
import Helper.Log.Logging;

/**
 * CardWork for the DropCard
 * @author abischoff
 *
 */
public class DropCardWork implements CardWork {

    private Result result = new Result();
	private int linesCount= 0; 
	private String language = "Unknown";
	private String transcription = "untranscribed";
	private String format = "word list";
	private String encoding = null;
	private String filePath = null;
    private Language previousResult;


    /**
     * sets the previous result
     * @param Result	previous result as Result object
     */
    @Override
    public void setPreviousResult(Result previousResult) {
    	this.previousResult = previousResult.getLanguage();
    }

    /**
     * initializes this.result
     */
    @Override
    public void runWhenShown() {
    	if (this.result == null) {
    		result = new Result();
    	}
    }

    /**
     * Getter for the result
     * @return Result
     */
    @Override
    public Result getResult() {
        return this.result;
    }

    /**
     * Getter for the path of the input file
     * @return	file path as String
     */
	public String getFilePath() {
		return this.filePath;
	}
	
	/**
	 * setter for the file path
	 * @param filePath as String
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * getter for the language name
	 * @return	the name of the language as String
	 */
	public String getLanguage() {
		return this.language;
	}
	
	/**
	 * getter for the line number
	 * @return number of lines as int
	 */
	public int getLines() {
		return this.linesCount;
	}

	/**
	 * getter for the encoding of the input file
	 * @return encoding as String
	 */
	public String getEncoding() {
		return this.encoding;
	}
	
	/**
	 * getter for the transcription type: IPA, SAMPA, or untranscribed
	 * @return String
	 */
	public String getTranscription() {
		return this.transcription;
	}
	
	/**
	 * setter for the transcription type
	 * @param value as String
	 */
	public void setTranscription(String value) {
		this.transcription = value;
	}

	/**
	 * getter for the format: word list, morpheme list, or corpus
	 * @return String
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * setter for the format
	 * @param format
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	
	/**
	 * reads the file und extracts the necessary data
	 */
	public void readFileInfo() {
		
		try {

			EncodingDetector e = new EncodingDetector(new FileInputStream(new File(filePath)), Encoding.UTF8);
			encoding = e.getEncoding().toString();
			
			BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(new File(filePath)) , encoding));
			String line = br.readLine();
			HashMap<String,Integer> iGuessTheFormat = new HashMap<String,Integer>();
			HashMap<String,Integer> iGuessTheTranscript = new HashMap<String,Integer>();
			iGuessTheFormat.put("corpus", 0); iGuessTheFormat.put("word list", 0); iGuessTheFormat.put("morpheme list", 0);
			iGuessTheTranscript.put("IPA", 0); iGuessTheTranscript.put("XSAMPA", 0); iGuessTheTranscript.put("untranscribed", 0); iGuessTheTranscript.put("untranscribed and letter-separated",0);
			while (line !=null) {

				linesCount++;
				
				String iGuessT = guessTranscription(line);
				iGuessTheTranscript.put(iGuessT, iGuessTheTranscript.get(iGuessT)+1);
				String iGuess = guessSourceDataFormat(line);
				iGuessTheFormat.put(iGuess, iGuessTheFormat.get(iGuess)+1);
				line = br.readLine();

			}

			if (iGuessTheFormat.get("corpus") >= iGuessTheFormat.get("word list") && iGuessTheFormat.get("corpus") >= iGuessTheFormat.get("morpheme list")) {
				this.setFormat("corpus");
			} else if (iGuessTheFormat.get("word list") >= iGuessTheFormat.get("morpheme list")) {
				this.setFormat("word list");
			} else {
				this.setFormat("morpheme list");
			}
			
			if (iGuessTheTranscript.get("IPA") >= iGuessTheTranscript.get("XSAMPA") && iGuessTheTranscript.get("IPA") >= iGuessTheTranscript.get("untranscribed") && iGuessTheTranscript.get("IPA") >= iGuessTheTranscript.get("untranscribed and letter-separated")) {
				this.setTranscription("IPA");
			} else if (iGuessTheTranscript.get("XSAMPA") >= iGuessTheTranscript.get("untranscribed") && iGuessTheTranscript.get("XSAMPA") >= iGuessTheTranscript.get("untranscribed and letter-separated")) {
				this.setTranscription("XSAMPA");
			} else if (iGuessTheTranscript.get("untranscribed and letter-separated") >= iGuessTheTranscript.get("untranscribed")) {
				this.setTranscription("untranscribed and letter-separated");
			} else {
				this.setTranscription("untranscribed");
			}

			br.close();

		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
		
	}
	
	/**
	 * tries to get the format of the input file:
	 * more than 5 words in a line -> corpus
	 * one word in a line -> morpheme list
	 * else: word list
	 * 
	 * @param line of the input file
	 * @return	"corpus", "morpheme list", or "word list"
	 */
	private String guessSourceDataFormat(String line) {
		if (line.split("\\s").length > 5 && line.split("\\s")[0].toCharArray().length > 2) { //more than 5 words -> corpus
			return "corpus";
		} else if (line.split("\\s").length == 0 && line.split("\\+").length > 3) {//only 1 word: morpheme list
			return "morpheme list";
		}
		return "word list";

	}
	
	/**
	 * tries to get the transcription type
	 * if there are special signs of IPA -> IPA
	 * if there are only signs typical for XSAMPA -> XSAMPA
	 * else: untranscribed
	 * 
	 * @param line of the input file
	 * @return "IPA", "XSAMPA", "untranscribed", or "untranscribed and letter-separated"
	 */
	private String guessTranscription(String line) {
		List<String> ipaMarker = Arrays.asList(new String[] {"ɛ","ʦ","ɪ","ç","ː","ə","ʃ","ŋ","ɔ","ɐ","œ","ʊ","ʧ","χ","ɡ","ʏ","ʔ","ø","ʒ","ʤ"});
		if (line.matches("[pbtd`cJ\\\\kgq>?mFnr4vszxH<lO|!=_iy1ue2@8793&6a\s]*")) {
			return "XSAMPA";

		}
		for (String ipa : ipaMarker) {
			if (line.contains(ipa)) {
				return "IPA";

			}
		}
		
		double countMonoAndDigraphs = 0;
		for (String sound : line.split("\\s")) {//are the data already separated by space?
			if (sound.toCharArray().length <= 2) {
				countMonoAndDigraphs++;
			}
		}
		
		if (countMonoAndDigraphs > (line.split("\\s").length *0.8)) {
			return "untranscribed and letter-separated";
		}
		
		return "untranscribed";
	}

	/**
	 * resets all variables
	 */
	public void resetData() {
		language = null;
		encoding = null;
		filePath = null;
		linesCount = 0;
	}
	

	/**
	 * generates a result objects and starts the next card deck
	 */
	public void close() {
		this.result.setLanguage(this.previousResult.getMethod(),language, filePath, encoding, transcription, format, linesCount);
		System.out.println(this.result.getLanguage().getIteration());
		new InternalReconstruction(this.getResult()).start();
	}
	
}
