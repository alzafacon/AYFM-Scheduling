package io.fidelcoria.ayfmPlanner.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

import javax.persistence.*;

import io.fidelcoria.ayfmPlanner.util.Assignment_t;
import io.fidelcoria.ayfmPlanner.util.Role;
import io.fidelcoria.ayfmPlanner.util.Section;

/**
 * The persistent class for the assignment database table.
 * 
 */
@Entity
@Table(
	indexes= {
		@Index(name="week_ix", columnList="week"),
		@Index(name="assgn_fk_person_id_ix", columnList="assignee"),
		@Index(name="assgn_fk_person_householder_ix", columnList="householder")
	}
)
public class Assignment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private int id;

	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date week;
	
	@Column(nullable=false)
	private int classroom;

	@Column(nullable=false)
	private int type;
	
	private int lesson;
	
	@Column(name="is_completed")
	private Boolean isCompleted;

	@Column(name="is_passed")
	private Boolean isPassed;

	// many-to-one association to Person
	@ManyToOne(cascade=CascadeType.REMOVE) // TODO: test the deletion
	@JoinColumn(name="assignee", nullable=false)
	private Person assignee;

	// many-to-one association to Person
	@ManyToOne(cascade=CascadeType.REMOVE)
	@JoinColumn(name="householder")
	private Person householder;

	public Assignment() {
	}
	
	public Assignment(LocalDate date, Assignment_t assignmentType, Section section) { 
		setWeek(date);
		setAssignmentType(assignmentType);
		setClassroom(section); 
	}

	public int getId() {
		return this.id;
	}
	
	// No setter for id

	public Section getClassroom() {
		return Section.get(this.classroom);
	}

	public void setClassroom(Section classroom) {
		this.classroom = classroom.toInt();
	}

	public Boolean isCompleted() {
		return this.isCompleted;
	}

	public void setCompleted(boolean completed) {
		this.isCompleted = completed;
	}

	public int getLesson() {
		return this.lesson;
	}

	public void setLesson(int lesson) {
		this.lesson = lesson;
	}

	public Boolean isPassed() {
		return this.isPassed;
	}

	public void setPassed(boolean passed) {
		this.isPassed = passed;
	}

	public Assignment_t getAssignmentType() {
		return Assignment_t.get(this.type);
	}

	public void setAssignmentType(Assignment_t type) {
		this.type = type.toInt();
	}

	public LocalDate getWeek() {
		if (this.week == null) {
			return null;
		}
		// in case java.sql is not available for some reason:  
		//  return week.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();  
		return new java.sql.Date(this.week.getTime()).toLocalDate(); 
	}

	public void setWeek(LocalDate week) {
		// in case java.sql is not available for some reason:  
		//   this.week = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());  
		this.week = java.sql.Date.valueOf(week);  
	}

	public Person getAssignee() {
		return this.assignee;
	}

	public void setAssignee(Person assignee) {
		this.assignee = assignee;
	}

	public Person getHouseholder() {
		return this.householder;
	}

	public void setHouseholder(Person householder) {
		this.householder = householder;
	}

	/**
	 * @param role Assignee or Householder roles
	 * @return Person in role possibly null, null for unexpected role also.
	 */
	public Person getPersonInRole(Role role) {

		switch (role) {
		case ASSIGNEE:
			return getAssignee();
			
		case HOUSEHOLDER:
			return getHouseholder();

		default:
			return null;
		}

	}
	
	/**
	 * Equality of Assignments does not include `this.id` because Hibernate
	 * fiddles around with it and can cause issues. 
	 * `this.isPassed`, `this.isCompleted` also not checked.
	 * Equality does include Week, Assignee, Householder, classroom, type, and lesson.
	 * Declared `final` because casting is guarded by `instanceof`.
	 * If there is an extended class the overriding .equals(Object) 
	 * implementation is not guaranteed to play nice with this implementation. 
	 */
	@Override
	final public boolean equals(Object obj) {
		
		if (this == obj) { // self check
			return true;
		}
		if (obj == null || !(obj instanceof Assignment)) {
			return false;
		}
		
		Assignment other = (Assignment) obj;
		
		// week, assignee, householder
		// Objects.equals() takes care of checking for null
		if (!Objects.equals(this.getWeek(), other.getWeek())
			|| !Objects.equals(this.getAssignee(), other.getAssignee())
			|| !Objects.equals(this.getHouseholder(), other.getHouseholder())) {
			return false;
		}
	
		// classroom, type, lesson
		if (this.getClassroom() != other.getClassroom()
			|| this.getAssignmentType() != other.getAssignmentType()
			|| this.getLesson() != other.getLesson()) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Since .equals() is overridden hashCode is also overridden.
	 * Not all members are included in the .equals() check but the
	 * default hashCode will include all members.
	 * This implementation only hashes immutable objects (which are
	 * a subset of the members checked in .equals() and provide a reasonably
	 * unique set of values for different Assignments and thus a good hash).
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.getWeek(), this.getClassroom(), this.getAssignmentType());
	}
	
	@Override
	public String toString() {
		StringBuilder strBld = new StringBuilder();
		
		strBld.append("date: ");
		if (week != null) {
			strBld.append(week);
			strBld.append(" ");
		}
		
		strBld.append("type: ");
		strBld.append(Assignment_t.get(type));
		strBld.append(" ");
		
		strBld.append("assignee: ");
		if (assignee != null) {
			strBld.append(assignee.getFullName());
			strBld.append(" ");
		}
		
		strBld.append("householder: ");
		if (householder != null) {
			strBld.append(householder.getFullName());
			strBld.append(" ");
		}
		
		strBld.append("class: ");
		if (getClassroom() != null) {
			strBld.append(getClassroom().toString());
			strBld.append(" ");
		}
		
		strBld.append("lesson: ");
		strBld.append(lesson);
		
		return strBld.toString();
	}

}