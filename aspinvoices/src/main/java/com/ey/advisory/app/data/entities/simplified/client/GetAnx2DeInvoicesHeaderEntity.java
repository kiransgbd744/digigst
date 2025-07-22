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
 * 
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "GETANX2_DE_HEADER")
public class GetAnx2DeInvoicesHeaderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "SGSTIN")
	private String sgstin;

	@Column(name = "CGSTIN")
	private String cgstin;

	@Column(name = "CGSTIN_PAN")
	private String cgstinPan;

	@Column(name = "SGSTIN_PAN")
	private String sgstinPan;

	@Column(name = "CFS")
	private String cfs;

	@Column(name = "CHKSUM")
	private String chkSum;

	@Column(name = "SUPPLIER_INV_NUM")
	private String invNum;

	@Column(name = "SUPPLIER_INV_DATE")
	private LocalDate invDate;

	@Column(name = "SUPPLIER_INV_VAL")
	private BigDecimal invValue;

	@Column(name = "POS")
	private String pos;

	@Column(name = "INV_TYPE")
	private String invType;

	@Column(name = "DIFF_PERCENT")
	private BigDecimal diffPercentage;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxable;

	@Column(name = "TAX_PERIOD")
	private String returnPeriod;

	@OneToMany(mappedBy = "header", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected List<GetAnx2DeInvoicesItemEntity> lineItems = new ArrayList<>();

	/*
	 * @ManyToOne
	 * 
	 * @JoinColumn(name = "BATCH_ID", referencedColumnName = "ID", nullable =
	 * false) private GetGstr2aBatchEntity deBatchIdAnx2;
	 */

	@Column(name = "BATCH_ID")
	private Long deBatchIdAnx2;

	@Column(name = "SEC_7_ACT")
	private String sec7;

	@Column(name = "REFUND_ELG")
	private String rfndElg;

	@Column(name = "ITC_ENT")
	private String itc;

	@Column(name = "CLAIM_REFUND")
	private String claimRfnd;

	@Column(name = "UPLOAD_DATE_SUPPLIER")
	private LocalDate uploadDate;

	@Column(name = "ACTION_TAKEN")
	private String action;

	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedRetPeriod;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "TABLE_SECTION")
	private String tableSection;

	@Column(name = "IS_ROLLOVER")
	private String isRollover;

	@Column(name = "IS_ROLLOVER_MATCHED")
	private String isRolloverMatched;

	@Column(name = "DOCKEY")
	private String docKey;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSgstin() {
		return sgstin;
	}

	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}

	public String getCgstin() {
		return cgstin;
	}

	public void setCgstin(String cgstin) {
		this.cgstin = cgstin;
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

	public String getCfs() {
		return cfs;
	}

	public void setCfs(String cfs) {
		this.cfs = cfs;
	}

	public String getChkSum() {
		return chkSum;
	}

	public void setChkSum(String chkSum) {
		this.chkSum = chkSum;
	}

	public String getInvNum() {
		return invNum;
	}

	public void setInvNum(String invNum) {
		this.invNum = invNum;
	}

	public LocalDate getInvDate() {
		return invDate;
	}

	public void setInvDate(LocalDate invDate) {
		this.invDate = invDate;
	}

	public BigDecimal getInvValue() {
		return invValue;
	}

	public void setInvValue(BigDecimal invValue) {
		this.invValue = invValue;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getInvType() {
		return invType;
	}

	public void setInvType(String invType) {
		this.invType = invType;
	}

	public BigDecimal getDiffPercentage() {
		return diffPercentage;
	}

	public void setDiffPercentage(BigDecimal diffPercentage) {
		this.diffPercentage = diffPercentage;
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

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public List<GetAnx2DeInvoicesItemEntity> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<GetAnx2DeInvoicesItemEntity> lineItems) {
		this.lineItems = lineItems;
	}

	/*
	 * public GetGstr2aBatchEntity getDeBatchIdAnx2() { return deBatchIdAnx2; }
	 * 
	 * public void setDeBatchIdAnx2(GetGstr2aBatchEntity deBatchIdAnx2) {
	 * this.deBatchIdAnx2 = deBatchIdAnx2; }
	 */

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

	public String getItc() {
		return itc;
	}

	public void setItc(String itc) {
		this.itc = itc;
	}

	public String getClaimRfnd() {
		return claimRfnd;
	}

	public void setClaimRfnd(String claimRfnd) {
		this.claimRfnd = claimRfnd;
	}

	public LocalDate getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(LocalDate uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Integer getDerivedRetPeriod() {
		return derivedRetPeriod;
	}

	public void setDerivedRetPeriod(Integer derivedRetPeriod) {
		this.derivedRetPeriod = derivedRetPeriod;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public Long getDeBatchIdAnx2() {
		return deBatchIdAnx2;
	}

	public void setDeBatchIdAnx2(Long deBatchIdAnx2) {
		this.deBatchIdAnx2 = deBatchIdAnx2;
	}

	public String getTableSection() {
		return tableSection;
	}

	public void setTableSection(String tableSection) {
		this.tableSection = tableSection;
	}

	public String getIsRollover() {
		return isRollover;
	}

	public void setIsRollover(String isRollover) {
		this.isRollover = isRollover;
	}

	public String getIsRolloverMatched() {
		return isRolloverMatched;
	}

	public void setIsRolloverMatched(String isRolloverMatched) {
		this.isRolloverMatched = isRolloverMatched;
	}

	public String getDocKey() {
		return docKey;
	}

	public void setDocKey(String docKey) {
		this.docKey = docKey;
	}

}
