package th.in.moe.devtools.sqlgenerator.controller;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import th.in.moe.devtools.sqlgenerator.MainApp;
import th.in.moe.devtools.sqlgenerator.common.bean.GeneratorCriteria;
import th.in.moe.devtools.sqlgenerator.common.constant.GeneratorConstant.DATABASE_PRODUCTION_NAME;
import th.in.moe.devtools.sqlgenerator.common.constant.GeneratorConstant.GENERATE_TYPE;
import th.in.moe.devtools.sqlgenerator.common.exception.GeneratedException;
import th.in.moe.devtools.sqlgenerator.common.util.DialogUtils;
import th.in.moe.devtools.sqlgenerator.service.CreateStatementGeneratorService;
import th.in.moe.devtools.sqlgenerator.service.InsertStatementGeneratorService;
import th.in.moe.devtools.sqlgenerator.service.MySqlCreateStatementGeneratorService;
import th.in.moe.devtools.sqlgenerator.service.OracleCreateStatementGeneratorService;
import th.in.moe.devtools.sqlgenerator.service.SqlServerCreateStatementGeneratorService;

public class MainPageController {
	
	@FXML
	private ComboBox<String> dbProductionNameComboBox;
	@FXML
	private ComboBox<String> generateTypeComboBox;
	@FXML
	private TextField dbUser;
	
