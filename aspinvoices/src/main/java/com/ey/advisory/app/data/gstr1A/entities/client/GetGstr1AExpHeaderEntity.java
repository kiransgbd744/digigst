package com.ey.advisory.app.data.gstr1A.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * 
 * @author Anand3.M
 *
 */

@Entity
@Table(name = "GETGSTR1A_EXP_HEADER")
public class GetGstr1AExpHeaderEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR1_EXP_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Column(name = "FLAG")
	protected String flag;

	@Column(name = "EXPORT_TYPE")
	protected String exportType;

	@Column(name = "INV_CHKSUM")
	protected String invChksum;

	@Column(name = "INV_NUM")
	protected String invNum;

	@Column(name = "INV_DATE")
	protected String invDate;

	@Column(name = "INV_VALUE")
	protected BigDecimal invValue = BigDecimal.ZERO;

	@Column(name = "DIFF_PERCENT")
	protected BigDecimal diffPercent = BigDecimal.ZERO;

	@Column(name = "SHIPP_BILL_PORT_CODE")
	protected String shipBillPortCode;

	@Column(name = "SHIPP_BILL_NUM")
	protected String shipBillNum;

	@Column(name = "SHIPP_BILL_DATE")
	protected String shipBillDate;

	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal taxValue = BigDecimal.ZERO;

	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt = BigDecimal.ZERO;

	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmt = BigDecimal.ZERO;

	@Column(name = "DOC_KEY")
	protected String docKey;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "GSTIN")
	protected String gstin;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "IRN_NUM")
	private String irnNum;

	@Column(name = "IRN_GEN_DATE")
	private String irnGenDate;

	@Column(name = "IRN_SOURCE_TYPE")
	private String irnSrcType;

	@OneToMany(mappedBy = "document")
	@OrderColumn(name = "ITEM_INDEX")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<GetGstr1AExpItemEntity> lineItems = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public Integer getDerivedTaxperiod() {
		return derivedTaxperiod;
	}

	public void setDerivedTaxperiod(Integer derivedTaxperiod) {
		this.derivedTaxperiod = derivedTaxperiod;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}

	public String getInvChksum() {
		return invChksum;
	}

	public void setInvChksum(String invChksum) {
		this.invChksum = invChksum;
	}

	public String getInvNum() {
		return invNum;
	}

	public void setInvNum(String invNum) {
		this.invNum = invNum;
	}

	public String getInvDate() {
		return invDate;
	}

	public void setInvDate(String invDate) {
		this.invDate = invDate;
	}

	public BigDecimal getInvValue() {
		return invValue;
	}

	public void setInvValue(BigDecimal invValue) {
		this.invValue = invValue;
	}

	public BigDecimal getDiffPercent() {
		return diffPercent;
	}

	public void setDiffPercent(BigDecimal diffPercent) {
		this.diffPercent = diffPercent;
	}

	public String getShipBillPortCode() {
		return shipBillPortCode;
	}

	public void setShipBillPortCode(String shipBillPortCode) {
		this.shipBillPortCode = shipBillPortCode;
	}

	public String getShipBillNum() {
		return shipBillNum;
	}

	public void setShipBillNum(String shipBillNum) {
		this.shipBillNum = shipBillNum;
	}

	public String getShipBillDate() {
		return shipBillDate;
	}

	public void setShipBillDate(String shipBillDate) {
		this.shipBillDate = shipBillDate;
	}

	public BigDecimal getTaxValue() {
		return taxValue;
	}

	public void setTaxValue(BigDecimal taxValue) {
		this.taxValue = taxValue;
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

	public String getDocKey() {
		return docKey;
	}

	public void setDocKey(String docKey) {
		this.docKey = docKey;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(LocalDateTime modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getIrnNum() {
		return irnNum;
	}

	public void setIrnNum(String irnNum) {
		this.irnNum = irnNum;
	}

	public String getIrnGenDate() {
		return irnGenDate;
	}

	public void setIrnGenDate(String irnGenDate) {
		this.irnGenDate = irnGenDate;
	}

	public String getIrnSrcType() {
		return irnSrcType;
	}

	public void setIrnSrcType(String irnSrcType) {
		this.irnSrcType = irnSrcType;
	}

	public List<GetGstr1AExpItemEntity> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<GetGstr1AExpItemEntity> lineItems) {
		this.lineItems = lineItems;
	}
}
