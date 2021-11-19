package comp3111.covid.datastorage;

import java.time.LocalDate;
import java.util.ArrayList;

public class LocationData {
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
	
	void clearLocationData() {
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
