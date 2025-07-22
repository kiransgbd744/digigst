/**
 * 
 */
package com.ey.advisory.service.days.revarsal180;

import java.util.List;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class ITCReversal180DaysComputeReqDto {
	
	private boolean isDocDate;
	
	private boolean isTaxPeriod;
	
	private List<String> gstins;
	
	private String toDate;
	
	private String fromDate;
	
	private Long entityId;
	
	private String toTaxPeriod;
	
	private String fromTaxPeriod;
	
	private String criteria;

}
