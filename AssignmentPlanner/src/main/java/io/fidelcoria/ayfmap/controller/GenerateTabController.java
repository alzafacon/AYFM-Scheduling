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
		
		// hide feedback when starting a new request
		feedbackLabel.setVisible(false);
		
		// let user choose folder to drop files in
		DirectoryChooser folderForSchedule = new DirectoryChooser();
		folderForSchedule.setInitialDirectory(new File(workspace));
		
		File outputDir = folderForSchedule.showDialog(null);
		
		if (outputDir != null) {
			// TODO: not visible (needs a separate thread)
			scheduleGenerateSpinner.setVisible(true);
			
			Month month = scheduleMonthChoiceBox.getValue();
			Integer year = scheduleYearChoiceBox.getValue();

			String directory = outputDir.getAbsolutePath()+"/"+year+"-"+month.getValue()+".docx";
			
			// TODO: change into a single call
			scheduleService.setYearMonth(year, month.getValue());
			scheduleService.generateSchedule();

			scheduleService.saveToDocxSchedule(new File(directory));
			
			scheduleGenerateSpinner.setVisible(false);
			feedbackLabel.setVisible(true);
		}
		
	}
	
	@FXML
	private void generateReminders(ActionEvent event) throws Exception {
		// TODO: should be logging
		System.out.println("gen reminders");
		
		// hide feedback when starting a new request
		remindersFeedbackLabel.setVisible(false);
		
		// let user choose folder to drop files in
		DirectoryChooser folderForReminders = new DirectoryChooser();
		folderForReminders.setInitialDirectory(new File(workspace));
		
		File outputDir = folderForReminders.showDialog(null);
		
		if (outputDir != null) {
			Month month = remindersMonthChoiceBox.getValue();
			Integer year = remindersYearChoiceBox.getValue();
			
			String directory = outputDir.getAbsolutePath()+"/";
			
			reminderGenerateSpinner.setVisible(true);
			
			pdfFormFillService.formFill(year, month.getValue(), directory);
			
			reminderGenerateSpinner.setVisible(false);
			remindersFeedbackLabel.setVisible(true);
		}
		System.out.println("leaving gen reminders");
	}
}
