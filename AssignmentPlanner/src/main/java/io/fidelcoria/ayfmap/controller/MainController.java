package io.fidelcoria.ayfmap.controller;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;

@Component
public class MainController {

	@FXML
	Label actionHeaderBar;
	
	@FXML
	TabPane actionTabPane;
	
	@FXML 
	private GenerateTabController generateTabController;
	
	@FXML
	private ImportTabController importTabController;
	
	@FXML
	private DataTabController dataTabController;
	
}
