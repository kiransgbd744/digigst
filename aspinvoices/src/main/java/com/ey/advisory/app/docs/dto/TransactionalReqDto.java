/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class TransactionalReqDto {

	@Expose
	private List<String> gstin;

	@Expose
	private String taxPeriod;

	@Expose
	private String sourceId;

	@Expose
	private List<String> status;
	
	@Expose
	private Long entityId;
}
