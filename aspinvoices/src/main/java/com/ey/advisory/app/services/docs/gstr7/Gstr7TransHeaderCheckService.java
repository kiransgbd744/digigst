package com.ey.advisory.app.services.docs.gstr7;

import org.javatuples.Pair;

/**
 * 
 * @author Siva.Reddy
 *
 */
public interface Gstr7TransHeaderCheckService {

	/**
	 * This method checks if the header cols is as expected for the uplaod
	 * format. If the headers is not as expected, then return an error message.
	 **/
	public Pair<Boolean, String> validate(Object[] headerCols);
}
