package util;

public enum Assignment_t {
	UNKNOWN(0),
	READING(1), INITIAL_CALL(2), RETURN_VISIT(3), BIBLE_STUDY(4), TALK(5);
	
	private final int value;
	
	private Assignment_t(int v) {
		this.value = v;
	}
	
	public int toInt() {
		return value;
	}
	
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
