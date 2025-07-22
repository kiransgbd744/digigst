package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class B2bLineItem {
	
	@Expose @SerializedName("num")
	private String lineNumber;
	
	@Expose @SerializedName("itm_det")
	private B2bLineItemDetail itemDetail;

	public String getLineNumber() {
		return lineNumber;
	}

	public B2bLineItemDetail getItemDetail() {
		return itemDetail;
	}
	
	/*@Override
	public String toString() {
		return lineNumber + "," + itemDetail.getRate() + ","
				+ itemDetail.getTaxableValue() + ","
				+ itemDetail.getIgstAmount() + "," + itemDetail.getCgstAmount()
				+ "," + itemDetail.getSgstAmount() + ","
				+ itemDetail.getCessAmount();
	}*/

	@Override
	public String toString() {
		return "B2bIineItem [lineNumber=" + lineNumber + ", itemDetail="
				+ itemDetail + "]";
	}

}
