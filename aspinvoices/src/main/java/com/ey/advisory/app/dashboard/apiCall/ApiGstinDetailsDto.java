package com.ey.advisory.app.dashboard.apiCall;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApiGstinDetailsDto {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("taxPeriodDetails")
	private List<TaxPeriodDetailsDto> taxPeriodDetails;
	
	@Expose
	@SerializedName("authStatus")
	private String authStatus;
	
	@Expose
	@SerializedName("registrationType")
	private String registrationType;
	
	@Expose
	@SerializedName("stateName")
	private String stateName;
	
}