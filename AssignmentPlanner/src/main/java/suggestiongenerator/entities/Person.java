package suggestiongenerator.entities;

import java.io.Serializable;
import javax.persistence.*;

import util.Assignment_t;

import java.util.ArrayList;
import java.util.List;


/**
 * The persistent class for the person database table.
 * 
 */
@Entity
@Table(name="person")
@NamedQuery(name="Person.findAll", query="SELECT p FROM Person p")
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

}
