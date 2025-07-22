package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HsnHeader {

	@Expose
	@SerializedName("flag")
	private String invoiceStatus;
	
	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("data")
	private List<HsnDetail> hsnDetails;
	
	@Expose
	@SerializedName("hsn_b2b")
	private List<HsnDetail> hsnB2bDetails;
	
	@Expose
	@SerializedName("hsn_b2c")
	private List<HsnDetail> hsnB2cDetails;

	public String getInvoiceStatus() {
		return invoiceStatus;
	}

	public List<HsnDetail> getHsnDetails() {
		return hsnDetails;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public List<HsnDetail> getHsnB2bDetails() {
		return hsnB2bDetails;
	}

	public List<HsnDetail> getHsnB2cDetails() {
		return hsnB2cDetails;
	}

	@Override
	public String toString() {
		return "HsnHeader [invoiceStatus=" + invoiceStatus + ", hsnDetails="
				+ hsnDetails + "]";
	}

}
