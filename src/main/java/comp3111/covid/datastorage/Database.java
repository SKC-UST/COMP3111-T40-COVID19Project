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
					this.dataDate = new SimpleDateFormat("MM/dd/yyyy").parse(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.newDeaths = Integer.parseInt(caseNum);
				this.newCases = Integer.parseInt(deathsNum);
				this.newVaccinations = Integer.parseInt(vcaccinationNum);
			}
			
			DayData(String date, int caseNum, int deathsNum, int vcaccinationNum){
				try {
					this.dataDate = new SimpleDateFormat("MM/dd/yyyy").parse(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.newDeaths = caseNum;
				this.newCases = deathsNum;
				this.newVaccinations = vcaccinationNum;
			}
			
		}
		
		LocationData(String isoCode, String continent, String location) {
			this.locationIsoCode = isoCode;
			this.locationContinent = continent;
			this.locationName = location;
		}
		
		private void addDayData(DayData newDayData) {
			dataList.add(newDayData);
		}
		
		public ArrayList<DayData> getDayData() {
			return this.dataList;
		}
	}
	
	private ArrayList<String[]> arrayStorage = new ArrayList<String[]>();
	private HashMap<String, LocationData> hashStorage = new HashMap<String, LocationData>(); //isoCode as key
	private boolean datasetPresent = false;
	
	private void rowToData(String[] row) {
		String isoCode = row[0];
		if(!(hashStorage.containsKey(isoCode))) {
			hashStorage.put(isoCode, new LocationData(isoCode, row[1], row[2]));
		}
		try {
			hashStorage.get(isoCode).addDayData(hashStorage.get(isoCode).new DayData(row[3], row[5], row[8], row[37]));
		}catch(Exception e) {
			System.out.println(isoCode + " no data for " + row[3]);
		}
	}
	
	public void importCSV(File csvDataset) {
		try {
			CSVReader csvReader = new CSVReader(new FileReader(csvDataset));
			String[] nextRecord = csvReader.readNext();
			
			while((nextRecord = csvReader.readNext()) != null) {
				rowToData(nextRecord);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void printDatabaseContent() {
		/*
		for(String[] row : this.arrayStorage) {
			for(String cell : row) {
				System.out.print(cell + "\t");
			}
			System.out.println();
		}
		*/
		String dateString = "7/1/2021"; //July 1
		try {
			Date testDate = new SimpleDateFormat("MM/dd/yyyy").parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
