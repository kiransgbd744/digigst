package com.ey.advisory.admin.azurebus.service;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */
@Data
public class ITPEventUserResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	private String groupCode;

	@Expose
	private String groupName;

	@Expose
	private String userId;

	@Expose
	private String userName;

	@Expose
	private String firstName;

	@Expose
	private String middleName;

	@Expose
	private String lastName;

	@Expose
	private String emailId;

}
