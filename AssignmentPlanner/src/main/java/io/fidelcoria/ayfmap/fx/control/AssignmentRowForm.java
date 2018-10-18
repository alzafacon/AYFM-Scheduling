package io.fidelcoria.ayfmap.fx.control;

import static java.time.temporal.TemporalAdjusters.firstInMonth;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.fidelcoria.ayfmap.domain.Assignment;
import io.fidelcoria.ayfmap.domain.Person;
import io.fidelcoria.ayfmap.util.Assignment_t;
import io.fidelcoria.ayfmap.util.ImportException;
import io.fidelcoria.ayfmap.util.Section;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;

public class AssignmentRowForm extends GridPane {

	@FXML
	ChoiceBox<Assignment_t> assgnType;
	@FXML
	ChoiceBox<Integer> lesson;
	@FXML
	ComboBox<Person> pubMain; // pub is for publisher
	@FXML
	ComboBox<Person> hholdMain;
	@FXML
	ComboBox<Person> pubAux;
	@FXML
	ComboBox<Person> hholdAux;

	private ObservableList<Person> ps;

	public AssignmentRowForm(ObservableList<Person> ps) {

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AssignmentRowForm.fxml"));
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

		// There are 20 lessons in the "Apply yourself to reading and teaching" manual
		for (int i = 1; i <= 20; i++) {
			lesson.getItems().add(i);
		}
		lesson.getSelectionModel().selectFirst();

		// initialize ComboBox values
		for (Assignment_t t : Assignment_t.values()) {
			if (t != Assignment_t.UNKNOWN) {
				assgnType.getItems().add(t);
			}
		}

		// add ComboBoxes to ArrayList so that they can be streamed
		ArrayList<ComboBox<Person>> cs = new ArrayList<ComboBox<Person>>();
		cs.add(pubMain);
		cs.add(hholdMain);
		cs.add(pubAux);
		cs.add(hholdAux);

		// ps added here (and not constructor) because the comboBoxes 
		// are guaranteed to be wired before initialize() is called
		cs.stream().forEach(c -> {
			// wrap ObservableList in FilteredList so drop down
			// can be filtered by selected assignment type
			c.setItems(new FilteredList<>(ps));
		});

		// event handler must be registered before doing the selection
		// future assignmentRow control
		assgnType.setOnAction(e -> {

			// hide householder ComboBox when it is not applicable
			if (assgnType.getValue() == Assignment_t.READING
					|| assgnType.getValue() == Assignment_t.TALK) {
				hholdMain.setVisible(false);
				hholdAux .setVisible(false);
			} else {
				hholdMain.setVisible(true);
				hholdAux .setVisible(true);
			}

			// clear selection when type changes
			// in case someone not eligible for the new type was selected before
			cs.forEach(c -> { c.setValue(null); });

			Predicate<Person> f = (p) -> {
				return p.getEligibility().contains(assgnType.getValue());
			};

			// update the filter predicate when type changes
			cs.forEach(c -> {
				((FilteredList<Person>) c.getItems())
				.setPredicate(f);
			});
		});

		assgnType.getSelectionModel().selectFirst();
	}

	public void setAssgnType(Assignment_t t) {
		assgnType.setValue(t);
	}
	
	public Assignment_t getAssgnType() {
		return assgnType.getValue();
	}
	
	public Integer getLesson() {
		return lesson.getValue();
	}
	
	public String getPubMain() {
		return pubMain.getValue().toString();
	}
	
	public String getHholdMain() {
		return hholdMain.getValue().toString();
	}
	
	public String getPubAux() {
		return pubAux.getValue().toString();
	}
	
	public String getHholdAux() {
		return hholdAux.getValue().toString();
	}

	public List<Assignment> getAllAssgns(int weekNum, Month month, Integer year) throws ImportException {
		
		LocalDate weekDate = LocalDate.of(year, month.getValue(), 1)
				.with(firstInMonth(DayOfWeek.MONDAY))
				.plusWeeks(weekNum);
		
		ArrayList<Assignment> as = new ArrayList<>();
		
		Assignment
			main = new Assignment(weekDate, getAssgnType(), Section.A),
			aux  = new Assignment(weekDate, getAssgnType(), Section.B);
		
		extractAndAddAssgn(pubMain, hholdMain, main, as);
		extractAndAddAssgn(pubAux, hholdAux, aux, as);
		
		// participants for an assignment row cannot repeat		
		List<Person> participants = Arrays.asList(
			main.getAssignee(),
			main.getHouseholder(),
			aux.getAssignee(),
			aux.getHouseholder()
		).stream().filter(a -> {
			return a != null;
		}).collect(Collectors.toList());
		 
		 if (participants.stream().distinct().count() != participants.size()) {
			 throw new ImportException(
					 "Participant repeated on week of "+
					 month+" "+weekDate.getDayOfMonth());
		 }
		
		return as;
	}

	private void extractAndAddAssgn(ComboBox<Person> pub, ComboBox<Person> hhold, Assignment a,
			ArrayList<Assignment> as) throws ImportException 
	{
		if (pub.getValue() != null) {
			a.setAssignee(pub.getValue());
			
			if (getLesson() != null) {
				a.setLesson(getLesson());
			}
			if (hhold.getValue() != null) {
				a.setHouseholder(hhold.getValue());
			} else if (getAssgnType() != Assignment_t.READING 
					&& getAssgnType() != Assignment_t.TALK
			) {
				throw new ImportException(
						getAssgnType()+
						" missing a householder on week of "+
						a.getWeek().getMonth()+" "+a.getWeek().getDayOfMonth());
			}
			
			as.add(a);
		}
	}
}
