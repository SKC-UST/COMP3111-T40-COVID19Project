package comp3111.covid.tabs;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

import comp3111.covid.Context;
import comp3111.covid.datastorage.Database;
import comp3111.covid.datastorage.Database.DataTitle;
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
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.commons.math3.stat.regression.SimpleRegression;

public class TabC3pageController {
	
	private Context context = Context.getInstance();
	private Database database = context.getDatabase();
	
	@FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;
	@FXML private LineChart<Number, Number> regressionChart;
	@FXML private Button testButton;
	@FXML private ComboBox<String> comboBox;
	
	public void initialize() {
		ObservableList<String> oList = FXCollections.observableArrayList("ABCV", "B", "C");
		this.comboBox.setItems(oList);
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
		
		this.regressionChart.setAnimated(false);
		this.regressionChart.setCreateSymbols(true);
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

