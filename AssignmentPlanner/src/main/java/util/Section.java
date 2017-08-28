package util;

/**
 * Enum for the classrooms where assignments can take place.
 * @author FidelCoria
 *
 */
public enum Section {
	
	UNKNOWN(0, "Unknown"),
	A(1, "a"), B(2, "b"), C(3, "c");
	
	private final int number;
	private final String name;
	
	/**
	 * Enum value constructor
	 * @param number integer representation
	 * @param name name of class room
	 */
	private Section(int number, String name) {
		this.number = number;
		this.name = name;
	}
	
	/**
	 * Convert from integer to a Section Enum object.
	 * @param section number to convert
	 * @return Section Enum Object corresponding to argument
	 */
	public static Section get(int section) {
		switch (section) {
		case 1:
			return Section.A;
		case 2:
			return Section.B;
		case 3:
			return Section.C;
		default:
			return Section.UNKNOWN;
		}
	}
	
	/**
	 * Convert from string (section name) to a Section Enum object.
	 * @param section name to convert
	 * @return Section Enum Object corresponding to argument
	 */
	public static Section get(String section) {
		switch (section.toLowerCase()) {
		case "a":
			return Section.A;
		case "b":
			return Section.B;
		case "c":
			return Section.C;
		default:
			return Section.UNKNOWN;
		}
	}
	
	/**
	 * Accessor to integer representation of Section object.
	 * @return integer value of Section
	 */
	public int toInt() {
		return number;
	}
	
	/**
	 * Accessor to string representation of Section object.
	 * @return string value of section
	 */
	@Override
	public String toString() {
		return name;
	}
}
