package io.fidelcoria.ayfmap.util;

/**
 * Convenience wrapper for Assignment parameters generated 
 * for making suggestions.
 * Immutable. 
 * @author FidelCoria
 *
 */
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
	
	
	@Override
	final public boolean equals(Object obj) {
		
		if (this == obj) { // self check
			return true;
		}
		if (obj == null || !(obj instanceof Progression)) {
			return false;
		}
		
		Progression other = (Progression) obj;
		
		return this.role == other.role 
				&& this.type == other.type 
				&& this.section == other.section;
	}
	
	public final static Progression[] defaultProgression = {
		new Progression(Role.ASSIGNEE, Assignment_t.READING, Section.A),
		new Progression(Role.ASSIGNEE,    Assignment_t.INITIAL_CALL, Section.A),
		new Progression(Role.HOUSEHOLDER, Assignment_t.INITIAL_CALL, Section.A),				
		new Progression(Role.ASSIGNEE,    Assignment_t.RETURN_VISIT, Section.A),
		new Progression(Role.HOUSEHOLDER, Assignment_t.RETURN_VISIT, Section.A),				
		new Progression(Role.ASSIGNEE,    Assignment_t.BIBLE_STUDY, Section.A),
		new Progression(Role.HOUSEHOLDER, Assignment_t.BIBLE_STUDY, Section.A),
		
		new Progression(Role.ASSIGNEE, Assignment_t.READING, Section.B),
		new Progression(Role.ASSIGNEE,    Assignment_t.INITIAL_CALL, Section.B),
		new Progression(Role.HOUSEHOLDER, Assignment_t.INITIAL_CALL, Section.B),				
		new Progression(Role.ASSIGNEE,    Assignment_t.RETURN_VISIT, Section.B),
		new Progression(Role.HOUSEHOLDER, Assignment_t.RETURN_VISIT, Section.B),
		new Progression(Role.ASSIGNEE,    Assignment_t.BIBLE_STUDY, Section.B),
		new Progression(Role.HOUSEHOLDER, Assignment_t.BIBLE_STUDY, Section.B)
	};
}
