package comp3111.covid.tabs;

import java.time.LocalDate;

import comp3111.covid.datastorage.*;

public class TabB1pageController extends TableTabsController{
	
	@Override
	protected long getTotalDataFromDB(String isoCode, LocalDate targetDate) {
		return this.getDatabase().searchTotalData(isoCode, targetDate, DataTitle.DEATH);
	}
		
	@Override
	protected double getRateDataFromDB(String isoCode, LocalDate targetDate) {
		return this.getDatabase().searchRateData(isoCode, targetDate, DataTitle.DEATH);
	}
		
	@Override
	protected void setTableTitle() {
		this.tableTitlelbl.setText("Number of Confirmed COVID-19 Deaths as of " + this.selectedDate);
	}
	
	@Override
	protected TableData getTableData(String iso, long totalData, double rateData) {
		return new TableData(iso, totalData, rateData, false);
	}
}