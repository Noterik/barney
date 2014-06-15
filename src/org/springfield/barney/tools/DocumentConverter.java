/* 
* DocumentConverter.java
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
