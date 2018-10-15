package io.fidelcoria.ayfmap.util;

/**
 * Enum for types of Assignments to be assigned.
 * @author FidelCoria
 *
 */
public enum Assignment_t {
	UNKNOWN(0, "Unknown"),
	READING(1, "Reading"),
	INIT_CALL(2, "Initial Call"), 
	FST_RET_VIS(3, "First Return Visit"),
	SND_RET_VIS(4, "Second Return Visit"), 
	BIBLE_STUDY(5, "Bible Study"), 
	TALK(6, "Talk");
	
	private final int value;
	private final String string;
	
	/**
	 * Enum value constructor
	 * @param v integer value of type
	 */
	private Assignment_t(int v, String s) {
		this.value = v;
		this.string = s;
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
			return INIT_CALL;
			
		case 3:
			return FST_RET_VIS;
		
		case 4:
			return SND_RET_VIS;
			
		case 5:
			return BIBLE_STUDY;
			
		case 6:
			return TALK;
			
		default:
			return UNKNOWN;
		}
	}
	
	// UI uses toString to display in choiceBox
	@Override
	public String toString() {
		return string;
	}
}
