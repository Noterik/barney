package org.springfield.barney.tools;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.dom4j.Document;

/**
 * Helper class for basic Http request functionality.
 * 
 * @author Jaap Blom <j.blom@noterik.nl>
 * @author Levi Pires <l.pires@noterik.nl>
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2008
 * @package com.noterik.springfield.tools
 * @access private
 * @version $Id: HttpHelper.java,v 1.22 2011-11-23 08:13:04 derk Exp $
 * 
 */
public class HttpHelper {
	
	private static final byte DEFAULT_MAX_RETRIES = 5;

	/**
	 * Sends a REST request to the specified URL using the determined method.
	 * Also a Document with XML content will be sent attached.
	 * 
	 * @param method
	 *            the request method: GET, POST, PUT or DELETE
	 * @param url
	 *            the URL to send the request to
	 * @param dom
	 *            the Document representing the XML content
	 * @deprecated
	 */
	public static String sendRestRequest(String method, String url, Document dom) {
		if (dom == null) {
			return null;
		}
		return sendRestRequest(method, url, dom.asXML());
	}

	/**
	 * Sends a REST request to the specified URL using the determined method.
	 * Also a Document with XML content will be sent attached.
	 * 
	 * @param method
	 *            the request method: GET, POST, PUT or DELETE
	 * @param url
	 *            the URL to send the request to
	 * @param xml
	 *            the XML content
	 * @deprecated           
	 */
	public static String sendRestRequest(String method, String url, String xml) {
		String resp = null;
		int i = 0;
		while(resp == null && i++ < DEFAULT_MAX_RETRIES){
			try {
				resp = sendRESTRequest(method, url, xml);
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e1) {/* no need for panic */}
				System.out.println("REQUEST FAILED, RETRY #" + (i + 1));
			}
		}
		return resp;
	}
	
	/**
	 * Sneds a REST request using the Restlet API
	 * 
	 * @param method
	 * @param url
	 * @param xml
	 * @return
	 * @throws Exception
	 * @deprecated CLOSE_WAIT connections
	 */
	private static String sendRESTRequest(String method, String url, String xml) throws Exception {
		return sendRequest(method, url, xml, "text/xml");
	}

	/**
	 * Send REST request (GET, PUT, POST or DELETE)
	 * 
	 * @param method
	 * @param url
	 * @param body
	 * @param contentType
	 * @param timeout		timeout in millis
	 * @return
	 */
	public static String sendRequest(String method, String url, String body, String contentType, int timeout) {
		return sendRequest(method, url, body, contentType, null, timeout);
	}
	
	/**
	 * Send REST request (GET, PUT, POST or DELETE)
	 * 
	 * @param method
	 * @param url
	 * @param body
	 * @param contentType
	 * @return
	 */
	public static String sendRequest(String method, String url, String body, String contentType) {
		return sendRequest(method, url, body, contentType, null, -1);
	}
	
	/**
	 * Send REST request (GET, PUT, POST or DELETE)
	 * 
	 * @param method
	 * @param url
	 * @param body
	 * @param contentType
	 * @param cookies
	 * @return
	 */
	public static String sendRequest(String method, String url, String body, String contentType, String cookies) {
		return sendRequest(method, url, body, contentType, cookies, -1);
	}

	/**
	 * Send REST request (GET, PUT, POST or DELETE)
	 * 
	 * @param method
	 * @param url
	 * @param body
	 * @param contentType
	 * @param cookies
	 * @param timeout		timeout in millis
	 * @return
	 */
	public static String sendRequest(String method, String url, String body, String contentType, String cookies, int timeout) {
		// http client
		HttpClient client = new HttpClient();

		// method
		HttpMethodBase reqMethod = null;
		if (method.equals("PUT")) {
			reqMethod = new PutMethod(url);
		} else if (method.equals("POST")) {
			reqMethod = new PostMethod(url);
		} else if (method.equals("GET")) {
			if( body != null ) {
				// hack to be able to send a request body with a get (only if required)
				reqMethod = new PostMethod(url) {
					public String getName() {
						return "GET";
					}
				};
			} else {
				reqMethod = new GetMethod(url);
			}
		} else if (method.equals("DELETE")) {
			if( body != null ) {
				// hack to be able to send a request body with a delete (only if required)
				reqMethod = new PostMethod(url) {
					public String getName() {
						return "DELETE";
					}
				};
			} else {
				reqMethod = new DeleteMethod(url);
			}
		}

		// add request body
		if (body != null) {
			try {
				RequestEntity entity = new StringRequestEntity(body, contentType, "UTF-8");
				((EntityEnclosingMethod)reqMethod).setRequestEntity(entity);
				reqMethod.setRequestHeader("Content-type", contentType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// add cookies
		if (cookies != null)
			reqMethod.addRequestHeader("Cookie", cookies);

		// do request
		try {
			if(timeout!=-1) client.getParams().setSoTimeout(timeout);
			int statusCode1 = client.executeMethod(reqMethod);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// read response
		String response = null;
		try {
			InputStream instream = reqMethod.getResponseBodyAsStream();
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
            int len;
            while ((len = instream.read(buffer)) > 0) {
                outstream.write(buffer, 0, len);
            }
            response = new String(outstream.toByteArray(), reqMethod.getResponseCharSet());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// release connection
		reqMethod.releaseConnection();

		// return
		return response;
	}
	
	/**
	 * Get a file through http
	 * 
	 * @param address
	 * 					remote file location
	 * @param localFileName
	 */
	public static boolean getFileWithHttp(String address, String localFileName) {		
		OutputStream out = null;
		URLConnection conn = null;
		InputStream  in = null;
		try {
			// create local folders
			new File(localFileName).getParentFile().mkdirs();
			
			// get file
			URL url = new URL(address);
			out = new BufferedOutputStream(new FileOutputStream(localFileName));
			conn = url.openConnection();
			in = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int numRead;
			long numWritten = 0;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
				numWritten += numRead;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public static String getFileNameFromURL(String url) {
		if (url == null) {
			return null;
		}
		if (url.lastIndexOf(".") == -1 || url.lastIndexOf("/") == -1) {
			return null;
		} else {
			String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
			return fileName;
		}
	}

}