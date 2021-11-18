package comp3111.covid.dataAnalysis;

import java.time.LocalDate;

//convert LocalDate to Long representation

public class DateConverter {
	private static final LocalDate START_DATE = LocalDate.of(2000, 1, 1); // 1st Jan 2000 will be converted to 0
	
	public long dateToLong(LocalDate date) {
		return java.time.temporal.ChronoUnit.DAYS.between(START_DATE, date);
	}
	
	public LocalDate longToDate(long dateValue) {
		return START_DATE.plusDays(dateValue);
	}
}
