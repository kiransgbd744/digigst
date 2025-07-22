/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * 
 * @author Umesha.M
 *
 */
@Data
public class Get2ARevIntReqDto {

	@Expose
	private String gstin;

	@Expose
	private String retPeriod;

	@Expose
	private Long batchId;

	@Expose
	private String section;

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
	private Long jobId;

}
