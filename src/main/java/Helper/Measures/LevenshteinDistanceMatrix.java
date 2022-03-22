package Helper.Measures;

/**
 * 
 * implements a Levenshtein Distance Matrix to get the sound correspondences
 *
 */
public class LevenshteinDistanceMatrix {

	private int levenshteinDistance = -1;
	
	public LevenshteinDistanceMatrix() {}

	/**
	 * Getter for the Levenshtein Distance of two strings
	 * @param a	string a
	 * @param b	strinb b
	 * @return	the Levenshein distance as integer
	 */
	public int getLevenshteinDistance(String a, String b) {
		if (this.levenshteinDistance == -1) {
			this.getCorrespondence(a, b);
		} 
		
		return this.levenshteinDistance;
	}
	
		/**
		 * returns the sound correspondences of two strings 
		 * @param w1	first string
		 * @param w2	second string
		 * @return	array of strings. The correspondences are separated by "|". 
		 */
	  public String[] getCorrespondence(String w1, String w2) {
		  
		  if (w1.contentEquals("") || w2.contentEquals("")) {
			  return new String[]{};
		  }
		  
		  int len1= w1.length(); 
		  int len2 = w2.length();   
		  int matrix[][] = new int[len1+1][len2+1];
		 
		  String[] word = new String[len1];
		  if (len1 < len2) {
		    word = new String[len2];
		  } 
		  
		  int cost; 
		  
		  for (int i = 0; i <= len1; i++) {
		      matrix[i][0] = i;
		  }
	
		  for (int j = 0; j <= len2; j++) {
		      matrix[0][j] = j;
		  }
	
		  for (int i = 1; i <=len1; i++) {
	
		      char s_i = w1.charAt (i - 1);
	
		      for (int j = 1; j <= len2; j++) {
	
		        char t_j = w2.charAt (j - 1);
	
		        if (s_i == t_j) {
		          cost = 0;
		        }
		        else {
		          cost = 1;
		          
		        }
		        
			    int smallestValue = matrix[i-1][j]+1;
			    if (matrix[i][j-1]+1 < smallestValue) {
			    	smallestValue = matrix[i][j-1]+1;
			    }
			    if (matrix[i-1][j-1] + cost < smallestValue) {
			    	smallestValue = matrix[i-1][j-1] + cost;
			    }
		        
		        matrix[i][j] = smallestValue;
		        
		      }
		      
		    }
		    
		    boolean finish = false; 
		    int countBack = word.length-1;
		    
		    while (!finish) {		    	
			    int mi = matrix[len1][len2];
			    if (len2 != 0 && len1 != 0 && matrix[len1-1][len2] < mi && matrix[len1][len2-1] > mi) {
			    	mi = matrix[len1-1][len2];
			    	len1--;
			    	if (countBack >= 0)
			    		word[countBack] = w1.charAt(len1) + "|" + "0";
	
			    } else if (len2 == 0 && len1 != 0 && matrix[len1-1][len2] < mi) { 
			    	mi = matrix[len1-1][len2];
			    	len1--;
			    	if (countBack >= 0)
			    		word[countBack] = w1.charAt(len1) + "|" + "0";
	
			    } else if (len2 != 0 && len1 == 0 && matrix[len1][len2-1] > mi) {
				      mi = matrix[len1][len2-1];
				      len2--;
				    	if (countBack >= 0)
				    		word[countBack] = "0" + "|" + w2.charAt(len2);
	
			    } else if (matrix[len1][len2-1] < mi) {
			      mi = matrix[len1][len2-1];
			      len2--;
			    	if (countBack >= 0)
			    		word[countBack] = "0" + "|" + w2.charAt(len2);
			    } else {
			    	len1--;
			    	len2--;
			    	if (countBack >= 0)
			    		word[countBack] = w1.charAt(len1) + "|" + w2.charAt(len2);
			    }
		    	countBack--;
		    	
		    	if (countBack < 0 && len1 != 0 && len2 != 0) { //word.length is wrong if both strings have 0 
		    		len1--;
		    		len2--;
		    		String[] word2 = new String[word.length+1];
		    		
		    		word2[0] = w1.charAt(len1) + "|" + w2.charAt(len2);
		    		for (int pp = 1, pq = 0; pp < word2.length ; pp++, pq++) {
		    			word2[pp] = word[pq];
		    		}
		    		
		    		word = word2;
		    		countBack--;
	
		    	}
			    
		    	if ((len1 == 0 && len2 == 0)) {
		    		finish = true;
		    	}
		    	
		    }
		    
		    this.levenshteinDistance = matrix[len1][len2];
	
		    return word;

	  }

