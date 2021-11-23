package comp3111.covid.tabs;

import java.time.LocalDate;

import comp3111.covid.datastorage.DataTitle;
/**
 * place holder
 * @author ytc314
 *
 */
public class TabC1pageController extends TableTabsController{
	
	@Override
	protected long getTotalDataFromDB(String isoCode, LocalDate targetDate) {
		return this.getDatabase().searchTotalData(isoCode, targetDate, DataTitle.VAC);
	}
		
	@Override
	protected double getRateDataFromDB(String isoCode, LocalDate targetDate) {
		return this.getDatabase().searchRateData(isoCode, targetDate, DataTitle.VAC);
	}
		
	@Override
	protected void setTableTitle() {
		this.tableTitlelbl.setText("Rate of Vaccination against COVID-19 as of " + this.selectedDate);
	}
	
	@Override
	protected TableData getTableData(String iso, long totalData, double rateData) {
		return new TableData(iso, totalData, rateData, true);
	}
}