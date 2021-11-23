package comp3111.covid.datastorage;

/**
 * Enum for distinguishing between the title of data.
 */
public enum DataTitle {
	/**
	 * Represents number of covid-19 cases,
	 * or the number of covid-19 cases per 1M people
	 */
	CASE, 
	/**
	 * Represents number of deaths of due to covid-19,
	 * or the number of covid-19 deaths per 1M people
	 */
	DEATH, 
	/** 
	 * Represents the number of people fully vaccinated,
	 * or the rate of Vaccination
	 */
	VAC
}
