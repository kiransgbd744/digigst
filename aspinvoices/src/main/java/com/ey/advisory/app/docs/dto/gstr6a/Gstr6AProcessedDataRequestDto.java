package com.ey.advisory.app.docs.dto.gstr6a;

import java.util.List;
import java.util.Map;

import com.beust.jcommander.internal.Lists;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Gstr6AProcessedDataRequestDto {

	@Expose
	@SerializedName(value = "entity", alternate = "entityId")
	private List<Long> entityId;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;
	
	@Expose
	@SerializedName("month")
	private List<String> month = Lists.newArrayList();

	@Expose
	@SerializedName("tableType")
	private List<String> tableType;

	@Expose
	@SerializedName("docType")
	private List<String> docType;

	@Expose
	@SerializedName("data")
	private List<String> data;

	@Expose
	@SerializedName("GSTIN")
	private List<String> gstin;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

	@Expose
	@SerializedName("type")
	private String type;
	
	@Expose
	@SerializedName("fy")
	private String fy;
	
	@Expose
	@SerializedName("fromPeriod")
	private String fromPeriod;

	@Expose
	@SerializedName("toPeriod")
	private String toPeriod;

}
