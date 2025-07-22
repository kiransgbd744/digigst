package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class Gstr1SubmitGstnDto {

	@Expose
	private String gstin;

	@Expose
	private String ret_period;

	@Expose
	private String generate_summary;

	@Expose
	private Long batchId;
	
	@Expose
	private String isnil;
}
