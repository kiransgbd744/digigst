package com.ey.advisory.app.docs.dto;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Anx2ReconResponseReqDTO {
	
	@Expose
	@SerializedName("entityId")
	private String entityId;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("gstin")
	private List<String> gstin;

	@Expose
	@SerializedName("tableType")
	private List<String> tableType;
	
	@Expose
	@SerializedName("docType")
	private List<String> docType;
	
	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;


}
