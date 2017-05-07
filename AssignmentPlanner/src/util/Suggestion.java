package util;

import java.time.LocalDate;


/* This is a DTO (Data Transfer Object)
 * perhaps in the future this could be extened to make for specific suggestions
 */
public class Suggestion {
	
	private String name;
	private LocalDate date;
	private Long id;
	
	public String getName() {
		return name;
	}
	public LocalDate getDate() {
		return date;
	}
	public Long getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
}
