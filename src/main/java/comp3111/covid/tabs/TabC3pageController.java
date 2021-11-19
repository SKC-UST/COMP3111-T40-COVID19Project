package comp3111.covid.tabs;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;

import comp3111.covid.Context;
import comp3111.covid.dataAnalysis.DateConverter;
import comp3111.covid.datastorage.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.util.Pair;
import javafx.util.StringConverter;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class TabC3pageController {
	
	private Context context = Context.getInstance();
	private Database database = context.getDatabase();
	private DateConverter dateConverter = context.getDateConverter();
	
	@FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;
	@FXML private LineChart<Number, Number> regressionChart;
	@FXML private ComboBox<Pair<String, LocationProperty>> xAxisCbx;
	@FXML private Slider dateSlider;
	@FXML private Label noDataLabel1;
	@FXML private Label noDataLabel2;
	
	final private String[] LOC_PROP_TEXT = {"Population", "Population Density", "Median Age", "Number of People Aged 65 or above", "Number of People Aged 70 or above", "GDP per Capita", "Diabetes Prevalence"};
	final private String noDataText1 = "No Data Avaialble for the given time and x-axis";
	final private String noDataText2 = "Change the slider to find data in another day!";
	private LocationProperty selectedProperty = null;
	private LocalDate selectedDate = null;
	
	// run after importing FX elements, before import dataset
	public void initialize() {
		ObservableList<Pair<String, LocationProperty>> pairs = this.generateLocPropPairs();
		xAxisCbx.setItems(pairs);
		
		// So that y-axis displays percentage 
		yAxis.setTickLabelFormatter(new StringConverter<Number>() {
			@Override
			public String toString(Number rate) {
				return (rate + "%");
			}
			
			@Override
			public Number fromString(String string) {
				NumberFormat percentage = NumberFormat.getPercentInstance();
				try {
					return percentage.parse(string);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 0;
				}
			}
		});
		
		// make combobox contains Pair objects but display the name of property
		xAxisCbx.setConverter(new StringConverter<Pair<String, LocationProperty>>(){
			@Override
			public String toString(Pair<String, LocationProperty> object) {
				return object.getKey();
			}
			
			@Override
			public Pair<String, LocationProperty> fromString(String string){
				return xAxisCbx.getItems().stream().filter(p -> p.getKey().equals(string)).findFirst().orElse(null);
			}
		});
		
		// so that the graph updates itself when the combobox chosen value is changed
		xAxisCbx.valueProperty().addListener((obs, oldval, newval) -> {
			if(newval != null) {
				System.out.println("Selected: " + newval.getValue());
				this.selectedProperty = newval.getValue();
				xAxis.setLabel(LOC_PROP_TEXT[selectedProperty.value()]);
				if(this.selectedDate != null)
					this.generateChart();
			}
		});
		
		this.dateSlider.setLabelFormatter(new StringConverter<>() {
			@Override
			public String toString(Double object) {
				return dateConverter.longToString(object.longValue());
			}
			@Override
			public Double fromString(String string) {
				return (double)dateConverter.dateToLong(LocalDate.parse(string));
			}
		});
		
		// so that the graph updates itself when the slider is changed, and make increments in slider to be steps
		this.dateSlider.valueProperty().addListener((obs, oldval, newval)->{
			final double roundedValue = Math.floor(newval.doubleValue());
			dateSlider.valueProperty().set(roundedValue);
			this.selectedDate = dateConverter.longToDate((long)roundedValue);
			System.out.println(selectedDate);
			if(this.selectedProperty != null) {
				this.generateChart();
			}
		});
	}
	
	public void initAfterImport() {
		LocalDate maxDate = this.database.getLatest();
		LocalDate minDate = this.database.getEarliest();
		this.selectedDate = minDate;
		this.dateSlider.maxProperty().set(dateConverter.dateToLong(maxDate));
		this.dateSlider.minProperty().set(dateConverter.dateToLong(minDate));
	}
	
	//Helper for initialize()
	private ObservableList<Pair<String, LocationProperty>> generateLocPropPairs(){
		ObservableList<Pair<String, LocationProperty>> result = FXCollections.observableArrayList();
		int i = 0;
		for(LocationProperty prop : LocationProperty.values()) {
			Pair<String, LocationProperty> newPair = new Pair<String, LocationProperty>(LOC_PROP_TEXT[i], prop);
			result.add(newPair);
			++i;
		}
		return result;
	}
	
	//Main function for generating the chart
	private void generateChart() {
		this.regressionChart.getData().clear();
		//ArrayList<Pair<Number, Number>> data = database.searchDataPair(selectedDate, selectedProperty);
		ArrayList<Pair<Number, Number>> data = database.searchDataPair(this.selectedDate, this.selectedProperty);
		if(data.isEmpty()) {
			this.regressionChart.setTitle("");
			this.noDataLabel1.setText(noDataText1);
			this.noDataLabel2.setText(noDataText2);
			this.noDataLabel1.setVisible(true);
			this.noDataLabel2.setVisible(true);
			return;
		}
		
		this.noDataLabel1.setVisible(false);
		this.noDataLabel2.setVisible(false);
		
		this.regressionChart.setTitle("Rgerssion based on data on " + this.selectedDate);
		
		Pair<Double, Double> regressionResult = this.generateRegression(data);
		Double regressionSlope = regressionResult.getKey();
		Double regressionIntercept = regressionResult.getValue();
		
		//turn actual data into series
		XYChart.Series<Number, Number> scatter = new XYChart.Series<>();
		XYChart.Series<Number, Number> regression = new XYChart.Series<>();
		scatter.setName("actual data points");
		regression.setName("Regression Line");
		for(Pair<Number, Number> pair : data) {
			scatter.getData().add(new Data<Number, Number>(pair.getKey(), pair.getValue()));
		}
		//turn regression into series
		double lastX = getLastX(data);
		double lastY = regressionSlope * lastX + regressionIntercept;
		regression.getData().add(new Data<Number, Number>(0, regressionIntercept));
		regression.getData().add(new Data<Number, Number>(lastX, lastY));
		
		//display the chart
		this.regressionChart.getData().addAll(scatter, regression);
		this.regressionChart.getScene().getStylesheets().add(getClass().getResource("/stylesheet/root.css").toExternalForm());
		
	}
	
	//Helper for generateChart()
	private double getLastX(ArrayList<Pair<Number, Number>> sourceData) {
		double maxX = 0;
		for(Pair<Number, Number> pair : sourceData) {
			double x = pair.getKey().doubleValue();
			maxX = (maxX > x) ? maxX : x; 
		}
		return maxX;
	}
	
	
	private Pair<Double, Double> generateRegression(ArrayList<Pair<Number, Number>> rawData) {
		SimpleRegression regression = new SimpleRegression(true);
		for(Pair<Number, Number> datum : rawData) {
			regression.addData(datum.getKey().doubleValue(), datum.getValue().doubleValue());
		}
		/*
		System.out.println("Intercept: " + regression.getIntercept());
		System.out.println("Slope: " + regression.getSlope());
		System.out.println("Statistical Significance of slope: " + regression.getSignificance());
		System.out.println("R: " + regression.getR());
		System.out.println("R-squared: " + regression.getRSquare());
		*/
		return new Pair<Double, Double>(regression.getSlope(), regression.getIntercept());
	}
	
}

