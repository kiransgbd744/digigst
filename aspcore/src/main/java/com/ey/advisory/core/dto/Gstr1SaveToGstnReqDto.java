package com.ey.advisory.core.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Gstr1SaveToGstnReqDto {

	@Expose
	@SerializedName("dataRecvFrom")
	private LocalDate dataRecvFrom;

	@Expose
	@SerializedName("dataRecvTo")
	private LocalDate dataRecvTo;

	@Expose
	@SerializedName("docDateFrom")
	private LocalDate docDateFrom;

	@Expose
	@SerializedName("docDateTo")
	private LocalDate docDateTo;

	@Expose
	@SerializedName("retPeriodFrom")
	private String retPeriodFrom;

	@Expose
	@SerializedName("retPeriodTo")
	private String retPeriodTo;

	@Expose
	@SerializedName("entityId")
	private Long entityId;

	@Expose
	@SerializedName("dates")
	private List<LocalDate> dates = new ArrayList<>();

	@Expose
	@SerializedName("gstins") //should not be null
	private List<String> sgstins = new ArrayList<>();

	/*@Expose
	@SerializedName("retPeriods")
	private List<String> returnPeriods = new ArrayList<>();*/
	
	@Expose
	@SerializedName("retPeriod")
	private String returnPeriod;
	
	// Reset and Save
	@Expose
	private List<String> tableSections = new ArrayList<>();
	
	@Expose
	private Boolean isResetSave = false;
	
	// user input selection for NIL/HSN
	
	@Expose
	private Boolean isNilUserInput;
	
	@Expose
	private Boolean isHsnUserInput;
	
	@Expose
	private Boolean isCrossItcUserInput;

}
