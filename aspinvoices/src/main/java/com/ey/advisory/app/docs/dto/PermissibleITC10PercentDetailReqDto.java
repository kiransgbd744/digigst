package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PermissibleITC10PercentDetailReqDto {
	
	@Expose
	@SerializedName("entityId")
	private Long entityId;
	
	@Expose
	@SerializedName("gstinList")
	private List<String> gstinList;
	
	@Expose
	@SerializedName("toTaxPeriod")
	private  String toTaxPeriod;
	
	@Expose
	@SerializedName("fromTaxPeriod")
	private  String fromTaxPeriod;
	
	@Expose
	@SerializedName("docType")
	private  List<String> docType;

	private String reconType;
}
