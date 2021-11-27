package comp3111.covid.tabs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import comp3111.covid.Context;
import comp3111.covid.dataAnalysis.DateConverter;

/**
* This is an abstract class representing the controllers of all the tabs that displays charts.
* This class is inherited by TabA2pageController, TabB2pageController, and TabC2pageController
*/
public abstract class ChartTabsController extends TabController{

    @FXML private Button confirmButton;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private NumberAxis xAxis;
    @FXML protected NumberAxis yAxis;
    @FXML private LineChart<Number, Number> dataChart;
    /**
     * The chosen lower bound of the date range.
     * Updated whenever a new startDate is chosen by the StartDatePicker.
     */
    protected LocalDate startDate = null;
    /**
     * The chosen upper bound of the desired date range.
     * Updated whenever a new endDate is chosen by the EndDatePicker.
     */
    protected LocalDate endDate = null;
    /**
     * {@link comp3111.covid.dataAnlysis.DateConverter}
     */
    private DateConverter dc = Context.getInstance().getDateConverter();
    /**
     * This method initializes the controller.
     * It is called after all the FXML elements are loaded.
     * In particular, it sets facilitates the conversion of the numerical values in the x-axis to String showing dates.
     */
    public void initialize() {
    	super.initialize();
    	xAxis.setTickLabelFormatter(new StringConverter<Number>() {
    		@Override
    		public String toString(Number dateNum) {
    			return dc.longToString(dateNum.longValue());
    		}
    		
    		@Override
    		public Number fromString(String string) {
    			return dc.dateToLong(LocalDate.parse(string));
    		}
    	});
    }
    @Override
    public void updateCheckboxList() {
    	super.updateCheckboxList();
    	this.dataChart.getData().clear();
    }
    
    /**
     * This method handles input errors.
     * It is used to ensure the user's input are correct.
     * It handles 6 types of errors:
     * 1. No Country is chosen.
     * 2. No Start date of the desired period is chosen.
     * 3. No End date of the desired period is chosen.
     * 4. The Start date is after the end date.
     * 5. The start date and the end date are the same day
     * 6. The the start date or the end date is out of the range of the chosen dataset.
     * A pop-up window with the corresponding message will remind the user of the error.
     * @return a negative integer error code if an error occurs, 0 if no error.
     */
    protected int handleChartError() {
    	if(this.getSelectedIso().isEmpty()) {
			this.handleError("Please Choose at Least One Country!", "Country Input Error");
			return -1;
		}
		if(this.startDate == null) {
			this.handleError("Please Choose a Start Date!", "Date Input Error");
			return -2;
		}
		if(this.endDate == null) {
			this.handleError("Please Choose an End Date", "Date Input Error");
			return -3;
		}
		if(this.startDate.isAfter(endDate)) {
			this.handleError("The start date cannot be after the end date!", "Date Input Error");
			return -4;
		}
		if(this.startDate.equals(endDate)) {
			this.handleError("The date range must span across at least two days!", "Date Input Error");
			return -5;
		}
		LocalDate earliestDate = getDatabase().getEarliest();
		LocalDate latestDate = getDatabase().getLatest();
		if(this.startDate.isBefore(earliestDate) || this.startDate.isAfter(latestDate) || this.endDate.isBefore(earliestDate) || this.endDate.isAfter(latestDate)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLL yyy"); 
			String startStr = getDatabase().getEarliest().format(formatter);
			String endStr = getDatabase().getLatest().format(formatter);
			this.handleError("The data in this data set starts from " + startStr + " and ends on " + endStr, "Date Out of Range");
			return -6;
		}
		return 0;
    }
    
    /**
     * This method is handles the event that the confirmatino button is pressed
     * It is reponsible for invoking {@link ChartTabsController#handleChartError() and {@link ChartTabsController#generateChartData()}.
     * Then it puts the generatend onto the chart for display.
     */
    @FXML
    void handleConfirmSelection(ActionEvent event) {
    	int errorCode = this.handleChartError();
    	if(errorCode != 0)
    		return;	//error occurs
    	
    	this.dataChart.getData().clear();
    	ArrayList<XYChart.Series<Number, Number>> data = generateChartData();    	
    	for(XYChart.Series<Number, Number> series : data) {
    		this.dataChart.getData().add(series);
    	}
    }

    @FXML
    /**
     * This method changes the value of {@link ChartTabsController#startDate} when a new value is chosen by the date picker.
     * @param event	JavaFX action event
     */
    void selectStartDate(ActionEvent event) {
    	this.startDate = this.startDatePicker.getValue();
    }

    @FXML
    /**
     * This method changes the value of {@link ChartTabsController#endDate} when a new value is chosen by the date picker.
     * @param event	JavaFX action event
     */
    void selectEndDate(ActionEvent event) {
    	this.endDate = this.endDatePicker.getValue();
    }
    /**
     * This method generates series for the chart using the given user input and data from the dataset.
     * @return	an ArrayList of {@link javafx.scene.chart.XYChart.Series}.
     */
	protected ArrayList<XYChart.Series<Number, Number>> generateChartData(){
		DateConverter dateConverter = new DateConverter();
    	ArrayList<XYChart.Series<Number, Number>> result = new ArrayList<XYChart.Series<Number, Number>>();
    	
    	for(String iso : this.getSelectedIso()) {
    		ArrayList<Pair<LocalDate, Number>> source = this.getDateDataPair(iso, this.startDate, this.endDate);
    		
    		if(source.isEmpty()) {
        		JOptionPane.showMessageDialog(null, "No data found for " + getDatabase().getLocationName(iso) + " between " + startDate + " and " + endDate + 
        				"\nTry another date or country", "No data found for " + getDatabase().getLocationName(iso), JOptionPane.WARNING_MESSAGE);
        		return null;
        	}
    		
    		XYChart.Series<Number, Number> series = new XYChart.Series<>();
    		//
    		for(Pair<LocalDate, Number> data : source) {
    			series.getData().add(new XYChart.Data<Number, Number>(dateConverter.dateToLong(data.getKey()), data.getValue()));
    		}
    		
    		series.setName(this.getDatabase().getLocationName(iso));
    		result.add(series);
    	}
    	LocalDateTime currentTime = LocalDateTime.now();
		String formattedTime = DateTimeFormatter.ofPattern("HH:mm:ss").format(currentTime);
		String importMessage = "[ " + formattedTime + " ] " + "Successfully generated chart\n";
		System.out.println(importMessage);
    	return result;
    }
    
	/**
     * This is method generates a list of data with the corresponding date range and desired location.
     * The returned data has x-value being the date of the data, and y-value being the value of the data.
     * @param iso		iso code of the desired location.
     * @param startDate	lower bound of the desired period.
     * @param endDate	uppert bound of the desired period.
     * @return			{@link java.util.ArrayList} of {@link javafx.util.Pair} representing the generated data.
     */
    protected abstract ArrayList<Pair<LocalDate, Number>> getDateDataPair(String iso, LocalDate startDate, LocalDate endDate);
}
