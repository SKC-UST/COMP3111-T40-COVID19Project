package comp3111.covid.tabs;

import java.time.LocalDate;
import java.util.ArrayList;

import comp3111.covid.dataAnalysis.DateConverter;
import comp3111.covid.datastorage.*;
import comp3111.covid.tabs.TableTabsController.TableData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;

public class TabA2pageController extends ChartTabsController{
	
	@Override
	protected ArrayList<Pair<LocalDate, Number>> getDateDataPair(String iso, LocalDate startDate, LocalDate endDate) { 
    	return this.getDatabase().searchChartData(iso, startDate, endDate, DataTitle.CASE);

    }
}