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
@Table(name = "GETANX1_EXPWOP_HEADER")
public class GetAnx1ExpwopInvoicesHeaderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "CHKSUM")
	private String chkSum;

	@Column(name = "RFNDELG")
	private String rfndElg;

	@Column(name = "SHIP_BILL_NUM")
	private String shipBillNum;

	@Column(name = "PORT_CODE")
	private String portCode;

	@Column(name = "SHIPP_DATE")
	private LocalDate shipDate;

	@Column(name = "DOC_NUMBER")
	private String docNum;

	@Column(name = "DOC_DATE")
	private LocalDate docDate;

	@Column(name = "DOC_AMT")
	private BigDecimal docAmt;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxable;

	@OneToMany(mappedBy = "header", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected List<GetAnx1ExpwopInvoicesItemEntity> lineItems = new ArrayList<>();

	@Column(name = "BATCH_ID")
	private Long expwopBatchId;

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

	public String getShipBillNum() {
		return shipBillNum;
	}

	public void setShipBillNum(String shipBillNum) {
		this.shipBillNum = shipBillNum;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public LocalDate getShipDate() {
		return shipDate;
	}

	public void setShipDate(LocalDate shipDate) {
		this.shipDate = shipDate;
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

	public BigDecimal getTaxable() {
		return taxable;
	}

	public void setTaxable(BigDecimal taxable) {
		this.taxable = taxable;
	}

	public List<GetAnx1ExpwopInvoicesItemEntity> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<GetAnx1ExpwopInvoicesItemEntity> lineItems) {
		this.lineItems = lineItems;
	}

	/*
	 * public GetAnx1BatchEntity getExpwopBatchId() { return expwopBatchId; }
	 * 
	 * public void setExpwopBatchId(GetAnx1BatchEntity expwopBatchId) {
	 * this.expwopBatchId = expwopBatchId; }
	 */

	/*
	 * public String getDocKey() { return docKey; }
	 */

	public Long getExpwopBatchId() {
		return expwopBatchId;
	}

	public void setExpwopBatchId(Long expwopBatchId) {
		this.expwopBatchId = expwopBatchId;
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

	public int getDerTaxPeriod() {
		return derTaxPeriod;
	}

	public void setDerTaxPeriod(int derTaxPeriod) {
		this.derTaxPeriod = derTaxPeriod;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public String getDocKey() {
		return docKey;
	}

	public void setDocKey(String docKey) {
		this.docKey = docKey;
	}

}
