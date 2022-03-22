package Distributional;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import Helper.Log.Logging;

/**
 * 
 * reads the corpus file
 * 
 * @author abischoff
 */

public class ReadCorpus {

	private boolean aroundOn = false; //allows the environment X_Y
	
	//transkription
	private boolean sampa = false;
	private boolean buchstaben = false;

	private String language;
	
	
	public SoundVector actual = null;
	public ArrayList<SoundVector> soundVectors = new ArrayList<SoundVector>();
	public ArrayList<String> soundVectorNames = new ArrayList<String>();
	

	/**
	 * @param file		source file. If it is txt, use it; use Wiktionary else
	 * @param lang		language name
	 * @param encoding	encoding of the source file
	 * @param around	allows the sound environments X_Y
	 * @param ipa		are the data IPA transcribed
	 */
	public ReadCorpus(String file, String lang, String encoding, boolean around, boolean ipa) {
		
		this.aroundOn = around;

		if (ipa) {
			this.buchstaben = false;
		} else {
			this.buchstaben = true;
		}
		
		if (file.contentEquals("")) {
			Logging.error("File not found");
		} else {
			this.readCorpus(file, encoding);
			this.language = lang;
		}
		
		this.serializeData(this.language);
		this.generateOccurrences(this.language);
	}
	

	/**
	 * reads textfiles
	 * @param file	file path
	 * @param encoding encoding of the file
	 */
	public void readCorpus(String file, String encoding) {
		try {

			String dataPath = file;
						
			BufferedReader br2 = new BufferedReader(new InputStreamReader( new FileInputStream(dataPath) , encoding));
			String line2 = br2.readLine();
			
			while (line2 != null) {
				if (buchstaben) {
					line2 = line2.toLowerCase().replaceAll("[^\\p{L}\\s]","").replaceAll("(_|-)", " ");
				}
				readLine(line2);
				line2 = br2.readLine();
			}
			
			br2.close();			

		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
	}
	
	/**
	 * generates the file Occurrences for a language
	 * @param language	the language in question
	 */
	public void generateOccurrences(String language) {
		
		BufferedWriter bw2;
		try {
			bw2 = new BufferedWriter( new OutputStreamWriter( new FileOutputStream("data\\transData\\Occurrences"+language) , "UTF-8"));

			if (SoundVector.getAllConditions() == null) {
				ArrayList<String> allConditons = new ArrayList<String>();
				for (String name : soundVectorNames) {
					allConditons.add(name + "_");
					allConditons.add("_" + name);
					if (this.aroundOn) {
						for (String name2 : soundVectorNames) {
							allConditons.add(name+"_"+name2);
						}
					}
					
				}
			
				SoundVector.setAllConditions(allConditons);
			}

			for (SoundVector sv : this.soundVectors) {
				bw2.write(sv.getName() + "= " + sv.printValues() + "\r\n"); 
			}
			bw2.close();
		
		} catch (IOException e) {
			Logging.error(e.getLocalizedMessage());
		}
		
	}

	/**
	 * reads a line of a file and initialize the sound vectors
	 * @param line
	 */
	public void readLine(String line) {
		
		line = ("# "+line+ " #").replaceAll("\\s+", " ");

		if (buchstaben) {
			line = line.toLowerCase();
		}
		
		if (sampa) {
			line = line.replaceAll("a:", "�").replaceAll("e:", "�").replaceAll("i:", "�").replaceAll("o:", "�").replaceAll("u:", "�")
				.replaceAll("E:", "�").replaceAll("O:", "�").replaceAll("y:", "�").replaceAll("Y:", "�").replaceAll("I:", "�").replaceAll("U:", "�").replaceAll("A:", "�")
				.replaceAll("2:", "�");
		}
		
		String[] sounds = line.split("\\s");
		
		for (int i = 0, beforeInt = -1, afterInt = 1; i < sounds.length ; i++, beforeInt++, afterInt++) {
			// Identify phoneme
			String sound = sounds[i];
			
			if (sound.matches("\\s+")) { sound = "#";}
			if (!this.soundVectorNames.contains(sound)) { //Is the phoneme already initialized?
				SoundVector newSV = new SoundVector(sound);
				this.soundVectorNames.add(sound);
				this.soundVectors.add(newSV);
				this.actual = newSV; 
			} else {
				for (SoundVector sv : this.soundVectors) {
					
					if (sv.getName().equals(sound)) {
						this.actual = sv;
					}
				}
			}
			
			/*
			 * Sound environments
			 */
			if (i != 0) {
				this.actual.add(sounds[beforeInt] +"_");
			}
			if (i != sounds.length -1) {
				this.actual.add("_" +sounds[afterInt]);
			}
			if (this.aroundOn) {
				if (i != 0 && i != sounds.length-1){
					this.actual.add(sounds[beforeInt] + "_" + sounds[afterInt]);
				}
			}
			
		}
		
		
	}
	
	/**
	 * method for serialize soundVectors
	 * @param 	lang	name of the language
	 */
	public void serializeData(String lang) {
			
		OutputStream fos = null;

		try
		{
		  fos = new FileOutputStream( ".\\data\\transData\\soundVectors"+ lang + ".ser" );
		  ObjectOutputStream o = new ObjectOutputStream( fos );

		  o.writeObject(this.soundVectors);
		  o.close();
		  
		} catch ( IOException e ) { 
			Logging.error(e.getLocalizedMessage());
		} finally { 
			try { 
				fos.close(); 
			} catch ( Exception e ) { 
				Logging.error(e.getLocalizedMessage());
			} 
		}
	}
	
	/**
	 * Getter for all generated sound vectors
	 * @return a arraylist of soundVectors
	 */
	public ArrayList<SoundVector> getSoundVectors() {
		return this.soundVectors;
	}
	
	/**
	 * puts the a context for a phon to zero to exclude this context during a iteration round
	 * @param phon		sound
	 * @param context	context to exclude
	 */
	public void excludeContext(String phon, String context) {
		
		for (SoundVector svec : this.soundVectors) {
			if (svec.getName().contentEquals(phon)) {
				svec.put(context,0);
			}
		}
		
		this.serializeData(this.language);
		this.generateOccurrences(this.language);
	}
	

	
	
}
