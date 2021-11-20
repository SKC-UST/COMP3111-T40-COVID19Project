package comp3111.covid.datastorage;

import java.util.Arrays;
import java.util.Optional;

/**
 * An Enum representing properties of a LocationData object. 
 * used for ease of searching data in the database. 
 * @see LocationData
 */
public enum LocationProperty {
	/**
	 * the location's population
	 */
	POPULATION(0),		
	/** 
	 * the location's population density
	 */
	POPULATION_DENSITY(1),
	/**
	 * The location's median age.
	 */
	AGE_MEDIAN(2),	
	/**
	 * The location's percentage of people aged 65 or older.
	 */
	AGE_65(3),
	/**
	 * The location's percentage of people aged 70 or older.
	 */
	AGE_70(4),	
	/**
	 * The location's GDP per capita.
	 */
	GDP(5),
	/**
	 * The location's diabetes prevalence.
	 */
	DIABETES(6);
	
	/**
	 * The integer value associated to the enum.
	 */
	private final int value;
	
	/**
	 * Constructor of the enum.
	 * @param value - {@link LocationProperty#value}
	 */
	LocationProperty(int value){
		this.value = value;
	}
	
	/**
	 * Given the integer value of the LocationProperty, returns the corresponding LocationProperty.
	 * @param value - the associated integer value of a location property.
	 * @return the corresponding LocationProperty enum.
	 */
	public static Optional<LocationProperty> valueOf(int value){
		return Arrays.stream(values()).filter(LocationProperty -> LocationProperty.value == value).findFirst();
	}
	
	/**
	 * @return {@link LocationProperty#value}
	 */
	public int value() {
		return this.value;
	}
}
