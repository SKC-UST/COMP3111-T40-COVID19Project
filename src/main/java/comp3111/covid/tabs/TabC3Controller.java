package comp3111.covid.tabs;

import java.util.ArrayList;

import org.controlsfx.control.CheckListView;

import comp3111.covid.Context;
import comp3111.covid.datastorage.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class TabC3Controller {
	
	private Context context = Context.getInstance();
	private Database database = context.getDatabase();

    @FXML
    private Button btnFooTab;

    @FXML
    private CheckListView<String> checkboxList;

    @FXML
    private Label lblText;

    @FXML
    void handleButton(ActionEvent event) {
    	/*
    	ArrayList<String> locations = database.getLocationNames();
    	// create the data to show in the CheckListView  
    	final ObservableList<String> strings = FXCollections.observableArrayList(); 
    	for (String location : locations) {     
    		strings.add(location); 
    	}  
    	// Create the CheckListView with the data
    	checkboxList.setItems(strings);
    	// and listen to the relevant events (e.g. when the selected indices or  // selected items change). 
    	
    	this.lblText.setText("bye");
    	*/
    }

}

