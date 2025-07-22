package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Data
public class GetApprovalStatusRespDto {

	@Expose
	private Integer status;
	
	@Expose
	private String approvedBy;
	
	@Expose
	private String approvedOn;
	
	@Expose
	private String initiatedBy;
	
	@Expose
	private String initiatedOn;
	
}