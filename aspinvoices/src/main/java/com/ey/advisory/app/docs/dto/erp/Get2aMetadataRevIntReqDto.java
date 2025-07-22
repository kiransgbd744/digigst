package com.ey.advisory.app.docs.dto.erp;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class Get2aMetadataRevIntReqDto {

	@Expose
	private String groupcode;
	
	@Expose 
	private Long batchId;
	
	@Expose
	private String scenarioName;
		
	@Expose
	private String sourceId;

	@Expose
	private Long jobId;
}
