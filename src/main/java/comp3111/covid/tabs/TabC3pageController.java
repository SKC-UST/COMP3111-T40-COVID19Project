package comp3111.covid.tabs;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;

import comp3111.covid.Context;
import comp3111.covid.dataAnalysis.DateConverter;
import comp3111.covid.datastorage.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Pair;
import javafx.util.StringConverter;
import javafx.scene.control.cell.PropertyValueFactory;

import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * place holder
 * @author ytc314
 *
 */
public class TabC3pageController {

	protected Database database = Context.getInstance().getDatabase();
	protected DateConverter dateConverter = Context.getInstance().getDateConverter();
	
	@FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;
	@FXML private LineChart<Number, Number> regressionChart;
	@FXML private ComboBox<Pair<String, LocationProperty>> xAxisCbx;
	@FXML private Slider dateSlider;
	@FXML private Label noDataLabel1;
	@FXML private Label noDataLabel2;
	@FXML private TableColumn<TableView<RegressionTableData>, String> regPropCol;
	@FXML private TableColumn<TableView<RegressionTableData>, Double> regValCol;
	@FXML private TableView<RegressionTableData> regTable;
	
	final protected String[] LOC_PROP_TEXT = {"Population", "Population Density", "Median Age", "Number of People Aged 65 or above", "Number of People Aged 70 or above", "GDP per Capita", "Diabetes Prevalence"};
	final private String noDataText1 = "No Data Avaialble for the given time and x-axis";
	final private String noDataText2 = "Change the slider to find data in another day!";
	protected LocationProperty selectedProperty = null;
	protected LocalDate selectedDate = null;
	
