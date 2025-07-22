package com.ey.advisory.gstr9.jsontocsv.model.gstr3B;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaidCashDetails {

	@SerializedName("liab_ldg_id")
	@Expose
	private String liabledgerId;

	@SerializedName("trans_typ")
	@Expose
	private String transType;

	@SerializedName("ipd")
	@Expose
	private String igstPaid;

	@SerializedName("cpd")
	@Expose
	private String cgstPaid;

	@SerializedName("spd")
	@Expose
	private String sgstPaid;

	@SerializedName("cspd")
	@Expose
	private String cessPaid;

	@SerializedName("i_intrpd")
	@Expose
	private String igstIntPaid;

	@SerializedName("c_intrpd")
	@Expose
	private String cgstIntPaid;

	@SerializedName("s_intrpd")
	@Expose
	private String sgstIntPaid;

	@SerializedName("cs_intrpd")
	@Expose
	private String cessIntPaid;

	@SerializedName("c_lfeepd")
	@Expose
	private String cgstLateFeePaid;

	@SerializedName("s_lfeepd")
	@Expose
	private String sgstLateFeePaid;
	
	@SerializedName("cs_lfeepd")
	@Expose
	private String cessLateFeePaid;
	
	@SerializedName("i_lfeepd")
	@Expose
	private String igstLateFeePaid;

	public String getLiabledgerId() {
		return liabledgerId;
	}

	public String getTransType() {
		return transType;
	}

	public String getIgstPaid() {
		return igstPaid;
	}

	public String getCgstPaid() {
		return cgstPaid;
	}

	public String getSgstPaid() {
		return sgstPaid;
	}

	public String getCessPaid() {
		return cessPaid;
	}

	public String getIgstIntPaid() {
		return igstIntPaid;
	}

	public String getCgstIntPaid() {
		return cgstIntPaid;
	}

	public String getSgstIntPaid() {
		return sgstIntPaid;
	}

	public String getCessIntPaid() {
		return cessIntPaid;
	}
	
	public String getIgstLateFeePaid() {
		return igstLateFeePaid;
	}

	public String getCessLateFeePaid() {
		return cessLateFeePaid;
	}

	public String getCgstLateFeePaid() {
		return cgstLateFeePaid;
	}

	public String getSgstLateFeePaid() {
		return sgstLateFeePaid;
	}

	@Override
	public String toString() {
		return "TaxPayableDetails [liabledgerId=" + liabledgerId
				+ ", transType=" + transType + ", igstPaid=" + igstPaid
				+ ",cgstPaid=" + cgstPaid + ",sgstPaid=" + sgstPaid + ","
				+ "cessPaid=" + cessPaid + ",igstIntPaid=" + igstIntPaid + ","
				+ ",cgstIntPaid=" + cgstIntPaid + ",sgstIntPaid=" + sgstIntPaid
				+ "," + "cgstLateFeePaid=" + cgstLateFeePaid
				+ "," + "igstLateFeePaid=" + igstLateFeePaid
				+ "," + "cessLateFeePaid=" + cessLateFeePaid
				+ ",sgstLateFeePaid=" + sgstLateFeePaid + "]";
	}
}
