package com.ey.advisory.app.docs.dto.erp;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class Get2BRevIntReqDto {

	@Expose
	private String gstin;

	@Expose
	private String retPeriod;

	@Expose
	private Long requestId;

	@Expose
	private Long entityId;

	@Expose
	private Long scenarioId;

	@Expose
	private String scenarioName;

	@Expose
	private String groupCode;

	@Expose
	protected String destinationName;

	@Expose
	protected Long erpId;
	
	@Expose
	private String sourceId;
	
	@Expose
	protected Long jobId;
	
	@Expose
	protected Long invocationId;
}
