package comp3111.covid.tabs;

import java.time.LocalDate;
import java.util.ArrayList;

import comp3111.covid.datastorage.Database.DataTitle;
import javafx.event.ActionEvent;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;

public class TabC2pageController extends ChartTabsController {
	
	@Override
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
	
	@Override
	void handleConfirmSelection(ActionEvent event) {
    	
    	xAxis.setLabel("Date");
    	yAxis.setLabel("Rate");
    	
    	this.dataChart.getData().clear();
    	this.dataChart.setTitle("Cumulative Rate of Vaccination Against COVID-19");
    	this.dataChart.setCreateSymbols(false);
    	
    	ArrayList<XYChart.Series<String, Number>> data = generateChartData();
    	for(XYChart.Series<String, Number> series : data) {
    		this.dataChart.getData().add(series);
    	}
    }
}
