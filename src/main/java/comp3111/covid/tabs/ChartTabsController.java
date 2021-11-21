package comp3111.covid.tabs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import comp3111.covid.Context;
import comp3111.covid.dataAnalysis.DateConverter;

/**
 * place holder
 * @author ytc314
 *
 */
public class ChartTabsController extends TabController{

    @FXML protected Button confirmButton;
    @FXML protected DatePicker startDatePicker;
    @FXML protected DatePicker endDatePicker;
    @FXML protected NumberAxis xAxis;
    @FXML protected NumberAxis yAxis;
    @FXML protected LineChart<Number, Number> dataChart;
    
    protected LocalDate startDate = null;    
    protected LocalDate endDate = null;
    
    private DateConverter dc = Context.getInstance().getDateConverter();
    
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
    
    @FXML
    //Generate the acutal chart
    void handleConfirmSelection(ActionEvent event) {
    	ArrayList<String> selectedIso = this.getSelectedIso();
    	if(selectedIso.isEmpty()) {
			this.handleError("Please Choose at Least One Country!", "Country Input Error");
			return;
		}
		if(this.startDate == null) {
			this.handleError("Please Choose a Start Date!", "Date Input Error");
			return;
		}
		if(this.endDate == null) {
			this.handleError("Please Choose an End Date", "Date Input Error");
			return;
		}
		if(this.startDate.isAfter(endDate)) {
			this.handleError("The start date cannot be after the end date!", "Date Input Error");
			return;
		}
		if(this.startDate.equals(endDate)) {
			this.handleError("The date range must span across at least two days!", "Date Input Error");
			return;
		}
		if(this.startDate.isBefore(getDatabase().getEarliest()) || this.endDate.isAfter(getDatabase().getLatest())) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLL yyy"); 
			String startStr = getDatabase().getEarliest().format(formatter);
			String endStr = getDatabase().getLatest().format(formatter);
			this.handleError("The data in this data set starts from " + startStr + " and ends on " + endStr, "Date Out of Range");
			return;
		}
    	
    	
    	this.dataChart.getData().clear();
    	ArrayList<XYChart.Series<Number, Number>> data = generateChartData();    	
    	for(XYChart.Series<Number, Number> series : data) {
    		this.dataChart.getData().add(series);
    	}
    }

    @FXML
    void selectStartDate(ActionEvent event) {
    	this.startDate = this.startDatePicker.getValue();
    }

    @FXML
    void selectEndDate(ActionEvent event) {
    	this.endDate = this.endDatePicker.getValue();
    }
    
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
    
    //to be overriden
    protected ArrayList<Pair<LocalDate, Number>> getDateDataPair(String iso, LocalDate startDate, LocalDate endDate) { 
    	return null;
    }
}
