package com.ey.advisory.app.inward.einvoice;

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
public class InwardEinvoiceEntitySummaryReqDto {

	@Expose
	@SerializedName("entityId")
	public Long entityId;

	@Expose
	@SerializedName("taxPeriod")
	public String taxPeriod;

	@Expose
	@SerializedName("supplyType")
	public List<String> supplyType;

	@Expose
	@SerializedName("gstins")
	public List<String> gstins;

	@Expose
	@SerializedName("type")
	public String type;

}
