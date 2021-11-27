package comp3111.covid.tabs;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.annotation.processing.Generated;

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
import javafx.scene.control.Tooltip;
import javafx.util.Pair;
import javafx.util.StringConverter;
import javafx.scene.control.cell.PropertyValueFactory;

import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * Controller for Tab C3
 * Rate of vaccination report.
 */
public class TabC3pageController {

	protected Database database = Context.getInstance().getDatabase();
	protected DateConverter dateConverter = Context.getInstance().getDateConverter();
	
	@FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;
	@FXML private LineChart<Number, Number> regressionChart;
	@FXML protected ComboBox<Pair<String, LocationProperty>> xAxisCbx;
	@FXML private Slider dateSlider;
	@FXML private Label noDataLabel1;
	@FXML private Label noDataLabel2;
	@FXML private TableColumn<TableView<RegressionTableData>, String> regPropCol;
	@FXML private TableColumn<TableView<RegressionTableData>, Double> regValCol;
	@FXML private TableView<RegressionTableData> regTable;
	/**
	 * An array of string to be displayed on the combobox.
	 * Each string corresponds to a {@link comp3111.covid.datastorage.LocationProperty}.
	 */
	final protected String[] LOC_PROP_TEXT = {"Population", "Population Density", "Median Age", "Number of People Aged 65 or above", "Number of People Aged 70 or above", "GDP per Capita", "Diabetes Prevalence"};
	final private String noDataText1 = "No Data Avaialble for the given time and x-axis";
	final private String noDataText2 = "Change the slider to find data in another day!";
	/**
	 * The chosen {@link comp3111.covid.datastorage.LocationProperty} from the combobox.
	 */
	protected LocationProperty selectedProperty = null;
	/**
	 * The chosen date from the slider.
	 */
	protected LocalDate selectedDate = null;
	
	/**
	 * This method initializes this controller after loading all FMXL elements.
	 * In particular, it dose four things.
	 * 1. It sets up the combobox to store pairs of LocationProperty and its corresponding string, and changes the value of {@link TabC3pageController#selectedProperty} upon change of choice.
	 * 2. It sets the y-axis of the chart to display percentage.
	 * 3. It sets the string converter of the slider and Listener fro the slider.
	 * 4. It sets the {@link javafx.scene.control.cell.PropertyValueFactory} for the table's columns.
	 */
	public void initialize() {
		// Initialize the comboBox to contain pairs of name of LocationProperty and LocationProperty Enums
		ObservableList<Pair<String, LocationProperty>> pairs = this.generateLocPropPairs();
		xAxisCbx.setItems(pairs);
		this.selectedDate = this.database.getEarliest();
		
		// make combobox contains Pair objects but display the name of property
		xAxisCbx.setConverter(new StringConverter<Pair<String, LocationProperty>>(){
			@Override @Generated("")
			public String toString(Pair<String, LocationProperty> object) {
				return object.getKey();
			}
			public Pair<String, LocationProperty> fromString(String string){
				return xAxisCbx.getItems().stream().filter(p -> p.getKey().equals(string)).findFirst().orElse(null);
			}
		});
		
		// So that y-axis displays percentage 
		yAxis.setTickLabelFormatter(new StringConverter<Number>() {
			public String toString(Number rate) {
				return (rate + "%");
			}
			
			public Number fromString(String string) {
				return null;
			}
		});
		
		// so that the graph updates itself when the combobox chosen value is changed
		xAxisCbx.valueProperty().addListener((obs, oldval, newval) -> {
			this.selectedProperty = newval.getValue();
			xAxis.setLabel(LOC_PROP_TEXT[selectedProperty.value()]);
			this.generateView();
		});
		
		// so that the slider displays label of dates
		this.dateSlider.setLabelFormatter(new StringConverter<>() {
			@Override @Generated("")
			public String toString(Double object) {
				return dateConverter.longToString(object.longValue());
			}
			@Override @Generated("")
			public Double fromString(String string) {
				return (double)dateConverter.dateToLong(LocalDate.parse(string));
			}
		});
		
		// so that the graph updates itself when the slider is changed, and make increments in slider to be steps
		this.dateSlider.valueProperty().addListener((obs, oldval, newval)->{
			final double roundedValue = Math.floor(newval.doubleValue());
			dateSlider.valueProperty().set(roundedValue);
			this.selectedDate = dateConverter.longToDate((long)roundedValue);
			this.generateView();
		});
		
		this.regPropCol.setCellValueFactory(new PropertyValueFactory<TableView<RegressionTableData>, String>("regName"));
		this.regValCol.setCellValueFactory(new PropertyValueFactory<TableView<RegressionTableData>, Double>("regValue"));
		
	}
	/**
	 * This method stores set the minimum and maximum value of the slider to reflect the database's earliest and latest date that a data belongs to.
	 * Invoked after importing the dataset.
	 */
	public void initAfterImport() {
		this.regressionChart.getData().clear();
		this.regTable.getItems().clear();
		LocalDate maxDate = this.database.getLatest();
		LocalDate minDate = this.database.getEarliest();
		this.selectedDate = minDate;
		this.dateSlider.maxProperty().set(dateConverter.dateToLong(maxDate));
		this.dateSlider.minProperty().set(dateConverter.dateToLong(minDate));
		
		this.noDataLabel1.setText("Select an X-axis to begin!");
		this.noDataLabel2.setText("Using the combobox above chart");
	}
	
