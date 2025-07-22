package com.ey.advisory.app.data.entities.simplified.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
 * 
 * @author Anand3.M
 *
 */

@Entity
@Table(name = "GETANX1_SEZWPA_HEADER")
public class GetAnx1SezwpaInvoicesHeaderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "BATCH_ID")
	private Long sezwpaBatchId;

	@Column(name = "CTIN")
	private String ctin;

	@Column(name = "ORG_CTIN")
	private String orgCtin;

	@Column(name = "ORG_DOC_TYPE")
	private String orgDocType;

	@Column(name = "FROM_TIME")
	private LocalDateTime fromTime;

	@Column(name = "INVALID")
	private boolean invalid;

	@Column(name = "IS_AMENDED")
	private boolean isAmended;

	@Column(name = "CHKSUM")
	private String chkSum;

	@Column(name = "RFNDELG")
	private String rfndElg;

	@Column(name = "ACTION")
	private String action;

	@Column(name = "POS")
	private String pos;

	@Column(name = "AMD_PERIOD")
	private String amdPeriod;

	@Column(name = "AMD_TYPE")
	private String amdType;

	@Column(name = "DOC_NUMBER")
	private String docNum;

	@Column(name = "DOC_DATE")
	private LocalDate docDate;

	@Column(name = "ORG_DOC_NUMBER")
	private String orgDocNum;

	@Column(name = "ORG_DOC_DATE")
	private LocalDate orgDocDate;

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

	@Column(name = "GSTIN")
	private String sGstin;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "CGSTIN_PAN")
	private String cgstinPan;

	@Column(name = "SGSTIN_PAN")
	private String sgstinPan;

	@Column(name = "DERIVED_RET_PERIOD")
	private int derTaxPeriod;

	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "DOCKEY")
	private String docKey;

	@OneToMany(mappedBy = "header", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected List<GetAnx1SezwpaInvoicesItemEntity> lineItems = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSezwpaBatchId() {
		return sezwpaBatchId;
	}

	public void setSezwpaBatchId(Long sezwpaBatchId) {
		this.sezwpaBatchId = sezwpaBatchId;
	}

	public String getCtin() {
		return ctin;
	}

	public void setCtin(String ctin) {
		this.ctin = ctin;
	}

	public String getOrgCtin() {
		return orgCtin;
	}

	public void setOrgCtin(String orgCtin) {
		this.orgCtin = orgCtin;
	}

	public String getOrgDocType() {
		return orgDocType;
	}

	public void setOrgDocType(String orgDocType) {
		this.orgDocType = orgDocType;
	}

	public LocalDateTime getFromTime() {
		return fromTime;
	}

	public void setFromTime(LocalDateTime fromTime) {
		this.fromTime = fromTime;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public boolean isAmended() {
		return isAmended;
	}

	public void setAmended(boolean isAmended) {
		this.isAmended = isAmended;
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

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getAmdPeriod() {
		return amdPeriod;
	}

	public void setAmdPeriod(String amdPeriod) {
		this.amdPeriod = amdPeriod;
	}

	public String getAmdType() {
		return amdType;
	}

	public void setAmdType(String amdType) {
		this.amdType = amdType;
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

	public String getOrgDocNum() {
		return orgDocNum;
	}

	public void setOrgDocNum(String orgDocNum) {
		this.orgDocNum = orgDocNum;
	}

	public LocalDate getOrgDocDate() {
		return orgDocDate;
	}

	public void setOrgDocDate(LocalDate orgDocDate) {
		this.orgDocDate = orgDocDate;
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

	public String getsGstin() {
		return sGstin;
	}

	public void setsGstin(String sGstin) {
		this.sGstin = sGstin;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
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

	public int getDerTaxPeriod() {
		return derTaxPeriod;
	}

	public void setDerTaxPeriod(int derTaxPeriod) {
		this.derTaxPeriod = derTaxPeriod;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public List<GetAnx1SezwpaInvoicesItemEntity> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<GetAnx1SezwpaInvoicesItemEntity> lineItems) {
		this.lineItems = lineItems;
	}

	public String getDocKey() {
		return docKey;
	}

	public void setDocKey(String docKey) {
		this.docKey = docKey;
	}

}
