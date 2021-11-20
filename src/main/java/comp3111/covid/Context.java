package comp3111.covid;

import comp3111.covid.datastorage.*;
import comp3111.covid.dataAnalysis.*;

/**
 * A class to enforce singleton on Database class and DateConverter class, so that all the controllers access the same database, instead of instances of datbase.
 * There is only one Context object across the entire program.
 * @author ytc314
 *
 */
public class Context {
	/**
	 * The instance of context
	 * This is used to establish singleton
	 */
	private final static Context instance = new Context(); 
	/**
	 * The single instance of internal database of the program
	 * @see Database
	 */
	private Database database = new Database();
	/**
	 * The single instance of date converter of the program
	 * @see DateConverter 
	 */
	private DateConverter dc = new DateConverter();
	
	/**
	 * @return the current instance of the Context object
	 */
	public static Context getInstance() {
		return instance;
	}
	
	/**
	 * {@link Context#database}
	 * @return the internal database of the program.
	 * @see Database
	 */
	public Database getDatabase() {
		return database;
	}
	
	/**
	 * {@link Context#dc}
	 * @return a date converter object
	 * @see	DateConverter
	 */
	public DateConverter getDateConverter() {
		return dc;
	}
}
