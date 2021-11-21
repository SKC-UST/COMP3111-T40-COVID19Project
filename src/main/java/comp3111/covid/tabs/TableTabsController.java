package comp3111.covid.tabs;

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

import comp3111.covid.datastorage.Database;

public class TableTabsController extends TabController {
	@FXML private DatePicker datePicker;
	@FXML private Button confirmButton;
	@FXML protected TableColumn<TableView<TableData>,String> countryCol;
	@FXML protected TableColumn<TableView<TableData>,String> totalCol;
	@FXML protected TableColumn<TableView<TableData>,String> rateCol;
	@FXML protected TableView<TableData> dataTable;
	@FXML protected Label tableTitlelbl;
	
	protected LocalDate selectedDate = null;
	
	public class TableData {
		private final SimpleStringProperty countryName;
		private final SimpleStringProperty totalData;
		private final SimpleStringProperty rateData;

		TableData (String location, long total, double rate, boolean needPercentage) {
			this.countryName = new SimpleStringProperty(location);
			this.totalData = new SimpleStringProperty(total >= 0 ? String.valueOf(total) : "No Data");
			this.rateData = new SimpleStringProperty(rate >= 0 ? rate + (needPercentage ? "%" : "") : "No Data");
		}
		
		public String getCountryName() {
			return this.countryName.get();
		}
		
		public String getTotalData() {
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
	
	protected ArrayList<TableData> generateDataList(ArrayList<String> isoCodes, LocalDate targetDate) throws Exception{
		ArrayList<TableData> result = new ArrayList<TableData>();
		Database db = this.getDatabase();
		for(String isoCode : isoCodes) {
			long totalData = this.getTotalDataFromDB(isoCode, targetDate);
			double rateData = this.getRateDataFromDB(isoCode, targetDate);
			result.add(this.getTableData(db.getLocationName(isoCode), totalData, rateData));
		}
		return result;
	}
	
	protected void generateTable(ArrayList<TableData> data, LocalDate date) {
		ObservableList<TableData> oList = FXCollections.observableArrayList(data);
		
		this.countryCol.setCellValueFactory(new PropertyValueFactory<TableView<TableData>, String>("countryName"));
		this.totalCol.setCellValueFactory(new PropertyValueFactory<TableView<TableData>, String>("totalData"));
		this.rateCol.setCellValueFactory(new PropertyValueFactory<TableView<TableData>, String>("rateData"));
		this.setTableTitle();
		
		this.dataTable.setItems(oList);
	}
	
	//TODO: Override
	protected long getTotalDataFromDB(String isoCode, LocalDate targetDate) {
		return this.getDatabase().searchTotalData(isoCode, targetDate, null);
	}
	
	//TODO: Override
	protected double getRateDataFromDB(String isoCode, LocalDate targetDate) {
		return this.getDatabase().searchRateData(isoCode, targetDate, null);
	}
	
	//TODO: Override
	protected void setTableTitle() {
		this.tableTitlelbl.setText("Table Title");
	}
	
	//TODO: Override
	protected TableData getTableData(String iso, long totalData, double rateData) {
		return null;
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
