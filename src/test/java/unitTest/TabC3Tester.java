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
		//population: x: 0, 29.2298 | y: 1.380004385E9,-0.2972
		//density: x: 0, 27.905 | y: 20546.77, 33.1774
		ArrayList<Pair<Number, Number>> rawData = this.database.searchDataPair(selectedDate, LocationProperty.POPULATION);
		XYChart.Series<Number, Number> testSeries = this.generateRegressionSeries(rawData);
		Assert.assertEquals(0, testSeries.getData().get(0).getXValue().doubleValue(), 0.01);
		Assert.assertEquals(29.23, testSeries.getData().get(0).getYValue().doubleValue(), 0.01);
		Assert.assertEquals(1.380004385E9, testSeries.getData().get(1).getXValue().doubleValue(), 0.01);
		Assert.assertEquals(-0.2972, testSeries.getData().get(1).getYValue().doubleValue(), 0.01);
	}
	
	@Test
	public void testGenerateScatterSeries() {
		ArrayList<Pair<Number, Number>> rawData = this.database.searchDataPair(selectedDate, LocationProperty.POPULATION);
		XYChart.Series<Number, Number> testSeries = this.generateScatterSeries(rawData);
		Assert.assertEquals(4822233, testSeries.getData().get(0).getXValue().doubleValue(), 0.01);
		Assert.assertEquals(13.03, testSeries.getData().get(0).getYValue().doubleValue(), 0.01);
		Assert.assertEquals(5.930869E7, testSeries.getData().get(testSeries.getData().size() - 1).getXValue().doubleValue(), 0.01);
		Assert.assertEquals(3.01, testSeries.getData().get(testSeries.getData().size() - 1).getYValue().doubleValue(), 0.01);
	}
}
