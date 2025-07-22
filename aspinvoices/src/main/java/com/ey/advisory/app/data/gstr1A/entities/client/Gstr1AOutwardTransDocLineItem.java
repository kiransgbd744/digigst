package com.ey.advisory.app.data.gstr1A.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.Cascade;

import com.ey.advisory.app.data.entities.client.TransDocLineItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "ANX_OUTWARD_DOC_ITEM_1A")
public class Gstr1AOutwardTransDocLineItem extends TransDocLineItem {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ANX_OUTWARD_DOC_ITEM_SEQ_1A", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID", nullable = false)
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Expose
	@SerializedName("itemNo")
	@Column(name = "ITM_NO")
	protected String lineNo;

	@Expose
	@SerializedName("fob")
	@Column(name = "FOB")
	protected BigDecimal fob;

	@Expose
	@SerializedName("errCodes")
	@Column(name = "ERROR_CODES")
	private String errCodes;

	@Expose
	@SerializedName("infoCodes")
	@Column(name = "INFORMATION_CODES")
	private String infoCodes;

	@Expose
	@SerializedName("exportDuty")
	@Column(name = "EXPORT_DUTY")
	protected BigDecimal exportDuty;

	@Expose
	@SerializedName("itcFlag")
	@Column(name = "ITC_FLAG")
	protected String itcFlag;

	@Column(name = "MEMO_VALUE_IGST")
	protected BigDecimal memoValIgst;

	@Column(name = "MEMO_VALUE_CGST")
	protected BigDecimal memoValCgst;

	@Column(name = "MEMO_VALUE_SGST")
	protected BigDecimal memoValSgst;

	@Column(name = "MEMO_VALUE_CESS")
	protected BigDecimal memoValCess;

	@Column(name = "IGST_RET1_IMPACT")
	protected BigDecimal ret1IgstImpact;

	@Column(name = "CGST_RET1_IMPACT")
	protected BigDecimal ret1CgstImpact;

	@Column(name = "SGST_RET1_IMPACT")
	protected BigDecimal ret1SgstImpact;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private Date createdDate;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private Date modifiedDate;

	// Start - Fix for Defect Error code "ER0037"
	@Column(name = "SALES_ORGANIZATION")
	protected String salesOrgnization;

	@Column(name = "DISTRIBUTION_CHANNEL")
	protected String distributionChannel;

	@Column(name = "CUST_GSTIN")
	protected String cgstin;

	@Column(name = "ORIGINAL_CUST_GSTIN")
	protected String origCgstin;

	@Column(name = "SHIP_BILL_NUM")
	protected String shippingBillNo;

	@Column(name = "SHIP_BILL_DATE")
	protected LocalDate shippingBillDate;

	@Column(name = "ACCOUNTING_VOUCHER_NUM")
	protected String accountingVoucherNumber;

	@Column(name = "ACCOUNTING_VOUCHER_DATE")
	protected LocalDate accountingVoucherDate;

	@Column(name = "CANCEL_REASON")
	protected String cancellationReason;

	@Column(name = "SUPP_LEGAL_NAME")
	protected String supplierLegalName;

	@Column(name = "SUPP_BUILDING_NUM")
	protected String supplierBuildingNumber;

	@Column(name = "SUPP_LOCATION")
	protected String supplierLocation;

	@Column(name = "SUPP_PINCODE")
	protected Integer supplierPincode;

	@Column(name = "DISPATCHER_PINCODE")
	protected Integer dispatcherPincode;

	@Column(name = "SHIP_TO_PINCODE")
	protected Integer shipToPincode;

	// End - Fix for Defect Error code "ER0037"

	@Expose
	@SerializedName("originalInvoiceNumber")
	@Column(name = "ORG_INV_NUM")
	private String originalInvoiceNumber;

	@Expose
	@SerializedName("originalInvoiceDate")
	@Column(name = "ORG_INV_DATE")
	private LocalDate originalInvoiceDate;

	@Expose
	@SerializedName("preceedingInvNo")
	@Column(name = "PRECEEDING_INV_NUM")
	private String preceedingInvoiceNumber;

	@Expose
	@SerializedName("preceedingInvDate")
	@Column(name = "PRECEEDING_INV_DATE")
	private LocalDate preceedingInvoiceDate;

	@Expose
	@SerializedName("supportingDocBase64")
	@Column(name = "SUPPORTING_DOC_BASE64")
	private String supportingDocBase64;

	@Expose
	@SerializedName("custPoRefNo")
	@Column(name = "CUST_PO_REF_NUM")
	protected String customerPOReferenceNumber;

	@Expose
	@SerializedName("distance")
	@Column(name = "DISTANCE")
	protected BigDecimal distance;

	@Expose
	@SerializedName("custPoRefDate")
	@Column(name = "CUST_PO_REF_DATE")
	protected LocalDate customerPOReferenceDate;

	@Column(name = "SUPP_BUILDING_NAME")
	protected String supplierBuildingName;

	@Column(name = "ONB_LINE_ITEM_AMT")
	private BigDecimal onbLineItemAmt;

	@Column(name = "PRECEEDING_INV_AMT")
	protected BigDecimal preceedingInvAmt;

	@Column(name = "PRECEEDING_TAXABLE_VALUE")
	protected BigDecimal preceedingTaxableVal;

	@Expose
	@SerializedName("itemUqcUser")
	@Column(name = "ITM_UQC_USER")
	protected String itemUqcUser;

	@Expose
	@SerializedName("itemQtyUser")
	@Column(name = "QTY_USER")
	protected BigDecimal itemQtyUser;

	
	@Expose
	@SerializedName("gstr1TableNo")
	@Column(name = "ITM_TABLE_SECTION")
	protected String itmTableType;

	@Expose
	@SerializedName("gstr1SubCategory")
	@Column(name = "ITM_TAX_DOC_TYPE")
	protected String itmGstnBifurcation;
	
	@Expose
	@SerializedName("attribDtls")
	@OneToMany(mappedBy = "attDetails", fetch = FetchType.EAGER)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<Gstr1AAttributeDetails> attribDtls = new ArrayList<>();

	@ManyToOne // (fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "DOC_HEADER_ID", referencedColumnName = "ID", nullable = false)
	private Gstr1AOutwardTransDocument document;

	public Gstr1AOutwardTransDocLineItem() {
		// Currently, there is not additional data members here. We can add
		// if additional data has to be stored as part of outward document
		// line item.
	}

	
	
	/**
	 * @return the lineNo
	 */
	public String getLineNo() {
		return lineNo;
	}

	/**
	 * @param lineNo
	 *            the lineNo to set
	 */
	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}

	/**
	 * @return the fob
	 */
	public BigDecimal getFob() {
		return fob;
	}

	/**
	 * @param fob
	 *            the fob to set
	 */
	public void setFob(BigDecimal fob) {
		this.fob = fob;
	}

	/**
	 * @return the errCodes
	 */
	public String getErrCodes() {
		return errCodes;
	}

	/**
	 * @param errCodes
	 *            the errCodes to set
	 */
	public void setErrCodes(String errCodes) {
		this.errCodes = errCodes;
	}

	/**
	 * @return the infoCodes
	 */
	public String getInfoCodes() {
		return infoCodes;
	}

	/**
	 * @param infoCodes
	 *            the infoCodes to set
	 */
	public void setInfoCodes(String infoCodes) {
		this.infoCodes = infoCodes;
	}

	/**
	 * @return the exportDuty
	 */
	public BigDecimal getExportDuty() {
		return exportDuty;
	}

	/**
	 * @param exportDuty
	 *            the exportDuty to set
	 */
	public void setExportDuty(BigDecimal exportDuty) {
		this.exportDuty = exportDuty;
	}

	/**
	 * @return the itcFlag
	 */
	public String getItcFlag() {
		return itcFlag;
	}

	/**
	 * @param itcFlag
	 *            the itcFlag to set
	 */
	public void setItcFlag(String itcFlag) {
		this.itcFlag = itcFlag;
	}

	/**
	 * @return the memoValIgst
	 */
	public BigDecimal getMemoValIgst() {
		return memoValIgst;
	}

	/**
	 * @param memoValIgst
	 *            the memoValIgst to set
	 */
	public void setMemoValIgst(BigDecimal memoValIgst) {
		this.memoValIgst = memoValIgst;
	}

	/**
	 * @return the memoValCgst
	 */
	public BigDecimal getMemoValCgst() {
		return memoValCgst;
	}

	/**
	 * @param memoValCgst
	 *            the memoValCgst to set
	 */
	public void setMemoValCgst(BigDecimal memoValCgst) {
		this.memoValCgst = memoValCgst;
	}

	/**
	 * @return the memoValSgst
	 */
	public BigDecimal getMemoValSgst() {
		return memoValSgst;
	}

	/**
	 * @param memoValSgst
	 *            the memoValSgst to set
	 */
	public void setMemoValSgst(BigDecimal memoValSgst) {
		this.memoValSgst = memoValSgst;
	}

	/**
	 * @return the memoValCess
	 */
	public BigDecimal getMemoValCess() {
		return memoValCess;
	}

	/**
	 * @param memoValCess
	 *            the memoValCess to set
	 */
	public void setMemoValCess(BigDecimal memoValCess) {
		this.memoValCess = memoValCess;
	}

	/**
	 * @return the ret1IgstImpact
	 */
	public BigDecimal getRet1IgstImpact() {
		return ret1IgstImpact;
	}

	/**
	 * @param ret1IgstImpact
	 *            the ret1IgstImpact to set
	 */
	public void setRet1IgstImpact(BigDecimal ret1IgstImpact) {
		this.ret1IgstImpact = ret1IgstImpact;
	}

	/**
	 * @return the ret1CgstImpact
	 */
	public BigDecimal getRet1CgstImpact() {
		return ret1CgstImpact;
	}

	/**
	 * @param ret1CgstImpact
	 *            the ret1CgstImpact to set
	 */
	public void setRet1CgstImpact(BigDecimal ret1CgstImpact) {
		this.ret1CgstImpact = ret1CgstImpact;
	}

	/**
	 * @return the ret1SgstImpact
	 */
	public BigDecimal getRet1SgstImpact() {
		return ret1SgstImpact;
	}

	/**
	 * @param ret1SgstImpact
	 *            the ret1SgstImpact to set
	 */
	public void setRet1SgstImpact(BigDecimal ret1SgstImpact) {
		this.ret1SgstImpact = ret1SgstImpact;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy
	 *            the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate
	 *            the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the salesOrgnization
	 */
	public String getSalesOrgnization() {
		return salesOrgnization;
	}

	/**
	 * @param salesOrgnization
	 *            the salesOrgnization to set
	 */
	public void setSalesOrgnization(String salesOrgnization) {
		this.salesOrgnization = salesOrgnization;
	}

	/**
	 * @return the distributionChannel
	 */
	public String getDistributionChannel() {
		return distributionChannel;
	}

	/**
	 * @param distributionChannel
	 *            the distributionChannel to set
	 */
	public void setDistributionChannel(String distributionChannel) {
		this.distributionChannel = distributionChannel;
	}

	/**
	 * @return the cgstin
	 */
	public String getCgstin() {
		return cgstin;
	}

	/**
	 * @param cgstin
	 *            the cgstin to set
	 */
	public void setCgstin(String cgstin) {
		this.cgstin = cgstin;
	}

	/**
	 * @return the origCgstin
	 */
	public String getOrigCgstin() {
		return origCgstin;
	}

	/**
	 * @param origCgstin
	 *            the origCgstin to set
	 */
	public void setOrigCgstin(String origCgstin) {
		this.origCgstin = origCgstin;
	}

	/**
	 * @return the shippingBillNo
	 */
	public String getShippingBillNo() {
		return shippingBillNo;
	}

	/**
	 * @param shippingBillNo
	 *            the shippingBillNo to set
	 */
	public void setShippingBillNo(String shippingBillNo) {
		this.shippingBillNo = shippingBillNo;
	}

	/**
	 * @return the shippingBillDate
	 */
	public LocalDate getShippingBillDate() {
		return shippingBillDate;
	}

	/**
	 * @param shippingBillDate
	 *            the shippingBillDate to set
	 */
	public void setShippingBillDate(LocalDate shippingBillDate) {
		this.shippingBillDate = shippingBillDate;
	}

	/**
	 * @return the accountingVoucherNumber
	 */
	public String getAccountingVoucherNumber() {
		return accountingVoucherNumber;
	}

	/**
	 * @param accountingVoucherNumber
	 *            the accountingVoucherNumber to set
	 */
	public void setAccountingVoucherNumber(String accountingVoucherNumber) {
		this.accountingVoucherNumber = accountingVoucherNumber;
	}

	/**
	 * @return the accountingVoucherDate
	 */
	public LocalDate getAccountingVoucherDate() {
		return accountingVoucherDate;
	}

	/**
	 * @param accountingVoucherDate
	 *            the accountingVoucherDate to set
	 */
	public void setAccountingVoucherDate(LocalDate accountingVoucherDate) {
		this.accountingVoucherDate = accountingVoucherDate;
	}

	/**
	 * @return the cancellationReason
	 */
	public String getCancellationReason() {
		return cancellationReason;
	}

	/**
	 * @param cancellationReason
	 *            the cancellationReason to set
	 */
	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}

	/**
	 * @return the supplierLegalName
	 */
	public String getSupplierLegalName() {
		return supplierLegalName;
	}

	/**
	 * @param supplierLegalName
	 *            the supplierLegalName to set
	 */
	public void setSupplierLegalName(String supplierLegalName) {
		this.supplierLegalName = supplierLegalName;
	}

	/**
	 * @return the supplierBuildingNumber
	 */
	public String getSupplierBuildingNumber() {
		return supplierBuildingNumber;
	}

	/**
	 * @param supplierBuildingNumber
	 *            the supplierBuildingNumber to set
	 */
	public void setSupplierBuildingNumber(String supplierBuildingNumber) {
		this.supplierBuildingNumber = supplierBuildingNumber;
	}

	/**
	 * @return the supplierLocation
	 */
	public String getSupplierLocation() {
		return supplierLocation;
	}

	/**
	 * @param supplierLocation
	 *            the supplierLocation to set
	 */
	public void setSupplierLocation(String supplierLocation) {
		this.supplierLocation = supplierLocation;
	}

	/**
	 * @return the supplierPincode
	 */
	public Integer getSupplierPincode() {
		return supplierPincode;
	}

	/**
	 * @param supplierPincode
	 *            the supplierPincode to set
	 */
	public void setSupplierPincode(Integer supplierPincode) {
		this.supplierPincode = supplierPincode;
	}

	/**
	 * @return the originalInvoiceNumber
	 */
	public String getOriginalInvoiceNumber() {
		return originalInvoiceNumber;
	}

	/**
	 * @param originalInvoiceNumber
	 *            the originalInvoiceNumber to set
	 */
	public void setOriginalInvoiceNumber(String originalInvoiceNumber) {
		this.originalInvoiceNumber = originalInvoiceNumber;
	}

	/**
	 * @return the originalInvoiceDate
	 */
	public LocalDate getOriginalInvoiceDate() {
		return originalInvoiceDate;
	}

	/**
	 * @param originalInvoiceDate
	 *            the originalInvoiceDate to set
	 */
	public void setOriginalInvoiceDate(LocalDate originalInvoiceDate) {
		this.originalInvoiceDate = originalInvoiceDate;
	}

	/**
	 * @return the preceedingInvoiceNumber
	 */
	public String getPreceedingInvoiceNumber() {
		return preceedingInvoiceNumber;
	}

	/**
	 * @param preceedingInvoiceNumber
	 *            the preceedingInvoiceNumber to set
	 */
	public void setPreceedingInvoiceNumber(String preceedingInvoiceNumber) {
		this.preceedingInvoiceNumber = preceedingInvoiceNumber;
	}

	/**
	 * @return the preceedingInvoiceDate
	 */
	public LocalDate getPreceedingInvoiceDate() {
		return preceedingInvoiceDate;
	}

	/**
	 * @param preceedingInvoiceDate
	 *            the preceedingInvoiceDate to set
	 */
	public void setPreceedingInvoiceDate(LocalDate preceedingInvoiceDate) {
		this.preceedingInvoiceDate = preceedingInvoiceDate;
	}

	/**
	 * @return the supportingDocBase64
	 */
	public String getSupportingDocBase64() {
		return supportingDocBase64;
	}

	/**
	 * @param supportingDocBase64
	 *            the supportingDocBase64 to set
	 */
	public void setSupportingDocBase64(String supportingDocBase64) {
		this.supportingDocBase64 = supportingDocBase64;
	}

	/**
	 * @return the customerPOReferenceNumber
	 */
	public String getCustomerPOReferenceNumber() {
		return customerPOReferenceNumber;
	}

	/**
	 * @param customerPOReferenceNumber
	 *            the customerPOReferenceNumber to set
	 */
	public void setCustomerPOReferenceNumber(String customerPOReferenceNumber) {
		this.customerPOReferenceNumber = customerPOReferenceNumber;
	}

	/**
	 * @return the customerPOReferenceDate
	 */
	public LocalDate getCustomerPOReferenceDate() {
		return customerPOReferenceDate;
	}

	/**
	 * @param customerPOReferenceDate
	 *            the customerPOReferenceDate to set
	 */
	public void setCustomerPOReferenceDate(LocalDate customerPOReferenceDate) {
		this.customerPOReferenceDate = customerPOReferenceDate;
	}

	/**
	 * @return the supplierBuildingName
	 */
	public String getSupplierBuildingName() {
		return supplierBuildingName;
	}

	/**
	 * @param supplierBuildingName
	 *            the supplierBuildingName to set
	 */
	public void setSupplierBuildingName(String supplierBuildingName) {
		this.supplierBuildingName = supplierBuildingName;
	}

	/**
	 * @return the onbLineItemAmt
	 */
	public BigDecimal getOnbLineItemAmt() {
		return onbLineItemAmt;
	}

	/**
	 * @param onbLineItemAmt
	 *            the onbLineItemAmt to set
	 */
	public void setOnbLineItemAmt(BigDecimal onbLineItemAmt) {
		this.onbLineItemAmt = onbLineItemAmt;
	}

	/**
	 * @return the preceedingInvAmt
	 */
	public BigDecimal getPreceedingInvAmt() {
		return preceedingInvAmt;
	}

	/**
	 * @param preceedingInvAmt
	 *            the preceedingInvAmt to set
	 */
	public void setPreceedingInvAmt(BigDecimal preceedingInvAmt) {
		this.preceedingInvAmt = preceedingInvAmt;
	}

	/**
	 * @return the preceedingTaxableVal
	 */
	public BigDecimal getPreceedingTaxableVal() {
		return preceedingTaxableVal;
	}

	/**
	 * @param preceedingTaxableVal
	 *            the preceedingTaxableVal to set
	 */
	public void setPreceedingTaxableVal(BigDecimal preceedingTaxableVal) {
		this.preceedingTaxableVal = preceedingTaxableVal;
	}

	/**
	 * @return the attribDtls
	 */
	public List<Gstr1AAttributeDetails> getAttribDtls() {
		return attribDtls;
	}

	/**
	 * @param attribDtls
	 *            the attribDtls to set
	 */
	public void setAttribDtls(List<Gstr1AAttributeDetails> attribDtls) {
		this.attribDtls = attribDtls;
	}

	/**
	 * @return the document
	 */
	public Gstr1AOutwardTransDocument getDocument() {
		return document;
	}

	/**
	 * @param document
	 *            the document to set
	 */
	public void setDocument(Gstr1AOutwardTransDocument document) {
		this.document = document;
	}

	/**
	 * @return the dispatcherPincode
	 */
	public Integer getDispatcherPincode() {
		return dispatcherPincode;
	}

	/**
	 * @param dispatcherPincode
	 *            the dispatcherPincode to set
	 */
	public void setDispatcherPincode(Integer dispatcherPincode) {
		this.dispatcherPincode = dispatcherPincode;
	}

	/**
	 * @return the shipToPincode
	 */
	public Integer getShipToPincode() {
		return shipToPincode;
	}

	/**
	 * @param shipToPincode
	 *            the shipToPincode to set
	 */
	public void setShipToPincode(Integer shipToPincode) {
		this.shipToPincode = shipToPincode;
	}

	/**
	 * @return the distance
	 */
	public BigDecimal getDistance() {
		return distance;
	}

	/**
	 * @param distance
	 *            the distance to set
	 */
	public void setDistance(BigDecimal distance) {
		this.distance = distance;
	}

	/**
	 * @return the itemUqcUser
	 */
	public String getItemUqcUser() {
		return itemUqcUser;
	}


	public String getItmTableType() {
		return itmTableType;
	}

	public void setItmTableType(String itmTableType) {
		this.itmTableType = itmTableType;
	}

	public String getItmGstnBifurcation() {
		return itmGstnBifurcation;
	}

	public void setItmGstnBifurcation(String itmGstnBifurcation) {
		this.itmGstnBifurcation = itmGstnBifurcation;
	}

	/**
	 * @param itemUqcUser
	 *            the itemUqcUser to set
	 */
	public void setItemUqcUser(String itemUqcUser) {
		this.itemUqcUser = itemUqcUser;
	}

	/**
	 * @return the itemQtyUser
	 */
	public BigDecimal getItemQtyUser() {
		return itemQtyUser;
	}

	/**
	 * @param itemQtyUser
	 *            the itemQtyUser to set
	 */
	public void setItemQtyUser(BigDecimal itemQtyUser) {
		this.itemQtyUser = itemQtyUser;
	}

	@Override
	public String toString() {
		return "OutwardTransDocLineItem [id=" + id + ", lineNo=" + lineNo
				+ ", fob=" + fob + ", errCodes=" + errCodes + ", infoCodes="
				+ infoCodes + ", exportDuty=" + exportDuty + ", itcFlag="
				+ itcFlag + ", memoValIgst=" + memoValIgst + ", memoValCgst="
				+ memoValCgst + ", memoValSgst=" + memoValSgst
				+ ", memoValCess=" + memoValCess + ", ret1IgstImpact="
				+ ret1IgstImpact + ", ret1CgstImpact=" + ret1CgstImpact
				+ ", ret1SgstImpact=" + ret1SgstImpact + ", createdBy="
				+ createdBy + ", createdDate=" + createdDate + ", modifiedBy="
				+ modifiedBy + ", modifiedDate=" + modifiedDate
				+ ", salesOrgnization=" + salesOrgnization
				+ ", distributionChannel=" + distributionChannel + ", cgstin="
				+ cgstin + ", origCgstin=" + origCgstin + ", shippingBillNo="
				+ shippingBillNo + ", shippingBillDate=" + shippingBillDate
				+ ", accountingVoucherNumber=" + accountingVoucherNumber
				+ ", accountingVoucherDate=" + accountingVoucherDate
				+ ", cancellationReason=" + cancellationReason
				+ ", supplierLegalName=" + supplierLegalName
				+ ", supplierBuildingNumber=" + supplierBuildingNumber
				+ ", supplierLocation=" + supplierLocation
				+ ", supplierPincode=" + supplierPincode
				+ ", dispatcherPincode=" + dispatcherPincode
				+ ", shipToPincode=" + shipToPincode
				+ ", originalInvoiceNumber=" + originalInvoiceNumber
				+ ", originalInvoiceDate=" + originalInvoiceDate
				+ ", preceedingInvoiceNumber=" + preceedingInvoiceNumber
				+ ", preceedingInvoiceDate=" + preceedingInvoiceDate
				+ ", supportingDocBase64=" + supportingDocBase64
				+ ", customerPOReferenceNumber=" + customerPOReferenceNumber
				+ ", distance=" + distance + ", customerPOReferenceDate="
				+ customerPOReferenceDate + ", supplierBuildingName="
				+ supplierBuildingName + ", onbLineItemAmt=" + onbLineItemAmt
				+ ", preceedingInvAmt=" + preceedingInvAmt
				+ ", preceedingTaxableVal=" + preceedingTaxableVal
				+ ", itemUqcUser=" + itemUqcUser + ", itemQtyUser="
				+ itemQtyUser + ", attribDtls=" + attribDtls + "]";
	}

}
