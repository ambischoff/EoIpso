package Helper.Measures;

/**
 * implements the Needleman-Wunsch algorithm
 * 
 *
 */
public class NeedlemanWunschAlgorithm {
	
	
	private String sequence1;
	private String sequence2;
	private float[][] matrix;
	private float gap;
	private float score;
	
	private char[] alignSequence1;
	private char[] alignSequence2;
	
	/*
	 * values of the starting cell for traceback
	 */
	private int startTracebackRow = 0;
	private int startTracebackColumn = 0;
	private float startTracebackScore = 0;

	/**
	 * 
	 * @param sequence1	first word
	 * @param sequence2	second word
	 */
    public NeedlemanWunschAlgorithm(String sequence1, String sequence2) {
    	this.sequence1 = sequence1;
    	this.sequence2 = sequence2;
    }

    /**
     * initializes the algorithm
     * @param matrix	matrix as float[][]
     * @param gap		open gap penalty
     */
    public void start(float[][] matrix, float gap) {

    	this.matrix = matrix;
    	this.gap = gap;
        byte[][] pointerMatrix = new byte[this.sequence1.length() + 1][this.sequence2.length() + 1];

        pointerMatrix[0][0] = 0;
        for (int i = 1; i < this.sequence1.length() + 1; i++) {
            pointerMatrix[i][0] = 3;
        }
        for (int j = 1; j < this.sequence2.length() + 1; j++) {
            pointerMatrix[0][j] = 1;
        }

        this.makeDirectionsMatrix(pointerMatrix);
        this.startTraceback(pointerMatrix);

    }
    
    public char[] getAlignedSequence1() {
    	return this.alignSequence1;
    }
    
    public char[] getAlignedSequence2() {
    	return this.alignSequence2;
    }

    /**
     * makes a directions matrix and initializes the starting point for traceback
     * 
     * @param pointerMatrix
     *            traceback matrix
     */
    private void makeDirectionsMatrix(byte[][] pointerMatrix) {

        float[] w = new float[this.sequence2.length() + 1];

        for (int j = 1; j < w.length; j++) {
            w[j] = j * - this.gap;
        }
        w[0] = 0;

        float x = 0.0f;
        float y = 0.0f;
        float z = 0.0f;
        float wOld = 0.0f;

        for (int i = 1; i < this.sequence1.length() + 1; i++) {
            w[0] = i * -this.gap;
            for (int j = 1; j < this.sequence2.length() + 1; j++) {
                x = w[j] - this.gap;
                y = w[j - 1] - this.gap;
                z = wOld + this.matrix[this.sequence1.toCharArray()[i - 1]][this.sequence2.toCharArray()[j - 1]];

                wOld = w[j];
                w[j] = Math.max(Math.max(y, z), x);

                // for the direction
                if (w[j] == x) {
                    pointerMatrix[i][j] = 3;
                } else if (w[j] == y) {
                    pointerMatrix[i][j] = 1;
                } else {
                    pointerMatrix[i][j] = 2;
                }

            }

            wOld = i * -this.gap;
        } 

        //traceback starts at the end of the matrix
        this.startTracebackRow = this.sequence1.length();
        this.startTracebackColumn = this.sequence2.length();
        this.startTracebackScore = w[this.sequence2.length()];
    }
    
    /**
     * Getter for the score
     * @return	score as float
     */
    public float getScore() {
    	return this.score;
    }

    /**
     * generates reversed arrays of the sequences and starts the aligment
     * 
     * @param pointers	traceback matrix
     */
    private void startTraceback(byte[][] pointers) {
        char[] seq1 = this.sequence1.toCharArray();
        char[] seq2 = this.sequence2.toCharArray();
        char letterSeq1;
        char letterSeq2;

        char[] revSequence1 = new char[this.sequence1.length() + this.sequence2.length()]; 
        char[] revSequence2 = new char[this.sequence1.length() + this.sequence2.length()]; 

        int length1 = 0; 
        int length2 = 0; 

        int i = this.startTracebackRow;
        int j = this.startTracebackColumn;


        boolean reachEnd = false;
        while (!reachEnd) {
            
            if (pointers[i][j] == 3) {
            	revSequence1[length1++] = seq1[--i];
            	revSequence2[length2++] = '-';
            } else if (pointers[i][j] == 2) {
            	letterSeq1 = seq1[--i];
            	letterSeq2 = seq2[--j];
                revSequence1[length1++] = letterSeq1;
                revSequence2[length2++] = letterSeq2;
            } else  if (pointers[i][j] == 1) {
            	revSequence1[length1++] = '-';
            	revSequence2[length2++] = seq2[--j];
            } else  if (pointers[i][j] == 0) {
                reachEnd = true;
            }
        }

        this.score = this.startTracebackScore;
        
        char[] turnedSeq1 = new char[length1];
        for (int it1 = length1 - 1, it2 = 0; it1 >= 0; it1--, it2++) {
           turnedSeq1[it2] = revSequence1[it1];
        }
        char[] turnedSeq2 = new char[length2];
        for (int it1 = length1 - 1, it2 = 0; it1 >= 0; it1--, it2++) {
           turnedSeq2[it2] = revSequence2[it1];
        }
        
        this.align(this.startTracebackScore, turnedSeq1,turnedSeq2,i,j);

    }
    
    /**
     * calculates the score of the algorithm
     * 
     * @param score		starting score
     * @param revSeq1	reversed sequence1
     * @param revSeq2	reversed sequence2
     * @param start1	starting point 1
     * @param start2	starting point 2
     */
    private void align(float score, char[] revSeq1, char[] revSeq2, int start1, int start2) {
    	
		float newScore = 0.0f;

		boolean thereWasAGap1 = false;
		boolean thereWasAGap2 = false;

		char sound1;
		char sound2;
		for (int i = 0; i <= revSeq1.length - 1; i++) {
			sound1 = revSeq1[i];
			sound2 = revSeq2[i];

			if (sound1 == '-') { //a gap?
				if (thereWasAGap1) {
					newScore -= this.gap;
				} else {
					newScore -= this.gap;
				}
				thereWasAGap1 = true;
				thereWasAGap2 = false;
			} else if (sound2 == '-') { //a gap?
				if (thereWasAGap2) {
					newScore -= this.gap;
				} else {
					newScore -= this.gap;
				}
				thereWasAGap1 = false;
				thereWasAGap2 = true;
			} else {
				newScore += matrix[sound1][sound2];
				thereWasAGap1 = false;
				thereWasAGap2 = false;
			}
		}
		
        this.alignSequence1 = revSeq1;
        this.alignSequence2 = revSeq2;
		
    	this.score = newScore;
    }

}

