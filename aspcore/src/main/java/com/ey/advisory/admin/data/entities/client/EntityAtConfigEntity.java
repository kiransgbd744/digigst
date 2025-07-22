package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author Umesha.M
 *
 */
@Entity
@Table(name = "ENTITY_AT_CONFIG")
public class EntityAtConfigEntity {

	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	
	@Column(name = "GROUP_ID")
	private Long groupId; 
	
	@Column(name = "GROUP_CODE")
	private String groupcode;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "AT_CODE")
	private String atCode;
	
	@Column(name = "AT_NAME")
	private String atName;
	
	@Column(name = "AT_INWARD")
	private String atInward;
	
	@Column(name = "AT_OUTWARD")
	private String atOutward;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the groupId
	 */
	public Long getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the groupcode
	 */
	public String getGroupcode() {
		return groupcode;
	}

	/**
	 * @param groupcode the groupcode to set
	 */
	public void setGroupcode(String groupcode) {
		this.groupcode = groupcode;
	}

	/**
	 * @return the entityId
	 */
	public Long getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return the atCode
	 */
	public String getAtCode() {
		return atCode;
	}

	/**
	 * @param atCode the atCode to set
	 */
	public void setAtCode(String atCode) {
		this.atCode = atCode;
	}

	/**
	 * @return the atName
	 */
	public String getAtName() {
		return atName;
	}

	/**
	 * @param atName the atName to set
	 */
	public void setAtName(String atName) {
		this.atName = atName;
	}

	/**
	 * @return the atInward
	 */
	public String getAtInward() {
		return atInward;
	}

	/**
	 * @param atInward the atInward to set
	 */
	public void setAtInward(String atInward) {
		this.atInward = atInward;
	}

	/**
	 * @return the atOutward
	 */
	public String getAtOutward() {
		return atOutward;
	}

	/**
	 * @param atOutward the atOutward to set
	 */
	public void setAtOutward(String atOutward) {
		this.atOutward = atOutward;
	}

	/**
	 * @return the isDelete
	 */
	public boolean isDelete() {
		return isDelete;
	}

	/**
	 * @param isDelete the isDelete to set
	 */
	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the createdOn
	 */
	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn the createdOn to set
	 */
	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @return the modifiedOn
	 */
	public LocalDateTime getModifiedOn() {
		return modifiedOn;
	}

	/**
	 * @param modifiedOn the modifiedOn to set
	 */
	public void setModifiedOn(LocalDateTime modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

}
