package com.ey.advisory.gstnapi;

import java.io.Serializable;

import com.ey.advisory.core.api.impl.APIExecChannel;

public class EYGSTNChannel implements APIExecChannel, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected String clientId;
	
	protected String clientSecret;
	
	protected String eyPublicIp;
	
	public EYGSTNChannel() {}
	
	public EYGSTNChannel(String clientId, String clientSecret,
			String eyPublicIp) {
		super();
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.eyPublicIp = eyPublicIp;
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

	public String getEYPublicIp() {
		return eyPublicIp;
	}

	@Override
	public String toString() {
		return "EYGSTNChannel [clientId=" + clientId + ", clientSecret="
				+ clientSecret + ", eyPublicIp=" + eyPublicIp + "]";
	}

	
}
