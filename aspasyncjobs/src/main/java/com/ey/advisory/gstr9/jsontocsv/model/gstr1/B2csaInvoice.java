package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class B2csaInvoice {

	@Expose
	@SerializedName("omon")
	private String orgMonthOfInvoice;

	@Expose
	@SerializedName("sply_ty")
	private String supplyType;

	@Expose
	@SerializedName("typ")
	private String type;

	@Expose
	@SerializedName("opos")
	private String orgPos;

	@Expose
	@SerializedName("pos")
	private String pos;

	@Expose
	@SerializedName("etin")
	private String ecomTin;

	@Expose
	@SerializedName("flag")
	private String invoicestatus;

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("diff_percent")
	private String diffPercent;

	@Expose
	@SerializedName("itms")
	List<B2csaLineItemDetail> items;

	public String getSupplyType() {
		return supplyType;
	}

	public String getType() {
		return type;
	}

	public String getPos() {
		return pos;
	}

	public String getEcomTin() {
		return ecomTin;
	}

	public String getOrgMonthOfInvoice() {
		return orgMonthOfInvoice;
	}

	public String getOrgPos() {
		return orgPos;
	}

	public List<B2csaLineItemDetail> getItems() {
		return items;
	}

	public String getInvoicestatus() {
		return invoicestatus;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public String getDiffPercent() {
		return diffPercent;
	}

	@Override
	public String toString() {
		return "B2csaInvoice [orgMonthOfInvoice=" + orgMonthOfInvoice
				+ ", supplyType=" + supplyType + ", type=" + type + ", orgPos="
				+ orgPos + ", pos=" + pos + ", ecomTin=" + ecomTin + ", items="
				+ items + "]";
	}

}
