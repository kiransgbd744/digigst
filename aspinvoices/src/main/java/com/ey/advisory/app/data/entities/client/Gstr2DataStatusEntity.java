package com.ey.advisory.app.data.entities.client;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;

public class Gstr2DataStatusEntity {
	
	@Column(name = "SUPPLIER_GSTIN")
	private List<String> sgstins;
	
	@Column(name = "ASPTOTAL")
	private Integer aspTotal;
	
	@Column(name="PROCESSED")
	private Integer aspProcessed;
	
	@Column(name="ERRORS")
	private Integer aspError;
	
	@Column(name="INFORMATION")
	private Integer aspInfo;
	
	@Column(name = "RECEIVED_DATE")
	private LocalDate receivedDate;
	
	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedRetPeriod;
	
	@Column(name = "DOC_DATE")
	private LocalDate documentDate;
	
	private Long entityId;

	public List<String> getSgstins() {
		return sgstins;
	}

	public void setSgstins(List<String> sgstins) {
		this.sgstins = sgstins;
	}

	public Integer getAspTotal() {
		return aspTotal;
	}

	public void setAspTotal(Integer aspTotal) {
		this.aspTotal = aspTotal;
	}

	public Integer getAspProcessed() {
		return aspProcessed;
	}

	public void setAspProcessed(Integer aspProcessed) {
		this.aspProcessed = aspProcessed;
	}

	public Integer getAspError() {
		return aspError;
	}

	public void setAspError(Integer aspError) {
		this.aspError = aspError;
	}

	public Integer getAspInfo() {
		return aspInfo;
	}

	public void setAspInfo(Integer aspInfo) {
		this.aspInfo = aspInfo;
	}

	public LocalDate getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(LocalDate receivedDate) {
		this.receivedDate = receivedDate;
	}

	public Integer getDerivedRetPeriod() {
		return derivedRetPeriod;
	}

	public void setDerivedRetPeriod(Integer derivedRetPeriod) {
		this.derivedRetPeriod = derivedRetPeriod;
	}

	public LocalDate getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(LocalDate documentDate) {
		this.documentDate = documentDate;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	@Override
	public String toString() {
		return "Gstr2DataStatusEntity [sgstins=" + sgstins + ", aspTotal="
				+ aspTotal + ", aspProcessed=" + aspProcessed + ", aspError="
				+ aspError + ", aspInfo=" + aspInfo + ", receivedDate="
				+ receivedDate + ", derivedRetPeriod=" + derivedRetPeriod
				+ ", documentDate=" + documentDate + ", entityId=" + entityId
				+ "]";
	}
}
