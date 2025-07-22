package com.ey.advisory.ewb.app.api;

import java.io.Serializable;
import java.util.Date;

public class GSTINAuthInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String gstin;
	protected String nicUserName;
	protected String nicPassword;
	protected String authToken;
	protected Date authTokenExpiryTime;
	protected String sessionKey;
	
	public GSTINAuthInfo(String gstin, String nicUserName, 
			String nicPassword, String authToken,
			Date authTokenExpiryTime, String sessionKey) {
		super();
		this.gstin = gstin;
		this.nicUserName = nicUserName;
		this.nicPassword = nicPassword;
		this.authToken = authToken;
		this.authTokenExpiryTime = authTokenExpiryTime;
		this.sessionKey = sessionKey;
	}
	
	public String getGstin() {
		return gstin;
	}
	public void setGstin(String gstin) {
		this.gstin = gstin;
	}
	public String getNicUserName() {
		return nicUserName;
	}
	public void setNicUserName(String nicUserName) {
		this.nicUserName = nicUserName;
	}
	public String getNicPassword() {
		return nicPassword;
	}
	public void setNicPassword(String nicPassword) {
		this.nicPassword = nicPassword;
	}
	public String getAuthToken() {
		return authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	public Date getAuthTokenExpiryTime() {
		return authTokenExpiryTime;
	}
	public void setAuthTokenExpiryTime(Date authTokenExpiryTime) {
		this.authTokenExpiryTime = authTokenExpiryTime;
	}
	public String getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	
	@Override
	public String toString() {
		return "GSTINAuthInfo [gstin=" + gstin + ", nicUserName=" + 
					nicUserName + ", nicPassword=" + nicPassword
				+ ", authToken=" + authToken + ", authTokenExpiryTime=" + 
					authTokenExpiryTime + ", sessionKey="
				+ sessionKey + "]";
	}
	
	
}
