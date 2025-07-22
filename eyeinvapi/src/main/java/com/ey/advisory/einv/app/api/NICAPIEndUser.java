package com.ey.advisory.einv.app.api;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class NICAPIEndUser implements APIEndUser, Serializable {

	private static final long serialVersionUID = 1L;

	protected String gstin;

	protected String nicUserName;

	protected String nicPassword;

	protected String clientId;

	protected String clientSecret;

	public NICAPIEndUser() {
	}

	public NICAPIEndUser(String gstin, String nicUserName, String nicPassword,
			String clientId, String clientSecret) {
		super();
		this.gstin = gstin;
		this.nicUserName = nicUserName;
		this.nicPassword = nicPassword;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

}
