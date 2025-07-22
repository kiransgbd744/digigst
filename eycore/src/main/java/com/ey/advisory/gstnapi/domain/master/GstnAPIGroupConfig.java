package com.ey.advisory.gstnapi.domain.master;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "GSTN_API_GROUP_CONFIG")
public class GstnAPIGroupConfig {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")	
	private Long id;
	
	@Column(name = "GROUP_ID")	
	private Long groupId;
	
	@Column(name = "GROUP_CODE")		
	private String groupCode;
	
	@Column(name = "API_KEY")	
	private String apiKey;
	
	@Column(name = "API_SECRET")	
	private String apiSecret;
	
	@Column(name = "GSP_TOKEN")	
	private String gspToken;
	
	@Column(name = "GSP_TOKEN_GEN_TIME")	
	private Date gspTokenGenTime;
	
	@Column(name = "GSP_TOKEN_EXP_TIME")	
	private Date gspTokenExpiryTime;

	@Column(name = "CREATE_DATE")
	private Date createdDate;

	@Column(name = "UPDATE_DATE")
	private Date updatedDate;
	
	@Column(name = "DIGIGST_USER_NAME")
	private String digiGstUserName;
	
	public GstnAPIGroupConfig() {}
			
	public GstnAPIGroupConfig(Long id, Long groupId, String groupCode,
			String apiKey, String apiSecret, String gspToken,
			Date gspTokenGenTime, Date gspTokenExpiryTime, Date createdDate,
			Date updatedDate, String digiGstUserName) {
		super();
		this.id = id;
		this.groupId = groupId;
		this.groupCode = groupCode;
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
		this.gspToken = gspToken;
		this.gspTokenGenTime = gspTokenGenTime;
		this.gspTokenExpiryTime = gspTokenExpiryTime;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.digiGstUserName = digiGstUserName;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getApiSecret() {
		return apiSecret;
	}

	public void setApiSecret(String apiSecret) {
		this.apiSecret = apiSecret;
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

	public String getDigiGstUserName() {
		return digiGstUserName;
	}

	public void setDigiGstUserName(String digiGstUserName) {
		this.digiGstUserName = digiGstUserName;
	}

	public String getGspToken() {
		return gspToken;
	}
	public void setGspToken(String gspToken) {
		this.gspToken = gspToken;
	}

	public Date getGspTokenGenTime() {
		return gspTokenGenTime;
	}

	public Date getGspTokenExpiryTime() {
		return gspTokenExpiryTime;
	}

	@Override
	public String toString() {
		return "GstnAPIGroupConfig [id=" + id + ", groupId=" + groupId
				+ ", groupCode=" + groupCode + ", apiKey=" + apiKey
				+ ", apiSecret=" + apiSecret + ", gspToken=" + gspToken
				+ ", gspTokenGenTime=" + gspTokenGenTime
				+ ", gspTokenExpiryTime=" + gspTokenExpiryTime
				+ ", createdDate=" + createdDate + ", updatedDate="
				+ updatedDate + ", digiGstUserName=" + digiGstUserName + "]";
	}


	
}
