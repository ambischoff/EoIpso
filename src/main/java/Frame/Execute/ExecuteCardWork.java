package Frame.Execute;

import java.util.ArrayList;
import java.util.Arrays;

import Distributional.SubtypologyModul;
import Frame.Result;
import Frame.CardWork;
import Helper.Language.Language;
import Helper.Log.Logging;

/**
 * CardWork for the DropCard
 * @author abischoff
 *
 */
public class ExecuteCardWork implements CardWork {

    private Result result;

    private Language myMethod;
	private int iteration = 10;
    
	/**
	 * 
	 * @param lang result of the previous card deck
	 */
	public ExecuteCardWork(Language lang) {
		this.myMethod = lang;
	}
	
    /**
     * sets the previous result
     * @param Result	previous result as Result object
     */
    @Override
    public void setPreviousResult(Result previousResult) {
    	this.myMethod = ((Result) previousResult).getLanguage();
    }

    /**
     * initializes this.result
     */
    @Override
    public void runWhenShown() {
    	this.result = new Result();
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
	 * opens the log frame and starts the next card deck
	 */
	public void close() {
		Logging.openProcessWindow();
		this.myMethod.start();
	}
	
	/**
	 * getter for the current iteration number
	 * @return int
	 */
	public int getCurrentIteration() {
		return this.myMethod.getCurrentIteration();
	}

	/**
	 * setter of the input data necessary for the method
	 * @param iter			number of iterations
	 * @param threshold		threshold
	 * @param excludeSounds	sounds that should be excluded
	 * @param typo			should typological information be used?
	 */
	public void setArguments(String iter, String threshold, String excludeSounds, boolean typo) {
		if (iter.matches("\\d+")) {
			this.iteration = Integer.parseInt(iter);
			this.myMethod.setIteration(iteration);
		}
		if (threshold.matches("\\d+([.,]\\d+)?")) {
			this.myMethod.setThreshold(Double.parseDouble(threshold));
		}
		if (!excludeSounds.matches("\\s*")) {
			this.myMethod.setExcludeSounds(excludeSounds);
		}
		this.myMethod.setTypological(typo);
	}

	/**
	 * setter for the method
	 * @param myMethod	method as Language object
	 */
	public void setMethod(Language myMethod) {
		this.myMethod = myMethod;
	}

	/**
	 * setter for the method to find complementary sounds
	 * @param text	 approach (e.g., Subtyological Approach)
	 * @param number number of subtypological languages (only necessary for the subtypological approach)
	 */
	public void setComplementaryFinder(String text, String number) {
		if (text.contentEquals("Subtypological Approach")) {
			Logging.debug("Start subtypological approach...");
			SubtypologyModul sub = new SubtypologyModul(this.myMethod.getLang(),this.myMethod.getPath());
			int langs = 5;
			if (number.matches("\\d+")) {
				langs = Integer.parseInt(number);
			}
			sub.findSubtypologicalLanguages();
			String[] subTypoLangs = sub.getMostSimilarLangs(langs);
			Logging.debug("Most similar languages are " + Arrays.asList(subTypoLangs).toString());
			sub.compareCorpusWithSubTypology(sub.getPreparedCorpusOrDefault(),sub.getUniversalCorpusOrDefault());
			this.myMethod.setComplementaryFinder(text);
			this.myMethod.setSubtypologicalModule(sub);
		} else {
			this.myMethod.setComplementaryFinder(text);
		}
		
	}

	/**
	 * setter for the weights of the distinctive approach
	 * @param g1	first weight
	 * @param g2	second weight
	 */
	public void setWeights(String g1, String g2) {
		Double w1 = null;
		Double w2 = null;
		if (g1.replaceAll(",", ".").trim().matches("\\d+(\\.\\d+)?")) {
			w1 = Double.parseDouble(g1.replaceAll(",", "."));
		}
		if (g2.replaceAll(",", ".").trim().matches("\\d+(\\.\\d+)?")) {
			w2 = Double.parseDouble(g2.replaceAll(",", "."));
		}
		this.myMethod.setWeights(w1,w2);
	}

	/**
	 * setter for the clusters of the distinctive approach
	 * @param texts	clusters as String[] separated by ", "
	 */
	public void setClusters(String[] texts) {
		
		ArrayList<String[]> clusters = new ArrayList<String[]>();
		for (String text : texts) {
		String[] cluster1 = null;
			if (!text.contains("...]")) {
				
				if (text.matches(".*,.*")) {
					cluster1 = text.replaceAll("(^\\[|\\]$)","").split("\\s*,\\s*");
				} else if (text.matches(".*\\s.*")) {
					cluster1 = text.replaceAll("(^\\[|\\]$)","").split("\\s+");
				} else if (text.matches("\\[?.\\]?")) {
					cluster1 = new String[] {text.replaceAll("(^\\[|\\]$)","")};
				}else {
					System.err.println("Cluster not identified: " + text);
				}
				clusters.add(cluster1);
			}
		}
		
		if (clusters.size() != 0) {
			this.myMethod.setClusters(clusters);
		}
	}

	
}