package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TxpaInvoice extends TxpInvoice {
	
	@Expose
	@SerializedName("omon")
	private String originalMonth;

	public String getOriginalMonth() {
		return originalMonth;
	}

	@Override
	public String toString() {
		return "AtaInvoice [originalMonth=" + originalMonth + "]";
	}
	
	

}
