package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Anand3.M
 *
 */

@Entity
@Table(name = "GETGSTR1_B2CLA_HEADER")
public class GetGstr1B2claHeaderEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR1_B2CLA_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("batchId")
	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Expose
	@SerializedName("flag")
	@Column(name = "FLAG")
	protected String flag;

	@Expose
	@SerializedName("etin")
	@Column(name = "ETIN")
	protected String etin;

	@Expose
	@SerializedName("stateCode")
	@Column(name = "STATE_CODE")
	protected String stateCode;

	@Expose
	@SerializedName("pos")
	@Column(name = "POS")
	protected String pos;

	@Expose
	@SerializedName("ret_period")
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Expose
	@SerializedName("derivedTaxperiod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Expose
	@SerializedName("chksum")
	@Column(name = "INV_CHKSUM")
	protected String chksum;

	@Expose
	@SerializedName("invNum")
	@Column(name = "INV_NUM")
	protected String invNum;

	@Expose
	@SerializedName("invDate")
	@Column(name = "INV_DATE")
	private String invDate;

	@Expose
	@SerializedName("orgInvNum")
	@Column(name = "ORG_INV_NUM")
	protected String orgInvNum;

	@Expose
	@SerializedName("orgInvDate")
	@Column(name = "ORG_INV_DATE")
	private LocalDate orgInvDate;

	@Expose
	@SerializedName("invValue")
	@Column(name = "INV_VALUE")
	protected BigDecimal invValue;

	@Expose
	@SerializedName("invType")
	@Column(name = "INV_TYPE")
	protected String invType;

	@Expose
	@SerializedName("diff_percent")
	@Column(name = "DIFF_PERCENT")
	protected BigDecimal diffPercent;

	@Expose
	@SerializedName("taxValue")
	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal taxValue;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt;

	@Expose
	@SerializedName("cessAmt")
	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmt;

	@Expose
	@SerializedName("token")
	@Column(name = "TOKEN")
	private String token;

	@Expose
	@SerializedName("estimatedTime")
	@Column(name = "ESTIMATED_TIME")
	private String estimatedTime;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Expose
	@SerializedName("docKey")
	@Column(name = "DOC_KEY")
	protected String docKey;

	@Expose
	@SerializedName("gstin")
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

	@OneToMany(mappedBy = "document")
	@OrderColumn(name = "ITEM_INDEX")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<GetGstr1B2claItemEntity> lineItems = new ArrayList<>();

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

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getEtin() {
		return etin;
	}

	public void setEtin(String etin) {
		this.etin = etin;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
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

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
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

	public String getOrgInvNum() {
		return orgInvNum;
	}

	public void setOrgInvNum(String orgInvNum) {
		this.orgInvNum = orgInvNum;
	}

	public LocalDate getOrgInvDate() {
		return orgInvDate;
	}

	public void setOrgInvDate(LocalDate orgInvDate) {
		this.orgInvDate = orgInvDate;
	}

	public BigDecimal getInvValue() {
		return invValue;
	}

	public void setInvValue(BigDecimal invValue) {
		this.invValue = invValue;
	}

	public String getInvType() {
		return invType;
	}

	public void setInvType(String invType) {
		this.invType = invType;
	}

	public BigDecimal getDiffPercent() {
		return diffPercent;
	}

	public void setDiffPercent(BigDecimal diffPercent) {
		this.diffPercent = diffPercent;
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEstimatedTime() {
		return estimatedTime;
	}

	public void setEstimatedTime(String estimatedTime) {
		this.estimatedTime = estimatedTime;
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

	public List<GetGstr1B2claItemEntity> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<GetGstr1B2claItemEntity> lineItems) {
		this.lineItems = lineItems;
	}

}
