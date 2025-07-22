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
 * @author Mahesh.Golla
 *
 */
@Entity
@Table(name = "GETANX1_IMPGSEZ_HEADER")
public class GetAnx1ImpgSezInvoicesHeaderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CTIN")
	private String ctin;

	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "CHKSUM")
	private String chkSum;

	@Column(name = "RFNDELG")
	private String rfndElg;

	@Column(name = "POS")
	private String pos;

	@Column(name = "BILL_ENTRY_NUM")
	private BigDecimal billEntryNum;

	@Column(name = "PORT_CODE")
	private String portCode;

	@Column(name = "BILL_ENTRY_DATE")
	private LocalDate billEntryDate;

	@Column(name = "DOC_AMT")
	private BigDecimal docAmt;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxable;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;

	@Column(name = "DOCKEY")
	private String docKey;

	@OneToMany(mappedBy = "header", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected List<GetAnx1ImpgSezInvoicesItemEntity> lineItems = new ArrayList<>();

	/*
	 * @ManyToOne
	 * 
	 * @JoinColumn(name = "BATCH_ID", referencedColumnName = "ID", nullable =
	 * false) private GetAnx1BatchEntity impgSezBatchId;
	 */
	@Column(name = "BATCH_ID")
	private Long impgSezBatchId;

	@Column(name = "GSTIN")
	private String sGstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	private int derTaxPeriod;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "UPLOAD_DOC_DATE")
	private LocalDate uploadDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public BigDecimal getBillEntryNum() {
		return billEntryNum;
	}

	public void setBillEntryNum(BigDecimal billEntryNum) {
		this.billEntryNum = billEntryNum;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public LocalDate getBillEntryDate() {
		return billEntryDate;
	}

	public void setBillEntryDate(LocalDate billEntryDate) {
		this.billEntryDate = billEntryDate;
	}

	public BigDecimal getDocAmt() {
		return docAmt;
	}

	public void setDocAmt(BigDecimal docAmt) {
		this.docAmt = docAmt;
	}

	public BigDecimal getTaxable() {
		return taxable;
	}

	public void setTaxable(BigDecimal taxable) {
		this.taxable = taxable;
	}

	public BigDecimal getIgstAmt() {
		return igstAmt;
	}

	public void setIgstAmt(BigDecimal igstAmt) {
		this.igstAmt = igstAmt;
	}

	public BigDecimal getCessAmt() {
		return cessAmt;
	}

	public void setCessAmt(BigDecimal cessAmt) {
		this.cessAmt = cessAmt;
	}

	public List<GetAnx1ImpgSezInvoicesItemEntity> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<GetAnx1ImpgSezInvoicesItemEntity> lineItems) {
		this.lineItems = lineItems;
	}

	/*
	 * public GetAnx1BatchEntity getImpgSezBatchId() { return impgSezBatchId; }
	 * 
	 * public void setImpgSezBatchId(GetAnx1BatchEntity impgSezBatchId) {
	 * this.impgSezBatchId = impgSezBatchId; }
	 */

	public Long getImpgSezBatchId() {
		return impgSezBatchId;
	}

	public void setImpgSezBatchId(Long impgSezBatchId) {
		this.impgSezBatchId = impgSezBatchId;
	}

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

	public LocalDate getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(LocalDate uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getDocKey() {
		return docKey;
	}

	public void setDocKey(String docKey) {
		this.docKey = docKey;
	}

}
