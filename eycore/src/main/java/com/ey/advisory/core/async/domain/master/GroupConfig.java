package com.ey.advisory.core.async.domain.master;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Code copied from the existing ASP service module.
 * 
 * @author Sai.Pakanati
 *
 */
@Entity
@Table(name = "EY_GROUP_CONFIG")
public class GroupConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long groupConfigId;
	
	@Column(name = "GROUP_CODE",nullable=false)
	private String groupCode;
	
	@Column(name = "CONFIG_CODE")
	private String configCode;
	
	@Column(name = "CONFIG_VALUE")
	private String configValue;
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
	
	@Column(name = "GROUP_ID")
	private Long groupId;
	
	
	public Long getGroupConfigId() {
		return groupConfigId;
	}
	public void setGroupConfigId(Long groupConfigId) {
		this.groupConfigId = groupConfigId;
	}
	
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	
	public String getConfigCode() {
		return configCode;
	}
	public void setConfigCode(String configCode) {
		this.configCode = configCode;
	}
	
	public String getConfigValue() {
		return configValue;
	}
	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}
	
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	@Override
	public String toString() {
		return "GroupConfig [groupConfigId=" + groupConfigId + ", groupCode=" 
				+ groupCode + ", configCode=" + configCode
				+ ", configValue=" + configValue + ", isActive=" + isActive 
				+ ", groupId=" + groupId + "]";
	}
	
}
