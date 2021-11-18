package comp3111.covid.tabs;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;

import comp3111.covid.dataAnalysis.DateConverter;
import comp3111.covid.datastorage.Database.DataTitle;
import javafx.event.ActionEvent;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;
import javafx.util.StringConverter;

public class TabC2pageController extends ChartTabsController {
	
	@Override
	public void initialize() {
		
		xAxis.setTickLabelFormatter(new StringConverter<Number>() {
    		DateConverter dc = new DateConverter();
    		@Override
    		public String toString(Number dateNum) {
    			return dc.longToString(dateNum.longValue());
    		}
    		
    		@Override
    		public Number fromString(String string) {
    			return dc.dateToLong(LocalDate.parse(string));
    		}
    	});
		
		yAxis.setTickLabelFormatter(new StringConverter<Number>() {
			@Override
			public String toString(Number rate) {
				return (rate + "%");
			}
			
			@Override
			public Number fromString(String string) {
				NumberFormat percentage = NumberFormat.getPercentInstance();
				try {
					return percentage.parse(string);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 0;
				}
			}
		});
		
	}
	
	@Override
	protected ArrayList<Pair<LocalDate, Number>> getDateDataPair(String iso, LocalDate startDate, LocalDate endDate) { 
    	return this.getDatabase().searchChartData(iso, startDate, endDate, DataTitle.VAC);
    }
	
}