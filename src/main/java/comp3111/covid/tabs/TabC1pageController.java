package comp3111.covid.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.ArrayList;

import org.controlsfx.control.CheckListView;

import comp3111.covid.Context;
import comp3111.covid.datastorage.Database;


public class TabC1pageController {
	private Context context = Context.getInstance();
	private Database database = context.getDatabase();
	private ArrayList<String> selectedLocations = new ArrayList<String>();

    @FXML
    private Button confirmButton;

    @FXML
    private CheckListView<String> locationsCheckboxList;
    
    public void updateCheckboxList() {
    	
    	System.out.println("update");
    	
    	ArrayList<String> locations = database.getLocationNames();
		final ObservableList<String> locationList = FXCollections.observableArrayList();
		for(String location : locations) {
			locationList.add(location);
		}
		locationsCheckboxList.setItems(locationList);
		
		this.locationsCheckboxList.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
			public void onChanged(ListChangeListener.Change<? extends String> c) {
				System.out.println(locationsCheckboxList.getCheckModel().getCheckedItems());
			}
		});
    }
    
    @FXML
    void handleConfirmSelection(ActionEvent event) {

    }
}