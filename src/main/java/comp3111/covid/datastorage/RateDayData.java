package comp3111.covid.datastorage;

import java.time.LocalDate;

public class RateDayData implements DayData<Double>, Comparable<RateDayData> {
	private LocalDate dataDate;
	private double rateData;
	
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
