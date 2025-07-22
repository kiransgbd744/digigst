package com.ey.advisory.gstr9.jsontocsv.model.gstr1;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NilRatedHeader {

	@Expose
	@SerializedName("flag")
	private String invoiceStatus;

	@Expose
	@SerializedName("chksum")
	private String checkSum;

	@Expose
	@SerializedName("inv")
	private List<NilRatedDetail> nilRatedDetails;

	public String getInvoiceStatus() {
		return invoiceStatus;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public List<NilRatedDetail> getNilRatedDetails() {
		return nilRatedDetails;
	}

	@Override
	public String toString() {
		return "NilRatedHeader [invoiceStatus=" + invoiceStatus
				+ ", nilRatedDetails=" + nilRatedDetails + "]";
	}

}
