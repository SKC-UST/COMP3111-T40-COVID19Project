package comp3111.covid;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
 
 
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
 
// source: https://docs.oracle.com/javafx/2/charts/line-chart.htm#CIHGBCFI
public class LineChartSample extends Application {
 
    @Override public void start(Stage stage) {
        stage.setTitle("Line Chart Sample"); // window title
        final CategoryAxis xAxis = new CategoryAxis(); // new xaxis
        final NumberAxis yAxis = new NumberAxis(); // new yaxis
         xAxis.setLabel("Month");
        final LineChart<String,Number> lineChart = 
                new LineChart<String,Number>(xAxis,yAxis); // seems making a new line chart, x is string, y is int here
       
        lineChart.setTitle("Stock Monitoring, 2010"); // linechart title
                          
        XYChart.Series series1 = new XYChart.Series(); // new series of data (aka new country)
        series1.setName("Portfolio 1"); // name of instance of data (aka country)
        
        series1.getData().add(new XYChart.Data("Jan", 23)); // lots of instances
        series1.getData().add(new XYChart.Data("Feb", 14));
        series1.getData().add(new XYChart.Data("Mar", 15));
        series1.getData().add(new XYChart.Data("Apr", 24));
        series1.getData().add(new XYChart.Data("May", 34));
        series1.getData().add(new XYChart.Data("Jun", 36));
        series1.getData().add(new XYChart.Data("Jul", 22));
        series1.getData().add(new XYChart.Data("Aug", 45));
        series1.getData().add(new XYChart.Data("Sep", 43));
        series1.getData().add(new XYChart.Data("Oct", 17));
        series1.getData().add(new XYChart.Data("Nov", 29));
        series1.getData().add(new XYChart.Data("Dec", 25));
        
        XYChart.Series series2 = new XYChart.Series(); // another country
        series2.setName("Portfolio 2"); // country name
        series2.getData().add(new XYChart.Data("Jan", 33)); // country data
        series2.getData().add(new XYChart.Data("Feb", 34));
        series2.getData().add(new XYChart.Data("Mar", 25));
        series2.getData().add(new XYChart.Data("Apr", 44));
        series2.getData().add(new XYChart.Data("May", 39));
        series2.getData().add(new XYChart.Data("Jun", 16));
        series2.getData().add(new XYChart.Data("Jul", 55));
        series2.getData().add(new XYChart.Data("Aug", 54));
        series2.getData().add(new XYChart.Data("Sep", 48));
        series2.getData().add(new XYChart.Data("Oct", 27));
        series2.getData().add(new XYChart.Data("Nov", 37));
        series2.getData().add(new XYChart.Data("Dec", 29));
        
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("Portfolio 3");
        series3.getData().add(new XYChart.Data("Jan", 44));
        series3.getData().add(new XYChart.Data("Feb", 35));
        series3.getData().add(new XYChart.Data("Mar", 36));
        series3.getData().add(new XYChart.Data("Apr", 33));
        series3.getData().add(new XYChart.Data("May", 31));
        series3.getData().add(new XYChart.Data("Jun", 26));
        series3.getData().add(new XYChart.Data("Jul", 22));
        series3.getData().add(new XYChart.Data("Aug", 25));
        series3.getData().add(new XYChart.Data("Sep", 43));
        series3.getData().add(new XYChart.Data("Oct", 44));
        series3.getData().add(new XYChart.Data("Nov", 45));
        series3.getData().add(new XYChart.Data("Dec", 44));
        
        Scene scene  = new Scene(lineChart,800,600);  // new scene with window size and chart?
        lineChart.getData().addAll(series1, series2, series3); // insert all data
       
        stage.setScene(scene); // set window with chart
        stage.show(); // show chart
    }
}