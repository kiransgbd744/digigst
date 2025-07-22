package com.ey.advisory.gstr9.jsontocsv.model.gstr3B;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InterSupDetailsInvoiceGstr3B {

	@SerializedName("unreg_details")
	@Expose
	private List<UnregCompUinDetailsGstr3B> unregDetails;

	@SerializedName("comp_details")
	@Expose
	private List<UnregCompUinDetailsGstr3B> compDetails;
	
	@SerializedName("uin_details")
	@Expose
	private List<UnregCompUinDetailsGstr3B> uinDetails;
	

	public List<UnregCompUinDetailsGstr3B> getUnregDetails() {
		return unregDetails;
	}

	public List<UnregCompUinDetailsGstr3B> getCompDetails() {
		return compDetails;
	}

	public List<UnregCompUinDetailsGstr3B> getUinDetails() {
		return uinDetails;
	}

	@Override
	public String toString() {
		return "InterSupDetailsInvoiceGstr3B [unregDetails=" + unregDetails + ", compDetails=" + compDetails + ","
				+ " uinDetails="+ uinDetails +"]";
	}
}
