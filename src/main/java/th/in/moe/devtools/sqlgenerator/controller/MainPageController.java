package th.in.moe.devtools.sqlgenerator.controller;

import javafx.fxml.FXML;
import th.in.moe.devtools.sqlgenerator.MainApp;
import th.in.moe.devtools.sqlgenerator.service.GeneratorService;

public class MainPageController {
	
	// Reference to the main application.
	private MainApp mainApp;
	
	// GeneratorService
	private GeneratorService generatorService;
	
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	@FXML
	public void initialize() {
		System.out.println("hello");
	}
	
}
