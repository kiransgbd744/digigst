package com.ey.advisory.core.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InitiateGetCallDto {
	
	@Expose
	@SerializedName("gstin")
	String gstin;
	
	@Expose
	@SerializedName("taxPeriod")
	List<String> taxPeriodList = new ArrayList<>();

}
