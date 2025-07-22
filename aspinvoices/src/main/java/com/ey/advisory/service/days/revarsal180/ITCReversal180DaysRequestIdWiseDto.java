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
public class ITCReversal180DaysRequestIdWiseDto {

	private Long computeId;

	private List<GstinDto> gstin;

	private String fromDate;

	private String toDate;

	private String createdBy;

	private String InitiatedOn;

	private String criteria;

	private String dwnPath;

	private String status;
	
	private Integer gstinCount;

}
