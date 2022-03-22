package Distributional;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import Helper.Language.Language;
import Helper.Language.Method;
import Helper.Log.Logging;

import java.util.Map.Entry;

/**
 * 
 * implements the phonotactic method
 * 
 * @author abischoff
 *
 */
public class PhonotacticLanguage extends Language {

	/*
	 * parameters for testing
	 */
	boolean around = false; //Trigramm
	boolean iterative = true; //nicht-Iterative
	
	ComplementarySounds method = ComplementarySounds.phonotactical; //Standard method for the determination of complementary sounds
	
	private ArrayList<String> deletedPairs = new ArrayList<String>(); //after each iteration, the best pair will be deleted
	private	HashMap<Integer, String> result = new HashMap<Integer,String>();
	private SubtypologyModul subTypo;
	
	/**
	 * Only for tests
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		PhonotacticLanguage pl = new PhonotacticLanguage("Deutsch",".\\data\\rawData\\Deutsch\\DeutschKorpusWortform.txt");
		pl.setIteration(100);
		pl.start();
	}
	
	/**
	 * 
	 * @param lang		language name
	 * @param fileName	txt file as input
	 * @param iteration	number of iterations
	 */
	public PhonotacticLanguage(String lang, String fileName) {
		super(lang,fileName);
		super.setThreshold(0.5);
		super.setIteration(30);
	}
	
	/**
	 * @return the method object phonotactic
	 */
	public Method getMethod() {
		return Method.phonotactic;
	}

	/**
	 * setter for the method to determine the complementary sounds
	 */
	@Override
	public void setComplementaryFinder(String name) {
		for (ComplementarySounds comp : ComplementarySounds.values()) {
			if (name.contentEquals(comp.toString()))
				this.method = comp;
		} 
	}
	
	/**
	 * setter for the threshold
	 */
	@Override
	public void setThreshold(double thr) {
		this.threshold = thr;
	}
	
	/**
	 * setter for the subtypological module
	 */
	@Override
	public void setSubtypologicalModule(SubtypologyModul modul) {
		this.method = ComplementarySounds.subtypological;
		this.subTypo = modul;
	}
	
