package com.ey.advisory.gstr9.jsontocsv.model.gstr3B;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaidItcDetails {

	@SerializedName("liab_ldg_id")
	@Expose
	private String liabledgerId;

	@SerializedName("trans_typ")
	@Expose
	private String transType;

	@SerializedName("i_pdi")
	@Expose
	private String IGSTPaidUsingIGST;

	@SerializedName("i_pdc")
	@Expose
	private String IGSTPaidUsingCGST;

	@SerializedName("i_pds")
	@Expose
	private String IGSTPaidUsingSGST;

	@SerializedName("c_pdi")
	@Expose
	private String CGSTPaidUsingIGST;

	@SerializedName("c_pdc")
	@Expose
	private String CGSTPaidUsingCGST;

	@SerializedName("s_pdi")
	@Expose
	private String SGSTPaidUsingIGST;

	@SerializedName("s_pds")
	@Expose
	private String SGSTPaidUsingSGST;

	@SerializedName("cs_pdcs")
	@Expose
	private String CessPaidUsingCess;

	public String getLiabledgerId() {
		return liabledgerId;
	}

	public String getTransType() {
		return transType;
	}

	public String getIGSTPaidUsingIGST() {
		return IGSTPaidUsingIGST;
	}

	public String getIGSTPaidUsingCGST() {
		return IGSTPaidUsingCGST;
	}

	public String getIGSTPaidUsingSGST() {
		return IGSTPaidUsingSGST;
	}

	public String getCGSTPaidUsingIGST() {
		return CGSTPaidUsingIGST;
	}

	public String getCGSTPaidUsingCGST() {
		return CGSTPaidUsingCGST;
	}

	public String getSGSTPaidUsingIGST() {
		return SGSTPaidUsingIGST;
	}

	public String getSGSTPaidUsingSGST() {
		return SGSTPaidUsingSGST;
	}

	public String getCessPaidUsingCess() {
		return CessPaidUsingCess;
	}


	@Override
	public String toString() {
		return "TaxPayableDetails [liabledgerId=" + liabledgerId
				+ ", transType=" + transType + ", IGSTPaidUsingIGST=" + IGSTPaidUsingIGST
				+ ",IGSTPaidUsingCGST=" + IGSTPaidUsingCGST + ",IGSTPaidUsingSGST=" + IGSTPaidUsingSGST + ","
				+ "CGSTPaidUsingIGST=" + CGSTPaidUsingIGST + ",CGSTPaidUsingCGST=" + CGSTPaidUsingCGST + ","
				+ ",SGSTPaidUsingIGST=" + SGSTPaidUsingIGST + ",SGSTPaidUsingSGST=" + SGSTPaidUsingSGST
				+ "," + "CessPaidUsingCess=" + CessPaidUsingCess+ "]";
	}
}
