/*package com.ey.advisory.jobs.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

*//**
 * 
 * @author Hemasundar.J
 *
 *//*
@Entity
@Table(name = "GST1_B2CS_TABLEVIEW", schema = "MASTER")
public class B2csViewPersistEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private int id;
	
	@Expose
	@SerializedName("invHeader")
	@Column(name = "INV_HEADER")
	private int invHeader;
	
	@Expose
	@SerializedName("supplyType")
	@Column(name = "SUPPLY_TYPE")
	private String supplyType;

	@Expose
	@SerializedName("taxDocType")
	@Column(name = "TAX_DOC_TYPE")
	private String taxDocType;

	@Expose
	@SerializedName("custGstn")
	@Column(name = "CUST_GSTN")
	private String custGstn;

	@Expose
	@SerializedName("invType")
	@Column(name = "INV_TYPE")
	private String invType;

	@Expose
	@SerializedName("cessRateSpecific")
	@Column(name = "CESS_RATE_SPECIFIC")
	private String cessRateSpecific;
	
	@Expose
	@SerializedName("cessAmtSpecific")
	@Column(name = "CESS_AMT_SPECIFIC")
	private String cessAmtSpecific;

	@Expose
	@SerializedName("cessRateAdavlorem")
	@Column(name = "CESS_RATE_ADVALOREM")
	private String cessRateAdavlorem;
	
	@Expose
	@SerializedName("cessAmtAdavlorem")
	@Column(name = "CESS_AMT_ADVALOREM")
	private String cessAmtAdavlorem;

	@Expose
	@SerializedName("itemType")
	@Column(name = "ITM_TYPE")
	private String itemType;

	
	@Expose
	@SerializedName("supplierGstn")
	@Column(name = "SUPPLIER_GSTN")
	private String supplierGstn;
	
//	@Expose
//	@SerializedName("originalInvPreiod")
//	@Column(name = "ORIGINAL_INV_PERIOD")
//	private String originalInvPreiod;
	
	@Expose
	@SerializedName("pos")
	@Column(name = "POS")
	private String pos;
	
	@Expose
	@SerializedName("ecomCustGstn")
	@Column(name = "ECOM_CUST_GSTN")
	private String ecomCustGstn;
	
	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	private int returnPeriod;
	
	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	private String igstAmt;
	
	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	private String cgstAmt;
	
	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	private String sgstAmt;
	
	@Expose
	@SerializedName("taxRate")
	@Column(name = "TAX_RATE")
	private String taxRate;
	
	@Expose
	@SerializedName("taxableAmt")
	@Column(name = "taxable_Amt")
	private BigDecimal taxableAmt;
	
	@Expose
	@SerializedName("uinorcopmposition")
	@Column(name = "UINORCOMPOSITION")
	private String uinorcopmposition;

	*//**
	 * @return the id
	 *//*
	public int getId() {
		return id;
	}

	*//**
	 * @param id the id to set
	 *//*
	public void setId(int id) {
		this.id = id;
	}

	*//**
	 * @return the invHeader
	 *//*
	public int getInvHeader() {
		return invHeader;
	}

	*//**
	 * @param invHeader the invHeader to set
	 *//*
	public void setInvHeader(int invHeader) {
		this.invHeader = invHeader;
	}

	*//**
	 * @return the supplyType
	 *//*
	public String getSupplyType() {
		return supplyType;
	}

	*//**
	 * @param supplyType the supplyType to set
	 *//*
	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	*//**
	 * @return the taxDocType
	 *//*
	public String getTaxDocType() {
		return taxDocType;
	}

	*//**
	 * @param taxDocType the taxDocType to set
	 *//*
	public void setTaxDocType(String taxDocType) {
		this.taxDocType = taxDocType;
	}

	*//**
	 * @return the custGstn
	 *//*
	public String getCustGstn() {
		return custGstn;
	}

	*//**
	 * @param custGstn the custGstn to set
	 *//*
	public void setCustGstn(String custGstn) {
		this.custGstn = custGstn;
	}

	*//**
	 * @return the invType
	 *//*
	public String getInvType() {
		return invType;
	}

	*//**
	 * @param invType the invType to set
	 *//*
	public void setInvType(String invType) {
		this.invType = invType;
	}

	*//**
	 * @return the cessRateSpecific
	 *//*
	public String getCessRateSpecific() {
		return cessRateSpecific;
	}

	*//**
	 * @param cessRateSpecific the cessRateSpecific to set
	 *//*
	public void setCessRateSpecific(String cessRateSpecific) {
		this.cessRateSpecific = cessRateSpecific;
	}

	*//**
	 * @return the cessAmtSpecific
	 *//*
	public String getCessAmtSpecific() {
		return cessAmtSpecific;
	}

	*//**
	 * @param cessAmtSpecific the cessAmtSpecific to set
	 *//*
	public void setCessAmtSpecific(String cessAmtSpecific) {
		this.cessAmtSpecific = cessAmtSpecific;
	}

	*//**
	 * @return the cessRateAdavlorem
	 *//*
	public String getCessRateAdavlorem() {
		return cessRateAdavlorem;
	}

	*//**
	 * @param cessRateAdavlorem the cessRateAdavlorem to set
	 *//*
	public void setCessRateAdavlorem(String cessRateAdavlorem) {
		this.cessRateAdavlorem = cessRateAdavlorem;
	}

	*//**
	 * @return the cessAmtAdavlorem
	 *//*
	public String getCessAmtAdavlorem() {
		return cessAmtAdavlorem;
	}

	*//**
	 * @param cessAmtAdavlorem the cessAmtAdavlorem to set
	 *//*
	public void setCessAmtAdavlorem(String cessAmtAdavlorem) {
		this.cessAmtAdavlorem = cessAmtAdavlorem;
	}

	*//**
	 * @return the itemType
	 *//*
	public String getItemType() {
		return itemType;
	}

	*//**
	 * @param itemType the itemType to set
	 *//*
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	*//**
	 * @return the supplierGstn
	 *//*
	public String getSupplierGstn() {
		return supplierGstn;
	}

	*//**
	 * @param supplierGstn the supplierGstn to set
	 *//*
	public void setSupplierGstn(String supplierGstn) {
		this.supplierGstn = supplierGstn;
	}

	*//**
	 * @return the pos
	 *//*
	public String getPos() {
		return pos;
	}

	*//**
	 * @param pos the pos to set
	 *//*
	public void setPos(String pos) {
		this.pos = pos;
	}

	*//**
	 * @return the ecomCustGstn
	 *//*
	public String getEcomCustGstn() {
		return ecomCustGstn;
	}

	*//**
	 * @param ecomCustGstn the ecomCustGstn to set
	 *//*
	public void setEcomCustGstn(String ecomCustGstn) {
		this.ecomCustGstn = ecomCustGstn;
	}

	*//**
	 * @return the returnPeriod
	 *//*
	public int getReturnPeriod() {
		return returnPeriod;
	}

	*//**
	 * @param returnPeriod the returnPeriod to set
	 *//*
	public void setReturnPeriod(int returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	*//**
	 * @return the igstAmt
	 *//*
	public String getIgstAmt() {
		return igstAmt;
	}

	*//**
	 * @param igstAmt the igstAmt to set
	 *//*
	public void setIgstAmt(String igstAmt) {
		this.igstAmt = igstAmt;
	}

	*//**
	 * @return the cgstAmt
	 *//*
	public String getCgstAmt() {
		return cgstAmt;
	}

	*//**
	 * @param cgstAmt the cgstAmt to set
	 *//*
	public void setCgstAmt(String cgstAmt) {
		this.cgstAmt = cgstAmt;
	}

	*//**
	 * @return the sgstAmt
	 *//*
	public String getSgstAmt() {
		return sgstAmt;
	}

	*//**
	 * @param sgstAmt the sgstAmt to set
	 *//*
	public void setSgstAmt(String sgstAmt) {
		this.sgstAmt = sgstAmt;
	}

	*//**
	 * @return the taxRate
	 *//*
	public String getTaxRate() {
		return taxRate;
	}

	*//**
	 * @param taxRate the taxRate to set
	 *//*
	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
	}

	*//**
	 * @return the taxableAmt
	 *//*
	public BigDecimal getTaxableAmt() {
		return taxableAmt;
	}

	*//**
	 * @param taxableAmt the taxableAmt to set
	 *//*
	public void setTaxableAmt(BigDecimal taxableAmt) {
		this.taxableAmt = taxableAmt;
	}

	*//**
	 * @return the uinorcopmposition
	 *//*
	public String getUinorcopmposition() {
		return uinorcopmposition;
	}

	*//**
	 * @param uinorcopmposition the uinorcopmposition to set
	 *//*
	public void setUinorcopmposition(String uinorcopmposition) {
		this.uinorcopmposition = uinorcopmposition;
	}
		}
*/