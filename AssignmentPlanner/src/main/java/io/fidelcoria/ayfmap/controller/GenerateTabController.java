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
	Label scheduleFeedbackLabel;
	
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
		
		scheduleFeedbackLabel.setVisible(false);
		
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
				scheduleService.generateSchedule(year, month.getValue());

				try {
					scheduleService.saveToDocxSchedule(new File(directory));
					
					updateProgress(1, 1);
				} catch (FileNotFoundException e) {
					updateMessage("Unable to open file"); // let user know through the Label
					updateProgress(0, 1);
					
					// log stack trace?
					e.printStackTrace();
				} catch (IOException e) {
					updateMessage("Something failed");
					updateProgress(0,1);
					
					// TODO logging... maybe?
					e.printStackTrace();
				}
				
				return null;
			}
		};
		
		scheduleGenerateSpinner.progressProperty().bind(task.progressProperty());
		// bind label to task as well (instead of spinner b/c it's harder)
		scheduleFeedbackLabel.textProperty().bind(task.messageProperty());
		
		new Thread(task).start();
		
		scheduleGenerateSpinner.setVisible(true);
		scheduleFeedbackLabel.setVisible(true);
	}
	
	@FXML
	private void generateReminders(ActionEvent event) throws Exception {
		// TODO: should be logging
		System.out.println("gen reminders");
		
		remindersFeedbackLabel.setVisible(false);
		
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
				int countFilled = 0;
				
				try {
					countFilled = pdfFormFillService.formFill(year, month.getValue(), directory);
					
					updateProgress(1, 1);
				} catch (Exception e) {
					updateProgress(0, 1); // failed
					updateMessage("Failed");
					
					// TODO logging?
					e.printStackTrace();
				}
				
				if (countFilled == 0) {
					updateProgress(0, 1); // failed
					updateMessage("Nothing to fill");		
				}
				
				return null;
			}
		};
		
		reminderGenerateSpinner.progressProperty().bind(task.progressProperty());
		remindersFeedbackLabel.textProperty().bind(task.messageProperty());
		
		new Thread(task).start();
		
		reminderGenerateSpinner.setVisible(true);
		remindersFeedbackLabel.setVisible(true);
	}
}
