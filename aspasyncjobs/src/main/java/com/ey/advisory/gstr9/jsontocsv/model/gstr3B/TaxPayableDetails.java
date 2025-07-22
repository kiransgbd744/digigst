package com.ey.advisory.gstr9.jsontocsv.model.gstr3B;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaxPayableDetails {

	@SerializedName("trans_typ")
	@Expose
	private String transType;

	@SerializedName("trans_desc")
	@Expose
	private String transDescription;

	@SerializedName("liab_ldg_id")
	@Expose
	private String liabLedgerId;

	@SerializedName("sgst")
	@Expose
	private AmountTypeDetail sgst;

	@SerializedName("cgst")
	@Expose
	private AmountTypeDetail cgst;

	@SerializedName("cess")
	@Expose
	private AmountTypeDetail cess;

	@SerializedName("igst")
	@Expose
	private AmountTypeDetail igst;

	public String getTransType() {
		return transType;
	}

	public String getTransDescription() {
		return transDescription;
	}

	public String getLiabLedgerId() {
		return liabLedgerId;
	}

	public AmountTypeDetail getSgst() {
		return sgst;
	}

	public AmountTypeDetail getCgst() {
		return cgst;
	}

	public AmountTypeDetail getCess() {
		return cess;
	}

	public AmountTypeDetail getIgst() {
		return igst;
	}

	@Override
	public String toString() {
		return "TaxPayableDetails [transType=" + transType
				+ ", transDescription=" + transDescription + ", liabLedgerId="
				+ liabLedgerId + ",sgst="+sgst+",cgst="+cgst+",igst="+igst
				+ ",cess="+cess+"]";
	}
}
