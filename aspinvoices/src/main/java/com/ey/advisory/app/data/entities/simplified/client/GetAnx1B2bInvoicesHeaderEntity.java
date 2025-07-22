/**
 * 
 */
package com.ey.advisory.app.data.entities.simplified.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "GETANX1_B2B_HEADER")
public class GetAnx1B2bInvoicesHeaderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CGSTIN_PAN")
	private String cgstinPan;

	@Column(name = "SGSTIN_PAN")
	private String sgstinPan;

	@Column(name = "CTIN")
	private String ctin;

	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "CHKSUM")
	private String chkSum;

	@Column(name = "DIFF_PERCENT")
	private BigDecimal diffPercentage;

	@Column(name = "ACTION")
	private String action;

	@Column(name = "SEC7ACT")
	private String sec7;

	@Column(name = "RFNDELG")
	private String rfndElg;

	@Column(name = "POS")
	private String pos;

	@Column(name = "DOC_NUMBER")
	private String docNum;

	@Column(name = "DOC_DATE")
	private LocalDate docDate;

	@Column(name = "DOC_AMT")
	private BigDecimal docAmt;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxable;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;

	@OneToMany(mappedBy = "header", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected List<GetAnx1B2bInvoicesItemEntity> lineItems = new ArrayList<>();

	// @ManyToOne
	// @JoinColumn(name = "BATCH_ID", referencedColumnName = "ID", nullable =
	// false)
	// private GetAnx1BatchEntity b2bBatchId;
	@Column(name = "BATCH_ID")
	private Long b2bBatchId;

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

	public int getDerTaxPeriod() {
		return derTaxPeriod;
	}

	public void setDerTaxPeriod(int derTaxPeriod) {
		this.derTaxPeriod = derTaxPeriod;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getCtin() {
		return ctin;
	}

	public void setCtin(String ctin) {
		this.ctin = ctin;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getChkSum() {
		return chkSum;
	}

	public void setChkSum(String chkSum) {
		this.chkSum = chkSum;
	}

	public BigDecimal getDiffPercentage() {
		return diffPercentage;
	}

	public void setDiffPercentage(BigDecimal diffPercentage) {
		this.diffPercentage = diffPercentage;
	}

	public String getSec7() {
		return sec7;
	}

	public void setSec7(String sec7) {
		this.sec7 = sec7;
	}

	public String getRfndElg() {
		return rfndElg;
	}

	public void setRfndElg(String rfndElg) {
		this.rfndElg = rfndElg;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getDocNum() {
		return docNum;
	}

	public void setDocNum(String docNum) {
		this.docNum = docNum;
	}

	public LocalDate getDocDate() {
		return docDate;
	}

	public void setDocDate(LocalDate docDate) {
		this.docDate = docDate;
	}

	public BigDecimal getDocAmt() {
		return docAmt;
	}

	public void setDocAmt(BigDecimal docAmt) {
		this.docAmt = docAmt;
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

	public BigDecimal getTaxable() {
		return taxable;
	}

	public void setTaxable(BigDecimal taxable) {
		this.taxable = taxable;
	}

	public List<GetAnx1B2bInvoicesItemEntity> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<GetAnx1B2bInvoicesItemEntity> lineItems) {
		this.lineItems = lineItems;
	}

	/*
	 * public GetAnx1BatchEntity getB2bBatchId() { return b2bBatchId; }
	 * 
	 * public void setB2bBatchId(GetAnx1BatchEntity b2bBatchId) {
	 * this.b2bBatchId = b2bBatchId; }
	 */

	/*
	 * public String getDocKey() { return docKey; }
	 */

	public Long getB2bBatchId() {
		return b2bBatchId;
	}

	public void setB2bBatchId(Long b2bBatchId) {
		this.b2bBatchId = b2bBatchId;
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

	public String getCgstinPan() {
		return cgstinPan;
	}

	public void setCgstinPan(String cgstinPan) {
		this.cgstinPan = cgstinPan;
	}

	public String getSgstinPan() {
		return sgstinPan;
	}

	public void setSgstinPan(String sgstinPan) {
		this.sgstinPan = sgstinPan;
	}

	public String getDocKey() {
		return docKey;
	}

	public void setDocKey(String docKey) {
		this.docKey = docKey;
	}

}
