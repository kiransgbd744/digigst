package com.ey.advisory.ewb.app.api;

import java.io.Serializable;

public class NICAPIEndUser implements APIEndUser, Serializable {

	private static final long serialVersionUID = 1L;

	protected String gstin;
	
	protected String nicUserName;
	
	protected String nicPassword;
	
	public NICAPIEndUser() {}
	
	
	public NICAPIEndUser(String gstin, String nicUserName, String nicPassword) {
		super();
		this.gstin = gstin;
		this.nicUserName = nicUserName;
		this.nicPassword = nicPassword;
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

	@Override
	public String toString() {
		return "NICAPIEndUser [gstin=" + gstin + ", nicUserName=" 
					+ nicUserName + ", nicPassword=" + nicPassword + "]";
	}
	
	

}
