package com.ey.advisory.app.docs.dto.gstr1;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Data
public class WorkFlowReqDto {
	
	@Expose
	private Long entityId;
	
	@Expose
	private String gstin;

	@Expose
	private String returnPeriod;
	
	
	@Expose
	private String mackerId;
	
	@Expose
	private String workflowType;
	
	
	@Expose
	private String requestComments;
	
	@Expose
	private List<String> checkerIds;	

}
