package comp3111.covid.tabs;

import java.util.ArrayList;

import org.controlsfx.control.CheckListView;

import comp3111.covid.Context;
import comp3111.covid.datastorage.Database;
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
	@FXML private LineChart<Number, Number> regressionChart;
	
	public void initialize() {
		System.out.println("init");
		
		
		
		xAxis.setLabel("Number of Month");		
		
		
		this.regressionChart.setTitle("Stock Monitoring");
		
		XYChart.Series<Number, Number> series = new XYChart.Series<>();
		series.setName("My portfolio");
		
		series.getData().add(new XYChart.Data(1,23));
		
		this.regressionChart.getData().add(series);
	}
	
	private void updateChart() {
		
	}
}

