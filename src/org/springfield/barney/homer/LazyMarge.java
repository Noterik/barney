package org.springfield.barney.homer;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;




public class LazyMarge extends Thread {
	
	String group = "224.0.0.0";
	int errorcounter = 0;
	int errorcounter2 = 0;
	private static final Logger LOG = Logger.getLogger(LazyMarge.class);
	private static boolean running = false;
	private static Map<String, MargeObserver> observers = new HashMap<String, MargeObserver>();
	private static enum methods { GET,POST,PUT,DELETE,INFO,TRACE,LINK; }
	private static MargeTimerThread timerthread = null;
	MulticastSocket s = null;
	
	public LazyMarge() {
		if (!running) {
			running = true;
			start();
		}
		if (timerthread==null) {
			timerthread = new MargeTimerThread();

		}
	}
	
	public static void addObserver(String url,MargeObserver o) {
		observers.put(url, o);
	}
	
	public static void addTimedObserver(String url,int counter,MargeObserver o) {
		if (timerthread!=null) timerthread.addTimedObserver(url,counter,o);
	}
	
	public void run() {
		try {
			s = new MulticastSocket(LazyHomer.getPort());
			s.joinGroup(InetAddress.getByName(group));
			byte[] buffer = new byte[1024];
			DatagramPacket dp = new DatagramPacket(buffer, 1024);
			while (running) {
				try {
					dp.setLength(1024);
					s.receive(dp);
					byte[] message = new byte[dp.getLength()];
					System.arraycopy(dp.getData(), 0, message, 0, dp.getLength());
					String line = new String(message);

					String[] result = new String(message).split("\\s");
					switch (methods.valueOf(result[1])) {
					case POST :
						signalObservers(result[0],result[1],result[2]);
						break;
					case PUT :
						signalObservers(result[0],result[1],result[2]);
						break;
					case DELETE :
						signalObservers(result[0],result[1],result[2]);
						break;
					case TRACE :
						signalObservers(result[0],result[1],result[2]);
						break;
					case LINK :
						signalObservers(result[0],result[1],result[2]);
						break;
					case INFO :
						if (result[2].equals("ALIVE")) {
							String inc = result[0];
							int pos = inc.indexOf(":");
							if (pos==-1) {
								System.out.println("BARNEY: FATAL ERROR OLD ADD SMITHERS STYLE (NOT IP:PORT:MPORT) "+inc);
							} else {
								String ipn = inc.substring(0,pos);
								String pon = inc.substring(pos+1);
								pos = pon.indexOf(":");
								if (pos==-1) {
									System.out.println("BARNEY: FATAL ERROR OLD ADD SMITHERS STYLE (NOT IP:PORT:MPORT) "+inc);	
								} else {
									String mpon = pon.substring(pos+1);
									pon = pon.substring(0,pos);
									pos = mpon.indexOf(":");
									if (pos!=-1) {
										// we also have a role
										String mrole = mpon.substring(pos+1);
										mpon = mpon.substring(0,pos);
										LazyHomer.addSmithers(ipn,pon,mpon,mrole);	
									}
									LazyHomer.addSmithers(ipn,pon,mpon,"production");
								}
							}
						}
						/*
						if (result[2].equals("ALIVE")) {
							String inc = result[0];
							int pos = inc.indexOf(":");
							if (pos==-1) {
								System.out.println("BARNEY FATAL ERROR OLD ADD SMITHERS STYLE (NOT IP:PORT:MPORT) "+inc);
							} else {
								String ipn = inc.substring(0,pos);
								String pon = inc.substring(pos+1);
								pos = pon.indexOf(":");
								if (pos==-1) {
									System.out.println("BARNEY FATAL ERROR OLD ADD SMITHERS STYLE (NOT IP:PORT:MPORT) "+inc);	
								} else {
									String mpon = pon.substring(pos+1);
									pon = pon.substring(0,pos);
									LazyHomer.addSmithers(ipn,pon,mpon);
								}
							}
						}
						*/
						break;
					}
				} catch(Exception e2) {	
					if (running) {
						if (errorcounter<10) {
							errorcounter++;
							LOG.info("ERROR Multicast innerloop");
							e2.printStackTrace();
						}
					}
				}
			}
		} catch(Exception e) {
			if (running) {
				if (errorcounter2<10) {
					errorcounter2++;
					LOG.info("ERROR Multicast outerloop");
					//e.printStackTrace();
				}
			}
		}
	}
	
	private void signalObservers(String from,String method,String url) {
		
		int pos = url.lastIndexOf("/");
		
		while (pos!=-1) { // we need to walk up the tree to check
			// only works for one entry now, daniel
			Object obs = observers.get(url);
			if (obs!=null) {
				if (obs instanceof MargeObserver) {
					((MargeObserver)obs).remoteSignal(from, method, url);
				} // should also have a list
			}
		
			url = url.substring(0,pos);
			pos = url.lastIndexOf("/");
		}
	}

    /**
     * Shutdown
     */
	public void destroy() {
		running = false;
		if (timerthread!=null) {
			timerthread.destroy();
		}
		running = false;
		if (s!=null) s.close();
	}
	
}
