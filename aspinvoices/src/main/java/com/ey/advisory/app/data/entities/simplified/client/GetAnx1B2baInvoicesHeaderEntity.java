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
@Table(name = "GETANX1_B2BA_HEADER")
public class GetAnx1B2baInvoicesHeaderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "BATCH_ID")
	private Long b2baBatchId;

	@Column(name = "ORG_DOC_TYPE")
	private String orgDocType;

	@Column(name = "CTIN")
	private String ctin;

	@Column(name = "ORG_CTIN")
	private String orgCtin;

	@Column(name = "FROM_TIME")
	private LocalDateTime fromTime;

	@Column(name = "CHKSUM")
	private String chkSum;

	@Column(name = "DIFF_PERCENT")
	private BigDecimal diffPercentage;

	@Column(name = "ACTION")
	private String action;

	@Column(name = "SEC7ACT")
	private String sec7Act;

	@Column(name = "RFNDELG")
	private String rfndElg;

	@Column(name = "POS")
	private String pos;

	@Column(name = "IS_AMENDED")
	private boolean isAmended;

	@Column(name = "AMD_PERIOD")
	private String amdPeriod;

	@Column(name = "AMD_TYPE")
	private String amdType;

	@Column(name = "DOC_NUMBER")
	private String docNumber;

	@Column(name = "ORG_DOC_NUMBER")
	private String orgDocNumber;

	@Column(name = "DOC_DATE")
	private LocalDate docDate;

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
	protected List<GetAnx1B2baInvoicesItemEntity> lineItems = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getB2baBatchId() {
		return b2baBatchId;
	}

	public void setB2baBatchId(Long b2baBatchId) {
		this.b2baBatchId = b2baBatchId;
	}

	public String getOrgDocType() {
		return orgDocType;
	}

	public void setOrgDocType(String orgDocType) {
		this.orgDocType = orgDocType;
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

	public LocalDateTime getFromTime() {
		return fromTime;
	}

	public void setFromTime(LocalDateTime fromTime) {
		this.fromTime = fromTime;
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

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getSec7Act() {
		return sec7Act;
	}

	public void setSec7Act(String sec7Act) {
		this.sec7Act = sec7Act;
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

	public boolean isAmended() {
		return isAmended;
	}

	public void setAmended(boolean isAmended) {
		this.isAmended = isAmended;
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

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	public String getOrgDocNumber() {
		return orgDocNumber;
	}

	public void setOrgDocNumber(String orgDocNumber) {
		this.orgDocNumber = orgDocNumber;
	}

	public LocalDate getDocDate() {
		return docDate;
	}

	public void setDocDate(LocalDate docDate) {
		this.docDate = docDate;
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

	public List<GetAnx1B2baInvoicesItemEntity> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<GetAnx1B2baInvoicesItemEntity> lineItems) {
		this.lineItems = lineItems;
	}

	public String getDocKey() {
		return docKey;
	}

	public void setDocKey(String docKey) {
		this.docKey = docKey;
	}

}
