/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.util.Date;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author Khalid1.Khan
 *
 */

@Entity
@Table(name = "A2_PR_BIGTABLE_DEMO")
public class A2PrBigtableEntity {

	@Id
	@Column(name = "ID", nullable = false)                                      
	private Long id;

	@Expose
	@Column(name = "INVOICE_KEY", length = 80)
	private String invoiceKey;

	@Expose
	@Column(name = "A2_ID")
	private Integer a2Id;

	@Expose
	@Column(name = "PR_ID")
	private Integer prId;

	@Expose
	@Column(name = "A2_BATCH_ID")
	private Integer a2BatchId;

	@Expose
	@Column(name = "A2_CHKSUM")
	private String a2CheckSum;

	@Expose
	@Column(name = "A2_SGSTIN")
	private String a2Sgstin;

	@Expose
	@Column(name = "PR_SGSTIN")
	private String prSgstin;

	@Expose
	@Column(name = "A2_CGSTIN")
	private String a2Cgstin;

	@Expose
	@Column(name = "PR_CGSTIN")
	private String prCgstin;

	@Expose
	@Column(name = "A2_INV_TYPE")
	private String a2InvType;

	@Expose
	@Column(name = "PR_INV_TYPE")
	private String prInvType;

	@Expose
	@Column(name = "A2_SUPPLIER_INV_NUM")
	private String a2SupplierInvNum;

	@Expose
	@Column(name = "PR_SUPPLIER_INV_NUM")
	private String prSupplierInvNum;

	@Expose
	@Column(name = "A2_SUPPLIER_INV_DATE")
	private Date a2SupplierInvDate;

	@Expose
	@Column(name = "PR_SUPPLIER_INV_DATE")
	private Date prSupplierInvDate;

	@Expose
	@Column(name = "A2_POS")
	private Integer a2Pos;

	@Expose
	@Column(name = "PR_POS")
	private Integer prPos;

	@Expose
	@Column(name = "A2_TAXABLE_VALUE")
	private BigDecimal a2TaxableValue = BigDecimal.ZERO;

	@Expose
	@Column(name = "PR_TAXABLE_VALUE")
	private BigDecimal prTaxableValue = BigDecimal.ZERO;

	@Expose
	@Column(name = "A2_IGST_AMT")
	private BigDecimal a2IgstAmount = BigDecimal.ZERO;

	@Expose
	@Column(name = "PR_IGST_AMT")
	private BigDecimal prIgstAmount = BigDecimal.ZERO;

	@Expose
	@Column(name = "A2_CGST_AMT")
	private BigDecimal a2CgstAmount = BigDecimal.ZERO;

	@Expose
	@Column(name = "PR_CGST_AMT")
	private BigDecimal prCgstAmount = BigDecimal.ZERO;

	@Expose
	@Column(name = "A2_SGST_AMT")
	private BigDecimal a2SgstAmount = BigDecimal.ZERO;

	@Expose
	@Column(name = "PR_SGST_AMT")
	private BigDecimal prSgstAmount = BigDecimal.ZERO;

	@Expose
	@Column(name = "A2_CESS_AMT")
	private BigDecimal a2CessAmount = BigDecimal.ZERO;

	@Expose
	@Column(name = "PR_CESS_AMT")
	private BigDecimal prCessAmount = BigDecimal.ZERO;

	@Expose
	@Column(name = "A2_TAX_PERIOD")
	private String a2TaxPeriod;

	@Expose
	@Column(name = "PR_TAX_PERIOD")
	private String prTaxPeriod;

	@Expose
	@Column(name = "IS_DELETE_FLAG", nullable = false)
	private String isDeleteFlag;

	@Expose
	@Column(name = "A2_STATUS_FLAG")
	private String a2StatusFlag;

	@Expose
	@Column(name = "PR_STATUS_FLAG")
	private String prStatusFlag;

	@Expose
	@Column(name = "A2_ACTION_TAKEN")
	private String a2ActionTaken;

