package com.ey.advisory.core.dto;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr2aProcessedDataRecordsReqDto {

	@Expose
	@SerializedName("entity")
	private String entity;

	@Expose
	@SerializedName("financialYear")
	private String financialYear;
	
	@Expose
	@SerializedName("returnType")
	private String returnType;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

}
