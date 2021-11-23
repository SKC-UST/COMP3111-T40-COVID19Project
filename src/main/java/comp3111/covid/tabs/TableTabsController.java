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
 * This is an abstract class representing the controllers of all the tabs that displays tables.
 * This class is inherited by TabA1pageController, TabB1pageController, and TabC1pageController
 */
public abstract class TableTabsController extends TabController {
	@FXML private DatePicker datePicker;
	@FXML private Button confirmButton;
	@FXML private TableColumn<TableView<TableData>,String> countryCol;
	@FXML private TableColumn<TableView<TableData>,String> totalCol;
	@FXML private TableColumn<TableView<TableData>,String> rateCol;
	@FXML private TableView<TableData> dataTable;
	@FXML protected Label tableTitlelbl;
	/**
	 * The target date chosen by the user.
	 * Updated whenever the user changes the value in the datepicker.
	 */
	protected LocalDate selectedDate = null;
	
	/**
	 * {@inheritDoc}
	 * In TableTabsController, this method is also responsible for setting {@link javafx.scene.control.cell.PropertyValueFactory} in each {@link javafx.scene.control.TableColumn} in the table.
	 */
	@Override
	public void initialize() {
		super.initialize();
		
		this.countryCol.setCellValueFactory(new PropertyValueFactory<TableView<TableData>, String>("countryName"));
		this.totalCol.setCellValueFactory(new PropertyValueFactory<TableView<TableData>, String>("totalData"));
		this.rateCol.setCellValueFactory(new PropertyValueFactory<TableView<TableData>, String>("rateData"));
	}
	/**
     * This method handles user input errors.
     * It handles 3 types of errors:
     * 1. No Country is chosen.
     * 2. No date is selected.
     * 3. The the target date is out of the range of the chosen dataset.
     * A pop-up window with the corresponding message will remind the user of the error.
     * @return a negative integer error code if an error occurs, 0 if no error.
     */
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
	/**
	 * A class holding data to be put into the table.
	 */
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
	/**
     * This method changes the value of {@link TableTabsController#selectedDate} to a new chosen date when a new value is chosen by the date picker.
     * @param event	JavaFX action event
     */
    void selectTargetDate(ActionEvent event) {
		this.selectedDate = this.datePicker.getValue();
    }
	/**
	 * This method generate an list of data to be put into the table, with the given user inputs of countries and date.
	 * @param isoCodes		A list of iso codes of the countries that the user has chosen.
	 * @param targetDate	The date chosen by the user.
	 * @return				An {@link java.util.ArrayList} of {@link TableData} of the given countries and date.
	 */
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
	/**
	 * This method puts data on the table.
	 * @param data	the list of data to be displayed.
	 * @param date	the chosen date, used for the title.
	 */
	protected void generateTable(ArrayList<TableData> data, LocalDate date) {
		ObservableList<TableData> oList = FXCollections.observableArrayList(data);
		this.setTableTitle();
		
		this.dataTable.setItems(oList);
	}
	
	/**
	 * This method fetches a value of data of the given location and date from the database.
	 * Return value is restricted to be that of cumulative number of cases, deaths, and people fully vaccinated.
	 * @param isoCode		iso code of the chosen location.
	 * @param targetDate	date of the user's choice.
	 * @return				a value of data of the given location and date.
	 */
	protected abstract long getTotalDataFromDB(String isoCode, LocalDate targetDate);
	
	/**
	 * This method fetches a value of data of the given location and date from the database.
	 * Return value is restricted to be that of cumulative number of cases per 1M people, deaths per 1M people, and rate of vaccination.
	 * @param isoCode		iso code of the chosen location
	 * @param targetDate	date of the user's choice
	 * @return				a value of the given parameters.
	 */
	protected abstract double getRateDataFromDB(String isoCode, LocalDate targetDate);
	
	/**
	 * This method sets the title of the table according to the type of the tab.
	 */
	protected abstract void setTableTitle();
	
	/**
	 * This method constructs a {@link TableData} object with the given parameters.
	 * Depending of the type of the tab, a percentage sign % may be added to the back of rate data during the construction.
	 * @param iso		Iso code of the desired location.
	 * @param totalData	Value for the second column.
	 * @param rateData	Value for the third column.
	 * @return			A {@link TableData} object consisting of the above values.
	 */
	protected abstract TableData getTableData(String iso, long totalData, double rateData);
	
	/**
	 * {@inheritDoc}
	 * In this instance, this method generates a list of {@link TableData} given the user's input with {@link TableTabsController#generateDataList(ArrayList, LocalDate)},
	 * and put them onto the table using {@link TableTabsController#generateTable(ArrayList, LocalDate)}.
	 */
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
