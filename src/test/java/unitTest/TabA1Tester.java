package unitTest;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import comp3111.covid.tabs.TabA1pageController;

public class TabA1Tester extends TabA1pageController {
	
	private String testIso = "HKG";
	private ArrayList<String> testIsoArr = new ArrayList<String>(Arrays.asList("HKG", "USA"));
	private LocalDate testDate = LocalDate.of(2021, 7, 20);
	private LocalDate failDate = LocalDate.of(2019, 7, 20);
	
	@Before
	public void setUp() {
		this.getDatabase().importCSV(new File("./src/main/resources/dataset/COVID_Dataset_v1.0.csv"));
	}
	
	@Test
	public void testGetTotalDataFromDB() {
		long totalData = this.getTotalDataFromDB(testIso, testDate);
		Assert.assertEquals(11965, totalData);
	}
	
	@Test
	public void testGetRateDataFromDB() {
		double rateData =this.getRateDataFromDB(testIso, testDate);
		Assert.assertEquals(1595.974, rateData, 0.01);
	}
	
	@Test
	public void testFailTotalDataFromDB() {
		long totalData = this.getTotalDataFromDB(testIso, failDate);
		Assert.assertEquals(-1, totalData);
	}
	
	@Test
	public void testFailRateDataFromDB() {
		double rateData =this.getRateDataFromDB(testIso, failDate);
		Assert.assertEquals(-1, rateData, 0.01);
	}
	
	@After
	public void cleanUp() {
		this.getDatabase().clearDatabase();
	}
}
