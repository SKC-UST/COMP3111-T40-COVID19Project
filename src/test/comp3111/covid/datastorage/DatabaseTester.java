package comp3111.covid.datastorage;

import static org.junit.Assert.*;

import java.util.Date;
import java.text.SimpleDateFormat; 

import org.junit.Before;
import org.junit.Test;

import comp3111.covid.MyApplication;
import comp3111.covid.Controller;
import comp3111.covid.DataAnalysis;
import comp3111.covid.datastorage.Database;
import comp3111.covid.datastorage.Database.DataTitle;
 

public class DatabaseTester {
	@Before
	public void setUp() throws Exception {
		arrAscending = new int[] {1, 2, 3, 4, 5};
		arrDescending = new int[] {5, 4, 3, 2, 1};
	}

	@Test
	public void computeOneWithValidInput() {
		assertEquals(-1, Database.searchRateData(String isoCode, Date targetDate, DataTitle title));
	}
}