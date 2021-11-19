package comp3111.covid.tabs;

import java.time.LocalDate;
import java.util.ArrayList;

import comp3111.covid.datastorage.*;
import comp3111.covid.tabs.TableTabsController.TableData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TabB1pageController extends TableTabsController{
	
	@Override
	protected ArrayList<TableData> generateDataList(ArrayList<String> isoCodes, LocalDate targetDate) throws Exception{
		ArrayList<TableData> result = new ArrayList<TableData>();
		Database db = this.getDatabase();
		for(String isoCode : isoCodes) {
			long totalData = db.searchTotalData(isoCode, targetDate, DataTitle.DEATH); // search all death data
			double rateData = db.searchRateData(isoCode, targetDate, DataTitle.DEATH); // search all death rate data
			if(totalData < 0 || rateData < 0) {
				//handle error
				System.out.println("Error: " + isoCode + " " + targetDate + " " + (totalData < 0 ? "total" : "rate"));
				return null;
			}
			result.add(new TableData(db.getLocationName(isoCode), totalData, rateData, false));
		}
		return result;
	}
	
	@Override
	protected void generateTable(ArrayList<TableData> data, LocalDate date) {
		ObservableList<TableData> oList = FXCollections.observableArrayList(data);
		
		this.tableTitlelbl.setText("Number of Confirmed COVID-19 Deaths as of " + date);
		
		this.countryCol.setCellValueFactory(new PropertyValueFactory<TableView<TableData>, String>("countryName"));
		this.totalCol.setCellValueFactory(new PropertyValueFactory<TableView<TableData>, Long>("totalData"));
		this.rateCol.setCellValueFactory(new PropertyValueFactory<TableView<TableData>, String>("rateData"));
		
		this.dataTable.setItems(oList);
	}
}