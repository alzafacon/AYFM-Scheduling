package io.fidelcoria.ayfmap.fx.control.weekForm;

import java.io.IOException;
import java.util.List;

import io.fidelcoria.ayfmap.domain.Person;
import io.fidelcoria.ayfmap.util.Assignment_t;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
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
	
	@FXML
	ChoiceBox<Assignment_t> assgnType;
	@FXML
	ChoiceBox<Integer> lesson;
	@FXML
	ComboBox<String> pubMain; // pub is for publisher
	@FXML
	ComboBox<String> hholdMain;
	@FXML
	ComboBox<String> pubAux;
	@FXML
	ComboBox<String> hholdAux;
	
	public static ObservableList<Person> ps;
	
	public WeekForm() {
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("WeekForm.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this); // needed for injection
		
		try {
			// injection of the annotated class members is done by load
			fxmlLoader.load();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public void initialize() {
		
		for (Assignment_t t : Assignment_t.values()) {
			if (t != Assignment_t.UNKNOWN) {
				assgnType.getItems().add(t);
			}
		}
		
		assgnType.getSelectionModel().selectFirst();
		
		// There are 20 lessons in the "Apply yourself to reading and teaching" manual
		for (int i = 1; i <= 20; i++) {
			lesson.getItems().add(i);
		}
		
		lesson.getSelectionModel().selectFirst();
		
		// future assignmentRow control
		assgnType.setOnAction(value -> {
			if (assgnType.getValue() == Assignment_t.READING
					|| assgnType.getValue() == Assignment_t.TALK) {
				hholdMain.setVisible(false);
				hholdAux.setVisible(false);
			} else {
				hholdMain.setVisible(true);
				hholdAux.setVisible(true);
			}
		});
	}
	
	public void addAllStudents(List<Person> ps) {
		
	}
	
	public void setWeekDate(String date) {
		weekDate.setText(date);
	}
	
	public String getWeekDate() {
		return weekDate.getText();
	}
	
	public String getPubMain() {
		return pubMain.getValue();
	}
	
	public String getHholdMain() {
		return hholdMain.getValue();
	}
	
	public String getPubAux() {
		return pubAux.getValue();
	}
	
	public String getHholdAux() {
		return hholdAux.getValue();
	}
	
}
