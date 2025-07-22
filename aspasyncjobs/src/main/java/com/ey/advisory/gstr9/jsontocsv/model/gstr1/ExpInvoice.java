package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExpInvoice {
	
	@Expose
	@SerializedName("exp_typ")
	private String expType;
	
	@Expose
	@SerializedName("inv")
	private List<ExpType> expInvoice;

	public String getExpType() {
		return expType;
	}

	public List<ExpType> getExpInvoice() {
		return expInvoice;
	}

	@Override
	public String toString() {
		return "ExpInvoice [expType=" + expType + ", expInvoice=" + expInvoice
				+ "]";
	}
	
	

}
