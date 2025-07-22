package com.ey.advisory.app.docs.dto;



import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Santosh.Gururaj
 *
 */
public class IsdInvoices {

	@Expose
	@SerializedName("ctin")
	private String cgstin;

	@Expose
	@SerializedName("cfs")
	private String cfs;

	@Expose
	@SerializedName("doclist")
	private List<IsdInvoiceData> isdInvoiceData;

	public String getCgstin() {
		return cgstin;
	}

	public void setCgstin(String cgstin) {
		this.cgstin = cgstin;
	}

	public String getCfs() {
		return cfs;
	}

	public void setCfs(String cfs) {
		this.cfs = cfs;
	}

	public List<IsdInvoiceData> getIsdInvoiceData() {
		return isdInvoiceData;
	}

	public void setIsdInvoiceData(List<IsdInvoiceData> isdInvoiceData) {
		this.isdInvoiceData = isdInvoiceData;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "ISDInvoices [cgstin=" + cgstin + ", cfs="
				+ cfs + ", isdInvoiceData="
				+ isdInvoiceData + "]";

	}
}

