package util;

public enum Section {
	
	UNKNOWN(0, null),
	A(1, "a"), B(2, "b");
	
	private final int value;
	private final String name;
	
	private Section(int value, String name) {
		this.value = value;
		this.name = name;
	}
	
	
	public static Section get(int section) {
		switch (section) {
		case 1:
			return Section.A;
		case 2:
			return Section.B;
		default:
			return Section.UNKNOWN;
		}
	}
	
	public static Section get(String section) {
		switch (section) {
		case "a":
			return Section.A;
		case "b":
			return Section.B;
		default:
			return Section.UNKNOWN;
		}
	}
	
	public int toInt() {
		return value;
	}
	
	public String toString() {
		return name;
	}
}
