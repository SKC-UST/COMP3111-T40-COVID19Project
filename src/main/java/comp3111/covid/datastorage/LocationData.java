package comp3111.covid.datastorage;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * A class representing a country in the database
 * It stores lists of DayData.
 */
public class LocationData {
	/**
	 * The iso code of the country
	 */
	private String locationIsoCode;
	/**
	 * The continent that the country belongs to
	 */
	private String locationContinent;
	/**
	 * The name of the country.
	 */
	private String locationName;
	/**
	 * The country's population.
	 */
	private long locationPopulation;
	/**
	 * The conutry's population density.
	 */
	private double locationDensity;
	/**
	 * The conutry's median age.
	 */
	private double locationMedianAge;
	/**
	 * The conutry's percentage of people at the age of 65 or older than 65 years old.
	 */
	private double location65Age;
	/**
	 * The conutry's percentage of people at the age of 70 or older than 70 years old.
	 */
	private double location70Age;
	/**
	 * The conutry's GDP per capita.
	 */
	private double locationGDPpc;
	/**
	 * The conutry's diabetes prevalence.
	 */
	private double locationDiabetes;
	/**
	 * A list storing the number of total COVID-19 cases of each day.
	 */
	private ArrayList<TotalDayData> caseTotalList = new ArrayList<TotalDayData>();
	/**
	 * A list storing the number of total COVID-19 deaths of each day.
	 */
	private ArrayList<TotalDayData> deathTotalList = new ArrayList<TotalDayData>();
	/**
	 * A list storing the number of people fully vaccinated of each day.
	 */
	private ArrayList<TotalDayData> vacTotalList = new ArrayList<TotalDayData>();
	/**
	 * A list storing the number of total number of COVID-19 cases per million pepole of each day.
	 */
	private ArrayList<RateDayData> caseRateList = new ArrayList<RateDayData>();
	/**
	 * A list storing the number of total number of COVID-19 deaths per million pepole of each day.
	 */
	private ArrayList<RateDayData> deathRateList = new ArrayList<RateDayData>();
	/**
	 * A list storing the cumulative vaccination rate of each day.
	 */
	private ArrayList<RateDayData> vacRateList = new ArrayList<RateDayData>();
	
	/**
	 * The constructor for LocationData
	 * @param isoCode 		{@link LocationData#locationIsoCode}
	 * @param continent 	{@link LocationData#locationContinent}
	 * @param location 		{@link LocationData#locationName}
	 * @param population	{@link LocationData#locationPopulation}
	 * @param density		{@link LocationData#locationDensity}
	 * @param medianAge		{@link LocationData#locationMedianAge}
	 * @param age65			{@link LocationData#location65Age}
	 * @param age70			{@link LocationData#location70Age}
	 * @param gdp			{@link LocationData#locationGDPpc}
	 * @param diabetes		{@link LocationData#locationDiabetes}
	 */
	LocationData(String isoCode, String continent, String location, long population, double density, double medianAge, double age65, double age70, double gdp, double diabetes) {
		this.locationIsoCode = isoCode;
		this.locationContinent = continent;
		this.locationName = location;
		this.locationPopulation = population;
		this.locationDensity = density;
		this.locationMedianAge = medianAge;
		this.location65Age = age65;
		this.location70Age = age70;
		this.locationGDPpc = gdp;
		this.locationDiabetes = diabetes;
	}
	
	/**
	 * {@link LocationData#locationName}
	 * @return the name of the location
	 */
	public String getLocationName() {
		return this.locationName;
	}
	
	/**
	 * {@link LocationData#locationPopulation}
	 * @return the population of the location
	 */
	public long getPopulation() {
		return this.locationPopulation;
	}
	
	/**
	 * Add a day data to the list of specified dataTitle
	 * @param dataTitle		the title of datum
	 * @see DataTitle
	 * @param newDayData	the DayData to be added
	 * @see DayData
	 */
	void addDayData(DataTitle dataTitle, DayData newDayData) {
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
	
	/**
	 * Get a list of day data of specified dataTitle
	 * but restricted to cases of COVID per 1M people, deaths per 1M people, or vaccination rate
	 * @param dataTitle		the title of datum to get
	 * @see DataTitle
	 */
	ArrayList<TotalDayData> getTotalDayList(DataTitle dataTitle){
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
	
	/**
	 * Get a list of day data of the specified dataTitle
	 * but restricted to cumulative cases of COVID, cumulative deaths, or cumulative number of people fully vaccinated.
	 * @param dataTitle		the title of datum to get
	 * @see DataTitle
	 */
	ArrayList<RateDayData> getRateDayList(DataTitle dataTitle){
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
	
	/**
	 * Get the value of specified dataTitle on the specified day
	 * but restricted to cumulative cases of COVID, cumulative deaths, or cumulative number of people fully vaccinated.
	 * For example, getting the exact number of people died of COVID-19 up till the specified date
	 * @param targetDate	the date of the datum to get
	 * @param dataTitle		the title of datum to get
	 * @see DataTitle
	 * @return				the specified data value. -1 if not found.
	 */
	long getTotalDayData(LocalDate targetDate, DataTitle dataTitle) {
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
	
	/**
	 * Get the value of specified dataTitle on the specified day
	 * but restricted to cumulative cases of COVID per 1M people, cumulative deaths per 1M people, or cumulative vaccination rate.
	 * For example, getting the exact number of people died of COVID-19 per 1 million people up till the specified date
	 * @param targetDate	the date of the datum to get
	 * @param dataTitle		the title of datum to get
	 * @see DataTitle
	 * @return				the specified data value. -1 if not found.
	 */
	double getRateDayData(LocalDate targetDate, DataTitle dataTitle) {
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
	/**
	 * Get the specified property of this location.
	 * @param property	the property to get
	 * @see 			LocationProperty
	 * @return			the specified property
	 */
	Number getLocationProperty(LocationProperty property) {
		switch(property) {
			case POPULATION:
				return this.locationPopulation;
			case POPULATION_DENSITY:
				return this.locationDensity;
			case AGE_MEDIAN:
				return this.locationMedianAge;
			case AGE_65:
				return this.location65Age;
			case AGE_70:
				return this.location70Age;
			case GDP:
				return this.locationGDPpc;
			case DIABETES:
				return this.locationDiabetes;
		}
		return null;
	}
	/**
	 * clear every list of this instance of LocationData
	 */
	void clearLocationData() {
		this.caseTotalList.clear();
		this.caseRateList.clear();
		this.deathTotalList.clear();
		this.deathRateList.clear();
		this.vacTotalList.clear();
		this.vacRateList.clear();
	}
}
