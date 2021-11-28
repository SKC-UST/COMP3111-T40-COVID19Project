package unitTest;

import comp3111.covid.datastorage.LocationProperty;
import comp3111.covid.tabs.TabC3pageController;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.After;
import org.junit.Assert;

public class TabC3Tester extends TabC3pageController{
	
	@Before
	public void setUp() {
		this.database.importCSV(new File("./src/main/resources/dataset/COVID_Dataset_v1.0.csv"));
		this.selectedDate = LocalDate.of(2021, 7, 20);
	}
	
	@Test
	public void testGenerateLocPropPairs() {
		ObservableList<Pair<String, LocationProperty>> testSubject = this.generateLocPropPairs();
		for(int i = 0; i < testSubject.size(); ++i) {
			Assert.assertEquals(LOC_PROP_TEXT[i], testSubject.get(i).getKey());
			Assert.assertEquals(LocationProperty.values()[i].value(), testSubject.get(i).getValue().value());
		}
	}
	
	@Test
	public void testGenerateRegressionSeries() {
		ArrayList<Triple<String, Number, Number>> rawData = this.database.searchDataPair(selectedDate, LocationProperty.POPULATION);
		XYChart.Series<Number, Number> testSeries = this.generateRegressionSeries(rawData, this.generateRegression(rawData));
		Assert.assertEquals(0, testSeries.getData().get(0).getXValue().doubleValue(), 0.01);
		Assert.assertEquals(29.23, testSeries.getData().get(0).getYValue().doubleValue(), 0.01);
		Assert.assertEquals(1.380004385E9, testSeries.getData().get(1).getXValue().doubleValue(), 0.01);
		Assert.assertEquals(-0.203, testSeries.getData().get(1).getYValue().doubleValue(), 0.01);
	}
	
	@Test
	public void testGenerateScatterSeries() {
		ArrayList<Triple<String, Number, Number>> rawData = new ArrayList<Triple<String,Number,Number>>(List.of(Triple.of("Hong Kong", 1, 0.1)));
		XYChart.Series<Number, Number> testSeries = this.generateScatterSeries(rawData);
		Assert.assertEquals(1, testSeries.getData().get(0).getXValue().doubleValue(), 0.01);
		Assert.assertEquals(0.1, testSeries.getData().get(0).getYValue().doubleValue(), 0.01);
	}
	
	@After
	public void cleanUp() {
		this.database.clearDatabase();
	}
}
