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
		
		private ArrayList<TotalDayData> getTotalDayList(DataTitle dataTitle){
			switch(dataTitle) {
				case CASE:
					return this.caseTotalList;
				case DEATH:
					return this.deathTotalList;
				case VAC:
					return this.vacTotalList;
			}
			return null;
		}
		
		private ArrayList<RateDayData> getRateDayList(DataTitle dataTitle){
			switch(dataTitle) {
				case CASE:
					return this.caseRateList;
				case DEATH:
					return this.deathRateList;
				case VAC:
					return this.vacRateList;
			}
			return null;
		}
		
		private long getTotalDayData(Date targetDate, DataTitle dataTitle) {
			ArrayList<TotalDayData> targetTitle = null;
			switch(dataTitle) {
				case CASE:
					targetTitle = this.caseTotalList;
					break;
				case DEATH:
					targetTitle = this.deathTotalList;
					break;
				case VAC:
					targetTitle = this.vacTotalList;
					break;
			}
			for(TotalDayData day : targetTitle) {
				if(day.getDate().equals(targetDate)) {
					return day.getData();
				}
			}
			return -1; // not found
		}
		
		private double getRateDayData(Date targetDate, DataTitle dataTitle) {
			ArrayList<RateDayData> targetTitle = null;
			switch(dataTitle) {
				case CASE:
					targetTitle = this.caseRateList;
					break;
				case DEATH:
					targetTitle = this.deathRateList;
					break;
				case VAC:
					targetTitle = this.vacRateList;
					break;
			}
			for(RateDayData day : targetTitle) {
				if(day.getDate().equals(targetDate)) {
					return day.getData();
				}
			}
			return -1; // not found
		}
		
		private void clearLocationData() {
			this.caseTotalList.clear();
			this.caseRateList.clear();
			this.deathTotalList.clear();
			this.deathRateList.clear();
			this.vacTotalList.clear();
			this.vacRateList.clear();
		}
	}
	
	// TODO: use apache CSV
	private void rowToData(String[] row) {
		String isoCode = row[0];
		if(!(hashStorage.containsKey(isoCode))) {
			long populationNum = Long.parseLong(row[44]);
			hashStorage.put(isoCode, new LocationData(isoCode, row[1], row[2], populationNum));
		}
		long caseTotal, deathsTotal, vacTotal;
		double caseRate, deathsRate, vacRate;
		
		// TODO: change from new cases to cumulative/total cases as of given date
		try { caseTotal = Long.parseLong(row[3]); } catch(Exception e) { caseTotal = 0; }
		try { deathsTotal = Long.parseLong(row[5]); } catch(Exception e) { deathsTotal = 0; }
		try { vacTotal = Long.parseLong(row[37]); } catch(Exception e) { vacTotal = 0; }
		
		
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
	// return -1 if not found
	public long searchTotalData(String isoCode, Date targetDate, DataTitle title) throws Exception {
		long result = this.hashStorage.get(isoCode).getTotalDayData(targetDate, title);
		return result;
	}
	
	public ArrayList<Long> searchTotalData(String isoCode, Date startDate, Date endDate, DataTitle title) {
		ArrayList<Long> result = new ArrayList<Long>();
		ArrayList<TotalDayData> targetList = this.hashStorage.get(isoCode).getTotalDayList(title);
		for(TotalDayData elem : targetList) {
			if(!elem.getDate().before(startDate) && !elem.getDate().after(endDate)) {
				result.add(elem.getData());
			}
		}
		return result;
	}
	
	//return -1 if not found
	public double searchRateData(String isoCode, Date targetDate, DataTitle title) throws Exception {
		double result = this.hashStorage.get(isoCode).getRateDayData(targetDate, title);
		return result;
	}
	
	public ArrayList<Double> searchRateData(String isoCode, Date startDate, Date endDate, DataTitle title) {
		ArrayList<Double> result = new ArrayList<Double>();
		ArrayList<RateDayData> targetList = this.hashStorage.get(isoCode).getRateDayList(title);
		for(RateDayData elem : targetList) {
			if(!elem.getDate().before(startDate) && !elem.getDate().after(endDate)) {
				result.add(elem.getData());
			}
		}
		return result;
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
		
	}
}
