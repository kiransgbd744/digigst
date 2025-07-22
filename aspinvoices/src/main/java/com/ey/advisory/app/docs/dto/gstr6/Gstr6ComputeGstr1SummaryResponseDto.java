package com.ey.advisory.app.docs.dto.gstr6;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author SriBhavya
 *
 */
@Setter
@Getter
@ToString
public class Gstr6ComputeGstr1SummaryResponseDto {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;
	
	@Expose
	@SerializedName("status")
	private String status;
	
	@Expose
	@SerializedName("timeStamp")
	private String timeStamp;
}
