package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Setter
@Getter
public class GetReviewSummaryReqDto{
	
	@Expose
	@SerializedName("retPeriod")
	private String returnPeriod;
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	

}