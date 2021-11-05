package comp3111.covid.datastorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Database {
	
	private class LocationData {
		private String isoCode;
		private String continent;
		private String location;
		
		private class DayData {
			//Represents a row in the CSV file
			private Date dataDate;
			private int newCases;
			private int newDeaths;
			private int newVaccinations;
		}
	}
	
	private ArrayList<ArrayList<String>> arrayStorage = new ArrayList<ArrayList<String>>();
	private boolean datasetPresent = false;
	private HashMap<String, LocationData> hashStorage = new HashMap<String, LocationData>();
	
	public void importCSV(File csvDataset) {
		//TODO: skip the first line (may consider using CSV reader)
		String line = "";
    	try {
			BufferedReader br = new BufferedReader(new FileReader(csvDataset));
			int index = 0;
			
			while((line = br.readLine()) != null) {
				arrayStorage.add(new ArrayList<String>());
				String[] values = line.split(",");
				for(String value : values) {
					arrayStorage.get(index).add(value);
				}
				++index;
			}
			br.close();
		} 
    	catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	catch (IOException e) {
    		e.printStackTrace();
    	}
    	datasetPresent = true;
	}
	
	public void removeDataset(){
		datasetPresent = false;
		for(int i = 0; i < arrayStorage.size(); ++i) {
			arrayStorage.get(i).clear();
		}
		arrayStorage.clear();
	}
	
	public void printDatabaseContent() {
		System.out.println(arrayStorage.get(1));
	}
}
