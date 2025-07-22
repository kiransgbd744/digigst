package com.ey.advisory.app.ims.handlers;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Ravindra V S
 *
 */
@Data
public class ImsRevIntgReqDto {

	@Expose
	private String gstin;
	
	@Expose
	private String taxPeriod;

	@Expose
	private Long scenarioId;
	
	@Expose
	private String scenarioName;

	@Expose
	private String destinationName;

	@Expose
	private Long erpId;

	@Expose
	private String groupCode;
	
	@Expose
	private String destinationType;

	@Expose
    private Long batchId;
	
	@Expose
	private String payloadId;
}
