package comp3111.covid.datastorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
import javafx.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Database {
	
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
		
		public String getLocationName() {
			return this.locationName;
		}
		
		public long getPopulation() {
			return this.locationPopulation;
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
						this.vacTotalList.add((TotalDayData) newDayData);
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
						this.vacRateList.add((RateDayData) newDayData);
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
	
	
	//Database 
	private ArrayList<Pair<String, String>> locationNames = new ArrayList<Pair<String, String>>(); //<isocode, locationName>
	private HashMap<String, LocationData> hashStorage = new HashMap<String, LocationData>(); //isoCode as key
	private boolean datasetPresent = false;
	public enum DataTitle {CASE, DEATH, VAC}
	final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");
	
	public boolean hasDataset() {
		return this.datasetPresent;
	}
	
	public String getLocationName(String isoCode) {
		return this.hashStorage.get(isoCode).getLocationName();
	}
	
	public ArrayList<Pair<String, String>> getLocationNames(){
		return this.locationNames;
	}
	
	public ArrayList<Pair<Number, Number>> getRateLocationPair(LocalDate date){
		ArrayList<Pair<Number, Number>> result = new ArrayList<Pair<Number, Number>>();
		Iterator<Entry<String, LocationData>> it = hashStorage.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, LocationData> pair = (Map.Entry<String, LocationData>)it.next();
			LocationData loc = pair.getValue();
			double rate = loc.getRateDayData(date, DataTitle.VAC);
			if(rate < 0) {
				continue;
			}
			Pair<Number, Number> newPair = new Pair<Number, Number>(loc.getPopulation(), loc.getRateDayData(date, DataTitle.VAC));
			result.add(newPair);
		}
		return result;
	}
	
	
	public void importCSV(File dataset) {
		FileResource fr = new FileResource(dataset);
		CSVParser parser = fr.getCSVParser(true);
		
		for(CSVRecord rec : parser) {
			rowToData(rec);
		}
	}
	
	private void rowToData(CSVRecord record) {
		String isoCode = record.get("iso_code");
		LocalDate date = LocalDate.parse(record.get("date"), this.DATE_FORMAT);
		if(!this.hashStorage.containsKey(isoCode)) {
			String locationName = record.get("location");
			String populationStr = record.get("population");
			if(populationStr.equals(""))
				return;
			long populationNum = Long.parseLong(populationStr);
			this.hashStorage.put(isoCode, new LocationData(isoCode, record.get("continent"), record.get("location"), populationNum));
			this.locationNames.add(new Pair(isoCode, locationName));
		}
		LocationData loc = this.hashStorage.get(isoCode);
		String s = record.get("total_cases");
		if(!s.equals("")) { loc.addDayData(DataTitle.CASE, new TotalDayData(date, Long.parseLong(s)));}
		s = record.get("total_cases_per_million");
		if(!s.equals("")) {loc.addDayData(DataTitle.CASE, new RateDayData(date, Double.parseDouble(s)));};
		s = record.get("total_deaths");
		if(!s.equals("")) { loc.addDayData(DataTitle.DEATH, new TotalDayData(date, Long.parseLong(s)));}
		s = record.get("total_deaths_per_million");
		if(!s.equals("")) {loc.addDayData(DataTitle.DEATH, new RateDayData(date, Double.parseDouble(s)));};
		s = record.get("people_fully_vaccinated");
		if(!s.equals("")) { 
			long total = Long.parseLong(s);
			loc.addDayData(DataTitle.VAC, new TotalDayData(date, total));
			double rate = ((((double)total)/((double)loc.locationPopulation)) * 100);
			loc.addDayData(DataTitle.VAC, new RateDayData(date, rate));
		}
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
	
	public ArrayList<Pair<LocalDate, Number>> searchChartData(String isoCode, LocalDate startDate, LocalDate endDate, DataTitle title){
		ArrayList<Pair<LocalDate, Number>> result = new ArrayList<Pair<LocalDate, Number>>();
		LocationData loc = this.hashStorage.get(isoCode);
		ArrayList<RateDayData> rateList = loc.vacRateList;
		for(RateDayData day : rateList) {
			if(!day.getDate().isBefore(startDate) && !day.getDate().isAfter(endDate)) {
				result.add(new Pair<LocalDate, Number>(day.getDate(), day.getData()));
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
		/*
		ArrayList<RateDayData> data = this.hashStorage.get("HKG").vacRateList;
		System.out.println("printing");
		for(RateDayData d : data) {
			System.out.println(d.getDate() + "\t" + d.getData());
		}
		*/
	}
}
