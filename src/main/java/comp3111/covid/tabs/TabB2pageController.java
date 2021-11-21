package comp3111.covid.tabs;

import java.time.LocalDate;
import java.util.ArrayList;

import comp3111.covid.datastorage.DataTitle;
import javafx.util.Pair;
/**
 * place holder
 * @author ytc314
 *
 */
public class TabB2pageController extends ChartTabsController {
	
	@Override
	protected ArrayList<Pair<LocalDate, Number>> getDateDataPair(String iso, LocalDate startDate, LocalDate endDate) { 
    	return this.getDatabase().searchChartData(iso, startDate, endDate, DataTitle.DEATH);
    }
	
}