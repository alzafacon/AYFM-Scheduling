package io.fidelcoria.ayfmap.fx.control;

import java.io.IOException;
import java.time.Month;
import java.util.ArrayList;

import io.fidelcoria.ayfmap.domain.Assignment;
import io.fidelcoria.ayfmap.domain.Person;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Custom JavaFX control for input of a schedule week
 * @author FidelCoria
 *
 */
public class WeekForm extends VBox {
	
	@FXML
	Label weekDate;
	
	private ObservableList<Person> ps;
	
	private final static int MAX_ASSGNS_PER_WEEK = 4;
	
	public WeekForm(ObservableList<Person> ps) {
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("WeekForm.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this); // needed for injection
		
		this.ps = ps;
		
		try {
			// injection of the annotated class members is done by load
			fxmlLoader.load();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public void initialize() {
		
		for (int i = 0; i < MAX_ASSGNS_PER_WEEK; i++) {
			this.getChildren().add(new AssignmentRowForm(ps));	
		}	
	}
	
	public void setWeekDate(String date) {
		weekDate.setText(date);
	}
	
	public String getWeekDate() {
		return weekDate.getText();
	}

	public ArrayList<Assignment> getAllAssgns(int weekNum, Month month, Integer year) {
		
		ArrayList<Assignment> assgns = new ArrayList<>();
		
		int size = this.getChildren().size();
		
		// skip index 0; it's a label
		for (Node node : this.getChildren().subList(1, size)) {
			AssignmentRowForm arf = (AssignmentRowForm) node;
			
			assgns.addAll(arf.getAllAssgns(weekNum, month, year));
		}
		
		return assgns;
	}
}
