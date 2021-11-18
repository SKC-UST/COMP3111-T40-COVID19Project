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
	protected ArrayList<Pair<LocalDate, Number>> getDateDataPair(String iso, LocalDate startDate, LocalDate endDate) { 
    	return this.getDatabase().searchChartData(iso, startDate, endDate, DataTitle.VAC);
    }
	
	@Override
	protected void setYAxisTitle() {
    	this.yAxis.setLabel("Vaccination Rate");
    }
}