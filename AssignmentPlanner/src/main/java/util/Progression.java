package util;

public class Progression {
	Role role;
	Assignment_t type;
	Section section;
	
	public Progression(Role role, Assignment_t type, Section section) {
		this.role = role;
		this.type = type;
		this.section = section;
	}
	
	public Role getRole() {
		return role;
	}

	public Assignment_t getType() {
		return type;
	}

	public Section getSection() {
		return section;
	}
}
