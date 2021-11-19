package comp3111.covid.datastorage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import edu.duke.FileResource;
import javafx.util.Pair;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Database {
	
	//Database 
	private ArrayList<Pair<String, String>> locationNames = new ArrayList<Pair<String, String>>(); //<isocode, locationName>
	private HashMap<String, LocationData> hashStorage = new HashMap<String, LocationData>(); //isoCode as key
	private boolean datasetPresent = false;
	final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");
	
	public boolean hasDataset() {
		return this.datasetPresent;
	}
	
	public String getLocationName(String isoCode) {
		LocationData loc = this.hashStorage.get(isoCode);
		if(loc == null)
			return null;
		return loc.getLocationName();
	}
	
	private void sortLocations() {
		Iterator<Entry<String, LocationData>> it = hashStorage.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, LocationData> pair = (Map.Entry<String, LocationData>)it.next();
			LocationData loc = pair.getValue();
			for(DataTitle title : DataTitle.values()) {
				Collections.sort(loc.getTotalDayList(title));
				Collections.sort(loc.getRateDayList(title));
			}
		}
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
		
		this.sortLocations();
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
			double rate = ((((double)total)/((double)loc.getPopulation())) * 100);
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
		ArrayList<RateDayData> rateList = loc.getRateDayList(title); 
		for(RateDayData day : rateList) {
			if(!day.getDate().isBefore(startDate) && !day.getDate().isAfter(endDate)) {
				result.add(new Pair<LocalDate, Number>(day.getDate(), day.getData()));
			}
		}
		return result;
	}
	
	public ArrayList<Pair<Number, Number>> searchDataPair(LocalDate targetDate){
		ArrayList<Pair<Number, Number>> result = new ArrayList<Pair<Number, Number>>();
		Iterator<Entry<String, LocationData>> it = hashStorage.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, LocationData> pair = (Map.Entry<String, LocationData>)it.next();
			Number rateValue = pair.getValue().getRateDayData(targetDate, DataTitle.VAC);
			if(rateValue.intValue() < 0 || pair.getKey().contains("OWID"))
					continue;
			Number xValue = pair.getValue().getPopulation();
			result.add(new Pair<Number, Number>(xValue, rateValue));
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
