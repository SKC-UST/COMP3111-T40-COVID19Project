package comp3111.covid.datastorage;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat; 

import comp3111.covid.MyApplication;
import comp3111.covid.Controller;
import comp3111.covid.DataAnalysis;
import comp3111.covid.datastorage.Database;
import comp3111.covid.datastorage.Database.DataTitle;
 

public class DatabaseTester {
	Database database;
	File csvFile;
	private static final double DELTA = 1e-15;
	
	@Before
	public void setUp() throws Exception {
		database = new Database();
		this.csvFile = new File("./src/main/resources/dataset/COVID_Dataset_v1.0.csv");
		database.importCSV(csvFile);
	}

	@Test
	public void searchTotalDataResult() {
	    String sDate1="2/24/2020";  
	    String sDate2="01/01/1970"; 
	    try 
	    {
		    Date date1=new SimpleDateFormat("MM/dd/yyyy").parse(sDate1);  
		    Date date2=new SimpleDateFormat("MM/dd/yyyy").parse(sDate2); 
			assertEquals(1, database.searchTotalData("AFG", date1, DataTitle.CASE));
			assertEquals(-1, database.searchTotalData("AFG", date2, DataTitle.CASE));
	    }

		catch (Exception e) 
	    {
			 e.printStackTrace();
		}
	}
	
	@Test
	public void searchRateDataResult() {
	    String sDate1="2/24/2020";  
	    try 
	    {
		    Date date1=new SimpleDateFormat("MM/dd/yyyy").parse(sDate1);   
			assertEquals(0.026, database.searchRateData("AFG", date1, DataTitle.CASE), DELTA);
	    }
		catch (Exception e) 
	    {
			 e.printStackTrace();
		}
	}
}