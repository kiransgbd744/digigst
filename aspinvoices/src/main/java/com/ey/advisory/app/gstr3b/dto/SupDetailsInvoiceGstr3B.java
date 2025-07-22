package com.ey.advisory.app.gstr3b.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SupDetailsInvoiceGstr3B {

	@SerializedName("osup_det")
	@Expose
	private SupDetailsGstr3B osupDet;

	@SerializedName("osup_zero")
	@Expose
	private SupDetailsGstr3B osupZero;

	@SerializedName("osup_nil_exmp")
	@Expose
	private SupDetailsGstr3B osupNilExmp;

	@SerializedName("isup_rev")
	@Expose
	private SupDetailsGstr3B isupRev;

	@SerializedName("osup_nongst")
	@Expose
	private SupDetailsGstr3B osupNongst;

	public SupDetailsGstr3B getOsupDet() {
		return osupDet;
	}

	public SupDetailsGstr3B getOsupZero() {
		return osupZero;
	}

	public SupDetailsGstr3B getOsupNilExmp() {
		return osupNilExmp;
	}

	public SupDetailsGstr3B getIsupRev() {
		return isupRev;
	}

	public SupDetailsGstr3B getOsupNongst() {
		return osupNongst;
	}

	@Override
	public String toString() {
		return "SupDetailsInvoiceGstr3B [osupDet="
				+ osupDet + ", osupZero=" + osupZero + ", osupNilExmp="
				+ osupNilExmp + ", isupRev=" + isupRev + ", osupNongst="
				+ osupNongst + "]";
	}
}
