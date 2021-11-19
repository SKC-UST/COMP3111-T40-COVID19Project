package comp3111.covid.datastorage;

import java.util.Arrays;
import java.util.Optional;

public enum LocationProperty {
	POPULATION(0),		//population
	POPULATION_DENSITY(1),	//population_density
	AGE_MEDIAN(2),	//median_age
	AGE_65(3),	//aged_60_older
	AGE_70(4),	//aged_70_older
	GDP(5),	//gdp_per_capita
	DIABETES(6); 	//diabetes_prevalence
	
	private final int value;
	
	LocationProperty(int value){
		this.value = value;
	}
	
	public static Optional<LocationProperty> valueOf(int value){
		return Arrays.stream(values()).filter(LocationProperty -> LocationProperty.value == value).findFirst();
	}
}
