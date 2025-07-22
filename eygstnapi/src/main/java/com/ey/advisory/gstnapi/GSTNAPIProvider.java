package com.ey.advisory.gstnapi;

import java.io.Serializable;
import java.security.PublicKey;

import com.ey.advisory.core.api.impl.APIProvider;

public class GSTNAPIProvider implements APIProvider, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected PublicKey gstnPubKey;
	
	public GSTNAPIProvider() {}
	
	
	public GSTNAPIProvider(PublicKey gstnPubKey) {
		super();
		this.gstnPubKey = gstnPubKey;
	}

	public PublicKey getGstnPubKey() {
		return gstnPubKey;
	}

	@Override
	public String toString() {
		return "GSTNAPIProvider [cert=" + gstnPubKey + "]";
	}

}