		/**
		 * returns the sound correspondences of two string arrays
		 * @param w1	first array
		 * @param w2	second array
		 * @return	array of strings. The correspondences are separated by "|". 
		 */
	  public String[] getCorrespondence(String[] w1, String[] w2) {

		  if (w1.length == 0 || w2.length == 0) {
			  return new String[]{};
		  }
		  
		  int len1= w1.length; 
		  int len2 = w2.length;   
		  int matrix[][] = new int[len1+1][len2+1];
		 
		  String[] word = new String[len1];
		  if (len1 < len2) {
		    word = new String[len2];
		  } 
		  
		  int cost; 

	    for (int i = 0; i <= len1; i++) {
	    	matrix[i][0] = i;
	    }

	    for (int j = 0; j <= len2; j++) {
	      matrix[0][j] = j;
	    }

	    for (int i = 1; i <= len1; i++) {

	      String s_i = w1[i - 1];

	      for (int j = 1; j <= len2; j++) {

	        String t_j = w2[j - 1];

	        if (s_i.equals(t_j)) {
	          cost = 0;
	        }
	        else {
	          cost = 1;
	          
	        }
	        
		    int smallestValue = matrix[i-1][j]+1;
		    if (matrix[i][j-1]+1 < smallestValue) {
		    	smallestValue = matrix[i][j-1]+1;
		    }
		    if (matrix[i-1][j-1] + cost < smallestValue) {
		    	smallestValue = matrix[i-1][j-1] + cost;
		    }
	        
		    matrix[i][j] = smallestValue;
	        
	      }
	      
	    }

	    
	    boolean finish = false; 
	    int countBack = word.length-1;
	    
	    while (!finish) {
	    	
		    int mi = matrix[len1][len2];
		    if (len2 != 0 && len1 != 0 && matrix[len1-1][len2] < mi && matrix[len1][len2-1] > mi) {
		    	mi = matrix[len1-1][len2];
		    	len1--;
		    	if (countBack >= 0)
		    		word[countBack] = w1[len1] + "|" + "0";

		    } else if (len2 == 0 && len1 != 0 && matrix[len1-1][len2] < mi) { 
		    	mi = matrix[len1-1][len2];
		    	len1--;
		    	if (countBack >= 0)
		    		word[countBack] = w1[len1] + "|" + "0";

		    } else if (len2 != 0 && len1 == 0 && matrix[len1][len2-1] > mi) {
			      mi = matrix[len1][len2-1];
			      len2--;
			    	if (countBack >= 0)
			    		word[countBack] = "0" + "|" + w2[len2];

		    } else if (matrix[len1][len2-1] < mi) {
		      mi = matrix[len1][len2-1];
		      len2--;
		    	if (countBack >= 0)
		    		word[countBack] = "0" + "|" + w2[len2];
		    } else {
		    	len1--;
		    	len2--;
		    	if (countBack >= 0)
		    		word[countBack] = w1[len1] + "|" + w2[len2];
		    }
	    	countBack--;
		    
	    	if (countBack < 0 && len1 != 0 && len2 != 0) { 
	    		len1--;
	    		len2--;
	    		String[] word2 = new String[word.length+1];
	    		
	    		word2[0] = w1[len1] + "|" + w2[len2];
	    		for (int pp = 1, pq = 0; pp < word2.length ; pp++, pq++) {
	    			word2[pp] = word[pq];
	    		}
	    		
	    		word = word2;
	    		countBack--;

	    	}
	    	
	    	if ((len1 == 0 && len2 == 0)) {
	    		finish = true;
	    	}
	    	
	    }
	    
	    this.levenshteinDistance = matrix[len1][len2];

	    return word;

	  }

}