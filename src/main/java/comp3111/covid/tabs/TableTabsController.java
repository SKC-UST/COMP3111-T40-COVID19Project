package comp3111.covid.tabs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import org.controlsfx.control.CheckListView;

public class TableTabsController extends TabController {
	@FXML private DatePicker datePicker;
	@FXML private TableColumn<TableView<TableData>,String> contryCol;
	@FXML private TableColumn<TableView<TableData>,Long> totalCol;
	@FXML private TableColumn<TableView<TableData>,Double> rateCol;
	@FXML private TableView<TableData> dataTable;
	@FXML private Label tableTitlelbl;
	
	private class TableData {
		private String countryName;
		private long totalData;
		private double rateData;

		private TableData (String location, long total, double rate) {
			this.countryName = location;
			this.totalData = total;
			this.rateData = rate;
		}
	}
}
