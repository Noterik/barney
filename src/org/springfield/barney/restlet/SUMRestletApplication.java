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