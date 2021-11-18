package comp3111.covid.tabs;

import java.time.LocalDate;
import java.util.ArrayList;

import comp3111.covid.datastorage.Database;
import comp3111.covid.datastorage.Database.DataTitle;
import comp3111.covid.tabs.TableTabsController.TableData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;

public class TabB2pageController extends ChartTabsController{
	
	
	@Override
	protected ArrayList<XYChart.Series<String, Number>> generateChartData(){
    	
    	ArrayList<XYChart.Series<String, Number>> result = new ArrayList<XYChart.Series<String, Number>>();
    	for(String iso : this.getSelectedIso()) {
    		ArrayList<Pair<LocalDate, Number>> source = this.getDatabase().searchChartData(iso, startDate, endDate, DataTitle.DEATH);    		
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
	
	@Override
	void handleConfirmSelection(ActionEvent event) {
    	
    	xAxis.setLabel("Date");
    	yAxis.setLabel("Rate");
    	
    	this.dataChart.getData().clear();
    	this.dataChart.setTitle("Cumulative Confirmed COVID-19 Deaths (per 1M)");
    	this.dataChart.setCreateSymbols(false);
    	
    	ArrayList<XYChart.Series<String, Number>> data = generateChartData();
    	for(XYChart.Series<String, Number> series : data) {
    		this.dataChart.getData().add(series);
    	}
    }
}