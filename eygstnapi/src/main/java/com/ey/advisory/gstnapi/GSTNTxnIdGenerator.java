package com.ey.advisory.gstnapi;

import com.ey.advisory.core.api.APIParams;

/**
 * Every API request sent to GSTN should be sent with a 
 * unique transaction id by calling application. Uniqueness of the txn can be 
 * achieved by prefixing initials of ASP/GSP at start. Txn should be 
 * Alphanumeric and 32 character long. Purpose of this id to track a request 
 * and response end to end. This will also help GSP to keep tab of 
 * request/response. Calling application will get back same txn back in the 
 * response.
 * 
 * @author Sai.Pakanati
 *
 */
public interface GSTNTxnIdGenerator {
	
	/**
	 * Generate a unique transaction id for the request and sent it.
	 * 
	 * @param params
	 * @return
	 */
	public abstract String generateTxnId(APIParams params);
}
