package com.ey.advisory.app.inward.einvoice;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author vishal.verma
 *
 */

@Data
public class InwardEinvoiceGstinLevelReqDto {

	@Expose
	@SerializedName("entityId")
	public Long entityId;

	@Expose
	@SerializedName("taxPeriod")
	public String taxPeriod;

	@Expose
	@SerializedName("gstins")
	public String gstin;
	
	@Expose
	@SerializedName("irnStatus")
	public List<String> irnStatus = new ArrayList<>();

}
