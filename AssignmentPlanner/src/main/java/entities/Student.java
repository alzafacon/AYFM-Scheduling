package entities;

public class Student {
	
	long id;

	String fullName;
	
	char gender;
	
	boolean active;
	
	public Student(long id, String full_name, char gender, boolean active) {
		this.id = id;
		this.fullName = full_name;
		this.gender = gender;
		this.active = active;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String full_name) {
		this.fullName = full_name;
	}

	public char getGender() {
		return gender;
	}

	public void setGender(char gender) {
		this.gender = gender;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
