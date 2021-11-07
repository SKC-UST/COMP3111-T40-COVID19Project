package comp3111.covid.datastorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Database {
	
	private ArrayList<String[]> arrayStorage = new ArrayList<String[]>();
	private HashMap<String, LocationData> hashStorage = new HashMap<String, LocationData>(); //isoCode as key
	private boolean datasetPresent = false;
	public enum DataTitle {CASES, DEATHS, VACCINATIONS};
	
	private class LocationData {
		private String locationIsoCode;
		private String locationContinent;
		private String locationName;
		private ArrayList<DayData> dataList = new ArrayList<DayData>();
		
		private class DayData {
			//Represents a row in the CSV file
			private Date dataDate;
			private int newCases;
			private int newDeaths;
			private int newVaccinations;
			
			DayData(String date, String caseNum, String deathsNum, String vcaccinationNum){
				try {
					this.dataDate = new SimpleDateFormat("MM/dd/yyyy").parse(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.newDeaths = Integer.parseInt(caseNum);
				this.newCases = Integer.parseInt(deathsNum);
				this.newVaccinations = Integer.parseInt(vcaccinationNum);
			}
			
			DayData(String date, int caseNum, int deathsNum, int vcaccinationNum){
				try {
					this.dataDate = new SimpleDateFormat("MM/dd/yyyy").parse(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.newDeaths = caseNum;
				this.newCases = deathsNum;
				this.newVaccinations = vcaccinationNum;
			}

			private Date getDate() {
				return dataDate;
			}
			
			// get single data value
			private int getDayDataContent(DataTitle title) {
				switch (title) {
				 	case CASES:
				 		return newCases;
				 	case DEATHS:
				 		return newDeaths;
				 	case VACCINATIONS:
				 		return newVaccinations;
				}
				return -1;
			}
			
		}
		
		LocationData(String isoCode, String continent, String location) {
			this.locationIsoCode = isoCode;
			this.locationContinent = continent;
			this.locationName = location;
		}
		
		private void addDayData(DayData newDayData) {
			dataList.add(newDayData);
		}
		
		// get single DayData
		private DayData getDayData(Date targetData) {
			for(DayData elem : this.dataList) {
				if(elem.getDate().equals(targetData)) {
					return elem;
				}
			}
			return null;
		}
		
		// get DayData across a date range (inclusive)
		private ArrayList<DayData> getDayData(Date startDate, Date endDate) {
			ArrayList<DayData> result = new ArrayList<DayData>();
			for(DayData elem : this.dataList) {
				if (!startDate.after(elem.dataDate) && !endDate.before(elem.dataDate)) {
					result.add(elem);
				}
			}
			return result;
		}
		
		private void printDataList() {
			for(DayData elem : this.dataList) {
				System.out.println(elem.dataDate + "\t" + elem.newCases);
			}
		}
		
	}
	
	private void rowToData(String[] row) {
		String isoCode = row[0];
		if(!(hashStorage.containsKey(isoCode))) {
			hashStorage.put(isoCode, new LocationData(isoCode, row[1], row[2]));
		}
		int caseData, deathsData, vacData;
		
		// TODO: change from new cases to cumulative/total cases as of given date
		try { caseData = Integer.parseInt(row[3]); } catch(Exception e) { caseData = 0; }
		try { deathsData = Integer.parseInt(row[5]); } catch(Exception e) { deathsData = 0; }
		try { vacData = Integer.parseInt(row[37]); } catch(Exception e) { vacData = 0; }
		
		hashStorage.get(isoCode).addDayData(hashStorage.get(isoCode).new DayData(row[3], caseData, deathsData, vacData));
		
	}
	
	public void importCSV(File csvDataset) {
		try {
			CSVReader csvReader = new CSVReader(new FileReader(csvDataset));
			String[] nextRecord = csvReader.readNext();
			
			while((nextRecord = csvReader.readNext()) != null) {
				rowToData(nextRecord); // add DayData to LocationData
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// search single data - for Table
	// return null if no data is found
	public int searchData(String isoCode, Date targetDate, DataTitle title) {
		int result = this.hashStorage.get(isoCode).getDayData(targetDate).getDayDataContent(title);
		return result;
	}
	
	// search a list of data over dates - for Chart
	public ArrayList<Integer> searchData(String isoCode, Date startDate, Date endDate, DataTitle title){
		ArrayList<Integer> results = new ArrayList<Integer>();
		ArrayList<LocationData.DayData> targetDays = this.hashStorage.get(isoCode).getDayData(startDate, endDate);
		for(LocationData.DayData days : targetDays) {
			results.add(days.getDayDataContent(title));		
		}
		return results;
	}
	
	
	public void printDatabaseContent() {
		//print the whole database for checking
	}
}
