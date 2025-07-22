package com.ey.advisory.app.docs.dto.erp;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Hemasundar.J
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class Get2AConsolidatedRevIntgDto {
	
	@Expose
	private String gstin;
	
	@Expose
	private Long entityId;
	
	@Expose
	private Long scenarioId;
	
	@Expose
	private String groupcode;

	@Expose
	protected String destinationName;
	
	@Expose
	protected Long erpId;
	
	@Expose
	private int retryCount;
	
	@Expose
	private Long batchId;
	
	@Expose
	private Long jobId;
}
