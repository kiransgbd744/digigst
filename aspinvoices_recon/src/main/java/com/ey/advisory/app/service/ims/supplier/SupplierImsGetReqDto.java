package com.ey.advisory.app.service.ims.supplier;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class SupplierImsGetReqDto {
	
	private Long batchId;

	private String gstin;

	private String taxPeriod;

	private String returnType;
	
	private String section;
	
}
