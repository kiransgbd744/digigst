package com.ey.advisory.gstr2.userdetails;

import java.time.LocalDate;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Umesha.M
 *
 */
public class UserCreationReqDto {
	
	@Expose
	@SerializedName("id")
	private Long id;
	
	
	@Expose
	@SerializedName("items")
	List<UserCreationItemsDetailsDto> userCreationItemsDetailsDtos;
	
	@Expose
	@SerializedName("groupCode")
	private String groupCode;
	
	@Expose
	@SerializedName("entityId")
	private Long entityId;
	
	@Expose
	@SerializedName("userName")
	private String userName;
	
	@Expose
	@SerializedName("isFlag")
	private boolean isFlag;	
	@Expose
	@SerializedName("firstName")
	private String firstName;
	
	@Expose
	@SerializedName("lastName")
	private String lastName;
	
	@Expose
	@SerializedName("email")
	private String email;
	
    @Expose
	@SerializedName("mobile")
	private String mobile;
	
	@Expose
	@SerializedName("userRole")
	private String userRole;
	
	@Expose
	@SerializedName("status")
	private boolean status;
	
	@Expose
	@SerializedName("createdBy")
	private String createdBy;
	
	@Expose
	@SerializedName("createdOn")
	private LocalDate createdOn;
	
	@Expose
	@SerializedName("modifiedBy")
	private String modifiedBy;
	
	@Expose
	@SerializedName("modifiedOn")
	private LocalDate modifiedOn;
	
	@Expose
	@SerializedName("entityName")
	private String entityName;
	


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
	 * @return the userCreationItemsDetailsDtos
	 */
	public List<UserCreationItemsDetailsDto> getUserCreationItemsDetailsDtos() {
		return userCreationItemsDetailsDtos;
	}

	/**
	 * @param userCreationItemsDetailsDtos the userCreationItemsDetailsDtos to set
	 */
	public void setUserCreationItemsDetailsDtos(
			List<UserCreationItemsDetailsDto> userCreationItemsDetailsDtos) {
		this.userCreationItemsDetailsDtos = userCreationItemsDetailsDtos;
	}

	/**
	 * @return the groupCode
	 */
	public String getGroupCode() {
		return groupCode;
	}

	/**
	 * @param groupCode the groupCode to set
	 */
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
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
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * @return the userRole
	 */
	public String getUserRole() {
		return userRole;
	}

	/**
	 * @param userRole the userRole to set
	 */
	public void setUserRole(String userRole) {
		this.userRole = userRole;
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
	 * @return the createdOn
	 */
	public LocalDate getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn the createdOn to set
	 */
	public void setCreatedOn(LocalDate createdOn) {
		this.createdOn = createdOn;
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
	 * @return the modifiedOn
	 */
	public LocalDate getModifiedOn() {
		return modifiedOn;
	}

	/**
	 * @param modifiedOn the modifiedOn to set
	 */
	public void setModifiedOn(LocalDate modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	/**
	 * @return the entityName
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * @return the status
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isFlag() {
		return isFlag;
	}

	public void setFlag(boolean isFlag) {
		this.isFlag = isFlag;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserCreationReqDto [id=" + id
				+ ", userCreationItemsDetailsDtos="
				+ userCreationItemsDetailsDtos + ", groupCode=" + groupCode
				+ ", entityId=" + entityId + ", userName=" + userName
				+ ", firstName=" + firstName + ", lastName=" + lastName
				+ ", email=" + email + ", mobile=" + mobile + ", userRole="
				+ userRole + ", status=" + status + ", createdBy=" + createdBy
				+ ", createdOn=" + createdOn + ", modifiedBy=" + modifiedBy
				+ ", modifiedOn=" + modifiedOn + ", entityName=" + entityName
				+ "]";
	}
	
	
}
