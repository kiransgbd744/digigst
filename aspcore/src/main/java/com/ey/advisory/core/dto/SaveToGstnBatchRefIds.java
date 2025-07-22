package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Data
public class SaveToGstnBatchRefIds {

	@Expose
	@SerializedName("gstnBatchId")
	private Long gstnBatchId;

	@Expose
	@SerializedName("refId")
	private String refId;

	@Expose
	@SerializedName("txnId")
	private String txnId;
	
	@Expose
	@SerializedName("returnType")
	private String returnType;
	
}
