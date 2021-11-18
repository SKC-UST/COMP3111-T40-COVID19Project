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
		ArrayList<Pair<Number, Number>> rawData = this.database.searchDataPair(LocalDate.of(2021, 4, 25));
		this.generateRegression(rawData);
		this.generateChart(rawData);
		
	}
	
	private void generateChart(ArrayList<Pair<Number, Number>> sourceData) {
		XYChart.Series<Number, Number> scatter = new XYChart.Series<>();
		scatter.setName("actual data points");
		ArrayList<Data<Number, Number>> dataList = new ArrayList<Data<Number, Number>>();
		for(Pair<Number, Number> pair: sourceData) {
			scatter.getData().add(new Data<Number, Number>(pair.getKey(), pair.getValue()));
		}
		this.regressionChart.getData().add(scatter);
		this.regressionChart.getData().add(scatter);
		System.out.println(System.getProperty("user.dir"));
		File css = new File("./src/main/resources/stylesheet/root1.css");
		System.out.println(css.toString());
		this.regressionChart.getScene().getStylesheets().add(getClass().getResource("/stylesheet/root.css").toExternalForm());
	}
	
	private void generateRegression(ArrayList<Pair<Number, Number>> rawData) {
		SimpleRegression regression = new SimpleRegression(true);
		for(Pair<Number, Number> datum : rawData) {
			regression.addData(datum.getKey().doubleValue(), datum.getValue().doubleValue());
		}
	}
	
}

