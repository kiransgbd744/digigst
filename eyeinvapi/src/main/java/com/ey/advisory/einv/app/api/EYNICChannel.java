package com.ey.advisory.einv.app.api;

import java.io.Serializable;

public class EYNICChannel implements APIExecChannel, Serializable {
	
	private static final long serialVersionUID = 1L;

	protected String clientId;
	
	protected String clientSecret;
	
	protected String ewbUserId;
	
	public EYNICChannel() {}
	
	public EYNICChannel(String clientId, String clientSecret, String ewbUserId) {
		super();
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.ewbUserId = ewbUserId;
	}


	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getEwbUserId() {
		return ewbUserId;
	}

	public void setEwbUserId(String ewbUserId) {
		this.ewbUserId = ewbUserId;
	}

	@Override
	public String toString() {
		return "EYNICChannel [clientId=" + clientId + ", clientSecret=" 
				+ clientSecret + ", ewbUserId=" + ewbUserId+ "]";
	}

	
}
