package com.ey.advisory.gstr9.jsontocsv.model.gstr2A;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class B2bLineItemGstr2A {
	
	@SerializedName("num")
	@Expose
	private String lineNumber;
	
	@SerializedName("itm_det")
	@Expose
	private B2bLineItemDetailGstr2A itemDetail;

	public String getLineNumber() {
		return lineNumber;
	}

	public B2bLineItemDetailGstr2A getItemDetail() {
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
