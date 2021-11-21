package comp3111.covid.tabs;

/**
 * place holder
 * @author ytc314
 *
 */
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.controlsfx.control.CheckListView;

import comp3111.covid.Context;
import comp3111.covid.datastorage.Database;


public class TabController {
	private Database database = Context.getInstance().getDatabase();
	protected ObservableList<Pair<String, String>> checkedPair = FXCollections.observableArrayList();

    @FXML
    private CheckListView<Pair<String, String>> locationsCheckboxList;
    
    protected Database getDatabase() {
    	return this.database;
    }
    
    public void initialize() {
    	System.out.println("init");
    	//for showing only the location name
    	this.locationsCheckboxList.setCellFactory(lv -> new CheckBoxListCell<Pair<String, String>>(locationsCheckboxList::getItemBooleanProperty){
    		@Override
    		public void updateItem(Pair<String, String> pair, boolean empty) {
    			super.updateItem(pair, empty);
    			setText(pair == null ? "" : pair.getValue());
    		}
    	});
    }
    
    public void updateCheckboxList() {
    	
    	// Initializations
    	this.checkedLocations.clear();
    	ArrayList<Pair<String, String>> locationsList = this.database.getLocationNames();
    	ObservableList<Pair<String, String>> oblist = FXCollections.observableArrayList();
    	for(Pair<String, String> pair : locationsList) {
    		oblist.add(pair);
    	}
    	
    	//set checkboxview items
    	this.locationsCheckboxList.setItems(oblist);
    	
    	this.locationsCheckboxList.getCheckModel().getCheckedItems().addListener(new ListChangeListener<Pair<String, String>>(){
    		public void onChanged(ListChangeListener.Change<? extends Pair<String, String>> c) {
    			System.out.println(locationsCheckboxList.getCheckModel().getCheckedItems());
    			checkedPair = locationsCheckboxList.getCheckModel().getCheckedItems();
    			System.out.println(checkedPair);
    		}
    	});
    }
    
    protected ArrayList<String> getSelectedIso(){
    	ArrayList<String> results = new ArrayList<String>();
    	for(Pair<String, String> pair : this.checkedPair) {
    		results.add(pair.getKey());
    	}
    	return results;
    }
    
    //Overriden
    @FXML
    void handleConfirmSelection(ActionEvent event) {    	
    }
    
    protected void handleError(String msg, String title) {
    	JOptionPane.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE);
    }
}