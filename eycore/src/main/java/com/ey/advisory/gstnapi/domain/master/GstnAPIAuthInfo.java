package com.ey.advisory.gstnapi.domain.master;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.ey.advisory.core.api.impl.APIAuthInfo;

@Entity
@Table(name = "GSTN_API_AUTH_INFO")
public class GstnAPIAuthInfo implements APIAuthInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")		
	private Long id;
	
	@Column(name = "GROUP_CODE")	
	private String groupCode;
	
	@Column(name = "PROVIDER_NAME")	
	private String providerName;
	
	@Column(name = "GSTIN")	
	private String gstin;
	
	@Column(name = "APP_KEY")	
	private String appKey;
	
	@Column(name = "OTP")		
	private String otp;
	
	@Column(name = "SESS_KEY")	
	private String sessionKey;
	
	@Column(name = "GSTN_TOKEN")	
	private String gstnToken;
	
	@Column(name = "GSTN_TOKEN_GEN_TIME")	
	private Date gstnTokenGenTime;
	
 	@Column(name = "GSTN_TOKEN_EXP_TIME")	
	private Date gstnTokenExpiryTime;
	
	@Column(name = "CREATE_DATE")
	private Date createdDate;

	@Column(name = "UPDATE_DATE")
	private Date updatedDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public String getGstnToken() {
		return gstnToken;
	}

	public void setGstnToken(String gstnToken) {
		this.gstnToken = gstnToken;
	}

	public Date getGstnTokenGenTime() {
		return gstnTokenGenTime;
	}

	public void setGstnTokenGenTime(Date gstnTokenGenTime) {
		this.gstnTokenGenTime = gstnTokenGenTime;
	}

	public Date getGstnTokenExpiryTime() {
		return gstnTokenExpiryTime;
	}

	public void setGstnTokenExpiryTime(Date gstnTokenExpiryTime) {
		this.gstnTokenExpiryTime = gstnTokenExpiryTime;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public boolean isExpired() {
		if(this.getGstnTokenExpiryTime() == null)
			return true;
		Date curDate = new Date();
		return curDate.compareTo(this.getGstnTokenExpiryTime()) >= 0;
	}

	@Override
	public String toString() {
		return "GstnAPIAuthInfo [id=" + id + ", groupCode=" + groupCode
				+ ", providerName=" + providerName + ", gstin=" + gstin
				+ ", appKey=" + appKey + ", otp=" + otp + ", sessionKey="
				+ sessionKey + ", gstnToken=" + gstnToken
				+ ", gstnTokenGenTime=" + gstnTokenGenTime
				+ ", gstnTokenExpiryTime=" + gstnTokenExpiryTime
				+ ", createdDate=" + createdDate + ", updatedDate="
				+ updatedDate + "]";
	} 

	

	
}
