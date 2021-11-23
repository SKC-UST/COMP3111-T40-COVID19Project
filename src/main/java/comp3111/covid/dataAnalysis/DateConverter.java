package comp3111.covid.dataAnalysis;

import java.time.LocalDate;

//convert LocalDate to Long representation

/**
 * A Converter that allow conversion between LocalDate objects, long value and String objects
 * mainly for the conversion between the return value of UI elements (such as slider) to an object for internal use.
 * This class is under the singleton restriction under the Context class.
 * @see comp3111.covid.Context
 */
public class DateConverter {
	/**
	 * This is a constant value as an anchor point for calculating the long value of a date
	 * The anchor is set to 1 Jan 2000 for convenience
	 */
	private static final LocalDate START_DATE = LocalDate.of(2000, 1, 1); // 1st Jan 2000 will be converted to 0
	
	/**
	 * This method returns the difference between the parameter date and the anchor point DateConverter#START_DATE
	 * The difference is represented in the number of days
	 * 
	 * @param 	date the date to transform into long value
	 * @return	days elapsed since {@link DateConverter#START_DATE} to specified date 
	 */
	public long dateToLong(LocalDate date) {
		return java.time.temporal.ChronoUnit.DAYS.between(START_DATE, date);
	}
	
	/**
	 * This method returns resultant date of adding number of days in the parameter to the anchor point {@link DateConverter#START_DATE}
	 * @param dateValue	the number of days to add to {@link DateConverter#START_DATE}
	 * @return			the the date of the specified number of days added to {@link DateConverter#START_DATE}
	 */
	public LocalDate longToDate(long dateValue) {
		return START_DATE.plusDays(dateValue);
	}
	
	/**
	 * This method returns the long-value representation of a date into a String of iso-format date.
	 * Mainly used by text converters of UI elements.
	 * @param dateValue	the long-value representation to convert to string
	 * @return			a string of iso-format date represented by the specified long value
	 */
	public String longToString(long dateValue) {
		return longToDate(dateValue).toString();
	}
}
