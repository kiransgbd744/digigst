/**
 * 
 */
package com.ey.advisory.gstnapi.services;

/**
 * @author Khalid1.Khan
 *
 */
public interface GenerateAuthTokenService {
	
	public boolean generateAuthToken(String gstin, String otpCode);

}
