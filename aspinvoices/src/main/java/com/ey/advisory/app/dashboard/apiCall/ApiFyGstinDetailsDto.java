package com.ey.advisory.app.dashboard.apiCall;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
public class ApiFyGstinDetailsDto {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("authStatus")
	private String authStatus;
	
	@Expose
	@SerializedName("fy")
	private String fy;
	
	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("status")
	private String apiStatus;
	
	@Expose
	@SerializedName("initiatedOn")
	private LocalDateTime initiatedOn;
	
	@Expose
	@SerializedName("registrationType")
	private String registrationType;
	
	@Expose
	@SerializedName("getCallDate")
	private LocalDate getCallDate;

	@Expose
	@SerializedName("getCallTime")
	private String getCallTime;

}