	/**
	 * starts the implementation of the phonotactic method
	 */
	public void start() {
		
		int round = 0;

		while (round < iteration) {
			super.currentIteration = round;
			Logging.debug("Iteration: "+round);
			String bestRond  ="";
			Double bestValue = 0.0;

			ReadCorpus rc = new ReadCorpus(this.file,lang,this.getEncoding(),this.around, ipa);
			Logging.debug("File read...");
			List<SoundVector> svListDeutsch = new ArrayList<SoundVector>();
			
			for (String del : this.deletedPairs) {
				ArrayList<SoundVector> subsets = new ArrayList<SoundVector>();
				if (del.matches(".*\\s+(\\\\|/)\\s+.*")) {
					if (subsets.isEmpty()) { 
						for (String context : del.replaceAll("^.*?\t","").split(";")) {
							rc.excludeContext(context.split(" / ")[0], context.split(" / ")[1]);
						}
					}
				}
			}

			TFIDFText ti = new TFIDFText(2,1,1,this.lang,this.around); //standard
			svListDeutsch.addAll(ti.getSVList());
			Logging.debug("Sound vectors initialized...");
				
			TreeMap<Double,String> bestslacks = new TreeMap<Double,String>();
			for (SoundVector sv : svListDeutsch) {

				HashMap<String,Double> slacks = (HashMap<String, Double>) new CompFinder(this.lang, ti.getSVList()).getSlacks(svListDeutsch, sv, threshold, 100.0);
				for (String slack : slacks.keySet()) {
					if (bestslacks.containsKey(slacks.get(slack))) {
						String old = bestslacks.get(slacks.get(slack));
						bestslacks.remove(slacks.get(slack));
						bestslacks.put(slacks.get(slack), old+";"+sv.getName()+" / "+slack);
					} else {
						bestslacks.put(slacks.get(slack),sv.getName()+" / "+slack);
					}
				}
			}

			Entry<Double,String> myBest = bestslacks.pollLastEntry();
			while (myBest != null && myBest.getKey()*0.0 != 0.0) { //delete NaN values
				myBest = bestslacks.pollLastEntry();
			}

			if (myBest == null) {
				bestRond = "- / -";
				bestValue = 0.0;
			} else {
				bestRond = myBest.getValue();
				bestValue = myBest.getKey();
			}
			
			/*
			 * searching for complementary sounds
			 */
			String mySound = bestRond.split(" / ")[0];
			String complementarySound = "";
			for (SoundVector sv : svListDeutsch) {
				if (sv.getName().contentEquals(mySound)) {
					CompFinder compDeterm =  new CompFinder(this.lang, ti.getSVList());
					compDeterm.setIteration(round);
					HashMap<String,Double> slacks = (HashMap<String, Double>) compDeterm.getSlacks(svListDeutsch, sv, threshold, 100.0);
					TreeMap<Double,String> pairs = compDeterm.findSlackComp(slacks, sv);
						
					ArrayList<String> candidates = new ArrayList<String>();
					for (Double d : pairs.keySet()) {
						if (d < 0.5)
							candidates.add(pairs.get(d).split(">")[0]);
					}
						
					compDeterm.similarSound(threshold, 100000.0, candidates.toArray(new String[candidates.size()]));
					if (this.method == ComplementarySounds.hoenigswald) {
						complementarySound = compDeterm.getHoenigswald();
					} else if (this.method == ComplementarySounds.chafe) {
						complementarySound = compDeterm.getChafe();
					} else if (this.method == ComplementarySounds.dolgopolsky) {
						complementarySound = compDeterm.getDolgopolsky();
					} else if (this.method == ComplementarySounds.condition) {
						complementarySound = compDeterm.getCondition();
					} else if (this.method == ComplementarySounds.subtypological && this.subTypo != null) {
						complementarySound = this.subTypo.getComplementarySound(sv.getName());
					} else if (this.method == ComplementarySounds.brown) {
						complementarySound = compDeterm.getBrown();
					} else if (this.method == ComplementarySounds.phonotactical) { 
						complementarySound = pairs.pollLastEntry().getValue().split(" / ")[0];
					} 
						
				
				}
			}
			this.deletedPairs.add(bestRond);
			this.result.put(round,bestValue +"\t"+complementarySound+">"+bestRond);
			
			round++;
		}

		try {

			if (!new File(".\\result").exists()) {
				new File(".\\result").mkdir();
			}
			if (!new File(".\\result\\distributional").exists()) {
				new File(".\\result\\distributional").mkdir();
			}
			if (!new File(".\\result\\distributional\\"+this.lang).exists()) {
				new File(".\\result\\distributional\\"+this.lang).mkdir();
				Logging.debug("Generate directory \\result\\distributional\\"+ this.lang);
			}
			BufferedWriter bw2 = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(".\\result\\distributional\\"+this.lang+"\\ResultList") , "UTF-8"));
			
			for (Integer i : this.result.keySet()) {
				bw2.write((i+1)+"\t"+this.result.get(i).trim()+"\r\n");
			}
			
			bw2.close();			
			if (this.subTypo != null) {
				BufferedWriter bw3 = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(".\\result\\distributional\\"+this.lang+"\\ComplementarySoundCandidates") , "UTF-8"));
				for (String laut : this.subTypo.getCandidates().keySet()) {
					bw3.write("=== " + laut + " ===\r\n");
					TreeMap<Double,String> list = this.subTypo.getCandidates().get(laut);
					for (Double d : list.keySet()) {
						bw3.write(list.get(d) + "\t" + d+"\r\n");
					}
				}
				bw3.close();
			}
			
			Logging.debug("Result files generated.");
			
		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}

	}
	
	/**
	 * @return the path of the result file
	 */
	@Override
	public String getResultFile() {
		return ".\\result\\distributional\\"+this.lang+"\\ResultList";
	}
	
}
