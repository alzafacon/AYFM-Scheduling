package util;

/**
 * Enum for types of Assignments to be assigned.
 * @author FidelCoria
 *
 */
public enum Assignment_t {
	UNKNOWN(0),
	READING(1), INITIAL_CALL(2), RETURN_VISIT(3), BIBLE_STUDY(4), TALK(5);
	
	private final int value;
	
	/**
	 * Enum value constructor
	 * @param v integer value of type
	 */
	private Assignment_t(int v) {
		this.value = v;
	}
	
	/**
	 * Accessor for integer value of Enum value.
	 * @return
	 */
	public int toInt() {
		return value;
	}
	
	/**
	 * Convert from integer to Enum type
	 * @param i integer to convert
	 * @return Assignment_t corresponding to the integer parameter
	 */
	static public Assignment_t get(int i){
		switch (i) {
		case 1:
			return READING;
		
		case 2:
			return INITIAL_CALL;
			
		case 3:
			return RETURN_VISIT;
		
		case 4:
			return BIBLE_STUDY;
			
		case 5:
			return TALK;
			
		default:
			return UNKNOWN;
		}
	}
}
