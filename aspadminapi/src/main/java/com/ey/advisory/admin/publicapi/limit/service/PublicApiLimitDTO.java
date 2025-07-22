package com.ey.advisory.admin.publicapi.limit.service;

import java.util.List;

import lombok.Data;

/**
 * 
 * 
 * @author Jithendra.B
 *
 */
@Data
public class PublicApiLimitDTO {

	private String groupCode;

	private List<LimitDTO> limits;
}