	// Reference to the main application.
	private MainApp mainApp;
	
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	@FXML
	public void initialize() {
		generateTypeComboBox.setItems(FXCollections.observableArrayList(Arrays.asList(
			GENERATE_TYPE.CREATE_TABLE,
			GENERATE_TYPE.INSERT_DATA
		)));
		generateTypeComboBox.getSelectionModel().select(0);
		generateTypeComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (GENERATE_TYPE.INSERT_DATA.equals(observable.getValue())) {
					dbProductionNameComboBox.setDisable(true);
				} else {
					dbProductionNameComboBox.setDisable(false);
				}
			}
		});
		
		dbProductionNameComboBox.setItems(FXCollections.observableArrayList(Arrays.asList(
			DATABASE_PRODUCTION_NAME.MYSQL,
			DATABASE_PRODUCTION_NAME.ORACLE,
			DATABASE_PRODUCTION_NAME.SQLSERVER
		)));
		dbProductionNameComboBox.getSelectionModel().select(0);
		dbProductionNameComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (DATABASE_PRODUCTION_NAME.SQLSERVER.equals(observable.getValue())) {
					dbUser.setDisable(false);
				} else {
					dbUser.setText(null);
					dbUser.setDisable(true);
				}
			}
		});
		
		dbUser.setDisable(true);
		dbUser.setTooltip(MainPageTooltip.getDbUser());
	}
	
	@FXML
	private void handleGenerate() {
		GeneratorCriteria criteria = bindingModel();
		try {
			if (GENERATE_TYPE.CREATE_TABLE.equals(criteria.getGenerateType())) {
				processGenerateSqlCreateTable(criteria);
			} else if (GENERATE_TYPE.INSERT_DATA.equals(criteria.getGenerateType())) {
				processGenerateSqlInsertData(criteria);
			}
		} catch (GeneratedException e) {
			// Show Error Alert
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText(e.getMessage());
			DialogUtils.createExpandableException(alert, e);
			alert.showAndWait();
		}
	}
	
	private GeneratorCriteria bindingModel() {
		GeneratorCriteria criteria = new GeneratorCriteria();
		criteria.setDatabaseProductionName(dbProductionNameComboBox.getValue());
		criteria.setGenerateType(generateTypeComboBox.getValue());
		criteria.setUser(dbUser.getText());
		
		return criteria;
	}
	
	private void processGenerateSqlCreateTable(GeneratorCriteria criteria) throws GeneratedException {
		final FileChooser xlsxFileChooser = new FileChooser();
		xlsxFileChooser.setTitle("Select Excel File");
		xlsxFileChooser.getExtensionFilters().add(new ExtensionFilter("Excel Workbook", "*.xlsx"));
		File xlsxFile = xlsxFileChooser.showOpenDialog(mainApp.getPrimaryStage());
		
		if (xlsxFile != null) {
			CreateStatementGeneratorService createStatementGeneratorService = getCreateStatementGeneratorService(criteria);
			createStatementGeneratorService.validateXlsxFile(xlsxFile);
			List<String> sqlTextList = createStatementGeneratorService.processXlsxFile(criteria, xlsxFile);
			
			// Show Information Alert
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("Success");
			alert.setHeaderText(null);
			alert.setContentText("Generate SQL success, Please select file to save result.");
			alert.showAndWait();
			
			final FileChooser sqlFileChooser = new FileChooser();
			sqlFileChooser.setTitle("Save SQL File");
			sqlFileChooser.getExtensionFilters().add(new ExtensionFilter("Structured Query Language file", "*.sql"));
			File sqlFile = sqlFileChooser.showSaveDialog(mainApp.getPrimaryStage());
			
			if (sqlFile != null) {
				createStatementGeneratorService.writeSqlFile(sqlTextList, sqlFile);
				
				// Show Information Alert
				alert = new Alert(AlertType.INFORMATION);
				alert.initOwner(mainApp.getPrimaryStage());
				alert.setTitle("Success");
				alert.setHeaderText(null);
				alert.setContentText("Save file succeeded!!");
				alert.showAndWait();
			}
		}
	}
	
	private void processGenerateSqlInsertData(GeneratorCriteria criteria) throws GeneratedException {
		final FileChooser xlsxFileChooser = new FileChooser();
		xlsxFileChooser.setTitle("Select Excel File");
		xlsxFileChooser.getExtensionFilters().add(new ExtensionFilter("Excel Workbook", "*.xlsx"));
		File xlsxFile = xlsxFileChooser.showOpenDialog(mainApp.getPrimaryStage());
		
		if (xlsxFile != null) {
			InsertStatementGeneratorService insertStatementGeneratorService = new InsertStatementGeneratorService();
			List<String> sqlTextList = insertStatementGeneratorService.processXlsxFile(xlsxFile);
			
			// Show Information Alert
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("Success");
			alert.setHeaderText(null);
			alert.setContentText("Generate SQL success, Please select file to save result.");
			alert.showAndWait();
			
			final FileChooser sqlFileChooser = new FileChooser();
			sqlFileChooser.setTitle("Save SQL File");
			sqlFileChooser.getExtensionFilters().add(new ExtensionFilter("Structured Query Language file", "*.sql"));
			File sqlFile = sqlFileChooser.showSaveDialog(mainApp.getPrimaryStage());
			
			if (sqlFile != null) {
				insertStatementGeneratorService.writeSqlFile(sqlTextList, sqlFile);
				
				// Show Information Alert
				alert = new Alert(AlertType.INFORMATION);
				alert.initOwner(mainApp.getPrimaryStage());
				alert.setTitle("Success");
				alert.setHeaderText(null);
				alert.setContentText("Save file succeeded!!");
				alert.showAndWait();
			}
		}
	}
	
	private CreateStatementGeneratorService getCreateStatementGeneratorService(GeneratorCriteria criteria) throws GeneratedException {
		if (DATABASE_PRODUCTION_NAME.MYSQL.equals(criteria.getDatabaseProductionName())) {
			return new MySqlCreateStatementGeneratorService();
		} else if (DATABASE_PRODUCTION_NAME.ORACLE.equals(criteria.getDatabaseProductionName())) {
			return new OracleCreateStatementGeneratorService();
		} else if (DATABASE_PRODUCTION_NAME.SQLSERVER.equals(criteria.getDatabaseProductionName())) {
			return new SqlServerCreateStatementGeneratorService();
		} else {
			return new MySqlCreateStatementGeneratorService();
		}
	}
	
	// Tooltip
	private static class MainPageTooltip {
		
		public static final Tooltip getDbUser() {
			Tooltip tooltip = new Tooltip();
			tooltip.setText(
				"Using 'dbo' for default"
			);
			return tooltip;
		}
	}
	
}
