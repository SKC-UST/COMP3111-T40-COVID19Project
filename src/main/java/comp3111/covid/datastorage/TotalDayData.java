package comp3111.covid.datastorage;

import java.time.LocalDate;

public class TotalDayData implements DayData<Long>, Comparable<TotalDayData>{
	private LocalDate dataDate;
	private long totalData;
	
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