	@Expose
	@Column(name = "A2_GET_TYPE")
	private String a2GetType;

	@Expose
	@Column(name = "PR_TABLE_SECTION")
	private String prTableSection;

	@Expose
	@Column(name = "A2_EXIST_FLAG", nullable = false)
	private Boolean a2ExistFlag;

	@Expose
	@Column(name = "PR_EXIST_FLAG", nullable = false)
	private Boolean prExistFlag;

	@Expose
	@Column(name = "PR_LOCK_FLAG")
	private String prLockFlag;

	@Expose
	@Column(name = "A2_LOCK_FLAG")
	private String a2LockFlag;

	@Expose
	@Column(name = "A2_GSTIN_ACTION_STATUS")
	private String a2GstinAction;

	@Expose
	@Column(name = "A2_DERIVED_RET_PERIOD")
	private Integer a2DerivedRetPeriod;

	@Expose
	@Column(name = "A2_IS_DELETE", nullable = false)
	private Boolean a2Isdelete;

	@Expose
	@Column(name = "PR_IS_DELETE", nullable = false)
	private Boolean prIsDelete;

	@Expose
	@Column(name = "A2_LINK_ID")
	private String a2LinkId;

	@Expose
	@Column(name = "PR_LINK_ID")
	private String prLinkId;

	@Expose
	@Column(name = "RECON_STATUS")
	private String reconStatus;

	@Expose
	@Column(name = "A2_INVOICE_KEY")
	private String a2Invoicekey;

	@Expose
	@Column(name = "PR_INVOICE_KEY")
	private String prInvoicekey;

	@Expose
	@Column(name = "A2_SUPPLIER_PAN")
	private String a2SupplierPan;

	@Expose
	@Column(name = "PR_SUPPLIER_PAN")
	private String prSupplierPan;

	@Expose
	@Column(name = "A2_TAXPAYABLE_VALUE")
	private Integer a2TaxPayableValue;

	@Expose
	@Column(name = "PR_TAXPAYABLE_VALUE")
	private Integer prTaxPayableValue;

	@Expose
	@Column(name = "A2_RECEPIENT_PAN")
	private String a2RecepientPan;

	@Expose
	@Column(name = "PR_RECEPIENT_PAN")
	private String prRecepientPan;

	@Expose
	@Column(name = "E_TAXABLE_VALUE")
	private BigDecimal eTaxableValue = BigDecimal.ZERO;

	@Expose
	@Column(name = "E_IGST")
	private BigDecimal eIgst = BigDecimal.ZERO;

	@Expose
	@Column(name = "E_CGST")
	private BigDecimal eCgst = BigDecimal.ZERO;

	@Expose
	@Column(name = "E_SGST")
	private BigDecimal eSgst = BigDecimal.ZERO;

	@Expose
	@Column(name = "E_CESS")
	private BigDecimal eCess = BigDecimal.ZERO;

	@Expose
	@Column(name = "A2_ACTION_TAKEN_TIMESTAMP")
	private Date a2ActionTakenTimeStamp;
	
	
	@Expose
	@Column(name="A2_TABLE_SECTION")
	private String a2TableSection;
	
	
	@Expose
	@Column(name="A2_DOC_VALUE")
	private BigDecimal a2DocValue = BigDecimal.ZERO;
	
	

	/**
	 * @return the a2Invoicekey
	 */
	public String getA2Invoicekey() {
		return a2Invoicekey;
	}

	/**
	 * @param a2Invoicekey the a2Invoicekey to set
	 */
	public void setA2Invoicekey(String a2Invoicekey) {
		this.a2Invoicekey = a2Invoicekey;
	}

	/**
	 * @return the a2ActionTakenTimeStamp
	 */
	public Date getA2ActionTakenTimeStamp() {
		return a2ActionTakenTimeStamp;
	}

	/**
	 * @param a2ActionTakenTimeStamp the a2ActionTakenTimeStamp to set
	 */
	public void setA2ActionTakenTimeStamp(Date a2ActionTakenTimeStamp) {
		this.a2ActionTakenTimeStamp = a2ActionTakenTimeStamp;
	}

