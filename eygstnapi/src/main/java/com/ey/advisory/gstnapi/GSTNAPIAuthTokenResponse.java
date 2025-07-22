package com.ey.advisory.gstnapi;

import java.time.LocalDateTime;

import com.ey.advisory.core.api.APIResponse;

/**
 * This is a GSTN specific Response class. After the Auth Token request response
 * is processed, an instance of this class is returned. This class holds all
 * the authentication related information like the Session encryption Key (sek),
 * the Session Key (sk) and the Auth Token.
 * 
 * @author Sai.Pakanati
 *
 */
public class GSTNAPIAuthTokenResponse extends APIResponse {
	
	/**
	 * The encrypted session key returned in the response by GSTN.
	 */
	protected String sek;
	
	/**
	 * The decrypted session key. We'll have to store this in the DB.
	 */
	protected String sk;
	
	/**
	 * The decrypted auth token. This has to be saved in the DB and further
	 * passed to all requests in the header. 
	 */
	protected String authToken;

	/**
	 * The expiry time in minutes since this instance was created.
	 */
	protected int expiry;
	
	
	protected LocalDateTime expiryTime;
	
	protected LocalDateTime creationTime;
	
	public GSTNAPIAuthTokenResponse(String sek, String sk, 
					String authToken, int expiryInMin) {
		super();
		this.sek = sek;
		this.sk = sk;
		this.authToken = authToken;
		this.expiry = expiryInMin;
		this.creationTime = LocalDateTime.now();
		this.expiryTime = this.creationTime.plusMinutes(expiry);
	}

	public String getSek() {
		return sek;
	}

	public String getSk() {
		return sk;
	}

	public String getAuthToken() {
		return authToken;
	}

	/**
	 * The expiry of the auth token in minutes, from the time the token 
	 * was created.
	 * 
	 * @return
	 */
	public int getExpiry() {
		return expiry;
	}

	public LocalDateTime getExpiryTime() {
		return expiryTime;
	}
	
	public LocalDateTime getCreationTime() {
		return creationTime;
	}

	@Override
	public String toString() {
		return "GSTNAPIAuthTokenResponse [sek=" + sek + ", sk=" + sk
				+ ", authToken=" + authToken + ", expiry=" + expiry
				+ ", expiryTime=" + expiryTime + ", creationTime="
				+ creationTime + "]";
	}

}
