package com.ey.advisory.gstr9.jsontocsv.model.gstr3B;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItcEligibleInvoiceGstr3B {

	@SerializedName("itc_avl")
	@Expose
	private List<ItcDetailsGstr3B> itcAvl;

	@SerializedName("itc_rev")
	@Expose
	private List<ItcDetailsGstr3B> itcRev;

	@SerializedName("itc_net")
	@Expose
	private ItcDetailsGstr3B itcNet;

	@SerializedName("itc_inelg")
	@Expose
	private List<ItcDetailsGstr3B> itcInelg;

	public List<ItcDetailsGstr3B> getItcAvl() {
		return itcAvl;
	}

	public List<ItcDetailsGstr3B> getItcRev() {
		return itcRev;
	}

	public ItcDetailsGstr3B getItcNet() {
		return itcNet;
	}

	public List<ItcDetailsGstr3B> getItcInelg() {
		return itcInelg;
	}

	@Override
	public String toString() {
		return "ItcEligibleInvoiceGstr3B [itcAvl=" + itcAvl + ", itcRev="
				+ itcRev + "," + " itcNet=" + itcNet + ", itcInelg=" + itcInelg
				+ "]";
	}
}
