package comp3111.covid.datastorage;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.text.SimpleDateFormat; 

import comp3111.covid.MyApplication;
import comp3111.covid.Controller;
import comp3111.covid.DataAnalysis;
import comp3111.covid.datastorage.Database;
import comp3111.covid.datastorage.Database.DataTitle;
 

public class DatabaseTester {
	Database database = new Database();
	File file = new File("COVID_Dataset_v1.0.csv");
	
	@Before
	public void setUp() throws Exception {
		database.importCSV(file);
	}

	@Test
	public void searchTotalDataNoResult() {
	    String sDate1="31/12/1998";  
	    Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);  
		assertEquals(-1, Database.searchTotalData("HKG", date1, DataTitle.CASE));
	}
}