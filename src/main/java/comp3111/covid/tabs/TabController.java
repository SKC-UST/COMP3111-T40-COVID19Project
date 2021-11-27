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

import javax.swing.JOptionPane;

import org.controlsfx.control.CheckListView;

import comp3111.covid.Context;
import comp3111.covid.datastorage.Database;

/**
 * Parent class to all tab controllers.
 * This class is responsible for handling the user choosing countries in the {@link org.controlsfx.control.CheckListView}.
 */
public abstract class TabController {
	@FXML private CheckListView<Pair<String, String>> locationsCheckboxList;
	private Database database = Context.getInstance().getDatabase();
	/**
	 * A {@link javafx.collections.ObservableList} storing the location checked in the CheckListView.
	 * A location is represented a Pair of the location's iso code and name.
	 */
	protected ObservableList<Pair<String, String>> checkedPair = FXCollections.observableArrayList();    
    /**
     * Getter for the internal database of this program.
     * @return	The internal database of this program
     */
    protected final Database getDatabase() {
    	return this.database;
    }
    /**
     * This method initializes items in the tabs.
     * It is called right after all FXML elements are loaded by the application.
     * In TabController, it initializes the {@link org.controlsfx.control.CheckListView} to show the country name, while internally, it holds a pair containing the country name and the iso code of the location.
     */
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
    /**
     * This initializes the checkboxList after the dataset is imported into the database.
     * It loads the country name - iso code pair into the checkbox list for display,
     * and it adds a (@link javafx.collections.ListChangeListener} to update checked items onece the user clicks on a checkbox.
     */
    public void updateCheckboxList() {
    	// Initializations
    	this.checkedPair.clear();
    	ArrayList<Pair<String, String>> locationsList = this.database.getLocationNames();
    	ObservableList<Pair<String, String>> oblist = FXCollections.observableArrayList();
    	for(Pair<String, String> pair : locationsList) {
    		oblist.add(pair);
    	}
    	
    	//set checkboxview items
    	this.locationsCheckboxList.setItems(oblist);
    	
    	this.locationsCheckboxList.getCheckModel().getCheckedItems().addListener(new ListChangeListener<Pair<String, String>>(){
    		public void onChanged(ListChangeListener.Change<? extends Pair<String, String>> c) {
    			checkedPair = locationsCheckboxList.getCheckModel().getCheckedItems();
    		}
    	});
    }
    /**
     * Gets the list of iso code of countries selected by the user in the CheckListView.
     * Used when generating data.
     * @return {@link java.util.ArrayList} of iso codes, representing the countries selected by the user.
     */
    protected ArrayList<String> getSelectedIso(){
    	ArrayList<String> results = new ArrayList<String>();
    	for(Pair<String, String> pair : this.checkedPair) {
    		results.add(pair.getKey());
    	}
    	return results;
    }
    /**
     * This method handles button click on the confirm selection button.
     * Different tabs perform different actions in response to a click.
     * @param event	javafx ActionEvent.
     */
    @FXML
    abstract void handleConfirmSelection(ActionEvent event);
    /**
     * This method is used to display a pop-up error message.
     * @param msg	Content of the pop up dialog box.
     * @param title	Title of the pop up dialog box.
     */
    protected void handleError(String msg, String title) {
    	JOptionPane.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE);
    }
}