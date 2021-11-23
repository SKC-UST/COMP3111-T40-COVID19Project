package unitTest;

import comp3111.covid.tabs.TabC2pageController;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;

import org.junit.Assert;
import org.junit.Before;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Test;

public class TabC2Tester extends TabC2pageController {
	
	@Before
	public void setUp() {
		this.getDatabase().importCSV(new File("./src/main/resources/dataset/COVID_Dataset_v1.0.csv"));
		this.startDate = LocalDate.of(2021, 7, 1);
		this.endDate = LocalDate.of(2021, 7, 10);
		this.checkedPair.add(new Pair<String, String>("HKG", "Hong Kong"));
	}
	
	@Test
	public void testGenerateChartData() {
		ArrayList<XYChart.Series<Number, Number>> seriesList = this.generateChartData();
		Assert.assertEquals(19.94, seriesList.get(0).getData().get(0).getYValue().doubleValue(), 0.01);
	}
	
	@Test
	public void failGenerateChartData() {
		this.startDate = LocalDate.of(2020, 7, 1);
		this.endDate = LocalDate.of(2020, 7, 10);
		ArrayList<XYChart.Series<Number, Number>> seriesList = this.generateChartData();
		Assert.assertNull(seriesList);
	}
	
	@Test
	public void testHandleChartError() {
		//check data out of range 1
		this.endDate = LocalDate.of(2021, 7, 21);
		this.startDate = LocalDate.of(2021, 7, 10);
		Assert.assertEquals(-6, this.handleChartError());
		
		//check data out of range 2
		this.endDate = LocalDate.of(2021, 7, 20);
		this.startDate = LocalDate.of(2019, 12, 31);
		Assert.assertEquals(-6, this.handleChartError());
		
		//check start end date equal
		this.endDate = LocalDate.of(2021, 7, 1);
		this.startDate = LocalDate.of(2021, 7, 1);
		Assert.assertEquals(-5, this.handleChartError());
		
		//check startDate > endDate
		this.endDate = LocalDate.of(2021, 6, 10);
		Assert.assertEquals(-4, this.handleChartError());
		
		//check no endDate
		this.endDate = null;
		Assert.assertEquals(-3, this.handleChartError());
		
		//check no startDate
		this.endDate= LocalDate.of(2021, 7, 1);
		this.startDate = null;
		Assert.assertEquals(-2, this.handleChartError());
		
		//check no country
		this.startDate = LocalDate.of(2021, 7, 1);
		this.endDate = LocalDate.of(2021, 7, 10);
		this.checkedPair.clear();
		Assert.assertEquals(-1, this.handleChartError());
		
		//no error
		this.startDate = LocalDate.of(2021, 7, 1);
		this.endDate = LocalDate.of(2021, 7, 10);
		this.checkedPair.add(new Pair<String, String>("HKG", "Hong Kong"));
		Assert.assertEquals(0, this.handleChartError());
	}
	
	@After
	public void cleanUp() {
		this.getDatabase().clearDatabase();
	}
}
