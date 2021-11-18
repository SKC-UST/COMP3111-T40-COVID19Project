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
    @FXML
	protected LineChart<String, Number> dataChart;
    
    protected LocalDate startDate = null;    
    protected LocalDate endDate = null;
    
    
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
    
    //to be overriden
    protected ArrayList<XYChart.Series<String, Number>> generateChartData(){
    	return null;
    }
}
