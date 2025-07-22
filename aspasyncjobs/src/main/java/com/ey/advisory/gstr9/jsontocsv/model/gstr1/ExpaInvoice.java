package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExpaInvoice {

	@Expose
	@SerializedName("exp_typ")
	private String expType;

	@Expose
	@SerializedName("inv")
	private List<ExpaType> expaInvoice;

	public String getExpType() {
		return expType;
	}

	public List<ExpaType> getExpInvoice() {
		return expaInvoice;
	}

	@Override
	public String toString() {
		return "ExpInvoice [expType=" + expType + ", expInvoice=" + expaInvoice
				+ "]";
	}

}
