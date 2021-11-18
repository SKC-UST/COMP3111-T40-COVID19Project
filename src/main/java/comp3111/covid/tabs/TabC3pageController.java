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
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Pair;

public class TabC3pageController extends TabController{
	
	
	@FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;
	@FXML private LineChart<Number, Number> regressionChart;
	@FXML private Button testButton;
	
	@FXML
	void handleTestButton(ActionEvent event) {
		this.updateChart();
	}
		
	private void updateChart() {
		ArrayList<Pair<Number, Number>> populations = this.getDatabase().getRateLocationPair(LocalDate.of(2021, 4, 25));
		
		xAxis.setLabel("Population");
		yAxis.setLabel("Vaccination Rate");
		
		XYChart.Series<Number, Number> series = new XYChart.Series<>();
		series.setName("regression line");
		
		for(Pair<Number, Number> pop : populations) {
			series.getData().add(new Data<Number, Number>(pop.getKey(), pop.getValue()));
		}
		this.regressionChart.getData().add(series);
	}
}

