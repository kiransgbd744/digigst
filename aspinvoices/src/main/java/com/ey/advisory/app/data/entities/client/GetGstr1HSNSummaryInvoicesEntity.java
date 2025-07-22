/*package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "GETGSTR1_HSN_SUMMARY_DETAILS")
public class GetGstr1HSNSummaryInvoicesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstinOfTaxPayer;

	@Column(name = "RET_PERIOD")
	private String returnPeriod;

	@Column(name = "FLAG")
	private String flag;

	@Column(name = "CHKSUM")
	private String invoiceCheckSum;

	@Column(name = "SERIAL_NUM")
	private Integer serialNumber;

	@Column(name = "HSN_SC")
	private String hsnGoodsOrService;

	@Column(name = "DESCRIPTION")
	private String descOfGoodsSold;

	@Column(name = "UNIT_OF_MEASURE")
	private String unitOfMeasureOfGoodsSold;

	@Column(name = "QUANTITY")
	private BigDecimal qtyOfGoodsSold;

	@Column(name = "TOT_VAL")
	private BigDecimal totalValue;

	@Column(name = "TAXABLE_VAL")
	private BigDecimal taxValOfGoodsOrService;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmount;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmount;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmount;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmount;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	*//**
	 * @return the gstinOfTaxPayer
	 *//*
	public String getGstinOfTaxPayer() {
		return gstinOfTaxPayer;
	}

	*//**
	 * @param gstinOfTaxPayer
	 *            the gstinOfTaxPayer to set
	 *//*
	public void setGstinOfTaxPayer(String gstinOfTaxPayer) {
		this.gstinOfTaxPayer = gstinOfTaxPayer;
	}

	*//**
	 * @return the returnPeriod
	 *//*
	public String getReturnPeriod() {
		return returnPeriod;
	}

	*//**
	 * @param returnPeriod
	 *            the returnPeriod to set
	 *//*
	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	*//**
	 * @return the flag
	 *//*
	public String getFlag() {
		return flag;
	}

	*//**
	 * @param flag
	 *            the flag to set
	 *//*
	public void setFlag(String flag) {
		this.flag = flag;
	}

	*//**
	 * @return the invoiceCheckSum
	 *//*
	public String getInvoiceCheckSum() {
		return invoiceCheckSum;
	}

	*//**
	 * @param invoiceCheckSum
	 *            the invoiceCheckSum to set
	 *//*
	public void setInvoiceCheckSum(String invoiceCheckSum) {
		this.invoiceCheckSum = invoiceCheckSum;
	}

	*//**
	 * @return the serialNumber
	 *//*
	public Integer getSerialNumber() {
		return serialNumber;
	}

	*//**
	 * @param serialNumber
	 *            the serialNumber to set
	 *//*
	public void setSerialNumber(Integer serialNumber) {
		this.serialNumber = serialNumber;
	}

	*//**
	 * @return the hsnGoodsOrService
	 *//*
	public String getHsnGoodsOrService() {
		return hsnGoodsOrService;
	}

	*//**
	 * @param hsnGoodsOrService
	 *            the hsnGoodsOrService to set
	 *//*
	public void setHsnGoodsOrService(String hsnGoodsOrService) {
		this.hsnGoodsOrService = hsnGoodsOrService;
	}

	*//**
	 * @return the descOfGoodsSold
	 *//*
	public String getDescOfGoodsSold() {
		return descOfGoodsSold;
	}

	*//**
	 * @param descOfGoodsSold
	 *            the descOfGoodsSold to set
	 *//*
	public void setDescOfGoodsSold(String descOfGoodsSold) {
		this.descOfGoodsSold = descOfGoodsSold;
	}

	*//**
	 * @return the unitOfMeasureOfGoodsSold
	 *//*
	public String getUnitOfMeasureOfGoodsSold() {
		return unitOfMeasureOfGoodsSold;
	}

	*//**
	 * @param unitOfMeasureOfGoodsSold
	 *            the unitOfMeasureOfGoodsSold to set
	 *//*
	public void setUnitOfMeasureOfGoodsSold(String unitOfMeasureOfGoodsSold) {
		this.unitOfMeasureOfGoodsSold = unitOfMeasureOfGoodsSold;
	}

	*//**
	 * @return the qtyOfGoodsSold
	 *//*
	public BigDecimal getQtyOfGoodsSold() {
		return qtyOfGoodsSold;
	}

	*//**
	 * @param qtyOfGoodsSold
	 *            the qtyOfGoodsSold to set
	 *//*
	public void setQtyOfGoodsSold(BigDecimal qtyOfGoodsSold) {
		this.qtyOfGoodsSold = qtyOfGoodsSold;
	}

	*//**
	 * @return the totalValue
	 *//*
	public BigDecimal getTotalValue() {
		return totalValue;
	}

	*//**
	 * @param totalValue
	 *            the totalValue to set
	 *//*
	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}

	*//**
	 * @return the taxValOfGoodsOrService
	 *//*
	public BigDecimal getTaxValOfGoodsOrService() {
		return taxValOfGoodsOrService;
	}

	*//**
	 * @param taxValOfGoodsOrService
	 *            the taxValOfGoodsOrService to set
	 *//*
	public void setTaxValOfGoodsOrService(BigDecimal taxValOfGoodsOrService) {
		this.taxValOfGoodsOrService = taxValOfGoodsOrService;
	}

	*//**
	 * @return the igstAmount
	 *//*
	public BigDecimal getIgstAmount() {
		return igstAmount;
	}

	*//**
	 * @param igstAmount
	 *            the igstAmount to set
	 *//*
	public void setIgstAmount(BigDecimal igstAmount) {
		this.igstAmount = igstAmount;
	}

	*//**
	 * @return the cgstAmount
	 *//*
	public BigDecimal getCgstAmount() {
		return cgstAmount;
	}

	*//**
	 * @param cgstAmount
	 *            the cgstAmount to set
	 *//*
	public void setCgstAmount(BigDecimal cgstAmount) {
		this.cgstAmount = cgstAmount;
	}

	*//**
	 * @return the sgstAmount
	 *//*
	public BigDecimal getSgstAmount() {
		return sgstAmount;
	}

	*//**
	 * @param sgstAmount
	 *            the sgstAmount to set
	 *//*
	public void setSgstAmount(BigDecimal sgstAmount) {
		this.sgstAmount = sgstAmount;
	}

	*//**
	 * @return the cessAmount
	 *//*
	public BigDecimal getCessAmount() {
		return cessAmount;
	}

	*//**
	 * @param cessAmount
	 *            the cessAmount to set
	 *//*
	public void setCessAmount(BigDecimal cessAmount) {
		this.cessAmount = cessAmount;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 
	@Override
	public String toString() {
		return "GetGstr1HSNSummaryInvoicesEntity [id=" + id
				+ ", gstinOfTaxPayer=" + gstinOfTaxPayer + ", returnPeriod="
				+ returnPeriod + ", flag=" + flag + ", invoiceCheckSum="
				+ invoiceCheckSum + ", serialNumber=" + serialNumber
				+ ", hsnGoodsOrService=" + hsnGoodsOrService
				+ ", descOfGoodsSold=" + descOfGoodsSold
				+ ", unitOfMeasureOfGoodsSold=" + unitOfMeasureOfGoodsSold
				+ ", qtyOfGoodsSold=" + qtyOfGoodsSold + ", totalValue="
				+ totalValue + ", taxValOfGoodsOrService="
				+ taxValOfGoodsOrService + ", igstAmount=" + igstAmount
				+ ", cgstAmount=" + cgstAmount + ", sgstAmount=" + sgstAmount
				+ ", cessAmount=" + cessAmount + ", isDelete=" + isDelete + "]";
	}

}
*/