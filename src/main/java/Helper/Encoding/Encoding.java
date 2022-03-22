package Helper.Encoding;

/**
 * enum for different Encodings
 * @author abischoff
 *
 */
public enum Encoding implements EncodingType {
	ASCII { 
		public String toString() {return "US-ASCII";};
	}, 
	ISO_8859_1{ 
		public String toString() {return "ISO-8859-1";};
	},
	UTF8{ 
		public String toString() {return "UTF-8";};
	}, 	
	UTF8BOM { 
		public String toString() {return "UTF-8";};
	}, 
	UNICODE { 
		public String toString() {return "UTF-16";};
	}, 
	UNICODELE { 
		public String toString() {return "UTF-16LE";};
	}, 
	UNICODEBE { 
		public String toString() {return "UTF-16BE";};
	}, 
	UTF32LE { 
		public String toString() {return "UTF-16";};
	}, 
	UTF32BE { 
		public String toString() {return "UTF-16";};
	};
	
}