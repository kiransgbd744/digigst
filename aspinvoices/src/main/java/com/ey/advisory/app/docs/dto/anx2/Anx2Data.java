package com.ey.advisory.app.docs.dto.anx2;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class Anx2Data {

	@Expose
	@SerializedName("ctin")
	private String sgstin;

	@Expose
	@SerializedName("cfs")
	private String cfs;

	@Expose
	@SerializedName("docs")
	private List<Anx2DocumentData> invoiceData;

	public String getSgstin() {
		return sgstin;
	}

	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}

	public String getCfs() {
		return cfs;
	}

	public void setCfs(String cfs) {
		this.cfs = cfs;
	}

	public List<Anx2DocumentData> getInvoiceData() {
		return invoiceData;
	}

	public void setInvoiceData(List<Anx2DocumentData> invoiceData) {
		this.invoiceData = invoiceData;
	}

	

}
