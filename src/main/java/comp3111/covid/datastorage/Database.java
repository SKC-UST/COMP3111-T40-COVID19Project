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

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import edu.duke.FileResource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class Database {
	
	private ArrayList<String[]> arrayStorage = new ArrayList<String[]>();
	private ArrayList<String> locationNames = new ArrayList<String>();
	private HashMap<String, LocationData> hashStorage = new HashMap<String, LocationData>(); //isoCode as key
	private boolean datasetPresent = false;
	public enum DataTitle {CASE, DEATH, VAC}
	
	private interface DayData<T> {
		public LocalDate getDate();
		public T getData();
	}
	
	private class TotalDayData implements DayData<Long> {
		private LocalDate dataDate;
		private long totalData;
		
		TotalDayData(LocalDate date, long data){
			this.dataDate = date;
			this.totalData = data;
		}
		
		@Override
		public LocalDate getDate() {
			return this.dataDate;
		}
		
		@Override
		public Long getData() {
			return this.totalData;
		}
	}
	
	private class RateDayData implements DayData<Double> {
		private LocalDate dataDate;
		private double rateData;
		
		RateDayData(LocalDate date, double data){
			this.dataDate = date;
			this.rateData = data;
		}
		
		@Override
		public LocalDate getDate() {
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
		
		private long getTotalDayData(LocalDate targetDate, DataTitle dataTitle) {
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
		
		private double getRateDayData(LocalDate targetDate, DataTitle dataTitle) {
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
		
		private void printLocationData() {
			for(int i = 0; i < 10; ++i) {
				System.out.println(this.caseTotalList.get(i).getData() + "\t" + this.caseRateList.get(i).getData() + "\t" + this.deathTotalList.get(i).getData() + "\t" + this.deathRateList.get(i).getData() + this.vacTotalList.get(i).getData() + "\t" + this.vacRateList.get(i).getData());
			}
		}
	}
	
	public boolean hasDataset() {
		return this.datasetPresent;
	}
	
	public ArrayList<String> getLocationNames(){
		return this.locationNames;
	}
	
	public void importCSV(File dataset) {
		FileResource fr = new FileResource(dataset);
		CSVParser parser = fr.getCSVParser(true);
		
		for(CSVRecord rec : parser) {
			rowToData(rec);
		}
	}
	
	private void rowToData(CSVRecord record) {
		
	}
	
	
	// search single data - for Table
	// return -1 if not found
	public long searchTotalData(String isoCode, LocalDate targetDate, DataTitle title) throws Exception {
		long result = this.hashStorage.get(isoCode).getTotalDayData(targetDate, title);
		return result;
	}
	
	public ArrayList<Long> searchTotalData(String isoCode, LocalDate startDate, LocalDate endDate, DataTitle title) {
		ArrayList<Long> result = new ArrayList<Long>();
		LocationData targetLocation = this.hashStorage.get(isoCode);
		ArrayList<TotalDayData> targetList = targetLocation.getTotalDayList(title);
		
		for(DayData<Long> elem : targetList) {
			if(!elem.getDate().isBefore(startDate) && !elem.getDate().isAfter(endDate))
				result.add(elem.getData());
		}
		
		return result;
	}
	
	//return -1 if not found
	public double searchRateData(String isoCode, LocalDate targetDate, DataTitle title) throws Exception {
		double result = this.hashStorage.get(isoCode).getRateDayData(targetDate, title);
		return result;
	}
	
	public ArrayList<Double> searchRateData(String isoCode, LocalDate startDate, LocalDate endDate, DataTitle title) {
		ArrayList<Double> result = new ArrayList<Double>();
		LocationData targetLocation = this.hashStorage.get(isoCode);
		ArrayList<RateDayData> targetList = targetLocation.getRateDayList(title);
		
		for(RateDayData elem : targetList) {
			if(!elem.getDate().isBefore(startDate) && !elem.getDate().isAfter(endDate)) {
				result.add(elem.getData());
			}
		}
		return result;
	}
	
	public void clearDatabase () {
		if(this.hasDataset() == false) {
			return;
		}
		Iterator<Entry<String, LocationData>> it = hashStorage.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, LocationData> pair = (Map.Entry<String, LocationData>)it.next();
			pair.getValue().clearLocationData();
		}
		hashStorage.clear();
		this.datasetPresent = false;
	}
	
	public void printDatabaseContent() {
		
	}
}