	/**
	 * This method constructs a list of pairs of {@link comp3111.covid.datastorage.LocationProperty} values and their corresponding names.
	 * A helper method for {@link TabC3pageController#initialize()}.
	 * @return {@link javafx.collections.ObservableList} of {@link javafx.util.Pair} of all {@link comp3111.covid.datastorage.LocationProperty} values and their corresponding names.
	 */
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
	
	/**
	 *  Main method for generating the chart and the table.
	 */
	private void generateView() {
		ArrayList<Pair<Number, Number>> rawData = database.searchDataPair(this.selectedDate, this.selectedProperty);
		RegressionResult regressionResult = this.generateRegression(rawData);
		this.generateChart(rawData, regressionResult);
		this.generateTable(regressionResult);
		
	}
	
	/**
	 * Main method for generating the regression chart.
	 * @param data				A {@link java.util.ArrayList} of raw data found from the database.
	 * @param regressionResult	A {@link RegressionResult} object storing regression values stored generated from raw data.
	 */
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
		this.addTooltip(scatter);
	}
	
	/**
	 * A method to get the largest X value in a list of raw data.
	 * A helper method to {@link TabC3pageController#generateRegressionSeries(ArrayList, RegressionResult)}.
	 * @param sourceData	A list of raw data from the Database.
	 * @return				the largest X value in the list of raw data.
	 */
	private double getLastX(ArrayList<Pair<Number, Number>> sourceData) {
		double maxX = 0;
		for(Pair<Number, Number> pair : sourceData) {
			double x = pair.getKey().doubleValue();
			maxX = (maxX > x) ? maxX : x; 
		}
		return maxX;
	}
	/**
	 * A method to generate a {@link RegressionResult} object from data obtained from database.
	 * @param rawData	list of data obtained from database.
	 * @return			{@link RegressionResult} object containing the values of regression performed on the set of data.
	 */
	protected RegressionResult generateRegression(ArrayList<Pair<Number, Number>> rawData) {
		SimpleRegression regression = new SimpleRegression(true);
		for(Pair<Number, Number> datum : rawData) {
			regression.addData(datum.getKey().doubleValue(), datum.getValue().doubleValue());
		}
		return new RegressionResult(regression.getIntercept(), regression.getSlope(), regression.getSignificance(), regression.getR(), regression.getRSquare());
	}
	/**
	 * A method creating the regression line in the chart.
	 * @param rawData			list of data obtained from the database.
	 * @param regressionResult	A {@link RegressionResult} object containing the values of regression performed on Raw Data.
	 * @return					A {@link javafx.scene.chart.XYChart.Series} object for creating the regression line in the chart.
	 */
	protected XYChart.Series<Number, Number> generateRegressionSeries(ArrayList<Pair<Number, Number>> rawData, RegressionResult regressionResult) {
		double slope = regressionResult.getSlope();
		double intercept = regressionResult.getIntercept();
		
		XYChart.Series<Number, Number> regressionSeries = new XYChart.Series<>();
		regressionSeries.setName("Regression Line");
		double lastX = getLastX(rawData);
		double lastY = slope * lastX + intercept;
		regressionSeries.getData().add(new Data<Number, Number>(0, intercept));
		regressionSeries.getData().add(new Data<Number, Number>(lastX, lastY));
		return regressionSeries;
	}
	/**
	 * A method creating the scatter plot series for raw data in the chart.
	 * @param rawData	Raw data to be plotted on the graph.
	 * @return			A {@link javafx.scene.chart.XYChart.Series} object for creating the scatter plot in the chart.
	 */
	protected XYChart.Series<Number, Number> generateScatterSeries(ArrayList<Pair<Number, Number>> rawData){
		XYChart.Series<Number, Number> scatter = new XYChart.Series<>();
		scatter.setName("actual data points");
		for(Pair<Number, Number> pair : rawData) {
			Data<Number,Number> data = new Data<Number,Number>(pair.getKey(),pair.getValue());
			data.setExtraValue("Hello");
			scatter.getData().add(data);
		}
		return scatter;
	}
	
	private void addTooltip(XYChart.Series<Number, Number> s) {
		DecimalFormat df = new DecimalFormat("#.####");
		df.setRoundingMode(RoundingMode.CEILING);
		for(Data<Number,Number> d : s.getData()) {
			Tooltip t = new Tooltip(
					d.getExtraValue().toString() + "\n" +
					this.LOC_PROP_TEXT[this.selectedProperty.value()] + ": " + d.getXValue().toString() + "\n" + 
					"Vaccination Rate: " + df.format(d.getYValue().doubleValue()) + "%");
			Tooltip.install(d.getNode(), t);
			d.getNode().setOnMouseEntered(event->d.getNode().getStyleClass().clear());
			d.getNode().setOnMouseEntered(event->d.getNode().getStyleClass().add("onHover"));
			d.getNode().setOnMouseEntered(event->d.getNode().getStyleClass().remove("onHover"));
		}
	}
	
	// ---------------- for table ----------------------
	
	/**
	 * A helper class for holding data to be put in the table.
	 */
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
	/**
	 * A helper class to hold values generated from performing simple linear regression on a set of data.
	 * It holds these properties:
	 * 1. Intercept
	 * 2. Slope
	 * 3. Statistical significance of the slope value
	 * 4. Pearson's Correlation Coefficient (commonly called R value)
	 * 5. Coefficient of Determination (commonly known as R-squared)
	 */
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
	/**
	 * The main method for generating the table.
	 * Its main functionality is to set the title on the left column and extract data from the given {@link RegressionResult} object to fill in the right row.
	 * @param regressionResult	{@link RegressionResult} object generated from data from the database.
	 */
	private void generateTable(RegressionResult regressionResult) {
		ObservableList<RegressionTableData> oList = FXCollections.observableArrayList();
		oList.add(new RegressionTableData("Intercept", regressionResult.getIntercept()));
		oList.add(new RegressionTableData("Slope", regressionResult.getSlope()));
		oList.add(new RegressionTableData("Level of Significance of Slope", regressionResult.getSignificance()));
		oList.add(new RegressionTableData("Pearson's Correlation Coefficient (R)", regressionResult.getR()));
		oList.add(new RegressionTableData("Coefficient of Determination (R-squared)", regressionResult.getRsquared()));
		// to make the columns get the right data from RegressionTableData
		this.regTable.setItems(oList);
	}
	
}

