package com.ey.advisory.core.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
@Data
public class Gstr2AProcessedRecordsReqDto {

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId = new ArrayList<>();

	@Expose
	@SerializedName("taxPeriod")
	private String retunPeriod;

	@Expose
	@SerializedName("tableType")
	private List<String> tableType = new ArrayList<>();

	@Expose
	@SerializedName("docType")
	private List<String> docType = new ArrayList<>();

	@Expose
    @SerializedName("action")
    private String action;
	
	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;
	
	@Expose
	@SerializedName("fromPeriod")
	private String fromPeriod;

	@Expose
	@SerializedName("toPeriod")
	private String toPeriod;
	
	@Expose
	@SerializedName("isDigigst")
	private Boolean isDigigst;
	
	@Expose
	@SerializedName("isVerified")
	private String isVerified;
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("returnType")
	private String returnType;

	/**
	 * 
	 */
	public Gstr2AProcessedRecordsReqDto() {
		super();
	}
	
	
}
