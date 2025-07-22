package com.ey.advisory.ewb.app.api;

import java.io.Serializable;

public class NICAPIProvider implements APIProvider, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected String publicKey;
	
	public NICAPIProvider() {}
	
	
	public NICAPIProvider(String publicKey) {
		super();
		this.publicKey = publicKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	@Override
	public String toString() {
		return "NICAPIProvider [publicKey=" + publicKey + "]";
	}

}
