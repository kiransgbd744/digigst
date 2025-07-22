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
public class ITCReversal180DaysReqDto {
	
	private boolean isDocDate;
	
	private List<String> gstins;
	
	private String toDate;
	
	private String fromDate;
	
	private Long entityId;
	
	private String criteria;
	
	//private String toDocDate;
	
	//private String fromDocDate;
	
	//private String toAccDate;
	
	//private String fromAccDate;
	
	private String toTaxPeriod;
	
	private String fromTaxPeriod;
	
	private List<String> initiationByUserId;
	
	private String reconStatus;
	
	private int returnPeriodFrom;

	private int returnPeriodTo;

}
