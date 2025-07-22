package com.ey.advisory.gstr9.jsontocsv.model.gstr2A;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CdnLineItemGstr2A {

	@SerializedName("num")
	@Expose
	private String lineNumber;

	@SerializedName("itm_det")
	@Expose
	private CdnLineItemDetailGstr2A itemDetail;

	public String getLineNumber() {
		return lineNumber;
	}

	public CdnLineItemDetailGstr2A getItemDetail() {
		return itemDetail;
	}

	@Override
	public String toString() {
		return "CdnLineItemGstr2A [lineNumber=" + lineNumber + ", itemDetail="
				+ itemDetail + "]";
	}

}
