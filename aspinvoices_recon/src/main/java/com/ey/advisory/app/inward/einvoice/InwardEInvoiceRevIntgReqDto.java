package com.ey.advisory.app.inward.einvoice;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class InwardEInvoiceRevIntgReqDto {

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
