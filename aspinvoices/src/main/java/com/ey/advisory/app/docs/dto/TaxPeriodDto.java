package com.ey.advisory.app.docs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaxPeriodDto {
	
	private String taxPeriod;
	private String order;
	private String key;
}
