package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Data
public class SetApprovalStatusReqDto {
	
	
	@Expose
	@SerializedName("id")
	private Long id;
	
	@Expose
	@SerializedName("groupId")
	private Long groupId;
	
	@Expose
	@SerializedName("entityId")
	private Long entityId;
	
	@Expose
	@SerializedName("retPeriod")
	private String returnPeriod;
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("docType")
	private String docType;
	
	@Expose
	@SerializedName("status")
	private Integer approvalStatus;
	
	@Expose
	@SerializedName("user")
	private String approvedUser;
	
	@Expose
	@SerializedName("date")
	private String approvedOn;	

}