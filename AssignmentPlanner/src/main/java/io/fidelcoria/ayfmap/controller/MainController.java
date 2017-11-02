package io.fidelcoria.ayfmap.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
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
	
	private static final Map<String, String> tabTitles;
	
	static {
		tabTitles = new HashMap<>();
		
		tabTitles.put("generate-tab", "Generate Documents");
		tabTitles.put("import-tab", "Import Documents");
		tabTitles.put("edit-tab", "Edit Data");
	}
	
	/**
	 * Update the actionHeaderBar to reflect the selected tab
	 */
	public void tabClicked() {
		
		for (Tab tab : actionTabPane.getTabs()) {
			
			if (tab.isSelected()) {
				
				String title = tabTitles.get(tab.getId());
				actionHeaderBar.setText(title);
				
				break;
			}
		}
	}
	
}
