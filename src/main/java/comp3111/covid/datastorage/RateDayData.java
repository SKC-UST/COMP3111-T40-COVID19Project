package comp3111.covid.datastorage;

import java.time.LocalDate;

/**
 * A class to represent data of Total Number of COVID cases per 1M people, total number of COVID deaths per 1M people, and vaccination rate.
 * A datum is represented by the value of data in double type, and the date of which the value belongs to.
 */
public class RateDayData implements DayData<Double>, Comparable<RateDayData> {
	/**
	 * The date this datum is found.
	 */
	private LocalDate dataDate;
	/**
	 * The value of this datum.
	 */
	private double rateData;
	
	/**
	 * Constructor taking in the date of the data and the value of the data.
	 * @param date - date of data.
	 * @param data - value of data.
	 */
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
	
	@Override
	public int compareTo(RateDayData o) {
		return this.dataDate.compareTo(o.dataDate);
	}
}
