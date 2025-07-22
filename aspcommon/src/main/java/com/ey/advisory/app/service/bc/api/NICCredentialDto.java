package com.ey.advisory.app.service.bc.api;

import com.google.gson.annotations.Expose;

/**
 * @author vishal.verma
 *
 */

import lombok.Data;

@Data
public class NICCredentialDto {

	@Expose
	private String gstin;
	
	@Expose
	private String activeIRP;

	@Expose
	private String state;

	@Expose
	private String authToken;
	
	@Expose
	private String regType;

	@Expose
	private String einvUserName;

	@Expose
	private String einvPassword;
	
	@Expose
	private String einvClientId;

	@Expose
	private String einvClientSecret;

	@Expose
	private String ewbUserName;

	@Expose
	private String ewbPassword;
	
	@Expose
	private String ewbClientId;

	@Expose
	private String ewbClientSecret;
	
	@Expose
	private String copyNIC;// E-Invoice to E-WayBill / E-WayBill to E-Invoice

	//IRP

	@Expose
	private String einvUserNameIRP;

	@Expose
	private String einvPasswordIRP;
	
	@Expose
	private String einvClientIdIRP;

	@Expose
	private String einvClientSecretIRP;

	@Expose
	private String ewbUserNameIRP;

	@Expose
	private String ewbPasswordIRP;
	
	@Expose
	private String ewbClientIdIRP;

	@Expose
	private String ewbClientSecretIRP;
	
}
