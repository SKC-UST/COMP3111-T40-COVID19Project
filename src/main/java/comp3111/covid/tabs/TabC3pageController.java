package comp3111.covid.tabs;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

import comp3111.covid.Context;
import comp3111.covid.datastorage.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class TabC3pageController {
	
	private Context context = Context.getInstance();
	private Database database = context.getDatabase();
	
	@FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;
	@FXML private LineChart<Number, Number> regressionChart;
	@FXML private Button testButton;
	@FXML private ComboBox<Pair<String, LocationProperty>> xAxisCbx;
	@FXML private Slider dateSlider;
	@FXML private Label dateLbl;
	final private String[] LOC_PROP_TEXT = {"Population", "Population Density", "Median Age", "Number of People Aged 65 or above", "Number of People Aged 70 or above", "GDP per Capita", "Diabetes Prevalence"};
	private LocationProperty selectedProperty = null;
	private LocalDate selectedDate = null;
	
	public void initialize() {
		ObservableList<Pair<String, LocationProperty>> pairs = this.generateLocPropPairs();
		xAxisCbx.setItems(pairs);
		
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
		
		xAxisCbx.valueProperty().addListener((obs, oldval, newval) -> {
			if(newval != null) {
				System.out.println("Selected: " + newval.getValue());
				this.selectedProperty = newval.getValue();
			}
		});
		
		this.dateSlider.valueProperty().addListener((obs, oldval, newval)->{
			final double roundedValue = Math.floor(newval.doubleValue());
			dateSlider.valueProperty().set(roundedValue);
			System.out.println(roundedValue);
		});
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
	
	@FXML
	void handleTestButton(ActionEvent event) {
		this.regressionChart.getData().clear();
		
		ArrayList<Pair<Number, Number>> rawData = this.database.searchDataPair(LocalDate.of(2021, 5, 25));
		
		for(Pair<Number, Number> data : rawData) {
			System.out.println(data.getKey());
		}
		
		Pair<Double, Double> regressionResults = this.generateRegression(rawData);
		this.generateChart(rawData, regressionResults.getKey(), regressionResults.getValue());
		
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
	
	private void generateChart(ArrayList<Pair<Number, Number>> sourceData, double regressionSlope, double regressionInt) {
		XYChart.Series<Number, Number> scatter = new XYChart.Series<>();
		XYChart.Series<Number, Number> regression = new XYChart.Series<>();
		scatter.setName("actual data points");
		regression.setName("Regression Line");
		
		ArrayList<Data<Number, Number>> dataList = new ArrayList<Data<Number, Number>>();
		for(Pair<Number, Number> pair: sourceData) {
			scatter.getData().add(new Data<Number, Number>(pair.getKey(), pair.getValue()));
		}
		double lastX = getLastX(sourceData);
		double lastY = regressionSlope * lastX + regressionInt;
		regression.getData().add(new Data<Number, Number>(0, regressionInt));
		regression.getData().add(new Data<Number, Number>(lastX, lastY));
		
		this.regressionChart.getData().addAll(scatter, regression);
		
		this.regressionChart.getScene().getStylesheets().add(getClass().getResource("/stylesheet/root.css").toExternalForm());
	}
	
	private Pair<Double, Double> generateRegression(ArrayList<Pair<Number, Number>> rawData) {
		SimpleRegression regression = new SimpleRegression(true);
		for(Pair<Number, Number> datum : rawData) {
			regression.addData(datum.getKey().doubleValue(), datum.getValue().doubleValue());
		}
		System.out.println("Intercep: " + regression.getIntercept());
		System.out.println("Slope: " + regression.getSlope());
		System.out.println("Statistical Significance of slope: " + regression.getSignificance());
		System.out.println("R: " + regression.getR());
		System.out.println("R-squared: " + regression.getRSquare());
		
		return new Pair<Double, Double>(regression.getSlope(), regression.getIntercept());
	}
	
}

