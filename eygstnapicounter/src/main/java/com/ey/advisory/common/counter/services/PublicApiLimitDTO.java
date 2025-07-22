package com.ey.advisory.common.counter.services;

import java.util.List;

import lombok.Data;

@Data
public class PublicApiLimitDTO {

	private String groupCode;
	
	private List<LimitDTO> limits;
}
