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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import comp3111.covid.datastorage.Database;

/**
 * place holder
 * @author ytc314
 *
 */
public class TableTabsController extends TabController {
	@FXML private DatePicker datePicker;
	@FXML private Button confirmButton;
	@FXML protected TableColumn<TableView<TableData>,String> countryCol;
	@FXML protected TableColumn<TableView<TableData>,String> totalCol;
	@FXML protected TableColumn<TableView<TableData>,String> rateCol;
	@FXML protected TableView<TableData> dataTable;
	@FXML protected Label tableTitlelbl;
	
	protected LocalDate selectedDate = null;
	
	@Override
	public void initialize() {
		super.initialize();
		
		this.countryCol.setCellValueFactory(new PropertyValueFactory<TableView<TableData>, String>("countryName"));
		this.totalCol.setCellValueFactory(new PropertyValueFactory<TableView<TableData>, String>("totalData"));
		this.rateCol.setCellValueFactory(new PropertyValueFactory<TableView<TableData>, String>("rateData"));
	}
	
	protected int handleTableError() {
		if(this.getSelectedIso().isEmpty()) {
			this.handleError("Please Choose at Least One Country!", "Country Input Error");
			return -1;
		}
		if(this.selectedDate == null) {
			this.handleError("Please Choose a Date!", "Date Input Error");
			return -2;
		}
		if(this.selectedDate.isBefore(getDatabase().getEarliest()) || this.selectedDate.isAfter(getDatabase().getLatest())) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLL yyy"); 
			String startStr = getDatabase().getEarliest().format(formatter);
			String endStr = getDatabase().getLatest().format(formatter);
			this.handleError("The data in this data set starts from " + startStr + " and ends on " + endStr, "Date Out of Range");
			return -3;
		}
		return 0;
	}
	
	public class TableData {
		private final SimpleStringProperty countryName;
		private final SimpleStringProperty totalData;
		private final SimpleStringProperty rateData;

		public TableData (String location, long total, double rate, boolean needPercentage) {
			this.countryName = new SimpleStringProperty(location);
			this.totalData = new SimpleStringProperty(total >= 0 ? String.format("%,d", total) : "No Data");
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
	
	protected ArrayList<TableData> generateDataList(ArrayList<String> isoCodes, LocalDate targetDate) {
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
		this.setTableTitle();
		
		this.dataTable.setItems(oList);
	}
	
	//TODO: Override
	protected long getTotalDataFromDB(String isoCode, LocalDate targetDate) {
		return 0;
	}
	
	//TODO: Override
	protected double getRateDataFromDB(String isoCode, LocalDate targetDate) {
		return 0;
	}
	
	//TODO: Override
	protected void setTableTitle() {}
	
	//TODO: Override
	protected TableData getTableData(String iso, long totalData, double rateData) {
		return null;
	}
	
	@Override
	final void handleConfirmSelection(ActionEvent event) {
		int errorCode = this.handleTableError();
		if(errorCode != 0)
			return;
		ArrayList<String> selectedIso = this.getSelectedIso();
		ArrayList<TableData> dataList = this.generateDataList(selectedIso, selectedDate);
		this.generateTable(dataList, this.selectedDate);
		LocalDateTime currentTime = LocalDateTime.now();
		String formattedTime = DateTimeFormatter.ofPattern("HH:mm:ss").format(currentTime);
		String importMessage = "[ " + formattedTime + " ] " + "Successfully generated table\n";
		System.out.println(importMessage);

		this.generateTable(dataList, this.selectedDate);
	}
}
