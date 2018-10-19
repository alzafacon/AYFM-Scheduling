package io.fidelcoria.ayfmap.controller;

import static java.time.temporal.TemporalAdjusters.firstInMonth;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.fidelcoria.ayfmap.domain.Assignment;
import io.fidelcoria.ayfmap.domain.Person;
import io.fidelcoria.ayfmap.domain.PersonRepository;
import io.fidelcoria.ayfmap.fx.control.AssignmentRowForm;
import io.fidelcoria.ayfmap.fx.control.WeekForm;
import io.fidelcoria.ayfmap.service.AssignmentImportService;
import io.fidelcoria.ayfmap.service.StudentImportService;
import io.fidelcoria.ayfmap.util.Assignment_t;
import io.fidelcoria.ayfmap.util.ImportException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

@Component
public class ImportTabController {

	@Autowired
	AssignmentImportService assignmentImportService;
	@Autowired
	StudentImportService studentImportService;
	@Autowired
	PersonRepository personRepository;
	
	// Schedule Import sub-tab
	@FXML ChoiceBox<Month> scheduleImportMonth;
	@FXML ChoiceBox<Integer> scheduleImportYear;
	@FXML ScrollPane scheduleScroll;
	@FXML VBox weekForms;
	@FXML Button doImportSchedule;
	@FXML ProgressIndicator scheduleImportProgress;
	@FXML Label scheduleImportFeedback;

	
	// Enrollment Import sub-tab
	@FXML TextField firstName;
	@FXML TextField lastName;
	@FXML RadioButton isMale;
	@FXML RadioButton isFemale;
	@FXML RadioButton isActive;
	@FXML RadioButton isInactive;
	@FXML CheckBox canRead;
	@FXML CheckBox canInitCall;
	@FXML CheckBox canRetVis;
	@FXML CheckBox canBibleStudy;
	@FXML CheckBox canTalk;
	@FXML Button doEnroll;
	@FXML ProgressIndicator enrollProgress;
	@FXML Label enrollFeedback;
	
	@Value("${installation.directory.workspace}")
	private String workspace;
	
	private static final int MAX_WEEKS_PER_MONTH = 5;

	private ObservableList<Person> ps = FXCollections.observableArrayList();
	
	/** 
	 * Introduction to FXML - Controllers:
	 * this method will be called once when the contents of its 
	 * associated [FXML] document have been completely loaded
	 */
	public void initialize() {

		scheduleImportMonth.getItems().addAll(Month.values());
		
		int currentYear = LocalDate.now().getYear();
		IntStream.rangeClosed(currentYear, currentYear+2)
			.forEachOrdered(scheduleImportYear.getItems()::add);
		
		ps.addAll(
			personRepository.findAllActiveStudents().stream()
				.map(s -> s.getStudent()).collect(Collectors.toList())
		);
		
		for (int i = 0; i < MAX_WEEKS_PER_MONTH; i++) {
			weekForms.getChildren().add(new WeekForm(ps));
		}
		 
		// apply default template schedule structure //
		
		// drop last 2 assignment rows in first week
		((WeekForm) weekForms.getChildren().get(0))
			.getChildren().remove(2, 4);
		
		// set default assgn types
		
		// second assignment of first week is a TALK
		WeekForm fstWeek = ((WeekForm) weekForms.getChildren().get(0)); 
		// note: indexing starts at 1 b/c first child is weekDate
		((AssignmentRowForm) fstWeek.getChildren().get(2))				
			.setAssgnType(Assignment_t.TALK);
		
		// ayfm parts in second week are INIT_CALL
		WeekForm sndWeek = ((WeekForm) weekForms.getChildren().get(1));
		
		// again, skip first item b/c its a weekDate
		sndWeek.getChildren().subList(1, 5)
		.forEach(node -> {
			((AssignmentRowForm) node)
				.setAssgnType(Assignment_t.INIT_CALL);
		});
		
		// ayfm parts in third week are FST_RET_VIS
		WeekForm trdWeek = ((WeekForm) weekForms.getChildren().get(2));
		
		trdWeek.getChildren().subList(1, 5)
		.forEach(node -> {
			((AssignmentRowForm) node)
				.setAssgnType(Assignment_t.FST_RET_VIS);
		});
		
		// ayfm parts in fourth week are SND_RET_VIS
		/// except last part (4th) is Bible study
		WeekForm fourthWeek = ((WeekForm) weekForms.getChildren().get(3));
		
		fourthWeek.getChildren().subList(1, 5)
		.forEach(node -> {
			((AssignmentRowForm) node)
				.setAssgnType(Assignment_t.SND_RET_VIS);
		});
		((AssignmentRowForm) fourthWeek.getChildren().get(4))
			.setAssgnType(Assignment_t.BIBLE_STUDY);
		
		// TODO fifth week ??
		// wait for a workbook that has 5th week
		
		// first assignment of every week is a READING
		weekForms.getChildren().forEach(node -> {
			((AssignmentRowForm)
				((WeekForm) 
					node
				).getChildren().get(1)
			).setAssgnType(Assignment_t.READING);
		});
			
		
		// fitToWidth applies to the node inside the ScrollPane
		// this ensures the inside node stretches all the way across
		scheduleScroll.setFitToWidth(true);
		
		scheduleImportMonth.getSelectionModel().selectFirst();
		scheduleImportYear .getSelectionModel().selectFirst();
		
		// since updateCalenar depends on both month and year having a value
		// and either one triggers updateCalendar
		// the EventHandlers are registered after setting values...
		scheduleImportMonth.setOnAction(e -> { updateCalendar(e); });
		scheduleImportYear .setOnAction(e -> { updateCalendar(e); });
		// ... and then a dummy event is used to call updateCalendar
		updateCalendar(new ActionEvent());
		
		
		// initialize enroll tab //
		ToggleGroup gender = new ToggleGroup();
		isMale.setToggleGroup(gender);
		isFemale.setToggleGroup(gender);
		
		isMale.setSelected(true);
		
		ToggleGroup active = new ToggleGroup();
		isActive.setToggleGroup(active);
		isInactive.setToggleGroup(active);
		
		isActive.setSelected(true);
	}
	
