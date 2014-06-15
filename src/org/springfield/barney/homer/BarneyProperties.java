package org.springfield.barney.homer;

public class BarneyProperties {
	private String ipnumber;
	private String name;
	private String status;
	private String defaultloglevel;
	private String preferedsmithers;
	
	public void setIpNumber(String i) {
		ipnumber = i;
	}
	

	public void setName(String n) {
		name = n;
	}
	
	public void setDefaultLogLevel(String l) {
		defaultloglevel = l;
	}
	
	public void setPreferedSmithers(String p) {
		preferedsmithers = p;
	}
	
	public void setStatus(String s) {
		status = s;
	}
	
	public String getName() {
		return name;
	}
	
	public String getIpNumber() {
		return ipnumber;
	}
	
	
	public String getStatus() {
		return status;
	}
	
	
	public String getDefaultLogLevel() {
		return defaultloglevel;
	}
	
	
	public String getPreferedSmithers() {
		return preferedsmithers;
	}
	
}
