package comp3111.covid.tabs;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import org.controlsfx.control.CheckListView;

public class TableTabsController extends TabController {
	@FXML private DatePicker datePicker;
	@FXML private TableColumn<TableView<TableData>,String> countryCol;
	@FXML private TableColumn<TableView<TableData>,Long> totalCol;
	@FXML private TableColumn<TableView<TableData>,Double> rateCol;
	@FXML private TableView<TableData> dataTable;
	@FXML private Label tableTitlelbl;
	
	public class TableData {
		private final SimpleStringProperty countryName;
		private final SimpleLongProperty totalData;
		private final SimpleDoubleProperty rateData;

		private TableData (String location, long total, double rate) {
			this.countryName = new SimpleStringProperty(location);
			this.totalData = new SimpleLongProperty(total);
			this.rateData = new SimpleDoubleProperty(rate);
		}
		
		public String getCountryName() {
			return this.countryName.get();
		}
		
		public Long getTotalData() {
			return this.totalData.get();
		}
		
		public Double getRateData() {
			return this.rateData.get();
		}
	}
	
	private void generateTable() {
		System.out.println("Generate Table");
		final ObservableList<TableData> data = FXCollections.observableArrayList(
				new TableData("Hong Kong", 10, 1.1),
				new TableData("USA", 12, 2.1),
				new TableData("Germany", 30, 3.3)
		);
		
		this.countryCol.setCellValueFactory(new PropertyValueFactory<TableView<TableData>, String>("countryName"));
		this.totalCol.setCellValueFactory(new PropertyValueFactory<TableView<TableData>, Long>("totalData"));
		this.rateCol.setCellValueFactory(new PropertyValueFactory<TableView<TableData>, Double>("rateData"));
		
		this.dataTable.setItems(data);
	}
	
	@Override
	void handleConfirmSelection(ActionEvent event) {
		this.generateTable();
	}
}
