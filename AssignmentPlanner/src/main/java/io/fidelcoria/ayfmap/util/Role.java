package io.fidelcoria.ayfmap.util;

/**
 * Assignment Roles come in two flavors, assignee and householder.
 * This enum is just syntactic sugar.
 * @author FidelCoria
 *
 */
public enum Role {
	ASSIGNEE("assignee"), HOUSEHOLDER("householder");
	
	private final String value;
	
	/**
	 * Enum value constructor
	 * @param v name of role
	 */
	private Role(String v) {
		this.value = v;
	}
	
	/**
	 * @return value string representation 
	 */
	@Override
	public String toString() {
		return value;
	}
}
