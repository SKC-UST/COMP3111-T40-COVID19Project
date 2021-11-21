package comp3111.covid.datastorage;

import java.time.LocalDate;

/**
 * A class to represent data of Total Number of COVID cases, total number of COVID deaths, and Total Number of people fully vaccinated against COVID.
 * A datum is represented by the value of data in long type, and the date of which the value belongs to.
 */
public class TotalDayData implements DayData<Long>, Comparable<TotalDayData>{
	/**
	 * The date this datum is found.
	 */
	private LocalDate dataDate;
	/**
	 * The value of this datum.
	 */
	private long totalData;
	
	/**
	 * Constructor taking in the date of the data and the value of the data.
	 * @param date - date of data.
	 * @param data - value of data.
	 */
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
	
	@Override
	public int compareTo(TotalDayData o) {
		return this.dataDate.compareTo(o.dataDate);
	}
}
