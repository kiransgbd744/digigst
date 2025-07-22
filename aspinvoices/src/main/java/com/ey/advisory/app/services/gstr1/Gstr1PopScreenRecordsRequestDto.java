package com.ey.advisory.app.services.gstr1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Gstr1PopScreenRecordsRequestDto {

	@Expose
	@SerializedName("entityId")
	private String entityId;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;
	
	@Expose
	@SerializedName("taxPeriodFrom")
	private String taxPeriodFrom;

	@Expose
	@SerializedName("taxPeriodTo")
	private String taxPeriodTo;

	@Expose
	@SerializedName("gstin")
	private List<String> gstins;

}
