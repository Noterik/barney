/* 
* TicketHandler.java
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
package org.springfield.barney.ticket;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springfield.barney.GlobalConfig;

/**
 * Class that generates, validates, refreshes tickets. Operations may alter a ticket.
 * 
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.barney.ticket
 * @access private
 *
 */
public class TicketHandler {
	/** TicketValidator's log4j Logger */
	private static Logger logger = Logger.getLogger(TicketHandler.class);
	
	/**
	 * Refreshes ticket where needed.
	 *  
	 * @param ticket	ticket to refresh
	 */
	public static void refresh(Ticket ticket) {
		// check ticket's expiration date
		Date now = ticket.getNow();
		Date expire = ticket.getExpirationDate();
		if(expire.before(now)) {
			System.out.println("refreshing ticket");
			logger.debug("refreshing ticket");
			ticket.setCreationDate(now);
			ticket.setRandom(""+getRandom());
		} else {
			System.out.println("not refreshing");
			logger.debug("not refreshing");
		}

		// refresh ticket expiration date
		ticket.setExpirationDate(new Date( System.currentTimeMillis() + GlobalConfig.instance().getExpirationMilis() ));
	}
	
	/**
	 * Determine validity of ticket, given a ticket value.
	 * 
	 * @param ticket
	 * @param ticketValue
	 * @return
	 */
	public static boolean validate(Ticket ticket, String ticketValue) {
		logger.debug("validating ticket: "+ticket+", against ticketValue: "+ticketValue);
		
		boolean valid = false;
		if(ticket!=null && ticketValue!=null) {
			// check expiration
			Date now = ticket.getNow();
			Date expire = ticket.getExpirationDate();
			boolean exp = expire.before(now);
			
			// check equality
			boolean eq = ticketValue.equals(ticket.getTicketValue());
			
			// determine validity
			valid = (eq && !exp);
			
			logger.debug("equal: "+eq+", expired: "+exp);
		}
		return valid;
	}
	
	/**
	 * Generate a new ticket.
	 * 
	 * @param userID
	 * @param domainID
	 * @return
	 */
	public static Ticket newTicket(String userID, String domainID) {
		// generate data
		long now = System.currentTimeMillis();
		Date creationDate = new Date( now );
		Date expirationDate = new Date( now + GlobalConfig.instance().getExpirationMilis() );
		int random = getRandom();
		
		// create ticket
		Ticket ticket = new Ticket(userID, domainID, expirationDate, creationDate, ""+random);
		return ticket;
	}
	
	/**
	 * Returns a random number between 0 and 1000
	 * 
	 * @return a random number between 0 and 1000
	 */
	private static int getRandom() {
		return (int) (Math.random()*1000.0);
	}
}
