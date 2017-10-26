package io.fidelcoria.ayfmap.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.glass.events.MouseEvent;

import io.fidelcoria.ayfmap.service.PdfFormFillService;
import io.fidelcoria.ayfmap.service.ScheduleService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

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
	Button chooseScheduleButton;
	@FXML
	Label scheduleChosenForRemindersLabel;
	@FXML
	Button generateRemindersButton;
	private String scheduleDirectory;
	private String scheduleToBuildReminders;
	
	public void initialize() {
		scheduleMonthChoiceBox.getItems().addAll(Month.values());
		
		int currentYear = LocalDate.now().getYear();
		
		scheduleYearChoiceBox.getItems().addAll((Integer)currentYear, (Integer)(currentYear+1), (Integer)(currentYear+2));
	}
	
	@FXML
	private void generateSchedule(ActionEvent event) throws FileNotFoundException, IOException {
		System.out.println("generating a schedule");
		
		DirectoryChooser folderForSchedule = new DirectoryChooser();
		folderForSchedule.setInitialDirectory(new File(System.getProperty("user.home")+"\\Documents\\AYFM\\"));
		
		File outputDocx = folderForSchedule.showDialog(null);
		
		if (outputDocx != null) {
			
			Month month = scheduleMonthChoiceBox.getValue();
			Integer year = scheduleYearChoiceBox.getValue();

			String directory = outputDocx.getAbsolutePath() + "\\"+year+"-"+month.getValue()+".docx";
			
			scheduleService.setYearMonth(year, month.getValue());

			scheduleService.generateSchedule();

			scheduleService.saveToDocxSchedule(new File(directory));
		}
	}
	
	@FXML
	private void openScheduleFilePicker() {
		FileChooser chooseSchedule = new FileChooser();
		chooseSchedule.setInitialDirectory(new File(System.getProperty("user.home")+"\\Documents\\AYFM\\"));
		chooseSchedule.getExtensionFilters().add(new ExtensionFilter("MS Word", "*.docx"));
		
		File schedule = chooseSchedule.showOpenDialog(null);
		
		if (schedule != null) {
			scheduleDirectory = schedule.getParent();
			scheduleToBuildReminders = schedule.getAbsolutePath();
			scheduleChosenForRemindersLabel.setText(schedule.getName());
		}
	}
	
	@FXML
	private void generateReminders(ActionEvent event) {
		System.out.println("gen reminders");
		
//		pdfFormFillService.formFill(scheduleToBuildReminders, scheduleDirectory);
		
	}
}
