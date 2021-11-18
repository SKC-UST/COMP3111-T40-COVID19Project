package comp3111.covid.tabs;

import java.time.LocalDate;
import java.util.ArrayList;

import org.controlsfx.control.CheckListView;

import comp3111.covid.Context;
import comp3111.covid.datastorage.Database;
import comp3111.covid.datastorage.Database.DataTitle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class TabC3pageController extends TabController{
	
	
	@FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;
	@FXML private LineChart<LocalDate, Number> regressionChart;
	/*
	public void initialize() {
		System.out.println("init");
		
		ArrayList<Double> vacTotal = this.getDatabase().searchRateData("HKG", LocalDate.of(2021, 3, 3), LocalDate.of(2021, 3, 13), DataTitle.VAC);
		ArrayList<LocalDate> dates = new ArrayList<LocalDate>();
		
		for(int i = 3; i <= 13; ++i) {
			dates.add(LocalDate.of(2021, 3, i));
		}
		
		xAxis.setLabel("Date");	
		xAxis.setAutoRanging(true);
		yAxis.setAutoRanging(true);
		
		
		this.regressionChart.setTitle("Stock Monitoring");
		XYChart.Series<LocalDate, Number> series = new XYChart.Series<>();
		series.setName("Linear Regression");
		
		for(int i = 0; i < dates.size(); ++i) {
			//series.getData().add(Data(dates.get(i), vacTotal.get(i)));
		}
		
		this.regressionChart.getData().add(series);
	}
*/
		
	private void updateChart() {
		
	}
}

