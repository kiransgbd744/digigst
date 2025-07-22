package com.ey.advisory.app.docs.dto;

import lombok.Data;

/**
 * 
 * 
 * @author Jithendra.B
 *
 */
@Data
public class LimitUsageDTO {

	private Integer usage;
	private Integer limit;
	private Integer remainingCnt;
	private String statusOn;

}
