package comp3111.covid.tabs;

import java.time.LocalDate;
import java.util.ArrayList;

import comp3111.covid.datastorage.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * place holder
 * @author ytc314
 *
 */
public class TabA1pageController extends TableTabsController{
	
	@Override
	protected long getTotalDataFromDB(String isoCode, LocalDate targetDate) {
		return this.getDatabase().searchTotalData(isoCode, targetDate, DataTitle.CASE);
	}
		
	@Override
	protected double getRateDataFromDB(String isoCode, LocalDate targetDate) {
		return this.getDatabase().searchRateData(isoCode, targetDate, DataTitle.CASE);
	}
		
	@Override
	protected void setTableTitle() {
		this.tableTitlelbl.setText("Number of Confirmed COVID-19 Cases as of " + this.selectedDate);
	}
	
	@Override
	protected TableData getTableData(String iso, long totalData, double rateData) {
		return new TableData(iso, totalData, rateData, false);
	}
}