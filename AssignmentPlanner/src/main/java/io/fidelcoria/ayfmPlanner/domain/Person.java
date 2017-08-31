package io.fidelcoria.ayfmPlanner.domain;

import java.io.Serializable;
import javax.persistence.*;

import io.fidelcoria.ayfmPlanner.util.Assignment_t;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * The persistent class for the person database table.
 * 
 */
@Entity
@Table(
	uniqueConstraints={
		@UniqueConstraint(columnNames= {"last_name", "first_name"})
	},
	indexes={@Index(name="full_name_Ix", columnList = "first_name,last_name")}
)

public class Person implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private int id;

	
	@Column(name="first_name", nullable=false, length=45)
	private String firstName;

	@Column(name="last_name", nullable=false, length=45)
	private String lastName;
	
	@Column(nullable=false, length=1)
	private String gender;
	
	@Column(name="is_active", nullable=false)
	private boolean isActive;
	
	// eligibility denormalized into columns as below
	@Column(name="is_eligible_reading", nullable=false)
	private boolean isEligibleReading;
	
	@Column(name="is_eligible_init_call", nullable=false)
	private boolean isEligibleInitCall;

	@Column(name="is_eligible_ret_visit", nullable=false)
	private boolean isEligibleRetVisit;

	@Column(name="is_eligible_bib_study", nullable=false)
	private boolean isEligibleBibStudy;
	
	@Column(name="is_eligible_talk", nullable=false)
	private boolean isEligibleTalk;
	

	public Person() {
	}

	public int getId() {
		return this.id;
	}

	// No setter for id

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullName() {
		return String.format("%s %s", getFirstName(), getLastName());
	}	
	
	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		if ("m".equals(gender) || "f".equals(gender)) {
			this.gender = gender;
		}
	}

	public boolean isActive() {
		return this.isActive;
	}

	public void setActive(boolean isactive) {
		this.isActive = isactive;
	}

	public List<Assignment_t> getEligibility() {
		List<Assignment_t> eligibility = new ArrayList<>();
		
		if (isEligibleReading) {
			eligibility.add(Assignment_t.READING);
		}
		if (isEligibleInitCall) {
			eligibility.add(Assignment_t.INITIAL_CALL);
		}
		if (isEligibleRetVisit) {
			eligibility.add(Assignment_t.RETURN_VISIT);
		}
		if (isEligibleBibStudy) {
			eligibility.add(Assignment_t.BIBLE_STUDY);
		}
		if (isEligibleTalk) {
			eligibility.add(Assignment_t.TALK);
		}
		
		return eligibility;
	}
	
	public boolean isEligibleReading() {
		return this.isEligibleReading;
	}

	public void setEligibleReading(boolean isEligibleReading) {
		this.isEligibleReading = isEligibleReading;
	}
	
	public boolean isEligibleInitCall() {
		return this.isEligibleInitCall;
	}

	public void setEligibleInitCall(boolean isEligibleInitCall) {
		this.isEligibleInitCall = isEligibleInitCall;
	}

	public boolean isEligibleRetVisit() {
		return this.isEligibleRetVisit;
	}

	public void setEligibleRetVisit(boolean isEligibleRetVisit) {
		this.isEligibleRetVisit = isEligibleRetVisit;
	}
	
	public boolean isEligibleBibStudy() {
		return this.isEligibleBibStudy;
	}

	public void setEligibleBibStudy(boolean isEligibleBibStudy) {
		this.isEligibleBibStudy = isEligibleBibStudy;
	}

	public boolean isEligibleTalk() {
		return this.isEligibleTalk;
	}

	public void setEligibleTalk(boolean isEligibleTalk) {
		this.isEligibleTalk = isEligibleTalk;
	}
	
	/**
	 * Equality only checked on firstName, lastName, and gender
	 * Declared `final` because casting is guarded by `instanceof`.
	 * If there is an extended class the overriding .equals(Object) 
	 * implementation is not guaranteed to play nice with this implementation. 
	 */
	@Override
	final public boolean equals(Object obj) {
		
		if (this == obj) { // self check
			return true;
		}
		if (obj == null || !(obj instanceof Person)) {
			return false;
		}
		
		Person other = (Person) obj;
		
		return Objects.equals(this.firstName, other.firstName) 
			&& Objects.equals(this.lastName, other.lastName)
			&& Objects.equals(this.gender, other.gender);
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
		return Objects.hash(firstName, lastName, gender);
	}
}
