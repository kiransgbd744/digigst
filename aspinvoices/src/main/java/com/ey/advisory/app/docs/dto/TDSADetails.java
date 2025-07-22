package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TDSADetails {
	
	@Expose
	@SerializedName("ogstin_ded")
	private String orggstindeductee;
	
	@Expose
	@SerializedName("oamt_ded")
	private BigDecimal orgamtdeducted;

	@Expose
	@SerializedName("gstin_ded")
	private String gstindeductee;

	@Expose
	@SerializedName("amt_ded")
	private BigDecimal amtdeducted;
	
	@Expose
	@SerializedName("omonth")
	private String mnthded;

	@Expose
	@SerializedName("iamt")
	private BigDecimal integratedamt;

	@Expose
	@SerializedName("camt")
	private BigDecimal centralamt;

	@Expose
	@SerializedName("samt")
	private BigDecimal stateamt;

		
	public String getOrggstindeductee() {
		return orggstindeductee;
	}


	public void setOrggstindeductee(String orggstindeductee) {
		this.orggstindeductee = orggstindeductee;
	}


	public BigDecimal getOrgamtdeducted() {
		return orgamtdeducted;
	}


	public void setOrgamtdeducted(BigDecimal orgamtdeducted) {
		this.orgamtdeducted = orgamtdeducted;
	}


	public String getGstindeductee() {
		return gstindeductee;
	}


	public void setGstindeductee(String gstindeductee) {
		this.gstindeductee = gstindeductee;
	}


	public BigDecimal getAmtdeducted() {
		return amtdeducted;
	}


	public void setAmtdeducted(BigDecimal amtdeducted) {
		this.amtdeducted = amtdeducted;
	}


	public String getMnthded() {
		return mnthded;
	}


	public void setMnthded(String mnthded) {
		this.mnthded = mnthded;
	}


	public BigDecimal getIntegratedamt() {
		return integratedamt;
	}


	public void setIntegratedamt(BigDecimal integratedamt) {
		this.integratedamt = integratedamt;
	}


	public BigDecimal getCentralamt() {
		return centralamt;
	}


	public void setCentralamt(BigDecimal centralamt) {
		this.centralamt = centralamt;
	}


	public BigDecimal getStateamt() {
		return stateamt;
	}


	public void setStateamt(BigDecimal stateamt) {
		this.stateamt = stateamt;
	}


	@Override
	public String toString() {
		return "TDSAdetails [orggstindeductee=" + orggstindeductee + ", orgamtdeducted="
				+ orgamtdeducted + ", gstindeductee=" + gstindeductee + ", amtdeducted="
				+ amtdeducted + ", mnthded=" + mnthded + ", integratedamt="
				+ integratedamt + ", centralamt=" 
				+ centralamt +" , stateamt =" 
				+ stateamt + "]";
	}

	
	


}
