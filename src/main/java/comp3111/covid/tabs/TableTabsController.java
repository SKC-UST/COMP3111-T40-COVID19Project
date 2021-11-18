package comp3111.covid.tabs;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.ArrayList;

import org.controlsfx.control.CheckListView;

import comp3111.covid.datastorage.Database;
import comp3111.covid.datastorage.Database.DataTitle;

public class TableTabsController extends TabController {
	@FXML private DatePicker datePicker;
	@FXML private Button confirmButton;
	@FXML protected TableColumn<TableView<TableData>,String> countryCol;
	@FXML protected TableColumn<TableView<TableData>,Long> totalCol;
	@FXML protected TableColumn<TableView<TableData>,String> rateCol;
	@FXML protected TableView<TableData> dataTable;
	@FXML protected Label tableTitlelbl;
	
	private LocalDate selectedDate = null;
	
	public class TableData {
		private final SimpleStringProperty countryName;
		private final SimpleLongProperty totalData;
		private final SimpleStringProperty rateData;

		TableData (String location, long total, double rate, boolean needPercentage) {
			this.countryName = new SimpleStringProperty(location);
			this.totalData = new SimpleLongProperty(total);
			this.rateData = new SimpleStringProperty(rate + (needPercentage ? "%" : ""));
		}
		
		public String getCountryName() {
			return this.countryName.get();
		}
		
		public Long getTotalData() {
			return this.totalData.get();
		}
		
		public String getRateData() {
			return this.rateData.get();
		}
	}
	
	@FXML
    void selectTargetDate(ActionEvent event) {
		this.selectedDate = this.datePicker.getValue();
    }
	
	//to be overriden
	protected ArrayList<TableData> generateDataList(ArrayList<String> isoCodes, LocalDate targetDate) throws Exception{
		return null;
	}
	
	protected void generateTable(ArrayList<TableData> data, LocalDate date) {
		ObservableList<TableData> oList = FXCollections.observableArrayList(data);
		
		this.countryCol.setCellValueFactory(new PropertyValueFactory<TableView<TableData>, String>("countryName"));
		this.totalCol.setCellValueFactory(new PropertyValueFactory<TableView<TableData>, Long>("totalData"));
		this.rateCol.setCellValueFactory(new PropertyValueFactory<TableView<TableData>, String>("rateData"));
		
		this.dataTable.setItems(oList);
	}
	
	@Override
	final void handleConfirmSelection(ActionEvent event) {
		ArrayList<String> selectedIso = this.getSelectedIso();
		if(selectedIso.isEmpty()) {
			//handle error
		}
		if(this.selectedDate == null) {
			return;
		}
		try {
			ArrayList<TableData> dataList = this.generateDataList(selectedIso, selectedDate);
			this.generateTable(dataList, this.selectedDate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
