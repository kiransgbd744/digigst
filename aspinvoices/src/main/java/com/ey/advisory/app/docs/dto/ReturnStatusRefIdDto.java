package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class ReturnStatusRefIdDto {

	@Expose
	private Long gstnBatchId;

	@Expose
	private String refId;

	@Expose
	private String status;
	
	@Expose
	private String returnType;
	
	@Expose
	private String section;
	
}
