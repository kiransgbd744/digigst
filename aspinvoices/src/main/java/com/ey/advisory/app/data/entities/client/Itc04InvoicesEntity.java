package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.ToString;

/**
 * 
 * @author Anand3.M
 *
 */
@Entity
@ToString
@Table(name = "GETITC04_INVOICE")
public class Itc04InvoicesEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETITC04_INVOICE_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "SUPPLIER_GSTIN")
	private String gstin;

	@Column(name = "JW_GSTIN")
	private String jwGstin;

	@Column(name = "QRET_PERIOD")
	private String returnPeriod;

	@Column(name = "QDERIVED_RET_PERIOD")
	private Integer qDerReturnPeriod;

	@Column(name = "TABLE_NUMBER")
	private String tableNum;

	@Column(name = "JW_STATE_CODE")
	private String jwStateCode;

	@Column(name = "CHALLAN_NO")
	private String challanNum;

	@Column(name = "CHALLAN_DATE")
	private String challanDate;

	@Column(name = "ORG_CHALLAN_NO")
	private String orgChallanNum;

	@Column(name = "ORG_CHALLAN_DATE")
	private String orgChallanDate;

	@Column(name = "JW2_CHALLAN_NO")
	private String jw2ChallanNum;

	@Column(name = "JW2_CHALLAN_DATE")
	private String jw2ChallanDate;

	@Column(name = "INV_NUM")
	private String invNum;

	@Column(name = "INV_DATE")
	private String invDate;

	@Column(name = "GOODS_TYPE")
	private String goodsType;

	@Column(name = "PRODUCT_DESC")
	private String productDesc;

	@Column(name = "NATURE_OF_JW")
	private String natureJw;

	@Column(name = "ITM_UQC")
	private String itmUqc;

	@Column(name = "ITM_QTY")
	private BigDecimal itmQty;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxValue;

	@Column(name = "IGST_RATE")
	private BigDecimal igst;

	@Column(name = "CGST_RATE")
	private BigDecimal cgst;

	@Column(name = "SGST_RATE")
	private BigDecimal sgst;

	@Column(name = "CESS_RATE")
	private BigDecimal cess;

	@Column(name = "LOSSES_UQC")
	private String lossesUqc;

	@Column(name = "LOSSES_QTY")
	private BigDecimal lossesQty;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
	@Column(name = "ITM_NO")
	private Integer itemNum;
	
	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;
	
	@Column(name = "CHALLAN_FY")
	private String challanFy;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getJwGstin() {
		return jwGstin;
	}

	public void setJwGstin(String jwGstin) {
		this.jwGstin = jwGstin;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public Integer getqDerReturnPeriod() {
		return qDerReturnPeriod;
	}

	public void setqDerReturnPeriod(Integer qDerReturnPeriod) {
		this.qDerReturnPeriod = qDerReturnPeriod;
	}

	public String getTableNum() {
		return tableNum;
	}

	public void setTableNum(String tableNum) {
		this.tableNum = tableNum;
	}

	public String getJwStateCode() {
		return jwStateCode;
	}

	public void setJwStateCode(String jwStateCode) {
		this.jwStateCode = jwStateCode;
	}

	public String getChallanNum() {
		return challanNum;
	}

	public void setChallanNum(String challanNum) {
		this.challanNum = challanNum;
	}

	public String getChallanDate() {
		return challanDate;
	}

	public void setChallanDate(String challanDate) {
		this.challanDate = challanDate;
	}

	public String getOrgChallanNum() {
		return orgChallanNum;
	}

	public void setOrgChallanNum(String orgChallanNum) {
		this.orgChallanNum = orgChallanNum;
	}

	public String getOrgChallanDate() {
		return orgChallanDate;
	}

	public void setOrgChallanDate(String orgChallanDate) {
		this.orgChallanDate = orgChallanDate;
	}

	public String getJw2ChallanNum() {
		return jw2ChallanNum;
	}

	public void setJw2ChallanNum(String jw2ChallanNum) {
		this.jw2ChallanNum = jw2ChallanNum;
	}

	public String getJw2ChallanDate() {
		return jw2ChallanDate;
	}

	public void setJw2ChallanDate(String jw2ChallanDate) {
		this.jw2ChallanDate = jw2ChallanDate;
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

	public String getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getNatureJw() {
		return natureJw;
	}

	public void setNatureJw(String natureJw) {
		this.natureJw = natureJw;
	}

	public String getItmUqc() {
		return itmUqc;
	}

	public void setItmUqc(String itmUqc) {
		this.itmUqc = itmUqc;
	}

	public BigDecimal getItmQty() {
		return itmQty;
	}

	public void setItmQty(BigDecimal itmQty) {
		this.itmQty = itmQty;
	}

	public BigDecimal getTaxValue() {
		return taxValue;
	}

	public void setTaxValue(BigDecimal taxValue) {
		this.taxValue = taxValue;
	}

	public BigDecimal getIgst() {
		return igst;
	}

	public void setIgst(BigDecimal igst) {
		this.igst = igst;
	}

	public BigDecimal getCgst() {
		return cgst;
	}

	public void setCgst(BigDecimal cgst) {
		this.cgst = cgst;
	}

	public BigDecimal getSgst() {
		return sgst;
	}

	public void setSgst(BigDecimal sgst) {
		this.sgst = sgst;
	}

	public BigDecimal getCess() {
		return cess;
	}

	public void setCess(BigDecimal cess) {
		this.cess = cess;
	}

	public String getLossesUqc() {
		return lossesUqc;
	}

	public void setLossesUqc(String lossesUqc) {
		this.lossesUqc = lossesUqc;
	}

	public BigDecimal getLossesQty() {
		return lossesQty;
	}

	public void setLossesQty(BigDecimal lossesQty) {
		this.lossesQty = lossesQty;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public LocalDateTime getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(LocalDateTime modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public Integer getItemNum() {
		return itemNum;
	}

	public void setItemNum(Integer itemNum) {
		this.itemNum = itemNum;
	}

	public BigDecimal getIgstAmt() {
		return igstAmt;
	}

	public void setIgstAmt(BigDecimal igstAmt) {
		this.igstAmt = taxValue.multiply(igst);
	}

	public BigDecimal getCgstAmt() {
		return cgstAmt;
	}

	public void setCgstAmt(BigDecimal cgstAmt) {
		this.cgstAmt = taxValue.multiply(cgst);
	}

	public BigDecimal getSgstAmt() {
		return sgstAmt;
	}

	public void setSgstAmt(BigDecimal sgstAmt) {
		this.sgstAmt = taxValue.multiply(sgst);
	}

	public BigDecimal getCessAmt() {
		return cessAmt;
	}

	public void setCessAmt(BigDecimal cessAmt) {
		this.cessAmt = taxValue.multiply(cess);
	}

	public String getChallanFy() {
		return challanFy;
	}

	public void setChallanFy(String challanFy) {
		if(challanDate != null){
			this.challanFy = FinancialYearConverter(challanDate);
		} else {
			this.challanFy = challanFy;
		}
	}
	
	public static String FinancialYearConverter(String challanDate) {
	        LocalDate date = LocalDate.parse(challanDate, java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	        int year = date.getYear();
	        int month = date.getMonthValue();
	        String financialYear;
	        if (month >= 4) {
	        	String nextFy = Integer.toString(year+1);
	            financialYear = year + "" + nextFy.substring(2,4);
	        } else {
	        	String preFy = Integer.toString(year);
	            financialYear = (year - 1) + "" + preFy.substring(2,4);
	        }
			return financialYear;
	    }
}
