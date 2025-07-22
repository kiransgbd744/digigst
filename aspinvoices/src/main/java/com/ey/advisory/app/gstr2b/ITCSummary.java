package com.ey.advisory.app.gstr2b;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class ITCSummary {
	
	@Expose
	@SerializedName("itcavl")
	private ITCAvailableSummary itcAvailableSummary;
	
	@Expose
	@SerializedName("itcunavl")
	private ITCUnAvailableSummary itcUnAvailableSummary;
	
	@Expose
	@SerializedName("itcRejected")
	private ITCRejectedSummary itcRejectedSummary;
	
}
