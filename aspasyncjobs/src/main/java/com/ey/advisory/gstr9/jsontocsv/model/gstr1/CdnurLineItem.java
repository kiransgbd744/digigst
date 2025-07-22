package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CdnurLineItem {

	@Expose @SerializedName("num")
	private String lineNumber;

	@Expose @SerializedName("itm_det")
	private CdnurLineItemDetail itemDetail;

	public String getLineNumber() {
		return lineNumber;
	}

	public CdnurLineItemDetail getItemDetail() {
		return itemDetail;
	}

	@Override
	public String toString() {
		return "B2bIineItem [lineNumber=" + lineNumber + ", itemDetail="
				+ itemDetail + "]";
	}

}
