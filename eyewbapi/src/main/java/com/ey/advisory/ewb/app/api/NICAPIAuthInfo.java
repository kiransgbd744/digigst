package com.ey.advisory.ewb.app.api;

import java.io.Serializable;
import java.util.Date;

public class NICAPIAuthInfo implements APIAuthInfo, Serializable {

	private static final long serialVersionUID = 1L;

	protected String appKey;
	
	protected String sessionEncKey;
	
	protected String sessionKey;
	
	protected String authToken;
	
	protected Date authTokenTime;
	
	protected Date authTokenExpiryTime;
	
	/**
	 * Auth token given by NIC expires in 6 hrs = 360 Minutes. Since the exact
	 * expiry time is not returned by NIC API, we keep a buffer of 5 mins. So
	 * we assume that the auth token expiry time is 5 hrs and 55 mins 
	 * (or 355 mins).
	 * 
	 */
	public static final long AUTHTOKEN_EXPIRY_IN_MILLIS = 355L * 60L * 1000L;

	public NICAPIAuthInfo(String appKey, String sessionEncKey,
			String sessionKey, String authToken) {
		super();
		this.appKey = appKey;
		this.sessionEncKey = sessionEncKey;
		this.sessionKey = sessionKey;
		this.authToken = authToken;
		
		// Set the auth token time to the current time.
		this.authTokenTime = new Date();
		
		// Calculate the expiry time of the auth token.
		long expTimeInMillis = 
				authTokenTime.getTime() + AUTHTOKEN_EXPIRY_IN_MILLIS;
		this.authTokenExpiryTime = new Date(expTimeInMillis);

	}

	public String getAppKey() {
		return appKey;
	}

	public String getSessionEncKey() {
		return sessionEncKey;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public String getAuthToken() {
		return authToken;
	}
	
	public Date getAuthTokenTime() {
		return authTokenTime;
	}

	public Date getAuthTokenExpiryTime() {
		return authTokenExpiryTime;
	}

	/**
	 * Check if the calculated AuthToken expirty time is greater than the 
	 * current time.
	 * 
	 * @return
	 */
	public boolean isActive() {
		return authTokenExpiryTime.getTime() > new Date().getTime();
	}
	
	@Override
	public String toString() {
		return "NICAPIAuthInfo [appKey=" + appKey + ", sessionEncKey=" 
				+ sessionEncKey + ", sessionKey=" + sessionKey
				+ ", authToken=" + authToken + "]";
	}
	
	
	
}
