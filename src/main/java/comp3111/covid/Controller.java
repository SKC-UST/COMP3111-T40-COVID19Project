package comp3111.covid;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.gargoylesoftware.htmlunit.javascript.host.Map;
import com.sun.prism.paint.Color;

import comp3111.covid.datastorage.Database;
import comp3111.covid.datastorage.Database.DataTitle;
import comp3111.covid.tabs.*;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

/**
 * Building on the sample skeleton for 'ui.fxml' Controller Class generated by SceneBuilder 
 */
public class Controller {

    @FXML
    private Tab tabTaskZero;

    @FXML
    private TextField textfieldISO;

    @FXML
    private Button buttonConfirmedDeaths;

    @FXML
    private TextField textfieldDataset;

    @FXML
    private Button buttonRateOfVaccination;

    @FXML
    private Button buttonConfirmedCases;
    
    @FXML
    private Button fileChoosingButton;

    @FXML
    private TextArea textAreaConsole; // the console that was gone
    
    // all tabs in program
    @FXML private Tab tabC3;
    @FXML private Tab tabC2;
    @FXML private Tab tabC1;
    @FXML private Tab tabB2;
    @FXML private Tab tabB1;
    @FXML private Tab tabA2;
    @FXML private Tab tabA1;
    
    // controller for all tabs
    @FXML private TabA1pageController tabA1pageController;
    @FXML private TabA2pageController tabA2pageController;
    @FXML private TabB1pageController tabB1pageController;
    @FXML private TabB2pageController tabB2pageController;
    @FXML private TabC1pageController tabC1pageController;
    @FXML private TabC2pageController tabC2pageController;
    @FXML private TabC3pageController tabC3pageController;
    
    private Context context = Context.getInstance();
    private Database database = context.getDatabase();
  

    /**
     *  Task Zero
     *  To be triggered by the "Confirmed Cases" button on the Task Zero Tab 
     *  
     */
    @FXML
    void doConfirmedCases(ActionEvent event) {
    	String iDataset = textfieldDataset.getText();
    	String iISO = textfieldISO.getText();
    	String oReport = DataAnalysis.getConfirmedCases(iDataset, iISO);
    	textAreaConsole.setText(oReport);
    }

  
    /**
     *  Task Zero
     *  To be triggered by the "Confirmed Deaths" button on the Task Zero Tab
     *  
     */
    @FXML
    void doConfirmedDeaths(ActionEvent event) {
    	String iDataset = textfieldDataset.getText();
    	String iISO = textfieldISO.getText();
    	String oReport = DataAnalysis.getConfirmedDeaths(iDataset, iISO);
    	textAreaConsole.setText(oReport);
    }

  
    /**
     *  Task Zero
     *  To be triggered by the "Rate of Vaccination" button on the Task Zero Tab
     *  
     */
    @FXML
    void doRateOfVaccination(ActionEvent event) {
    	String iDataset = textfieldDataset.getText();
    	String iISO = textfieldISO.getText();
    	String oReport = DataAnalysis.getRateOfVaccination(iDataset, iISO);
    	textAreaConsole.setText(oReport);
    }
    
    private File chooseCSVFile() {
    	JFileChooser csvChooser = new JFileChooser("./src/main/resources/dataset");
    	FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV files", "csv");
    	csvChooser.setFileFilter(csvFilter);
    	int chooserReturnVal = csvChooser.showOpenDialog(null);
    	if(chooserReturnVal == JFileChooser.APPROVE_OPTION) {
    		return csvChooser.getSelectedFile();
    	}
    	return null;
    }
    
    @FXML
    void importCSVtoDatabase(ActionEvent event) {
    	  File csvFile = chooseCSVFile(); // Official function
    	//File csvFile = new File("./src/main/resources/dataset/COVID_Dataset_v1.0.csv");
    	// TODO: handle file not chosen
    	if(csvFile != null) {
    		this.database.importCSV(csvFile);
        	System.out.println("successfully imported");
        	this.database.printDatabaseContent();
        	
        	this.tabA1pageController.updateCheckboxList();
        	this.tabA2pageController.updateCheckboxList();
        	this.tabB1pageController.updateCheckboxList();
        	this.tabB2pageController.updateCheckboxList();
        	this.tabC1pageController.updateCheckboxList();
        	this.tabC2pageController.updateCheckboxList();
    	}
    	
    	//this.tabC3pageController.updateCheckboxList();

    }
    
    Database getDatabase() {
    	return this.database;
    }
}

