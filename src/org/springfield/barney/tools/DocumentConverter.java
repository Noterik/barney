package org.springfield.barney.tools;

import org.dom4j.Document;
import org.dom4j.io.DOMWriter;

public class DocumentConverter {
	/**
	 *  
	 * This method converts a regular Document to the org.w3c.dom.Document format
	 * 
	 * @param doc1
	 * @return XML Document
	 */
	
	public static org.w3c.dom.Document convert(Document doc1) 
	{
		if (doc1 == null) 
		{
			return null;
		}
		DOMWriter writer = new DOMWriter();
		org.w3c.dom.Document doc2 = null;
		try 
		{
			doc2 = writer.write(doc1);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return doc2;
	}
}
