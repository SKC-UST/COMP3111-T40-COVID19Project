package unitTest;

import comp3111.covid.tabs.TabC1pageController;
import comp3111.covid.tabs.TableTabsController.TableData;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

public class TabC1Tester extends TabC1pageController{
	
	private String testIso = "HKG";
	private ArrayList<String> testIsoArr = new ArrayList<String>(Arrays.asList("HKG", "USA"));
	private LocalDate testDate = LocalDate.of(2021, 7, 20);
	private LocalDate failDate = LocalDate.of(2020, 7, 20);
	
	@Before
	public void setUp() {
		this.getDatabase().importCSV(new File("./src/main/resources/dataset/COVID_Dataset_v1.0.csv"));
	}
	
	@Test
	public void testGetTotalDataFromDB() {
		long totalData = this.getTotalDataFromDB(testIso, testDate);
		Assert.assertEquals(totalData, 2065375);
	}
	
	@Test
	public void testGetRateDataFromDB() {
		double rateData =this.getRateDataFromDB(testIso, testDate);
		Assert.assertEquals(27.55, rateData, 0.01);
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
	
	@Test
	public void testGenerateDataList() {
		ArrayList<TableData> resultArr = this.generateDataList(testIsoArr, testDate);
		TableData[] expectedArr = {new TableData("Hong Kong", 2065375, 27.55, true), new TableData("United States", 161631676, 48.33, true)};
		for(int i = 0; i < resultArr.size(); ++i) {
			Assert.assertEquals(expectedArr[i].getCountryName(), resultArr.get(i).getCountryName());
			Assert.assertEquals(expectedArr[i].getTotalData(), resultArr.get(i).getTotalData());
			Assert.assertEquals(expectedArr[i].getRateData(), resultArr.get(i).getRateData());
		}
	}
	
	@Test
	public void testTableDataConstructor() {
		TableData testData = new TableData("", -1, -1, false);
		Assert.assertEquals("No Data", testData.getTotalData());
		Assert.assertEquals("No Data", testData.getRateData());
		testData = new TableData("", -1, -1, true);
		Assert.assertEquals("No Data", testData.getRateData());
	}
	
	@After
	public void cleanUp() {
		this.getDatabase().clearDatabase();
	}
}
