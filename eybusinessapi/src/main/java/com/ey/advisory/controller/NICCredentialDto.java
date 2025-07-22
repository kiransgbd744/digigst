package com.ey.advisory.controller;

import lombok.Data;
/**
 * 
 * @author vishal.verma
 *
 */

@Data
public class NICCredentialDto {

	private String gstin;
	private String einvUserName;
	private String einvPassword;
	private String ewbUserName;
	private String ewbPassword;
	private String einvClientId;
	private String einvClientSecret;
}
