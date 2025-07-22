package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class B2bLineItem {

	@Expose
	@SerializedName("num")
	private int lineNumber;

	@Expose
	@SerializedName("itm_det")
	private B2bLineItemDetail itemDetail;

	public int getLineNumber() {
		return lineNumber;
	}

	public B2bLineItemDetail getItemDetail() {
		return itemDetail;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public void setItemDetail(B2bLineItemDetail itemDetail) {
		this.itemDetail = itemDetail;
	}

	@Override
	public String toString() {
		return "B2bLineItem [lineNumber=" + lineNumber + ", itemDetail="
				+ itemDetail + "]";
	}

//	public B2bItcDetails getItcDetail() {
//		return itcDetail;
//	}
//
//	public void setItcDetail(B2bItcDetails itcDetail) {
//		this.itcDetail = itcDetail;
//	}

	
}
