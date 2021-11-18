package comp3111.covid.tabs;

import java.time.LocalDate;
import java.util.ArrayList;

import comp3111.covid.dataAnalysis.DateConverter;
import comp3111.covid.datastorage.Database.DataTitle;
import javafx.event.ActionEvent;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;

public class TabC2pageController extends ChartTabsController {
	
	@Override
	protected ArrayList<XYChart.Series<Number, Number>> generateChartData(){
		DateConverter dateConverter = new DateConverter();
    	ArrayList<XYChart.Series<Number, Number>> result = new ArrayList<XYChart.Series<Number, Number>>();
    	
    	for(String iso : this.getSelectedIso()) {
    		ArrayList<Pair<LocalDate, Number>> source = this.getDatabase().searchChartData(iso, startDate, endDate, DataTitle.VAC);    		
    		XYChart.Series<Number, Number> series = new XYChart.Series<>();
    		//
    		for(Pair<LocalDate, Number> data : source) {
    			series.getData().add(new XYChart.Data<Number, Number>(dateConverter.dateToLong(data.getKey()), data.getValue()));
    		}
    		
    		series.setName(this.getDatabase().getLocationName(iso));
    		result.add(series);
    	}
    	return result;
    }
	
	@Override
	void handleConfirmSelection(ActionEvent event) {
    	
    	xAxis.setLabel("Date");
    	xAxis.setForceZeroInRange(false);
    	
    	yAxis.setLabel("Rate");
    	
    	this.dataChart.getData().clear();
    	this.dataChart.setTitle("Cumulative Rate of Vaccination Against COVID-19");
    	this.dataChart.setCreateSymbols(false);
    	
    	ArrayList<XYChart.Series<Number, Number>> data = generateChartData();
    	for(XYChart.Series<Number, Number> series : data) {
    		this.dataChart.getData().add(series);
    	}
    }
}
