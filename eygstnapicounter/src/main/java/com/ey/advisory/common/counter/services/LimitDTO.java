package com.ey.advisory.common.counter.services;

import lombok.Data;

@Data
public class LimitDTO {

	private Integer fromDate;
	
	private Integer toDate;
	
	private Integer limit;
	
	private String fy;

}
