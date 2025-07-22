/*package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

*//**
 * 
 * @author Hemasundar.J
 *
 *//*
@Entity
@Table(name = "GETGSTR2A_B2B_B2BA_DETAILS")
public class GetGstr2B2bInvoicesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CTIN")
	private String ctin;

	@Column(name = "CFS")
	private String cfs;

	@Column(name = "CHKSUM")
	private String chkSum;

	@Column(name = "SUPPLIER_INV_NUM")
	private String invNum;

	@Column(name = "SUPPLIER_INV_DATE")
	private LocalDate invDate;

	@Column(name = "ORG_INV_NUM")
	private String origInvNum;

	@Column(name = "ORG_INV_DATE")
	private LocalDate origInvDate;

	@Column(name = "SUPPLIER_INV_VAL")
	private BigDecimal invValue;

	@Column(name = "POS")
	private int pos;

	@Column(name = "RCHRG")
	private String rchrg;

	@Column(name = "INV_TYPE")
	private String invType;	
	
	@Column(name = "DIFF_PERCENT")
	private BigDecimal diffPercentage;

	@Column(name = "ITEM_NUM")
	private int itmNum;

	@Column(name = "TAX_VAL")
	private BigDecimal TaxableValue;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;
	
	@Column(name = "TAX_RATE")
	private BigDecimal TaxRate;

	public Long getId() {
		return id;
	}

	public String getCtin() {
		return ctin;
	}

	public String getCfs() {
		return cfs;
	}

	public String getChkSum() {
		return chkSum;
	}

	public String getInvNum() {
		return invNum;
	}

	public LocalDate getInvDate() {
		return invDate;
	}

	public String getOrigInvNum() {
		return origInvNum;
	}

	public LocalDate getOrigInvDate() {
		return origInvDate;
	}

	public BigDecimal getInvValue() {
		return invValue;
	}

	public int getPos() {
		return pos;
	}

	public String getRchrg() {
		return rchrg;
	}

	public String getInvType() {
		return invType;
	}

	public BigDecimal getDiffPercentage() {
		return diffPercentage;
	}

	public int getItmNum() {
		return itmNum;
	}

	public BigDecimal getTaxableValue() {
		return TaxableValue;
	}

	public BigDecimal getIgstAmt() {
		return igstAmt;
	}

	public BigDecimal getCgstAmt() {
		return cgstAmt;
	}

	public BigDecimal getSgstAmt() {
		return sgstAmt;
	}

	public BigDecimal getCessAmt() {
		return cessAmt;
	}

	public BigDecimal getTaxRate() {
		return TaxRate;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCtin(String ctin) {
		this.ctin = ctin;
	}

	public void setCfs(String cfs) {
		this.cfs = cfs;
	}

	public void setChkSum(String chkSum) {
		this.chkSum = chkSum;
	}

	public void setInvNum(String invNum) {
		this.invNum = invNum;
	}

	public void setInvDate(LocalDate invDate) {
		this.invDate = invDate;
	}

	public void setOrigInvNum(String origInvNum) {
		this.origInvNum = origInvNum;
	}

	public void setOrigInvDate(LocalDate origInvDate) {
		this.origInvDate = origInvDate;
	}

	public void setInvValue(BigDecimal invValue) {
		this.invValue = invValue;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public void setRchrg(String rchrg) {
		this.rchrg = rchrg;
	}

	public void setInvType(String invType) {
		this.invType = invType;
	}

	public void setDiffPercentage(BigDecimal diffPercentage) {
		this.diffPercentage = diffPercentage;
	}

	public void setItmNum(int itmNum) {
		this.itmNum = itmNum;
	}

	public void setTaxableValue(BigDecimal taxableValue) {
		TaxableValue = taxableValue;
	}

	public void setIgstAmt(BigDecimal igstAmt) {
		this.igstAmt = igstAmt;
	}

	public void setCgstAmt(BigDecimal cgstAmt) {
		this.cgstAmt = cgstAmt;
	}

	public void setSgstAmt(BigDecimal sgstAmt) {
		this.sgstAmt = sgstAmt;
	}

	public void setCessAmt(BigDecimal cessAmt) {
		this.cessAmt = cessAmt;
	}

	public void setTaxRate(BigDecimal taxRate) {
		TaxRate = taxRate;
	}
	
}
*/