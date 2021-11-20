package comp3111.covid.tabs;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;

import comp3111.covid.datastorage.*;
import javafx.util.Pair;
import javafx.util.StringConverter;

/**
 * place holder
 * @author ytc314
 *
 */
public class TabC2pageController extends ChartTabsController {
	
	@Override
	public void initialize() {
		
		super.initialize();
		
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