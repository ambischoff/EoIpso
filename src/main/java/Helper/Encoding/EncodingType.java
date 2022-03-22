package Helper.Encoding;

/**
 * interface for Encodings
 * 
 * @author abischoff
 *
 */
public interface EncodingType {
	String toString();
	default boolean is(Encoding e) {
		if (this.toString().contentEquals(e.toString())) {
			return true;
		}
		return false;
	};
}