package com.ey.advisory.app.gstr2b;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class ITCAvailableSummary {
	
	@Expose
	@SerializedName("nonrevsup")
	private ReverseAndNonReverseChargeSupplies nonRevSup;
	
	@Expose
	@SerializedName("revsup")
	private ReverseAndNonReverseChargeSupplies revSup;
	
	@Expose
	@SerializedName("isdsup")
	private ISDSupplies isdSup;
	
	@Expose
	@SerializedName("imports")
	private Imports imports;
	
	@Expose
	@SerializedName("othersup")
	private OtherSupplies otherSup;
	
	

}
