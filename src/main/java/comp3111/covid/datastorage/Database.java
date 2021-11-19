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
	private LocalDate earliest = LocalDate.now(), latest = LocalDate.of(2000, 1, 1); //earliest = date that the first data is found, latest = last data found in dataset -- initialized in such a way to facilitate searching
	final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");
	
	public LocalDate getEarliest() {
		return this.earliest;
	}
	
	public LocalDate getLatest() {
		return this.latest;
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
		if(date.isAfter(latest)) { latest = date;}
		if(date.isBefore(earliest)) { earliest = date;}
		
		//Initializing a LocationData from record
		if(!this.hashStorage.containsKey(isoCode)) {
			String locationName = record.get("location");
			String populationStr = record.get("population");
			long populationNum;
			double populationDensity, medianAge, age65, age70, gdp, diabetes;
			
			try {populationNum = Long.parseLong(populationStr);} catch(Exception e){return;} //"countries" without population numbers do not contain meaning data in any way
			try {populationDensity = Double.parseDouble(record.get("population_density"));} catch(Exception e) {populationDensity = -1;}
			try {medianAge = Double.parseDouble(record.get("median_age"));} catch(Exception e) {medianAge = -1;}
			try {age65 = Double.parseDouble(record.get("aged_65_older"));} catch(Exception e) {age65 = -1;}
			try {age70 = Double.parseDouble(record.get("aged_70_older"));} catch(Exception e) {age70 = -1;}
			try {gdp = Double.parseDouble(record.get("gdp_per_capita"));} catch(Exception e) {gdp = -1;}
			try {diabetes = Double.parseDouble(record.get("diabetes_prevalence"));} catch(Exception e) {diabetes = -1;}
			if(populationStr.equals("")) return;
			this.hashStorage.put(isoCode, new LocationData(
					isoCode, record.get("continent"), record.get("location"), populationNum, populationDensity, medianAge, age65, age70, gdp, diabetes));
			this.locationNames.add(new Pair<String, String>(isoCode, locationName));
		}
		
		//put DayData into LocationData
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
		if(!s.equals("")) { loc.addDayData(DataTitle.VAC, new TotalDayData(date, Long.parseLong(s)));}
		s = record.get("people_fully_vaccinated_per_hundred");
		if(!s.equals("")) { loc.addDayData(DataTitle.VAC, new RateDayData(date, Double.parseDouble(s)));}
	}
	
	// search single data - for Table
	// return -1 if not found
	public long searchTotalData(String isoCode, LocalDate targetDate, DataTitle title) {
		long result = this.hashStorage.get(isoCode).getTotalDayData(targetDate, title);
		return result;
	}
	
	//return -1 if not found
	public double searchRateData(String isoCode, LocalDate targetDate, DataTitle title) {
		double result = this.hashStorage.get(isoCode).getRateDayData(targetDate, title);
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
	
	// for Tab C3 only
	public ArrayList<Pair<Number, Number>> searchDataPair(LocalDate targetDate, LocationProperty lp){
		ArrayList<Pair<Number, Number>> result = new ArrayList<Pair<Number, Number>>();
		
		Iterator<Entry<String, LocationData>> it = hashStorage.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, LocationData> pair = (Map.Entry<String, LocationData>)it.next();
			
			//y-value
			Number rateValue = pair.getValue().getRateDayData(targetDate, DataTitle.VAC);
			if(rateValue.intValue() < 0 || pair.getKey().contains("OWID"))
					continue;
			//x-value
			Number xValue = pair.getValue().getLocationProperty(lp);
			if(xValue.intValue() < 0) //corresponding data field is blank in csv
				continue;
			result.add(new Pair<Number, Number>(xValue, rateValue));
		}
		return result;
	}
	
	public void clearDatabase () {
		Iterator<Entry<String, LocationData>> it = hashStorage.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, LocationData> pair = (Map.Entry<String, LocationData>)it.next();
			pair.getValue().clearLocationData();
		}
		hashStorage.clear();
	}
	
	public void printDatabaseContent() {
		System.out.println("Earliest: " + this.earliest);
		System.out.println("Latest: " + this.latest);
	}
}
