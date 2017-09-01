package io.fidelcoria.ayfmPlanner.controller;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;

@Component
public class MainController {

	@FXML 
	private GenerateTabController generateTabController;
	
	@FXML
	private ImportTabController importTabController;
	
	@FXML
	private DataTabController dataTabController;
	
}
