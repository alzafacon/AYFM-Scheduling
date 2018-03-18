package io.fidelcoria.ayfmap.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.fidelcoria.ayfmap.service.PdfFormFillService;
import io.fidelcoria.ayfmap.service.ScheduleService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.DirectoryChooser;

@Component
public class GenerateTabController {

	@Autowired
	ScheduleService scheduleService;
	@Autowired
	PdfFormFillService pdfFormFillService;
	
	
	@FXML
	ChoiceBox<Month> scheduleMonthChoiceBox;
	@FXML
	ChoiceBox<Integer> scheduleYearChoiceBox;
	@FXML
	Button generateScheduleButton;
	@FXML
	ProgressIndicator scheduleGenerateSpinner;
	@FXML
	Label feedbackLabel;
	
	@FXML
	ChoiceBox<Month> remindersMonthChoiceBox;
	@FXML
	ChoiceBox<Integer> remindersYearChoiceBox;
	@FXML
	Button generateRemindersButton;
	@FXML
	ProgressIndicator reminderGenerateSpinner;
	@FXML
	Label remindersFeedbackLabel;
	
	@Value("${installation.directory.workspace}")
	private String workspace;
	
	public void initialize() {
		
		int currentYear = LocalDate.now().getYear();
		
		scheduleMonthChoiceBox.getItems().addAll(Month.values());
		scheduleYearChoiceBox.getItems().addAll(
				(Integer)currentYear, (Integer)(currentYear+1), (Integer)(currentYear+2));
		scheduleMonthChoiceBox.getSelectionModel().selectFirst();
		scheduleYearChoiceBox.getSelectionModel().selectFirst();
		
		
		remindersMonthChoiceBox.getItems().addAll(Month.values());
		remindersYearChoiceBox.getItems().addAll(
				(Integer)currentYear, (Integer)(currentYear+1), (Integer)(currentYear+2));
		remindersMonthChoiceBox.getSelectionModel().selectFirst();
		remindersYearChoiceBox.getSelectionModel().selectFirst();
	}
	
	@FXML
	private void generateSchedule(ActionEvent event) throws FileNotFoundException, IOException {
		// TODO: should be logging...
		System.out.println("generating a schedule");
		
		// let user choose folder to drop files in
		DirectoryChooser folderForSchedule = new DirectoryChooser();
		folderForSchedule.setInitialDirectory(new File(workspace));
		
		File outputDir = folderForSchedule.showDialog(null);
		
		if (outputDir == null) {
			return;
		}

		Month month = scheduleMonthChoiceBox.getValue();
		Integer year = scheduleYearChoiceBox.getValue();
		
		String directory = outputDir.getAbsolutePath()+"/"+year+"-"+month.getValue()+".docx";
		
		// prepare task to be run on a separate thread
		Task<Void> task = new Task<Void>() {
			@Override public Void call() {
				scheduleService.setYearMonth(year, month.getValue());
				scheduleService.generateSchedule();

				try {
					scheduleService.saveToDocxSchedule(new File(directory));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					// let user know through the Label
					// log stack trace?
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// indicate that step 1 of 1 is complete
				updateProgress(1, 1);
				
				return null;
			}
		};
		
		scheduleGenerateSpinner.progressProperty().bind(task.progressProperty());
		// TODO: bind label to task as well
		new Thread(task).start();
		
		scheduleGenerateSpinner.setVisible(true);	
	}
	
	@FXML
	private void generateReminders(ActionEvent event) throws Exception {
		// TODO: should be logging
		System.out.println("gen reminders");
		
		// let user choose folder to drop files in
		DirectoryChooser folderForReminders = new DirectoryChooser();
		folderForReminders.setInitialDirectory(new File(workspace));
		
		File outputDir = folderForReminders.showDialog(null);
		
		if (outputDir == null) {
			return;
		}
		
		Month month = remindersMonthChoiceBox.getValue();
		Integer year = remindersYearChoiceBox.getValue();
			
		String directory = outputDir.getAbsolutePath()+"/";
			
		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() {
				try {
					// TODO: what if there are no assignments for chosen month???
					pdfFormFillService.formFill(year, month.getValue(), directory);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// indicate that step 1 of 1 is complete
				updateProgress(1, 1);

				return null;
			}
		};
		
		reminderGenerateSpinner.progressProperty().bind(task.progressProperty());
		new Thread(task).start();
		
		reminderGenerateSpinner.setVisible(true);
	}
}
