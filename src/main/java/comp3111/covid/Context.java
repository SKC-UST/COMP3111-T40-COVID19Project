package comp3111.covid;

import comp3111.covid.datastorage.*;
import comp3111.covid.dataAnalysis.*;

public class Context {
	private final static Context instance = new Context(); //singleton
	private Database database = new Database();
	private DateConverter dc = new DateConverter();
	
	public static Context getInstance() {
		return instance;
	}
	
	public Database getDatabase() {
		return database;
	}
	
	public DateConverter getDateConverter() {
		return dc;
	}
}
