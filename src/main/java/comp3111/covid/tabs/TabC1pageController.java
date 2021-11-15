package comp3111.covid.tabs;

import java.time.LocalDate;
import java.util.ArrayList;

import comp3111.covid.datastorage.Database;
import comp3111.covid.datastorage.Database.DataTitle;
import comp3111.covid.tabs.TableTabsController.TableData;
import javafx.event.ActionEvent;

public class TabC1pageController extends TableTabsController{
	
	@Override
	protected ArrayList<TableData> generateDataList(ArrayList<String> isoCodes, LocalDate targetDate) throws Exception{
		ArrayList<TableData> result = new ArrayList<TableData>();
		Database db = this.getDatabase();
		for(String isoCode : isoCodes) {
			long totalData = db.searchTotalData(isoCode, targetDate, DataTitle.VAC);
			double rateData = db.searchRateData(isoCode, targetDate, DataTitle.VAC);
			if(totalData < 0 || rateData < 0) {
				//handle error
				System.out.println("Error: " + isoCode + " " + targetDate + " " + (totalData < 0 ? "total" : "rate"));
				return null;
			}
			result.add(new TableData(db.getLocationName(isoCode), totalData, rateData));
		}
		return result;
	}
}