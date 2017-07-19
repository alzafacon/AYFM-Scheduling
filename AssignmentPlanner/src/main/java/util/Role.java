package util;

public enum Role {
	ASSIGNEE("assignee"), HOUSEHOLDER("householder");
	
	private final String value;
	
	private Role(String v) {
		this.value = v;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
