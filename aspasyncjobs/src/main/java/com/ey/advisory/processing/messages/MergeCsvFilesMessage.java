package com.ey.advisory.processing.messages;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class contains all the information required to Merge all csv.
 * 
 * @author Sai.Pakanati
 *
 */
public class MergeCsvFilesMessage {

	@Expose
	@SerializedName("startTaxPeriod")
	private String startPeriod;

	@Expose
	@SerializedName("basePath")
	private String basePath;

	@Expose
	@SerializedName("returnType")
	private String returnType;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("endTaxPeriod")
	private String endPeriod;

	@Expose
	@SerializedName("invoiceType")
	private String invoiceType;

	public MergeCsvFilesMessage() {
	}

	public MergeCsvFilesMessage(String basePath, String returnType,
			String gstin, String invoiceType, String stPeriod,
			String endPeriod) {
		this.basePath = basePath;
		this.returnType = returnType;
		this.gstin = gstin;
		this.invoiceType = invoiceType;
		this.startPeriod = stPeriod;
		this.endPeriod = endPeriod;
	}

	public String getGstin() {
		return gstin;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public String getReturnType() {
		return returnType;
	}

	public String getStartPeriod() {
		return startPeriod;
	}

	public String getEndPeriod() {
		return endPeriod;
	}

	public String getBasePath() {
		return basePath;
	}

	@Override
	public String toString() {
		return "MergeCsvFilesMessage [startPeriod=" + startPeriod
				+ ", basePath=" + basePath + ", returnType=" + returnType
				+ ", gstin=" + gstin + ", endPeriod=" + endPeriod
				+ ", invoiceType=" + invoiceType + "]";
	}

}
