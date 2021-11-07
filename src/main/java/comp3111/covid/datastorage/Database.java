package comp3111.covid.datastorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Database {
	
	private ArrayList<String[]> arrayStorage = new ArrayList<String[]>();
	private HashMap<String, LocationData> hashStorage = new HashMap<String, LocationData>(); //isoCode as key
	private boolean datasetPresent = false;
	public enum DataTitle {CASE, DEATH, VAC}
	
	private interface DayData<T> {
		public Date getDate();
		public T getData();
	}
	
	private class TotalDayData implements DayData<Long> {
		private Date dataDate;
		private long totalData;
		
		TotalDayData(Date date, long data){
			this.dataDate = date;
			this.totalData = data;
		}
		
		@Override
		public Date getDate() {
			return this.dataDate;
		}
		
		@Override
		public Long getData() {
			return this.totalData;
		}
	}
	
	private class RateDayData implements DayData<Double> {
		private Date dataDate;
		private double rateData;
		
		RateDayData(Date date, double data){
			this.dataDate = date;
			this.rateData = data;
		}
		
		@Override
		public Date getDate() {
			return this.dataDate;
		}
		
		@Override
		public Double getData() {
			return this.rateData;
		}
	}
	
	private class LocationData {
		private String locationIsoCode;
		private String locationContinent;
		private String locationName;
		private long locationPopulation;
		private ArrayList<TotalDayData> caseTotalList = new ArrayList<TotalDayData>();
		private ArrayList<TotalDayData> deathTotalList = new ArrayList<TotalDayData>();
		private ArrayList<TotalDayData> vacTotalList = new ArrayList<TotalDayData>();
		private ArrayList<RateDayData> caseRateList = new ArrayList<RateDayData>();
		private ArrayList<RateDayData> deathRateList = new ArrayList<RateDayData>();
		private ArrayList<RateDayData> vacRateList = new ArrayList<RateDayData>();
		
		LocationData(String isoCode, String continent, String location, long population) {
			this.locationIsoCode = isoCode;
			this.locationContinent = continent;
			this.locationName = location;
			this.locationPopulation = population;
		}
		
		private void addDayData(DataTitle dataTitle, DayData newDayData) {
			if(newDayData instanceof TotalDayData) {
				switch(dataTitle) {
					case CASE:
						this.caseTotalList.add((TotalDayData) newDayData);
						break;
					case DEATH:
						this.deathTotalList.add((TotalDayData) newDayData);
						break;
					case VAC:
						this.deathTotalList.add((TotalDayData) newDayData);
						break;
				}
			}
			else {
				switch(dataTitle) {
					case CASE:
						this.caseRateList.add((RateDayData) newDayData);
						break;
					case DEATH:
						this.deathRateList.add((RateDayData) newDayData);
						break;
					case VAC:
						this.deathRateList.add((RateDayData) newDayData);
						break;
				}
			}
		}
		
		// get single DayData
		private ArrayList<TotalDayData> getTotalDayData(Date targetDate, DataTitle dataTitle){
			
		}
		private DayData getRateData(Date targetData, DataTitle dataTitle) {
			ArrayList<DayData> targetType = this.dayDataMap.get(dataTitle);
			for(DayData elem : targetType) {
				if(elem.getDate().equals(targetData)) {
					return elem;
				}
			}
			return null;
		}
		
		// get DayData across a date range (inclusive)
		private ArrayList<DayData> getDayData(DataTitle dataTitle, Date startDate, Date endDate) {
			ArrayList<DayData> targetType = this.dayDataMap.get(dataTitle);
			ArrayList<DayData> result = new ArrayList<DayData>();
			for(DayData elem : targetType) {
				Date elemDate = elem.getDate();
				if (!startDate.after(elemDate) && !endDate.before(elemDate)) {
					result.add(elem);
				}
			}
			return result;
		}
		
		private void clearLocationData() {
			Iterator<Entry<DataTitle, ArrayList<DayData>>> it = this.dayDataMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<DataTitle, ArrayList<DayData>> pair = (Map.Entry<DataTitle, ArrayList<DayData>>)it.next();
				pair.getValue().clear();
			}
			dayDataMap.clear();
		}
	}
	
	private void rowToData(String[] row) {
		String isoCode = row[0];
		if(!(hashStorage.containsKey(isoCode))) {
			long populationNum = Long.parseLong(row[44]);
			hashStorage.put(isoCode, new LocationData(isoCode, row[1], row[2], populationNum));
		}
		int caseData, deathsData, vacData, caseRate;
		
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
	public double searchData(String isoCode, Date targetDate, RateDataTitle title) {
		double result = (double) this.hashStorage.get(isoCode).getDayData(targetDate, title).getData();
		return result;
	}
	
	public long searchData(String isoCode, Date targetDate, TotalDataTitle title) {
		long result = (long) this.hashStorage.get(isoCode).getDayData(targetDate, title).getData();
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
	
	public void clearDatabase () {
		Iterator it = hashStorage.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, LocationData> pair = (Map.Entry<String, LocationData>)it.next();
			pair.getValue().clearLocationData();
		}
		hashStorage.clear();
	}
	
	public void printDatabaseContent() {
		//print the whole database for checking
		for(LocationData location : this.hashStorage.values()) {
			System.out.println(location.locationIsoCode + "\t" + location.locationName);
			for(DayData day : location.dataList) {
				System.out.println(day.getDayDataContent(DataTitle.CASES) + "\t" + day.getDayDataContent(DataTitle.DEATHS) + "\t" + day.getDayDataContent(DataTitle.VACCINATIONS));
			}
		}
	}
}
