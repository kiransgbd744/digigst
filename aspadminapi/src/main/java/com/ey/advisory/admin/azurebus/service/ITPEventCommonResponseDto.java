package com.ey.advisory.admin.azurebus.service;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */
@Data
public class ITPEventCommonResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	private int groupId;

	@Expose
	private String groupCode;

	@Expose
	private String groupName;

	@Expose
	private String groupDomain;

	@Expose
	private String entityCode;

	@Expose
	private Long entityId;

	@Expose
	private String entityName;

	@Expose
	private String entityPan;

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
	
	@Expose
	private String pId;
	
	@Expose
	@SerializedName("entityAccess")
	private List<ITPEventEntityAccessResponseDto> entityAccess;

	@Expose
	@SerializedName("groupAccess")
	private ITPEventGroupAccessResponseDto groupAccess;

}
