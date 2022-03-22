package Helper.Language;

/**
 * Enum of the different methods
 * @author abischoff
 *
 */
public enum Method {
	paradigmatic { 
		public String toString() {return "Paradigmatic Approach";};
	}, 
	derivational { 
		public String toString() {return "Derivational Approach";};
	}, 
	semantic { 
		public String toString() {return "Semantic Approach";};
	}, 
	phonotactic { 
		public String toString() {return "Phonotactic Approach";};
	}, 
	distinctive { 
		public String toString() {return "Distinctive Approach";};
	}, 
	gap { 
		public String toString() {return "Gap Approach";};
	}
}