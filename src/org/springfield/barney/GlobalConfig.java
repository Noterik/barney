package org.springfield.barney;

import java.util.Properties;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * 
 * Class responsible for creating and setting the properties of the config file, namely the 
 * database host, name, user and password.
 * Also sets the path for the file itself.
 *
 */

// TODO use caches

public class GlobalConfig
{
	private static GlobalConfig instance;
	private static String identifier = "barney";
	private static String SmithersAddr = "http://localhost:8080/smithers2/domain/";
	private static String CONFIG_FILE = "config.xml";
	public static String baseDir;
	
	public static final String PACKAGE_ROOT = "org.springfield.barney";

	
	// ticket expiration (default 2 hours)
	private long expirationMilis = 2*60*60*1000;
	
	public long getExpirationMilis() {
		return expirationMilis;
	}
	
	public static String getIdentifier(){
		return GlobalConfig.identifier;
	}
	
	public static String getSmithers(){
		return GlobalConfig.SmithersAddr;
	}
	
	public static void setSmithers(String addr){
		GlobalConfig.SmithersAddr = addr;
	}

	public static String getBaseDir()
	{
		return GlobalConfig.baseDir;
	}

	public static void initialize(String baseDir)
	{
		GlobalConfig.baseDir = baseDir;
	}

	public String getBaseAssetPath()
	{
		return baseDir + "xml/";
	}

	

	private GlobalConfig()
	{
		initConfig();
		//initCaches();
	}
	
	/**
	 * Builds the config file path and sets it's properties: database name, host, user and password
	 *
	 */
	public void initConfig()
	{
		initLogging();
		
		File file;
		Properties props = new Properties();

		baseDir = baseDir + "META-INF/config/" + CONFIG_FILE;
		
	//	System.out.println("Trying to read config file: " + baseDir);
		
		
	}
	
	/**
	 * Initialize logging
	 */
	private void initLogging() {
		System.out.println("Initializing logging");
		
		// enable appenders
    	String logPath = baseDir.substring(0,baseDir.indexOf("webapps"));
		logPath += "logs/barney/barney.log";	
		
		try {
			// default layout
			//Layout layout = new PatternLayout("%r [%t] %-5p %c %x - %m%n");
			Layout layout = new PatternLayout("%-5p: %d{yyyy-MM-dd HH:mm:ss} %c %x - %m%n");
			
			// rolling file appender
			DailyRollingFileAppender appender1 = new DailyRollingFileAppender(layout,logPath,"'.'yyyy-MM-dd");
			BasicConfigurator.configure(appender1);
			
			// console appender 
			ConsoleAppender appender2 = new ConsoleAppender(layout);
			// only log error messages to console
			//appender2.setThreshold(Level.ERROR);
			BasicConfigurator.configure(appender2);
		}
		catch(IOException e) {
			System.out.println("GlobalConfig got an exception while initializing the logging configuration");
			e.printStackTrace();
		}
		
		/*
		 *  turn off all logging, and enable ERROR logging for noterik root package
		 *  use restlet.LoggingResource to enable specific logging
		 */
		Logger.getRootLogger().setLevel(Level.OFF);
		Logger.getLogger(PACKAGE_ROOT).setLevel(Level.ERROR);
		
		System.out.println("Initializing logging done");
	}

	public static GlobalConfig instance()
	{
		if (instance == null)
		{
			instance = new GlobalConfig();
		}
		return instance;
	}
}