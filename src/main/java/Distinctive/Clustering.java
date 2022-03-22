package Distinctive;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Helper.Log.Logging;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.SumOfSquaredErrors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.filter.normalize.NormalizeMidrange;
import net.sf.javaml.tools.InstanceTools;
import net.sf.javaml.tools.data.FileHandler;

/**
 * implements k-means-clustering of sounds
 * 
 * @author abischoff
 *
 */
public class Clustering {

	private ArrayList<Sound> sounds;
	
	/**
	 * @param allSounds list of all sounds
	 */
	public Clustering(ArrayList<Sound> allSounds) {
		this.sounds = allSounds;
		this.clustering(0);
	}


	/**
	 * starts kMeans-clustering
	 * if there is no method given, elbow method is used
	 * 
	 * @param k	number of clusters
	 * @return	a map with { cluster name -> [sounds] }
	 */
	public HashMap<String,ArrayList<String>> clustering(int k) { 

		StringBuilder text = new StringBuilder();
		StringBuilder text2 = new StringBuilder();
		
		for (Sound suchSound1 : sounds) { //first line
			if (suchSound1.getName().contentEquals(sounds.get(0).getName())) {
				text.append(suchSound1.getName());
			} else {
				text.append("\t"+suchSound1.getName());
			}
		}
	//	System.out.println("== Ausdruck für R ==");
	//	System.out.println("names <- c(\""+text.toString().replaceAll("\t", "\",\"")+"\")");

		
		HashMap<String,double[]> jaccVectors = new HashMap<String,double[]>(); 
		double durchSchnittJKoeff = 0.0;
		for (Sound mySound1 : this.sounds) { 
			
			String suchSound1 = mySound1.getName();
			double[] jaccVector = new double[this.sounds.size()]; 
			int i = 0; 
			
			String output = "";		

			
			for (Sound mySound2 : this.sounds) {
				String suchSound2 = mySound2.getName();

				double vereinigung = (double) mySound1.getWordCollection().size() + (double) mySound2.getWordCollection().size() - (double) mySound1.getDistinctivePairs().get(suchSound2);
				double jkoeff = ((double) mySound1.getDistinctivePairs().get(suchSound2)+1.0) / vereinigung;
				
				if (jkoeff < 0.005009354196776765/4) {
					jkoeff = 0.0000000000;
				}

				jaccVector[i] = jkoeff;
				durchSchnittJKoeff += jkoeff;
				i++;
				
				DecimalFormat df = new DecimalFormat("0.0000000000"); 
				output += "\t" + df.format(jkoeff).replaceAll(",", ".");

			}
			text.append(output.trim()+"\r\n");
			text2.append(output.trim()+ "\t"+suchSound1+"\r\n");

			jaccVectors.put(suchSound1, jaccVector);
	
		}
		
		
		
		durchSchnittJKoeff = durchSchnittJKoeff/(double) (sounds.size() * sounds.size());
		double threshold = durchSchnittJKoeff / 4.0;

		/*
		 * integrates the threshold
		 */
 
		Pattern pattern = Pattern.compile("(\\d\\.\\d+)(\\s)");
		String header = text.toString().split("\r")[0];
		Matcher matcher = pattern.matcher(text);
		StringBuilder neu = new StringBuilder(header+"\r\n");
		while(matcher.find()) {
			double zahl = Double.parseDouble(matcher.group(1));
			if (zahl< threshold) {
				neu.append("0.0000000000" + matcher.group(2));
			} else {
				DecimalFormat df = new DecimalFormat("0.0000000000");
				neu.append(df.format(zahl).replaceAll(",", ".") + matcher.group(2));
			}
		}
		text = neu;
		
		Pattern pattern2 = Pattern.compile("(\\d+\\.\\d+)(\\t([^.]+\\r\\n)?)");
		Matcher matcher2 = pattern2.matcher(text2);
		StringBuilder neu2 = new StringBuilder();
		while(matcher2.find()) {
			double zahl = Double.parseDouble(matcher2.group(1));
			if (zahl< threshold) {
				neu2.append("0.0000000000" + matcher2.group(2));
			} else {
				DecimalFormat df = new DecimalFormat("0.0000000000");
				neu2.append(df.format(zahl).replaceAll(",", ".") + matcher2.group(2));
			}
		}

		text2 = neu2;
		
		//for R, IPA should be replaced by XSAMPA to avoid character errors
		String textR = new String(text).replaceAll("ɪ", "I").replaceAll("ɐ","6").replaceAll("ɛ","E").replaceAll("ç","C").replaceAll("ʊ","U").replaceAll("ə","@").replaceAll("ʏ","Y").replaceAll("ɔ","O").replaceAll("ʃ","S").replaceAll("ŋ","N").replaceAll("ø","2").replaceAll("œ","9").replaceAll("ʒ","Z").replaceAll("ʎ","l");

		try {
			if (!new File(".\\data").exists()) {
				new File(".\\data").mkdir();
			}
			if (!new File(".\\data\\transData").exists()) {
				new File(".\\data\\transData").mkdir();
			}
			if (!new File(".\\data\\transData\\distinctive").exists()) {
				new File(".\\data\\transData\\distinctive").mkdir();
			}
			if (!new File(".\\data\\transData\\distinctive\\Clustering").exists()) {
				new File(".\\data\\transData\\distinctive\\Clustering").mkdir();
			}
			BufferedWriter bw2 = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(".\\data\\transData\\distinctive\\Clustering\\DistChainsMatrix.txt"), UTF_8));
			BufferedWriter bw3 = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(".\\data\\transData\\distinctive\\Clustering\\DistChainsMatrixR.txt"), UTF_8));

			bw2.write(text2.toString());
			bw3.write(textR.toString());
	
			bw2.close();
			bw3.close();
		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
		
		/*
		 * Start Clustering
		 */

		try {
			Dataset data = FileHandler.loadDataset(new File(".\\data\\transData\\distinctive\\Clustering\\DistChainsMatrix.txt"), sounds.size(), "\t");
			
			 /*
			  *  Normalization
			  */
			 
			NormalizeMidrange nmr=new NormalizeMidrange(0.5,1);
			nmr.build(data);
			 
			Instance rgB=InstanceTools.randomInstance(5);
			nmr.filter(rgB);


			/*
			 *  the maximal number of clusters should be mySounds.size
			 *  but this takes too much time
			 */

			int optimalCluster = k;
			if (k == 0) {
				optimalCluster = elbowMethod(5,data);
			} 
			Clusterer km = new KMeans(optimalCluster);
			Dataset[] clusters = km.cluster(data);
			
			HashMap<String,ArrayList<String>> result = new HashMap<String,ArrayList<String>>();
			for(int c = 0 ; c < clusters.length ; c++) {
				Dataset cluster = clusters[c];
				ArrayList<String> elementOfCluster = new ArrayList<String>();
				for (int i = 0 ; i < cluster.size() ; i++) {
					String unit = cluster.get(i).toString();
					if (unit.contains(";")) {
						unit = unit.split(";")[1].replaceAll("}", "");
					}
					elementOfCluster.add(unit);
				}
				result.put(Integer.toString(c),elementOfCluster);
			}

			return result;

		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
		return null;
		
	}

	/**
	 * which sound classes exist among the sounds
	 * reads the file Merkmalsphonetik_IPA
	 * @return a map: {sound class -> [sounds, ...]}
	 */
	public HashMap<String,ArrayList<String>> soundClasses() {
		HashMap<String,ArrayList<String>> allFeat = new HashMap<String,ArrayList<String>>(); //Feature -> a,b,c
		HashMap<String,List<String>> allIPA = new HashMap<String,List<String>>();
		ArrayList<String> soundNames = new ArrayList<String>();
		for (Sound s : this.sounds) {
			soundNames.add(s.getName());
		}
		try {
			BufferedReader br = new BufferedReader( new InputStreamReader ( new FileInputStream(".\\data\\rawData\\Merkmalsphonetik\\Distinktiv\\Merkmalsphonetik_IPA"), UTF_8));

			String line = br.readLine();
			while (line != null) {
				allIPA.put(line.split("\t")[0],Arrays.asList(line.replaceAll("^.*?\t", "").split("\t")));
				line = br.readLine();
			}
			
			br.close();
			
		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
		
		/*
		 * add sounds with diacratics
		 */
		for (String a : soundNames) {
			if (!allIPA.containsKey(a)) {
				List<String> features = new ArrayList<String>();
				for (char an : a.toCharArray()) {
					String str = Character.toString(an);
					if (allIPA.containsKey(str)) {
						features.addAll(allIPA.get(str));
					}
				}
				allIPA.put(a, features);
			}
		}
		
		for (String sound : soundNames) {
			for (String feat : allIPA.get(sound)) {
				if (allFeat.containsKey(feat)) {
					ArrayList<String> myFeat = allFeat.get(feat);
					if (!myFeat.contains(sound))
						myFeat.add(sound);
					allFeat.put(feat, myFeat);
				} else {
					ArrayList<String> myFeat = new ArrayList<String>();
					myFeat.add(sound);
					allFeat.put(feat, myFeat);
				}
			}
		}
		
		/*
		 * exclude the features consonant, vowel, voiced and voiceless
		 */
		allFeat.remove("konsonant");
		allFeat.remove("vokal");
		allFeat.remove("stimmhaft");
		allFeat.remove("stimmlos");
		
		return allFeat;
	}
	

	/**
	 * implements the elbow method of the clustering
	 * @param maxClusters
	 * @param data
	 * @return the sharpest point +1
	 */
	public static int elbowMethod(int maxClusters, Dataset myData) {
		try {
			double[] dfdf = new double[maxClusters];
			for (int k = 1; k <= maxClusters; k++) {
				
				Dataset[] clusters = new KMeans(k).cluster(myData);
				ClusterEvaluation eval = new SumOfSquaredErrors();
	
				double score = eval.score(clusters);	
				dfdf[k-1] = score;
			}
		
			double sharpestValue = 0.0;
			int sharpePoint = 0;
			for (int s = 1; s < dfdf.length-1; s++) {
				double diff2Prev = dfdf[s] - dfdf[s-1]; //should be high
				double diff2Next = dfdf[s] - dfdf[s+1]; //should be low
				if (diff2Prev < 0.0) 
					diff2Prev = diff2Prev*-1.0;
				if (diff2Next < 0.0) 
					diff2Next = diff2Next*-1.0;
				
				double diff = diff2Prev-diff2Next;
				if (diff > sharpestValue) {
					sharpestValue = diff;
					sharpePoint = s;
					
				}
				
			}
			return sharpePoint+1; //because [0,1,2,...]
		} catch (ArrayIndexOutOfBoundsException e) {
			Logging.error(e.getLocalizedMessage());
		}
		return 2; //standard if something went wrong
	}

	
}
