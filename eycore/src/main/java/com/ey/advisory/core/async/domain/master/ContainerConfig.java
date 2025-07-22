package com.ey.advisory.core.async.domain.master;

import java.io.Serializable;
import java.util.Date;

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
@Table(name = "tblContainerConfig")
public class ContainerConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ContainerConfigId")
	private Long containerConfigId;
	
	@Column(name = "GroupCode",nullable=false)
	private String groupCode;
	
	@Column(name = "StorageName")
	private String storageName;
	
	@Column(name = "StorageKey")
	private String storageKey;
	
	@Column(name = "IsActive")
	private Boolean isActive;
	
	@Column(name = "CreatedDate")
	private Date createdDate;

	public Long getContainerConfigId() {
		return containerConfigId;
	}

	public void setContainerConfigId(Long containerConfigId) {
		this.containerConfigId = containerConfigId;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getStorageName() {
		return storageName;
	}

	public void setStorageName(String storageName) {
		this.storageName = storageName;
	}

	public String getStorageKey() {
		return storageKey;
	}

	public void setStorageKey(String storageKey) {
		this.storageKey = storageKey;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public String toString() {
		return "ContainerConfig [containerConfigId=" + containerConfigId
				+ ", groupCode=" + groupCode + ", storageName=" + storageName
				+ ", storageKey=" + storageKey + ", isActive=" + isActive
				+ ", createdDate=" + createdDate + "]";
	}
	
	
	
}
