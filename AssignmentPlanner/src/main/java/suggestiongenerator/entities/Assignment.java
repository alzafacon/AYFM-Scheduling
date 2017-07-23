package suggestiongenerator.entities;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.*;

import util.Assignment_t;
import util.Section;

import java.util.Date;


/**
 * The persistent class for the assignment database table.
 * 
 */
@Entity
@Table(name="assignment")
@NamedQuery(name="Assignment.findAll", query="SELECT a FROM Assignment a")
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
	private boolean isCompleted;

	@Column(name="is_passed")
	private boolean isPassed;

	//many-to-one association to Person
	@ManyToOne(cascade=CascadeType.REMOVE)
	@JoinColumn(name="assignee", nullable=false)
	private Person assignee;

	//bi-directional many-to-one association to Person
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

	public boolean isCompleted() {
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

	public boolean isPassed() {
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
	
	// TODO: handle null householder elegantly; null pointers...
	@Override
	public String toString() {
		return String.format("date: %s assignee: %s householder: %s class: %d, lesson: %d", 
				week, assignee.getFullName(), householder, classroom, lesson);
	}

}