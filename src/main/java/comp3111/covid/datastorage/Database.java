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

/**
 * A class representing the internal database of the system.
 * It contains all the data obtained from the original dataset.
 * Each location read is stored in a HashMap, and each location is represented by a LocationData object.
 * {@link LocationData}
 */
public class Database {
	
	/**
	 * A list of pairs containing the iso_code and name of each location read from the dataset. 
	 * each pair is represented by <isocode, locationName>
	 */
	private ArrayList<Pair<String, String>> locationNames = new ArrayList<Pair<String, String>>(); 
	/**
	 * A HashMap storing all LocationData objects created from the dataset.
	 * the Key is the iso_code, and value is the LocationData object.
	 */
	private HashMap<String, LocationData> hashStorage = new HashMap<String, LocationData>();
	/**
	 * The earliest date in the dataset that a data exist.
	 * i.e. the dataset does not contain any data before this date.
	 */
	private LocalDate earliest = LocalDate.now();
	/**
	 * The latest date in the dataset that a data exist.
	 * i.e. the dataset does not contain any data after this date.
	 */
	private LocalDate latest = LocalDate.of(2000, 1, 1); 
	/**
	 * A date formatter for transforming the date read from the dataset into a LocalDate value.
	 */
	final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");
	
	/**
	 * Getter for earliest
	 * @return {@link Database#earliest}
	 */
	public LocalDate getEarliest() {
		return this.earliest;
	}
	
	/**
	 * Getter for latest
	 * @return {@link Database#latest}
	 */
	public LocalDate getLatest() {
		return this.latest;
	}
	
	/**
	 * This method gets the name of the location with the given iso-code. 
	 * @param isoCode	the iso-code of the location.
	 * @return			the name of the location.
	 */
	public String getLocationName(String isoCode) {
		LocationData loc = this.hashStorage.get(isoCode);
		if(loc == null)
			return null;
		return loc.getLocationName();
	}
	
	/**
	 * This method sorts every list of DayData in every LocationData object in the database by date.
	 * @see DayData
	 * @see LocationData
	 */
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
	
	/**
	 * This method returns a list of iso-code - location name pairs of every location in this database.
	 * @return {@link Database#locationNames}
	 */
	public ArrayList<Pair<String, String>> getLocationNames(){
		return this.locationNames;
	}
	
	/**
	 * A method that takes the given dataset file chosen by the user, and read all its data into the database.
	 * @param dataset a .csv dataset file.
	 */
	public void importCSV(File dataset) {
		FileResource fr = new FileResource(dataset);
		CSVParser parser = fr.getCSVParser(true);
		
		for(CSVRecord rec : parser) {
			rowToData(rec);
		}
		
		this.sortLocations();
	}
	
	/**
	 * A helper method for {@link Database#importCSV(File)}.
	 * This method takes a row of data and create a DayData object for each data read.
	 * The DayData is then placed into the corresponding list of a LocationData.
	 * A new LocationData is created and placed into {@link Database#hashStorage} if the location is new.
	 * @param record	a CSVRecord that represents a row of data from the csv file.
	 * @see LocationData, DayData
	 */
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
	
	/**
	 * A method that searches a data value given the location, date and title of data.
	 * The the type of data returned is only restricted to Cumulative COVID cases, Cumulative Covid deaths, and cumulative number of people fully vaccinated.
	 * Returns -1 if no data found.
	 * @param isoCode		iso-code of the desired location
	 * @param targetDate	the desired date
	 * @param title			the desired DataTitle 
	 * @see DataTitle
	 * @return	the value of data of given location, date and title. Returns -1 if no data found.
	 */
	public long searchTotalData(String isoCode, LocalDate targetDate, DataTitle title) {
		long result = this.hashStorage.get(isoCode).getTotalDayData(targetDate, title);
		return result;
	}
	
	/**
	 * A method that searches a data value given the location, date and title of data.
	 * The the type of data returned is only restricted to COVID cases per 1M people, Cumulative Covid deaths per 1M people, and cumulative vaccination rate.
	 * Returns -1 if no data found.
	 * @param isoCode		iso-code of the desired location
	 * @param targetDate	the desired date
	 * @param title			the desired DataTitle 
	 * @see DataTitle
	 * @return	the value of data of given location, date and title. Returns -1 if no data found.
	 */
	public double searchRateData(String isoCode, LocalDate targetDate, DataTitle title) {
		double result = this.hashStorage.get(isoCode).getRateDayData(targetDate, title);
		return result;
	}
	
	/**
	 * This method returns a list of data point represented by a pair of date and number.
	 * The points corresponds are those within the given range of dates, of the given location, and of the given DataTitle.
	 * The date range is inclusive of the <code>startDate</code> and the <code>endDate</code>.
	 * @see DataTitle
	 * @param isoCode	iso-code of the desired location.
	 * @param startDate	the starting date of the desired date range.
	 * @param endDate	the last date of the desired date range.
	 * @param title		the desired DataTitle.
	 * @return			a list of pairs with the date as x-value and the data value as y-value.
	 */
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
	
	/**
	 * This method returns a list of data points represented by a pair.
	 * The x-value of this pair is dictated by the parameter <code>lp</code> a {@link LocationProperty} value.
	 * The y-value of this pair is always a vaccination rate value.
	 * The returned list always contains data of all locations stored in the database.
	 * @param targetDate	the date of interest for the list of data.
	 * @param lp			the desired location property.
	 * @return				a list of data points of all locations of the desired date and location property.
	 */
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
	
	/**
	 * This method clears the all the content of the database.
	 */
	public void clearDatabase () {
		Iterator<Entry<String, LocationData>> it = hashStorage.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, LocationData> pair = (Map.Entry<String, LocationData>)it.next();
			pair.getValue().clearLocationData();
		}
		hashStorage.clear();
	}
	
	/**
	 * This method prints all contents of the database to the console.
	 */
	public void printDatabaseContent() {
		System.out.println("Earliest: " + this.earliest);
		System.out.println("Latest: " + this.latest);
	}
}
