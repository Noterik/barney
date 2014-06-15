/* 
* SUMRestletApplication.java
* 
* Copyright (c) 2012 Noterik B.V.
* 
* This file is part of barney, related to the Noterik Springfield project.
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
* along with Lou.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.springfield.barney.restlet;

import java.util.List;
import javax.servlet.ServletContext;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.springfield.barney.GlobalConfig;
import org.springfield.barney.homer.LazyHomer;

import com.noelios.restlet.ext.servlet.ServletContextAdapter;
import com.noterik.bart.marge.model.Service;
import com.noterik.bart.marge.server.MargeContainer;




public  class SUMRestletApplication extends Application 
{
	Context cx;
	MargeContainer container=new MargeContainer();
	List<Service> svclist;
	private static LazyHomer lh = null; 
	

	public SUMRestletApplication(Context cx) 
	{
		 	this.cx=cx;
	}
	public SUMRestletApplication() 
	{
		super();
	}
	
	public void start() {
		try{
			super.start();
		}catch(Exception e){
			System.out.println("Error starting application");
			e.printStackTrace();
		}
	}

	@Override
	public Restlet createRoot() 
    {  	
		ServletContextAdapter adapter = (ServletContextAdapter) getContext();
		ServletContext servletContext = adapter.getServletContext();
		GlobalConfig.initialize(servletContext.getRealPath("/"));
		
		lh = new LazyHomer();
		lh.init(servletContext.getRealPath("/"));
		
		return new SUMRestlet(super.getContext());		
    }
    
	
}