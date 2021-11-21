package comp3111.covid;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import comp3111.covid.datastorage.Database;
import comp3111.covid.tabs.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * Building on the sample skeleton for 'ui.fxml' Controller Class generated by SceneBuilder 
 */
public class Controller {

    
    @FXML private Button fileChoosingButton;
    @FXML private TextArea textAreaConsole; // the console
    
    // Tabs under the main Tab pane
    @FXML private Tab tabReport1;
    @FXML private Tab tabReport2;
    @FXML private Tab tabReport3;
    @FXML private Tab tabApp1;
    @FXML private Tab tabApp2;
    @FXML private Tab tabApp3;
    @FXML private Tab tabC3;
    
    // controller for all tabs
    @FXML private TabA1pageController tabA1pageController;
    @FXML private TabA2pageController tabA2pageController;
    @FXML private TabB1pageController tabB1pageController;
    @FXML private TabB2pageController tabB2pageController;
    @FXML private TabC1pageController tabC1pageController;
    @FXML private TabC2pageController tabC2pageController;
    @FXML private TabC3pageController tabC3pageController;
    
    @FXML private TabPane tabPane;
    
    @FXML private Text popUpMessageText;
    
    
    private Context context = Context.getInstance();
    private Database database = context.getDatabase();
    
    
    /**
     * This method launches a JFileChooser with the restriction of only choosing csv files.
     * The returns null if "cancel is pressed". 
     * Otherwise, it generates a file and will pass it to importCSVtoDatabase
     * @return a File object pointing to the chosen csv file
     * @see importCSVtoDatabase()
     */
    
    protected File chooseCSVFile() {
    	JFileChooser csvChooser = new JFileChooser(".");
    	FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV files", "csv");
    	csvChooser.setFileFilter(csvFilter);
    	int chooserReturnVal = csvChooser.showOpenDialog(null);
    	if(chooserReturnVal == JFileChooser.APPROVE_OPTION) {
    		File csvFile = csvChooser.getSelectedFile();
    		System.out.println(csvFile.getAbsolutePath());
    		return csvChooser.getSelectedFile();
    	}
    	return null;
    }
    
    /**
     * Triggered when the "Choose Dataset" button is pressed.
     * This method gets a csv file as a dataset from a the method chooseCSVFile().
     * Then, it passes the csv file to the database for the database to process.
     * The result is that the database would contain the data from the csv file.
     * Upon successful import, the method updates the UI elements and logic of all the tabs' controllers,
     * and unlocks the tabs for the user to interact with.
     * @param event JavaFX ActionEvent
     * @see Database
     */
    
    @FXML
    //triggered by button
    void importCSVtoDatabase(ActionEvent event) {
    	File csvFile = chooseCSVFile(); // Official function

    	if(csvFile != null) {
    		this.database.importCSV(csvFile);
    		LocalDateTime currentTime = LocalDateTime.now();
    		LocalDate databaseEarlistTime = this.database.getEarliest();
    		LocalDate databaseLastestTime = this.database.getLatest();
    		String formattedTime = DateTimeFormatter.ofPattern("HH:mm:ss").format(currentTime);
    		String importMessage = "[ " + formattedTime + " ] " + "Successfully imported " + csvFile.getName() + "\n"; 
    		String rangeMessage = "[ " + formattedTime + " ] " + "Range of Date: " + databaseEarlistTime + " to " + 
    							  databaseLastestTime + "\n";
    		System.out.println(importMessage);
    		System.out.println(rangeMessage);
    		
        	
        	this.tabA1pageController.updateCheckboxList();
        	this.tabA2pageController.updateCheckboxList();
        	this.tabB1pageController.updateCheckboxList();
        	this.tabB2pageController.updateCheckboxList();
        	this.tabC1pageController.updateCheckboxList();
        	this.tabC2pageController.updateCheckboxList();
        	this.tabC3pageController.initAfterImport();
        	
        	this.tabReport1.setDisable(false);
        	this.tabReport2.setDisable(false);
        	this.tabReport3.setDisable(false);
        	this.tabApp1.setDisable(false);
        	this.tabApp2.setDisable(false);
        	this.tabApp3.setDisable(false);
        	this.tabC3.setDisable(false);
    	}
    }
}
