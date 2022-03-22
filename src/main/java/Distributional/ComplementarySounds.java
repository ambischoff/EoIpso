package Distributional;

/**
 * 
 * enum of all methods that are used to determine the complementary sound
 * 
 * @author abischoff
 *
 */
public enum ComplementarySounds {
	hoenigswald { 
		public String toString() {return "Hoenigswald's Principle";};
	}, chafe { 
		public String toString() {return "Chafe's Principle";};
	}, subtypological{ 
		public String toString() {return "Subtypological Approach";};
	}, dolgopolsky { 
		public String toString() {return "Dolgopolsky's Principle";};
	}, condition { 
		public String toString() {return "Condition Principle";};
	}, brown { 
		public String toString() {return "Empirical-Probabilistic Approach";};
	}, phonotactical{ 
		public String toString() {return "Phonotactical Approach";};
	}
}