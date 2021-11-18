package comp3111.covid.tabs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;

import org.controlsfx.control.CheckListView;

public class ChartTabsController extends TabController{

    @FXML private Button confirmButton;
    @FXML private DatePicker startDatePicker;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    
    private LocalDate selecedDate = null;
    
    
    
    
    @FXML
    void handleConfirmSelection(ActionEvent event) {

    }

    @FXML
    void selectTargetDate(ActionEvent event) {

    }

}
