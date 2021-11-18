package comp3111.covid.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.util.Pair;

import java.time.LocalDate;
import java.util.ArrayList;

import org.controlsfx.control.CheckListView;

import comp3111.covid.datastorage.Database.DataTitle;

public class ChartTabsController extends TabController{

    @FXML protected Button confirmButton;
    @FXML protected DatePicker startDatePicker;
    @FXML protected DatePicker endDatePicker;
    @FXML protected CategoryAxis xAxis;
    @FXML protected NumberAxis yAxis;
    @FXML private LineChart<String, Number> dataChart;
    
    private LocalDate startDate = null;    
    private LocalDate endDate = null;
    
    
    @FXML
    void handleConfirmSelection(ActionEvent event) {
    	
    	xAxis.setLabel("Date");
    	yAxis.setLabel("Rate");
    	
    	this.dataChart.getData().clear();
    	ArrayList<XYChart.Series<String, Number>> data = generateChartData();
    	for(XYChart.Series<String, Number> series : data) {
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
    
    protected ArrayList<XYChart.Series<String, Number>> generateChartData(){
    	
    	ArrayList<XYChart.Series<String, Number>> result = new ArrayList<XYChart.Series<String, Number>>();
    	for(String iso : this.getSelectedIso()) {
    		ArrayList<Pair<LocalDate, Number>> source = this.getDatabase().searchChartData(iso, startDate, endDate, DataTitle.VAC);    		
    		XYChart.Series<String, Number> series = new XYChart.Series<>();
    		//
    		for(Pair<LocalDate, Number> data : source) {
    			series.getData().add(new XYChart.Data<String, Number>(data.getKey().toString(), data.getValue()));
    		}
    		
    		series.setName(iso);
    		result.add(series);
    	}
    	return result;
    	
    }
}
