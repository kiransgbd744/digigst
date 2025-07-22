package com.ey.advisory.gstnapi.domain.client;

import java.util.Date;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

/**
 * This class represents a user corresponding to a GSTIN for executing the GSTN
 * API. The actual GSTN API user is this user corresponding to GSTIN. EY
 * executes the API on behalf of this user. Every piece of information about
 * this API end user that's required by the GSTN API Provider and any other
 * servers through which the HTTP request is routed, will be enclosed within
 * this class. Currently the API invocation deals with the GSTN server and the
 * GSP server are present in this class. So the GSTIN end user information
 * required by both the servers can be encapsulated within this class.
 * 
 * @author Sai.Pakanati
 *
 */

@Entity
@Table(name = "GSTN_API_GSTIN_CONFIG")
public class GstnAPIGstinConfig {

	private static Map<String, Config> configMap = null;

	public GstnAPIGstinConfig() {
	}

	public GstnAPIGstinConfig(Long id, String gstinUserName, String groupCode,
			String gstin, String mobileNo, String emailId, String password,
			Date createdDate, Date updatedDate) {
		super();
		this.id = id;
		this.gstinUserName = gstinUserName;
		this.groupCode = groupCode;
		this.gstin = gstin;
		this.mobileNo = mobileNo;
		this.emailId = emailId;
		this.password = password;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
	}

	public static GstnAPIGstinConfig getPublicApiInstance() {
		ConfigManager configManager = StaticContextHolder
				.getBean("ConfigManagerImpl", ConfigManager.class);
		if (configMap == null) {
			configMap = configManager.getConfigs("GSTNAPI", "api");
		}
		String userName = configMap.containsKey(
				"api.gstn.global.public_gstin_username")
						? String.valueOf(
								configMap.get(
										"api.gstn.global.public_gstin_username")
										.getValue())
						: "";
		String password = configMap.containsKey(
				"api.gstn.global.public_gstin_password")
						? String.valueOf(
								configMap.get(
										"api.gstn.global.public_gstin_password")
										.getValue())
						: "";
		return new GstnAPIGstinConfig(0L, userName,
				APIConstants.DEFAULT_PUBLIC_API_GROUP,
				APIConstants.DEFAULT_PUBLIC_API_GSTIN, "", "", password, null,
				null);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTN_USER_NAME")
	private String gstinUserName;

	@Column(name = "GROUP_CODE")
	private String groupCode;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "MOBILE_NO")
	private String mobileNo;

	@Column(name = "EMAIL_ID")
	private String emailId;

	@Column(name = "PASSWORD")
	private String password;

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

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
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

	public String getGstinUserName() {
		return gstinUserName;
	}

	public void setGstinUserName(String gstinUserName) {
		this.gstinUserName = gstinUserName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "GstnAPIGstinConfig [id=" + id + ", gstinUserName="
				+ gstinUserName + ", groupCode=" + groupCode + ", gstin="
				+ gstin + ", mobileNo=" + mobileNo + ", emailId=" + emailId
				+ ", createdDate=" + createdDate + ", updatedDate="
				+ updatedDate + "]";
	}

}
