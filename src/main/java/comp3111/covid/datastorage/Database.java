package comp3111.covid.datastorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Database {
	
	private class LocationData {
		private String locationIsoCode;
		private String locationContinent;
		private String locationName;
		private ArrayList<DayData> dataList = new ArrayList();
		
		private class DayData {
			//Represents a row in the CSV file
			private Date dataDate;
			private int newCases;
			private int newDeaths;
			private int newVaccinations;
			
			DayData(String date, String caseNum, String deathsNum, String vcaccinationNum){
				try {
					this.dataDate = new SimpleDateFormat("d/MM/yyyy").parse(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.newDeaths = Integer.parseInt(caseNum);
				this.newCases = Integer.parseInt(deathsNum);
				this.newVaccinations = Integer.parseInt(vcaccinationNum);
			}
		}
		
		LocationData(String isoCode, String continent, String location) {
			this.locationIsoCode = isoCode;
			this.locationContinent = continent;
			this.locationName = location;
		}
	}
	
	private List<String[]> arrayStorage;
	private HashMap<String, LocationData> hashStorage = new HashMap<String, LocationData>(); //isoCode as key
	private boolean datasetPresent = false;
	
	public void importCSV(File csvDataset) {
		try {
			CSVReader csvReader = new CSVReaderBuilder(new FileReader(csvDataset)).withSkipLines(1).build();
			this.arrayStorage = csvReader.readAll();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void printDatabaseContent() {
		for(String[] row : this.arrayStorage) {
			for(String cell : row) {
				System.out.print(cell + "\t");
			}
			System.out.println();
		}
	}
}
