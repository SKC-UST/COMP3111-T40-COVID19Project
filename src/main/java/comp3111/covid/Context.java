package comp3111.covid;

import comp3111.covid.datastorage.*;

public class Context {
	private final static Context instance = new Context(); //singleton
	
	public static Context getInstance() {
		return instance;
	}
	
	private Database database = new Database();
	
	public Database getDatabase() {
		return database;
	}
}