	/**
	 * @return the a2TableSection
	 */
	public String getA2TableSection() {
		return a2TableSection;
	}

	/**
	 * @param a2TableSection the a2TableSection to set
	 */
	public void setA2TableSection(String a2TableSection) {
		this.a2TableSection = a2TableSection;
	}

	/**
	 * @return the a2DocValue
	 */
	public BigDecimal getA2DocValue() {
		return a2DocValue;
	}

	/**
	 * @param a2DocValue the a2DocValue to set
	 */
	public void setA2DocValue(BigDecimal a2DocValue) {
		this.a2DocValue = a2DocValue;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the invoiceKey
	 */
	public String getInvoiceKey() {
		return invoiceKey;
	}

	/**
	 * @param invoiceKey
	 *            the invoiceKey to set
	 */
	public void setInvoiceKey(String invoiceKey) {
		this.invoiceKey = invoiceKey;
	}

	/**
	 * @return the a2Id
	 */
	public Integer getA2Id() {
		return a2Id;
	}

	/**
	 * @param a2Id
	 *            the a2Id to set
	 */
	public void setA2Id(Integer a2Id) {
		this.a2Id = a2Id;
	}

	/**
	 * @return the prId
	 */
	public Integer getPrId() {
		return prId;
	}

	/**
	 * @param prId
	 *            the prId to set
	 */
	public void setPrId(Integer prId) {
		this.prId = prId;
	}

	/**
	 * @return the a2BatchId
	 */
	public Integer getA2BatchId() {
		return a2BatchId;
	}

	/**
	 * @param a2BatchId
	 *            the a2BatchId to set
	 */
	public void setA2BatchId(Integer a2BatchId) {
		this.a2BatchId = a2BatchId;
	}

	/**
	 * @return the a2CheckSum
	 */
	public String getA2CheckSum() {
		return a2CheckSum;
	}

	/**
	 * @param a2CheckSum
	 *            the a2CheckSum to set
	 */
	public void setA2CheckSum(String a2CheckSum) {
		this.a2CheckSum = a2CheckSum;
	}

	/**
	 * @return the a2Sgstin
	 */
	public String getA2Sgstin() {
		return a2Sgstin;
	}

	/**
	 * @param a2Sgstin
	 *            the a2Sgstin to set
	 */
	public void setA2Sgstin(String a2Sgstin) {
		this.a2Sgstin = a2Sgstin;
	}

	/**
	 * @return the prSgstin
	 */
	public String getPrSgstin() {
		return prSgstin;
	}

	/**
	 * @param prSgstin
	 *            the prSgstin to set
	 */
	public void setPrSgstin(String prSgstin) {
		this.prSgstin = prSgstin;
	}

	/**
	 * @return the a2Cgstin
	 */
	public String getA2Cgstin() {
		return a2Cgstin;
	}

	/**
	 * @param a2Cgstin
	 *            the a2Cgstin to set
	 */
	public void setA2Cgstin(String a2Cgstin) {
		this.a2Cgstin = a2Cgstin;
	}

	/**
	 * @return the prCgstin
	 */
	public String getPrCgstin() {
		return prCgstin;
	}

	/**
	 * @param prCgstin
	 *            the prCgstin to set
	 */
	public void setPrCgstin(String prCgstin) {
		this.prCgstin = prCgstin;
	}

	/**
	 * @return the a2InvType
	 */
	public String getA2InvType() {
		return a2InvType;
	}

	/**
	 * @param a2InvType
	 *            the a2InvType to set
	 */
	public void setA2InvType(String a2InvType) {
		this.a2InvType = a2InvType;
	}

	/**
	 * @return the prInvType
	 */
	public String getPrInvType() {
		return prInvType;
	}

	/**
	 * @param prInvType
	 *            the prInvType to set
	 */
	public void setPrInvType(String prInvType) {
		this.prInvType = prInvType;
	}

	/**
	 * @return the a2SupplierInvNum
	 */
	public String getA2SupplierInvNum() {
		return a2SupplierInvNum;
	}

	/**
	 * @param a2SupplierInvNum
	 *            the a2SupplierInvNum to set
	 */
	public void setA2SupplierInvNum(String a2SupplierInvNum) {
		this.a2SupplierInvNum = a2SupplierInvNum;
	}

	/**
	 * @return the prSupplierInvNum
	 */
	public String getPrSupplierInvNum() {
		return prSupplierInvNum;
	}

	/**
	 * @param prSupplierInvNum
	 *            the prSupplierInvNum to set
	 */
	public void setPrSupplierInvNum(String prSupplierInvNum) {
		this.prSupplierInvNum = prSupplierInvNum;
	}

	/**
	 * @return the a2SupplierInvDate
	 */
	public Date getA2SupplierInvDate() {
		return a2SupplierInvDate;
	}

	/**
	 * @param a2SupplierInvDate
	 *            the a2SupplierInvDate to set
	 */
	public void setA2SupplierInvDate(Date a2SupplierInvDate) {
		this.a2SupplierInvDate = a2SupplierInvDate;
	}

	/**
	 * @return the prSupplierInvDate
	 */
	public Date getPrSupplierInvDate() {
		return prSupplierInvDate;
	}

	/**
	 * @param prSupplierInvDate
	 *            the prSupplierInvDate to set
	 */
	public void setPrSupplierInvDate(Date prSupplierInvDate) {
		this.prSupplierInvDate = prSupplierInvDate;
	}

	/**
	 * @return the a2Pos
	 */
	public Integer getA2Pos() {
		return a2Pos;
	}

	/**
	 * @param a2Pos
	 *            the a2Pos to set
	 */
	public void setA2Pos(Integer a2Pos) {
		this.a2Pos = a2Pos;
	}

	/**
	 * @return the prPos
	 */
	public Integer getPrPos() {
		return prPos;
	}

	/**
	 * @param prPos
	 *            the prPos to set
	 */
	public void setPrPos(Integer prPos) {
		this.prPos = prPos;
	}

	/**
	 * @return the a2TaxableValue
	 */
	public BigDecimal getA2TaxableValue() {
		return a2TaxableValue;
	}

	/**
	 * @param a2TaxableValue
	 *            the a2TaxableValue to set
	 */
	public void setA2TaxableValue(BigDecimal a2TaxableValue) {
		this.a2TaxableValue = a2TaxableValue;
	}

	/**
	 * @return the prTaxableValue
	 */
	public BigDecimal getPrTaxableValue() {
		return prTaxableValue;
	}

	/**
	 * @param prTaxableValue
	 *            the prTaxableValue to set
	 */
	public void setPrTaxableValue(BigDecimal prTaxableValue) {
		this.prTaxableValue = prTaxableValue;
	}

	/**
	 * @return the a2IgstAmount
	 */
	public BigDecimal getA2IgstAmount() {
		return a2IgstAmount;
	}

	/**
	 * @param a2IgstAmount
	 *            the a2IgstAmount to set
	 */
	public void setA2IgstAmount(BigDecimal a2IgstAmount) {
		this.a2IgstAmount = a2IgstAmount;
	}

	/**
	 * @return the prIgstAmount
	 */
	public BigDecimal getPrIgstAmount() {
		return prIgstAmount;
	}

	/**
	 * @param prIgstAmount
	 *            the prIgstAmount to set
	 */
	public void setPrIgstAmount(BigDecimal prIgstAmount) {
		this.prIgstAmount = prIgstAmount;
	}

	/**
	 * @return the a2CgstAmount
	 */
	public BigDecimal getA2CgstAmount() {
		return a2CgstAmount;
	}

	/**
	 * @param a2CgstAmount
	 *            the a2CgstAmount to set
	 */
	public void setA2CgstAmount(BigDecimal a2CgstAmount) {
		this.a2CgstAmount = a2CgstAmount;
	}

	/**
	 * @return the prCgstAmount
	 */
	public BigDecimal getPrCgstAmount() {
		return prCgstAmount;
	}

	/**
	 * @param prCgstAmount
	 *            the prCgstAmount to set
	 */
	public void setPrCgstAmount(BigDecimal prCgstAmount) {
		this.prCgstAmount = prCgstAmount;
	}

	/**
	 * @return the a2SgstAmount
	 */
	public BigDecimal getA2SgstAmount() {
		return a2SgstAmount;
	}

	/**
	 * @param a2SgstAmount
	 *            the a2SgstAmount to set
	 */
	public void setA2SgstAmount(BigDecimal a2SgstAmount) {
		this.a2SgstAmount = a2SgstAmount;
	}

	/**
	 * @return the prSgstAmount
	 */
	public BigDecimal getPrSgstAmount() {
		return prSgstAmount;
	}

	/**
	 * @param prSgstAmount
	 *            the prSgstAmount to set
	 */
	public void setPrSgstAmount(BigDecimal prSgstAmount) {
		this.prSgstAmount = prSgstAmount;
	}

	/**
	 * @return the a2CessAmount
	 */
	public BigDecimal getA2CessAmount() {
		return a2CessAmount;
	}

	/**
	 * @param a2CessAmount
	 *            the a2CessAmount to set
	 */
	public void setA2CessAmount(BigDecimal a2CessAmount) {
		this.a2CessAmount = a2CessAmount;
	}

	/**
	 * @return the prCessAmount
	 */
	public BigDecimal getPrCessAmount() {
		return prCessAmount;
	}

	/**
	 * @param prCessAmount
	 *            the prCessAmount to set
	 */
	public void setPrCessAmount(BigDecimal prCessAmount) {
		this.prCessAmount = prCessAmount;
	}

	/**
	 * @return the a2TaxPeriod
	 */
	public String getA2TaxPeriod() {
		return a2TaxPeriod;
	}

	/**
	 * @param a2TaxPeriod
	 *            the a2TaxPeriod to set
	 */
	public void setA2TaxPeriod(String a2TaxPeriod) {
		this.a2TaxPeriod = a2TaxPeriod;
	}

	/**
	 * @return the prTaxPeriod
	 */
	public String getPrTaxPeriod() {
		return prTaxPeriod;
	}

	/**
	 * @param prTaxPeriod
	 *            the prTaxPeriod to set
	 */
	public void setPrTaxPeriod(String prTaxPeriod) {
		this.prTaxPeriod = prTaxPeriod;
	}

	/**
	 * @return the isDeleteFlag
	 */
	public String getIsDeleteFlag() {
		return isDeleteFlag;
	}

	/**
	 * @param isDeleteFlag
	 *            the isDeleteFlag to set
	 */
	public void setIsDeleteFlag(String isDeleteFlag) {
		this.isDeleteFlag = isDeleteFlag;
	}

	/**
	 * @return the a2StatusFlag
	 */
	public String getA2StatusFlag() {
		return a2StatusFlag;
	}

	/**
	 * @param a2StatusFlag
	 *            the a2StatusFlag to set
	 */
	public void setA2StatusFlag(String a2StatusFlag) {
		this.a2StatusFlag = a2StatusFlag;
	}

	/**
	 * @return the prStatusFlag
	 */
	public String getPrStatusFlag() {
		return prStatusFlag;
	}

	/**
	 * @param prStatusFlag
	 *            the prStatusFlag to set
	 */
	public void setPrStatusFlag(String prStatusFlag) {
		this.prStatusFlag = prStatusFlag;
	}

	/**
	 * @return the a2ActionTaken
	 */
	public String getA2ActionTaken() {
		return a2ActionTaken;
	}

	/**
	 * @param a2ActionTaken
	 *            the a2ActionTaken to set
	 */
	public void setA2ActionTaken(String a2ActionTaken) {
		this.a2ActionTaken = a2ActionTaken;
	}

	/**
	 * @return the a2GetType
	 */
	public String getA2GetType() {
		return a2GetType;
	}

	/**
	 * @param a2GetType
	 *            the a2GetType to set
	 */
	public void setA2GetType(String a2GetType) {
		this.a2GetType = a2GetType;
	}

	/**
	 * @return the prTableSection
	 */
	public String getPrTableSection() {
		return prTableSection;
	}

	/**
	 * @param prTableSection
	 *            the prTableSection to set
	 */
	public void setPrTableSection(String prTableSection) {
		this.prTableSection = prTableSection;
	}

	/**
	 * @return the a2ExistFlag
	 */
	public Boolean getA2ExistFlag() {
		return a2ExistFlag;
	}

	/**
	 * @param a2ExistFlag
	 *            the a2ExistFlag to set
	 */
	public void setA2ExistFlag(Boolean a2ExistFlag) {
		this.a2ExistFlag = a2ExistFlag;
	}

	/**
	 * @return the prExistFlag
	 */
	public Boolean getPrExistFlag() {
		return prExistFlag;
	}

	/**
	 * @param prExistFlag
	 *            the prExistFlag to set
	 */
	public void setPrExistFlag(Boolean prExistFlag) {
		this.prExistFlag = prExistFlag;
	}

	/**
	 * @return the prLockFlag
	 */
	public String getPrLockFlag() {
		return prLockFlag;
	}

	/**
	 * @param prLockFlag
	 *            the prLockFlag to set
	 */
	public void setPrLockFlag(String prLockFlag) {
		this.prLockFlag = prLockFlag;
	}

	/**
	 * @return the a2LockFlag
	 */
	public String getA2LockFlag() {
		return a2LockFlag;
	}

	/**
	 * @param a2LockFlag
	 *            the a2LockFlag to set
	 */
	public void setA2LockFlag(String a2LockFlag) {
		this.a2LockFlag = a2LockFlag;
	}

	/**
	 * @return the a2GstinAction
	 */
	public String getA2GstinAction() {
		return a2GstinAction;
	}

	/**
	 * @param a2GstinAction
	 *            the a2GstinAction to set
	 */
	public void setA2GstinAction(String a2GstinAction) {
		this.a2GstinAction = a2GstinAction;
	}

	/**
	 * @return the a2DerivedRetPeriod
	 */
	public Integer getA2DerivedRetPeriod() {
		return a2DerivedRetPeriod;
	}

	/**
	 * @param a2DerivedRetPeriod
	 *            the a2DerivedRetPeriod to set
	 */
	public void setA2DerivedRetPeriod(Integer a2DerivedRetPeriod) {
		this.a2DerivedRetPeriod = a2DerivedRetPeriod;
	}

	/**
	 * @return the a2Isdelete
	 */
	public Boolean getA2Isdelete() {
		return a2Isdelete;
	}

	/**
	 * @param a2Isdelete
	 *            the a2Isdelete to set
	 */
	public void setA2Isdelete(Boolean a2Isdelete) {
		this.a2Isdelete = a2Isdelete;
	}

	/**
	 * @return the prIsDelete
	 */
	public Boolean getPrIsDelete() {
		return prIsDelete;
	}

	/**
	 * @param prIsDelete
	 *            the prIsDelete to set
	 */
	public void setPrIsDelete(Boolean prIsDelete) {
		this.prIsDelete = prIsDelete;
	}

	/**
	 * @return the a2LinkId
	 */
	public String getA2LinkId() {
		return a2LinkId;
	}

	/**
	 * @param a2LinkId
	 *            the a2LinkId to set
	 */
	public void setA2LinkId(String a2LinkId) {
		this.a2LinkId = a2LinkId;
	}

	/**
	 * @return the prLinkId
	 */
	public String getPrLinkId() {
		return prLinkId;
	}

	/**
	 * @param prLinkId
	 *            the prLinkId to set
	 */
	public void setPrLinkId(String prLinkId) {
		this.prLinkId = prLinkId;
	}

	/**
	 * @return the reconStatus
	 */
	public String getReconStatus() {
		return reconStatus;
	}

	/**
	 * @param reconStatus
	 *            the reconStatus to set
	 */
	public void setReconStatus(String reconStatus) {
		this.reconStatus = reconStatus;
	}

	/**
	 * @return the a2InvoiceKey
	 */
	public String getA2InvoiceKey() {
		return a2Invoicekey;
	}

	/**
	 * @param a2InvoiceKey
	 *            the a2InvoiceKey to set
	 */
	public void setA2InvoiceKey(String a2InvoiceKey) {
		this.a2Invoicekey = a2InvoiceKey;
	}

	/**
	 * @return the prInvoicekey
	 */
	public String getPrInvoicekey() {
		return prInvoicekey;
	}

	/**
	 * @param prInvoicekey
	 *            the prInvoicekey to set
	 */
	public void setPrInvoicekey(String prInvoicekey) {
		this.prInvoicekey = prInvoicekey;
	}

	/**
	 * @return the a2SupplierPan
	 */
	public String getA2SupplierPan() {
		return a2SupplierPan;
	}

	/**
	 * @param a2SupplierPan
	 *            the a2SupplierPan to set
	 */
	public void setA2SupplierPan(String a2SupplierPan) {
		this.a2SupplierPan = a2SupplierPan;
	}

	/**
	 * @return the prSupplierPan
	 */
	public String getPrSupplierPan() {
		return prSupplierPan;
	}

	/**
	 * @param prSupplierPan
	 *            the prSupplierPan to set
	 */
	public void setPrSupplierPan(String prSupplierPan) {
		this.prSupplierPan = prSupplierPan;
	}

	/**
	 * @return the a2TaxPayableValue
	 */
	public Integer getA2TaxPayableValue() {
		return a2TaxPayableValue;
	}

	/**
	 * @param a2TaxPayableValue
	 *            the a2TaxPayableValue to set
	 */
	public void setA2TaxPayableValue(Integer a2TaxPayableValue) {
		this.a2TaxPayableValue = a2TaxPayableValue;
	}

	/**
	 * @return the prTaxPayableValue
	 */
	public Integer getPrTaxPayableValue() {
		return prTaxPayableValue;
	}

	/**
	 * @param prTaxPayableValue
	 *            the prTaxPayableValue to set
	 */
	public void setPrTaxPayableValue(Integer prTaxPayableValue) {
		this.prTaxPayableValue = prTaxPayableValue;
	}

	/**
	 * @return the a2RecepientPan
	 */
	public String getA2RecepientPan() {
		return a2RecepientPan;
	}

	/**
	 * @param a2RecepientPan
	 *            the a2RecepientPan to set
	 */
	public void setA2RecepientPan(String a2RecepientPan) {
		this.a2RecepientPan = a2RecepientPan;
	}

	/**
	 * @return the prRecepientPan
	 */
	public String getPrRecepientPan() {
		return prRecepientPan;
	}

	/**
	 * @param prRecepientPan
	 *            the prRecepientPan to set
	 */
	public void setPrRecepientPan(String prRecepientPan) {
		this.prRecepientPan = prRecepientPan;
	}

	/**
	 * @return the eTaxableValue
	 */
	public BigDecimal geteTaxableValue() {
		return eTaxableValue;
	}

	/**
	 * @param eTaxableValue
	 *            the eTaxableValue to set
	 */
	public void seteTaxableValue(BigDecimal eTaxableValue) {
		this.eTaxableValue = eTaxableValue;
	}

	/**
	 * @return the eIgst
	 */
	public BigDecimal geteIgst() {
		return eIgst;
	}

	/**
	 * @param eIgst
	 *            the eIgst to set
	 */
	public void seteIgst(BigDecimal eIgst) {
		this.eIgst = eIgst;
	}

	/**
	 * @return the eCgst
	 */
	public BigDecimal geteCgst() {
		return eCgst;
	}

	/**
	 * @param eCgst
	 *            the eCgst to set
	 */
	public void seteCgst(BigDecimal eCgst) {
		this.eCgst = eCgst;
	}

	/**
	 * @return the eSgst
	 */
	public BigDecimal geteSgst() {
		return eSgst;
	}

	/**
	 * @param eSgst
	 *            the eSgst to set
	 */
	public void seteSgst(BigDecimal eSgst) {
		this.eSgst = eSgst;
	}

	/**
	 * @return the eCess
	 */
	public BigDecimal geteCess() {
		return eCess;
	}

	/**
	 * @param eCess
	 *            the eCess to set
	 */
	public void seteCess(BigDecimal eCess) {
		this.eCess = eCess;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "A2PrBigtableEntity [id=" + id + ", invoiceKey=" + invoiceKey
				+ ", a2Id=" + a2Id + ", prId=" + prId + ", a2BatchId="
				+ a2BatchId + ", a2CheckSum=" + a2CheckSum + ", a2Sgstin="
				+ a2Sgstin + ", prSgstin=" + prSgstin + ", a2Cgstin=" + a2Cgstin
				+ ", prCgstin=" + prCgstin + ", a2InvType=" + a2InvType
				+ ", prInvType=" + prInvType + ", a2SupplierInvNum="
				+ a2SupplierInvNum + ", prSupplierInvNum=" + prSupplierInvNum
				+ ", a2SupplierInvDate=" + a2SupplierInvDate
				+ ", prSupplierInvDate=" + prSupplierInvDate + ", a2Pos="
				+ a2Pos + ", prPos=" + prPos + ", a2TaxableValue="
				+ a2TaxableValue + ", prTaxableValue=" + prTaxableValue
				+ ", a2IgstAmount=" + a2IgstAmount + ", prIgstAmount="
				+ prIgstAmount + ", a2CgstAmount=" + a2CgstAmount
				+ ", prCgstAmount=" + prCgstAmount + ", a2SgstAmount="
				+ a2SgstAmount + ", prSgstAmount=" + prSgstAmount
				+ ", a2CessAmount=" + a2CessAmount + ", prCessAmount="
				+ prCessAmount + ", a2TaxPeriod=" + a2TaxPeriod
				+ ", prTaxPeriod=" + prTaxPeriod + ", isDeleteFlag="
				+ isDeleteFlag + ", a2StatusFlag=" + a2StatusFlag
				+ ", prStatusFlag=" + prStatusFlag + ", a2ActionTaken="
				+ a2ActionTaken + ", a2GetType=" + a2GetType
				+ ", prTableSection=" + prTableSection + ", a2ExistFlag="
				+ a2ExistFlag + ", prExistFlag=" + prExistFlag + ", prLockFlag="
				+ prLockFlag + ", a2LockFlag=" + a2LockFlag + ", a2GstinAction="
				+ a2GstinAction + ", a2DerivedRetPeriod=" + a2DerivedRetPeriod
				+ ", a2Isdelete=" + a2Isdelete + ", prIsDelete=" + prIsDelete
				+ ", a2LinkId=" + a2LinkId + ", prLinkId=" + prLinkId
				+ ", reconStatus=" + reconStatus + ", a2InvoiceKey="
				+ a2Invoicekey + ", prInvoicekey=" + prInvoicekey
				+ ", a2SupplierPan=" + a2SupplierPan + ", prSupplierPan="
				+ prSupplierPan + ", a2TaxPayableValue=" + a2TaxPayableValue
				+ ", prTaxPayableValue=" + prTaxPayableValue
				+ ", a2RecepientPan=" + a2RecepientPan + ", prRecepientPan="
				+ prRecepientPan + ", eTaxableValue=" + eTaxableValue
				+ ", eIgst=" + eIgst + ", eCgst=" + eCgst + ", eSgst=" + eSgst
				+ ", eCess=" + eCess + "]";
	}

}