	// run after importing FX elements, before import dataset
	public void initialize() {
		// Initialize the comboBox to contain pairs of name of LocationProperty and LocationProperty Enums
		ObservableList<Pair<String, LocationProperty>> pairs = this.generateLocPropPairs();
		xAxisCbx.setItems(pairs);
		
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
		
		// so that the graph updates itself when the combobox chosen value is changed
		xAxisCbx.valueProperty().addListener((obs, oldval, newval) -> {
			if(newval != null) {
				this.selectedProperty = newval.getValue();
				xAxis.setLabel(LOC_PROP_TEXT[selectedProperty.value()]);
				if(this.selectedDate != null)
					this.generateView();
			}
		});
		
		// so that the slider displays label of dates
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
			if(this.selectedProperty != null) {
				this.generateView();
			}
		});
		
		this.regPropCol.setCellValueFactory(new PropertyValueFactory<TableView<RegressionTableData>, String>("regName"));
		this.regValCol.setCellValueFactory(new PropertyValueFactory<TableView<RegressionTableData>, Double>("regValue"));
	}
	
	public void initAfterImport() {
		LocalDate maxDate = this.database.getLatest();
		LocalDate minDate = this.database.getEarliest();
		this.selectedDate = minDate;
		this.dateSlider.maxProperty().set(dateConverter.dateToLong(maxDate));
		this.dateSlider.minProperty().set(dateConverter.dateToLong(minDate));
	}
	
	//Helper for initializing the combo box
	protected ObservableList<Pair<String, LocationProperty>> generateLocPropPairs(){
		ObservableList<Pair<String, LocationProperty>> result = FXCollections.observableArrayList();
		int i = 0;
		for(LocationProperty prop : LocationProperty.values()) {
			Pair<String, LocationProperty> newPair = new Pair<String, LocationProperty>(LOC_PROP_TEXT[i], prop);
			result.add(newPair);
			++i;
		}
		return result;
	}
	
	// Main function for generating the chart and the table
	private void generateView() {
		ArrayList<Pair<Number, Number>> rawData = database.searchDataPair(this.selectedDate, this.selectedProperty);
		RegressionResult regressionResult = this.generateRegression(rawData);
		this.generateChart(rawData, regressionResult);
		this.generateTable(regressionResult);
		
	}
	
	//Main function for generating the chart
	@SuppressWarnings("unchecked")
	private void generateChart(ArrayList<Pair<Number, Number>> data, RegressionResult regressionResult) {
		this.regressionChart.getData().clear();
	
		//Handle no data found
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
		
		//turn actual data into series
		XYChart.Series<Number, Number> scatter = this.generateScatterSeries(data);
		XYChart.Series<Number, Number> regression = this.generateRegressionSeries(data, regressionResult);
		
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
	
	
	private RegressionResult generateRegression(ArrayList<Pair<Number, Number>> rawData) {
		SimpleRegression regression = new SimpleRegression(true);
		for(Pair<Number, Number> datum : rawData) {
			regression.addData(datum.getKey().doubleValue(), datum.getValue().doubleValue());
		}
		return new RegressionResult(regression.getIntercept(), regression.getSlope(), regression.getSignificance(), regression.getR(), regression.getRSquare());
	}
	
	protected XYChart.Series<Number, Number> generateRegressionSeries(ArrayList<Pair<Number, Number>> rawData, RegressionResult regressionResult) {
		double slope = regressionResult.getSlope();
		double intercept = regressionResult.getIntercept();
		
		XYChart.Series<Number, Number> regressionSeries = new XYChart.Series<>();
		regressionSeries.setName("Regression Line");
		double lastX = getLastX(rawData);
		double lastY = slope * lastX + intercept;
		regressionSeries.getData().add(new Data<Number, Number>(0, intercept));
		regressionSeries.getData().add(new Data<Number, Number>(lastX, lastY));
		//System.out.println("First: " + 0 + "," + intercept);
		//System.out.println("Last: " + lastX + "," + lastY);
		return regressionSeries;
	}
	
	protected XYChart.Series<Number, Number> generateScatterSeries(ArrayList<Pair<Number, Number>> rawData){
		XYChart.Series<Number, Number> scatter = new XYChart.Series<>();
		scatter.setName("actual data points");
		for(Pair<Number, Number> pair : rawData) {
			scatter.getData().add(new Data<Number, Number>(pair.getKey(), pair.getValue()));
		}
		return scatter;
	}
	
	// ---------------- for table ----------------------
	public class RegressionTableData {
		private final SimpleStringProperty regName;
		private final SimpleDoubleProperty regValue;
		
		RegressionTableData(String propertyName, double propertyValue){
			this.regName = new SimpleStringProperty(propertyName);
			this.regValue = new SimpleDoubleProperty(Math.round(propertyValue * 100000d) / 100000d);
		}
		
		public String getRegName() {
			return this.regName.get();
		}
		
		public double getRegValue() {
			return this.regValue.get();
		}
	}
	
	public class RegressionResult {
		private double intercept;
		private double slope;
		private double significance;
		private double rValue;
		private double rSquared;
		
		RegressionResult(double intercept, double slp, double sig, double r, double rs){
			this.intercept = intercept;
			this.slope = slp;
			this.significance = sig;
			this.rValue = r;
			this.rSquared = rs;
		}
		
		double getIntercept() {
			return this.intercept;
		}
		
		double getSlope() {
			return this.slope;
		}
		
		double getSignificance() {
			return this.significance;
		}
		
		double getR() {
			return this.rValue;
		}
		
		double getRsquared() {
			return this.rSquared;
		}
	}
	
	private void generateTable(RegressionResult regressionResult) {
		ObservableList<RegressionTableData> oList = FXCollections.observableArrayList();
		oList.add(new RegressionTableData("Intercept", regressionResult.getIntercept()));
		oList.add(new RegressionTableData("Slope", regressionResult.getSlope()));
		oList.add(new RegressionTableData("Level of Significance of Slope", regressionResult.getSignificance()));
		oList.add(new RegressionTableData("Pearson's Correlation Coefficient (R)", regressionResult.getR()));
		oList.add(new RegressionTableData("Coefficient of Determination (R-squred)", regressionResult.getRsquared()));
		// to make the columns get the right data from RegressionTableData
		this.regTable.setItems(oList);
	}
	
}

