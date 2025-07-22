/**
 * 
 */
package com.ey.advisory.app.data.entities.simplified.client;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "GETANX1_ECOM_DETAILS")
public class GetAnx1EcomInvoicesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "ETIN")
	private String etin;

	@Column(name = "CHKSUM")
	private String chkSum;

	@Column(name = "SUPP_MADE_VALUE")
	private BigDecimal supMadeVal;

	@Column(name = "SUPP_RET_VALUE")
	private BigDecimal supRetVal;

	@Column(name = "SUPP_NET_VALUE")
	private BigDecimal supNetVal;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;

	@Column(name = "BATCH_ID")
	private Long ecomBatchId;

	@Column(name = "DOCKEY")
	private String docKey;

	@Column(name = "GSTIN")
	private String sGstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	private int derTaxPeriod;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEtin() {
		return etin;
	}

	public void setEtin(String etin) {
		this.etin = etin;
	}

	public String getChkSum() {
		return chkSum;
	}

	public void setChkSum(String chkSum) {
		this.chkSum = chkSum;
	}

	public BigDecimal getSupMadeVal() {
		return supMadeVal;
	}

	public void setSupMadeVal(BigDecimal supMadeVal) {
		this.supMadeVal = supMadeVal;
	}

	public BigDecimal getSupRetVal() {
		return supRetVal;
	}

	public void setSupRetVal(BigDecimal supRetVal) {
		this.supRetVal = supRetVal;
	}

	public BigDecimal getSupNetVal() {
		return supNetVal;
	}

	public void setSupNetVal(BigDecimal supNetVal) {
		this.supNetVal = supNetVal;
	}

	public BigDecimal getIgstAmt() {
		return igstAmt;
	}

	public void setIgstAmt(BigDecimal igstAmt) {
		this.igstAmt = igstAmt;
	}

	public BigDecimal getCgstAmt() {
		return cgstAmt;
	}

	public void setCgstAmt(BigDecimal cgstAmt) {
		this.cgstAmt = cgstAmt;
	}

	public BigDecimal getSgstAmt() {
		return sgstAmt;
	}

	public void setSgstAmt(BigDecimal sgstAmt) {
		this.sgstAmt = sgstAmt;
	}

	public BigDecimal getCessAmt() {
		return cessAmt;
	}

	public void setCessAmt(BigDecimal cessAmt) {
		this.cessAmt = cessAmt;
	}

	/*
	 * public GetAnx1BatchEntity getEcomBatchId() { return ecomBatchId; }
	 * 
	 * public void setEcomBatchId(GetAnx1BatchEntity ecomBatchId) {
	 * this.ecomBatchId = ecomBatchId; }
	 */

	/*
	 * public String getDocKey() { return docKey; }
	 */

	public Long getEcomBatchId() {
		return ecomBatchId;
	}

	public void setEcomBatchId(Long ecomBatchId) {
		this.ecomBatchId = ecomBatchId;
	}

	/*
	 * public void setDocKey(String docKey) { this.docKey = docKey; }
	 */

	public String getsGstin() {
		return sGstin;
	}

	public void setsGstin(String sGstin) {
		this.sGstin = sGstin;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public int getDerTaxPeriod() {
		return derTaxPeriod;
	}

	public void setDerTaxPeriod(int derTaxPeriod) {
		this.derTaxPeriod = derTaxPeriod;
	}

	public String getDocKey() {
		return docKey;
	}

	public void setDocKey(String docKey) {
		this.docKey = docKey;
	}

}
