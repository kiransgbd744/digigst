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
public class Gstr6ComputeTimeStampResponseDto {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;
	
	@Expose
	@SerializedName("lastUpdatedDigiGstTimeStamp")
	private String lastUpdatedDigiGstTimeStamp;
	
	@Expose
	@SerializedName("digiGstStatus")
	private String digiGstStatus;
	
	@Expose
	@SerializedName("lastUpdatedGstnTimeStamp")
	private String lastUpdatedGstnTimeStamp;
	
	@Expose
	@SerializedName("gstnStatus")
	private String gstnStatus;
	
	@Expose
	@SerializedName("lastUpdatedCreditTimeStamp")
	private String lastUpdatedCreditTimeStamp;
	
	@Expose
	@SerializedName("creditStatus")
	private String creditStatus;
}
