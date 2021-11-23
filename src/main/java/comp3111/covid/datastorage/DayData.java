package comp3111.covid.datastorage;

import java.time.LocalDate;

/**
 * An interface representing one day of data within the database
 * @param <T>	the type of data represented. Typically long or double
 */
public interface DayData<T> {
	/**
	 * @return the date represented by the datum 
	 */
	public LocalDate getDate();
	/**
	 * @return the value of the datum
	 */
	public T getData();
}
