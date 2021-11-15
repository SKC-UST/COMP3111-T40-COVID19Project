package comp3111.covid.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.controlsfx.control.CheckListView;

import comp3111.covid.Context;
import comp3111.covid.datastorage.Database;


public class TabController {
	private Context context = Context.getInstance();
	private Database database = context.getDatabase();
	private ObservableList<Pair<String, String>> checkedPair;
	private ArrayList<String> locations;
	private HashMap<String, Boolean> checkedLocations = new HashMap<String, Boolean>();

    @FXML
    private Button confirmButton;

    @FXML
    private CheckListView<Pair<String, String>> locationsCheckboxList;
    
    protected Database getDatabase() {
    	return this.database;
    }
    
    public void updateCheckboxList() {
    	
    	System.out.println("update");
    	
    	// Initializations
    	this.checkedLocations.clear();
    	ArrayList<Pair<String, String>> locationsList = this.database.getLocationNames();
    	for(Pair<String, String> pair : locationsList) {
    		this.checkedLocations.put(pair.getKey(), false);
    	}
    	ObservableList<Pair<String, String>> oblist = FXCollections.observableArrayList();
    	for(Pair<String, String> pair : locationsList) {
    		oblist.add(pair);
    	}
    	
    	//set checkboxview items
    	this.locationsCheckboxList.setItems(oblist);
    	//for showing only the location name
    	this.locationsCheckboxList.setCellFactory(lv -> new CheckBoxListCell<Pair<String, String>>(locationsCheckboxList::getItemBooleanProperty){
    		@Override
    		public void updateItem(Pair<String, String> pair, boolean empty) {
    			super.updateItem(pair, empty);
    			setText(pair == null ? "" : pair.getValue());
    		}
    	});
    	/*
    	this.locationsCheckboxList.getCheckModel().getCheckedIndices().addListener(new ListChangeListener<Integer>(){
    		@Override
    		public void onChanged(ListChangeListener.Change<? extends Integer> c) {
    			while(c.next()) {
    				if(c.wasAdded()) {
    					for(int i : c.getAddedSubList()) {
    						checkedLocations.put(locationsCheckboxList.getItems().get(i).getKey(), true);
    					}
    				}
    				if(c.wasRemoved()) {
    					for(int i : c.getRemoved()) {
    						checkedLocations.put(locationsCheckboxList.getItems().get(i).getKey(), false);
    					}
    				}
    			}
    		}
    	}); */
    	
    	this.locationsCheckboxList.getCheckModel().getCheckedItems().addListener(new ListChangeListener<Pair<String, String>>(){
    		public void onChanged(ListChangeListener.Change<? extends Pair<String, String>> c) {
    			checkedPair = locationsCheckboxList.getCheckModel().getCheckedItems();
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
    
    @FXML
    void handleConfirmSelection(ActionEvent event) {    	
    	ArrayList<String> checkedList = this.getSelectedIso();
    	for(String loc : checkedList) {
    		System.out.println(loc);
    	}
    }
}