package com.ey.advisory.bcadmin.common.dto;

public class NICCredentialDto {

	private String gstin;
	private String einvUserName;
	private String einvPassword;
	private String ewbUserName;
	private String ewbPassword;
	private String einvClientId;
	private String einvClientSecret;
	public String getGstin() {
		return gstin;
	}
	public void setGstin(String gstin) {
		this.gstin = gstin;
	}
	public String getEinvUserName() {
		return einvUserName;
	}
	public void setEinvUserName(String einvUserName) {
		this.einvUserName = einvUserName;
	}
	public String getEinvPassword() {
		return einvPassword;
	}
	public void setEinvPassword(String einvPassword) {
		this.einvPassword = einvPassword;
	}
	public String getEwbUserName() {
		return ewbUserName;
	}
	public void setEwbUserName(String ewbUserName) {
		this.ewbUserName = ewbUserName;
	}
	public String getEwbPassword() {
		return ewbPassword;
	}
	public void setEwbPassword(String ewbPassword) {
		this.ewbPassword = ewbPassword;
	}
	public String getEinvClientId() {
		return einvClientId;
	}
	public void setEinvClientId(String einvClientId) {
		this.einvClientId = einvClientId;
	}
	public String getEinvClientSecret() {
		return einvClientSecret;
	}
	public void setEinvClientSecret(String einvClientSecret) {
		this.einvClientSecret = einvClientSecret;
	}
	@Override
	public String toString() {
		return "NICCredentialDto [gstin=" + gstin + ", einvUserName="
				+ einvUserName + ", einvPassword=" + einvPassword
				+ ", ewbUserName=" + ewbUserName + ", ewbPassword="
				+ ewbPassword + ", einvClientId=" + einvClientId
				+ ", einvClientSecret=" + einvClientSecret + "]";
	}
	
	
}
