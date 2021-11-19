package comp3111.covid.datastorage;

import java.time.LocalDate;

public interface DayData<T> {
	public LocalDate getDate();
	public T getData();
}