	public void updateCalendar(ActionEvent e) {
		
		LocalDate date = LocalDate
				.of(scheduleImportYear.getValue(), scheduleImportMonth.getValue(), 1)
				.with(firstInMonth(DayOfWeek.MONDAY));
		
		for (int i = 0; i < MAX_WEEKS_PER_MONTH; i++) {
			if (date.getMonth() == scheduleImportMonth.getValue()) {
				((WeekForm) weekForms.getChildren().get(i))
					.setWeekDate(date.getMonth()+" "+date.getDayOfMonth());
				weekForms.getChildren().get(i).setVisible(true);
			} else {
				weekForms.getChildren().get(i).setVisible(false);
			}
			date = date.plusWeeks(1);
		}
	}
	
	/**
	 * Called when Import Tab selected to keep data fresh
	 */
	public void updateUnderlyingPersons() {
		ps.clear();
		ps.addAll(
			personRepository.findAllActiveStudents().stream()
				.map(s -> s.getStudent()).collect(Collectors.toList())
		);
	}
	
	public void importSchedule() {
		
		// hide label
		scheduleImportFeedback.setVisible(false);
		
		// snapshot alias's
		Month month = scheduleImportMonth.getValue();
		Integer year = scheduleImportYear.getValue();
		
		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() {
				
				try {
					List<Assignment> assignments = new ArrayList<>();
					
					int weekNum = 0;
					for (Node node : weekForms.getChildren()) {
						WeekForm wf = (WeekForm) node;
						
						assignments.addAll(wf.getAllAssgns(weekNum, month, year));
						weekNum++;
					}
					
					// TODO what if an assignment already exists...
					assignmentImportService.save(assignments);
					
					updateProgress(1, 1);
					updateMessage("");
				} catch (ImportException e) {
					updateProgress(0,1);
					updateMessage(e.getMessage());
					
					e.printStackTrace();
				} catch (Exception e) {
					updateProgress(0, 1);
					updateMessage("Import Failed!");
					throw e;
				}
				
				return null;
			}
		};
		
		scheduleImportProgress.progressProperty().bind(task.progressProperty());
		scheduleImportFeedback.textProperty().bind(task.messageProperty());
		
		new Thread(task).start();
		
		scheduleImportProgress.setVisible(true);
		scheduleImportFeedback.setVisible(true);
	}
	
	public void importEnrollment() throws Exception {
		
		enrollFeedback.setVisible(false);
		
		
		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() {
				
				Person p = new Person();
				
				String fn = firstName.getText();
				String ln = lastName.getText();
				
				try {
					
					if (fn == null || ln == null || fn.isEmpty() || ln.isEmpty()) {
						throw new ImportException("Please enter full name");
					}
					
					if (!canRead.isSelected() && !canInitCall.isSelected()
							&& !canRetVis.isSelected() && !canBibleStudy.isSelected()
							&& !canTalk.isSelected()
					) {
						throw new ImportException("Please check at least one box.");
					}
					
					p.setFirstName(fn);
					p.setLastName(ln);
					p.setActive(isActive.isSelected());
					p.setGender(isMale.isSelected()? "m" : "f");
					
					p.setEligibleReading(canRead.isSelected());
					p.setEligibleInitCall(canInitCall.isSelected());
					p.setEligibleRetVisit(canRetVis.isSelected());
					p.setEligibleBibStudy(canBibleStudy.isSelected());
					p.setEligibleTalk(canTalk.isSelected());
					
					studentImportService.saveStudents(Arrays.asList(p));
					
					updateProgress(1, 1);
				} catch (ImportException e) {
					updateProgress(0, 1);
					updateMessage(e.getMessage());
					
					e.printStackTrace();
				} catch (Exception e) {
					updateProgress(0,1);
					updateMessage("Import Failed!");
					throw e;
				}

				return null;
			}
		};
		
		enrollProgress.progressProperty().bind(task.progressProperty());
		enrollFeedback.textProperty().bind(task.messageProperty());
		
		new Thread(task).start();
		
		enrollProgress.setVisible(true);
		enrollFeedback.setVisible(true);
	}
}
