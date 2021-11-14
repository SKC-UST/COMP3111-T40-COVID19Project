package unitTest;
import static org.junit.Assert.*;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import comp3111.covid.datastorage.Database;
import comp3111.covid.datastorage.Database.DataTitle;

public class DatabaseTester {
	Database database;
	File csvFile;
	Date targetDate, startDate, endDate;
	String location = "HKG";
	Long[] sampleOutput;
	
	@SuppressWarnings("deprecation")
	@Before
	public void setUp() throws Exception {
		database = new Database();
		this.csvFile = new File("./src/main/resources/dataset/COVID_Dataset_v1.0.csv");
		database.importCSV(csvFile);
		
		targetDate = new Date(2020, 2, 5);
		startDate = new Date(2020, 2, 5);
		endDate = new Date(2020, 2, 15);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date date = new Date();
		try {
			targetDate = dateFormat.parse("2/5/2020");
			startDate = dateFormat.parse("2/5/2020");
			endDate = dateFormat.parse("2/15/2020");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.sampleOutput = new Long[] {21L, 24L, 25L, 26L, 29L, 38L, 49L, 50L, 53L, 56L, 56L};
	}
	
	@Test
	public void testSingleData() {
		try {
			assertEquals(21, database.searchTotalData(location, targetDate, DataTitle.CASE));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRangeData() {
		ArrayList<Long> testArr = database.searchTotalData(location, startDate, endDate, DataTitle.CASE);
		for(Long elem : testArr) {
			System.out.print(elem);
		}
		assertArrayEquals(this.sampleOutput, testArr.toArray());
	}
}
