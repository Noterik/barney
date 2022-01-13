/* 
* ShadowFiles.java
* 
* Copyright (c) 2014 Noterik B.V.
* 
* This file is part of Barney, related to the Noterik Springfield project.
*
* Barney is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Barney is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Barney.  If not, see <http://www.gnu.org/licenses/>.
*/
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
			
			File cf = new File("/springfield/barney/data/shadowfiles"+path+"/properties.txt");
			if (!cf.exists()) {
				// ok its a new file lets create it
			    PrintWriter writer = new PrintWriter("/springfield/barney/data/shadowfiles"+path+"/properties.txt", "UTF-8");
				writer.println(fieldname+"="+value);
				writer.close();
				return null;
			}
			
		    PrintWriter writer = new PrintWriter(writedir+"/properties.tmp", "UTF-8");
			BufferedReader file = new BufferedReader(new FileReader("/springfield/barney/data/shadowfiles"+path+"/properties.txt"));
			try {
		        String prop = file.readLine();

		        while (prop != null) {
		        	int pos = prop.indexOf("=");
		        	if (pos!=-1) {
		        		String ofield = prop.substring(0,pos);
		        		String ovalue = prop.substring(pos+1);
		        		if (ofield.equals(fieldname)) {
		        			// do nothing we will add at the end
		        		} else {
		        			writer.println(ofield+"="+ovalue);
		        		}
		        	}
		            prop = file.readLine();
		        }
		    } finally {
		        file.close();
		    }
		    
		    
		   // we removed the old one if needed add the new one.
		   writer.println(fieldname+"="+value);
		   writer.close();
		   
		   // delete old file
		   File oldf = new File("/springfield/barney/data/shadowfiles"+path+"/properties.txt");
		   oldf.delete();
		   // rename tmpfile;
		   File tmpf = new File("/springfield/barney/data/shadowfiles"+path+"/properties.tmp");
		   tmpf.renameTo(new File("/springfield/barney/data/shadowfiles"+path+"/properties.txt"));
		   
		} catch(Exception e) {}
		return null;
	}
	
	


}
