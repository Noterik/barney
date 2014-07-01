package org.springfield.barney;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;


public class ShadowFiles {
	
	protected static String getProperty(String path,String fieldname) {
		try {
		BufferedReader file = new BufferedReader(new FileReader("/springfield/barney/data/shadowfiles"+path+"/properties.txt"));
		    try {
		        String prop = file.readLine();

		        while (prop != null) {
		        	int pos = prop.indexOf("=");
		        	if (pos!=-1) {
		        		String field = prop.substring(0,pos);
		        		String value = prop.substring(pos+1);
		        		if (field.equals(fieldname)) {
		        			file.close();
		        			return value;
		        		}
		        	}
		            prop = file.readLine();
		        }
		    } finally {
		        file.close();
		    }
		} catch(Exception e) {}
		return null;
	}
	
	protected static String setProperty(String path,String fieldname,String value) {
		try {
			String writedir =  "/springfield/barney/data/shadowfiles"+path;
			File md = new File(writedir);
			md.mkdirs();
			
		    PrintWriter writer = new PrintWriter(writedir+"/properties.txt", "UTF-8");
		    writer.println(fieldname+"="+value);
		    writer.close();
		} catch(Exception e) {}
		return null;
	}
	
	


}
