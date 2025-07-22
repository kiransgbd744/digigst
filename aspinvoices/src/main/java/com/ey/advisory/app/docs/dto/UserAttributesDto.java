package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class UserAttributesDto {
	
	@Expose
	protected String userId;
	
	@Expose
	protected String firstName;
	
	@Expose
	protected String lastName;
	
	@Expose
	protected String phoneNo;
	
	@Expose
	protected String groupCode;
	
	@Expose
	protected String email;
	
	@Expose
	protected String groupName;
	
	@Expose
	protected String displayName;
	
	@Expose
	protected List<String> roles;
	
	@Expose
	protected UserPermissionDto groupPermissions;
	
	public UserAttributesDto(){}

	public UserAttributesDto(String firstName, String lastName, String phoneNo,
			String groupCode, String email, String groupName, String displayName,
			List<String> roles, String userId) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNo = phoneNo;
		this.groupCode = groupCode;
		this.email = email;
		this.groupName = groupName;
		this.displayName = displayName;
		this.roles = roles;
		this.userId = userId;
	}

/*	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "UserAttributesDto [userId=" + userId + ", firstName="
				+ firstName + ", lastName=" + lastName + ", phoneNo=" + phoneNo
				+ ", groupCode=" + groupCode + ", email=" + email
				+ ", groupName=" + groupName + ", displayName=" + displayName
				+ ", roles=" + roles + "]";
	}*/

	

	
	
}
