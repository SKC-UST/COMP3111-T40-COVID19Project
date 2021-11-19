package comp3111.covid.tabs;

import java.time.LocalDate;
import java.util.ArrayList;

import comp3111.covid.datastorage.*;
import javafx.util.Pair;

public class TabA2pageController extends ChartTabsController{
	
	@Override
	protected ArrayList<Pair<LocalDate, Number>> getDateDataPair(String iso, LocalDate startDate, LocalDate endDate) { 
    	return this.getDatabase().searchChartData(iso, startDate, endDate, DataTitle.CASE);

    }
}