package entities;

import java.time.LocalDate;

// Persistence Object for Assignments
public class Assignment {
	
	public Assignment() {
		id = null;
		assignee = "";
		householder = "";
	}
	
	public Assignment(LocalDate date, Assgn_t type, String section) {
		this();
		
		this.date = date;
		this.type = type;
		this.section = section;		
	}
	
	//	GETTERS
	public Long getId() {
		return id;
	}
	public String getSection() {
		return section;
	}
	public Assgn_t getType() {
		return type;
	}
	public String getAssignee() {
		return assignee;
	}
	public String getHouseholder() {
		return householder;
	}
	public int getLesson() {
		return lesson;
	}
	public boolean isPassed() {
		return passed;
	}
	public LocalDate getDate() {
		return date;
	}
	public boolean isCompleted() {
		return completed;
	}
	
	
	//	SETTERS
	public void setId(Long id) {
		this.id = id;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public void setType(Assgn_t type) {
		this.type = type;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public void setHouseholder(String householder) {
		this.householder = householder;
	}
	public void setLesson(int lesson) {
		this.lesson = lesson;
	}
	public void setPassed(boolean passed) {
		this.passed = passed;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	Long id;
	
	LocalDate date;
	Assgn_t type;
	String section;
	
	String assignee;
	String householder;
	
	int lesson;
	boolean passed;
	
	boolean completed;
	
	public String toString() {
		return String.format("Date: %s type: %d sec: %s publr: %s hhold: %s", date.toString(), type.toInt(), section, assignee, householder);
	}
}
