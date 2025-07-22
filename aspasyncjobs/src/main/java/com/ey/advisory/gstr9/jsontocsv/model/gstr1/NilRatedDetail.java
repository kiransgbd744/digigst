package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NilRatedDetail {

	@Expose
	@SerializedName("sply_ty")
	private String supplyType;

	@Expose
	@SerializedName("expt_amt")
	private String totalExemptedAmount;

	@Expose
	@SerializedName("nil_amt")
	private String nilRatedAmount;

	@Expose
	@SerializedName("ngsup_amt")
	private String totalNonGstAmount;

	public String getSupplyType() {
		return supplyType;
	}

	public String getTotalExemptedAmount() {
		return totalExemptedAmount;
	}

	public String getNilRatedAmount() {
		return nilRatedAmount;
	}

	public String getTotalNonGstAmount() {
		return totalNonGstAmount;
	}

	@Override
	public String toString() {
		return "NilRatedDetail [supplyType=" + supplyType
				+ ", totalExemptedAmount=" + totalExemptedAmount
				+ ", nilRatedAmount=" + nilRatedAmount + ", totalNonGstAmount="
				+ totalNonGstAmount + "]";
	}

}
