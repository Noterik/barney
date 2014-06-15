package org.springfield.barney.dao;

import java.io.Serializable;

/**
 * Generic Data Access Object interface, for basic CRUD functionality.
 * 
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.barney.dao
 * @access private
 *
 */
public interface GenericDAO <T,K extends Serializable> {
	
	/**
	 * Create object in persistent data source.
	 *  
	 * @param transferObject	transfer object
	 * @return					success
	 */
	public boolean create(T transferObject);
	
	/**
	 * Loads object from data source and returns a transfer object.
	 * 
	 * @param key	key defining object in data source.
	 * @return		transfer object, null upon failure
	 */
	public T read(K key);
	
	/**
	 * Update object in persistent data source.
	 * 
	 * @param transferObject	transfer object
	 * @return 					success
	 */
	public boolean update(T transferObject);
	
	/**
	 * Delete object from data source, defined by key.
	 * 
	 * @param key				key defining object in data source.
	 * @return 					success
	 */
	public boolean delete(K key);
}
