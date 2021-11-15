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
	@FXML private TableColumn<?,?> contryCol;
	@FXML private TableColumn<?,?> totalCol;
	@FXML private TableColumn<?,?> rateCol;
	@FXML private TableView<?> dataTable;
	@FXML private Label tableTitlelbl;
}
