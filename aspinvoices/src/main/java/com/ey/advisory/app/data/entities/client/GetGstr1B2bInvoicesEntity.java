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
@Table(name = "GETGSTR1_B2B_B2BA_DETAILS")
public class GetGstr1B2bInvoicesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CTIN")
	private String ctin;

	@Column(name = "CFS")
	private String cfs;

	@Column(name = "FLAG")
	private String flag;

	@Column(name = "INV_CHKSUM")
	private String chkSum;

	@Column(name = "INV_UPDBY")
	private String invUpdBy;

	@Column(name = "INV_NUM")
	private String invNum;

	@Column(name = "INV_DATE")
	private LocalDate invDate;

	@Column(name = "INV_VALUE")
	private BigDecimal invValue;

	@Column(name = "POS")
	private int pos;

	@Column(name = "RCHRG")
	private String rchrg;

	@Column(name = "ETIN")
	private String etin;

	@Column(name = "INV_TYPE")
	private String invType;

	@Column(name = "CFLAG")
	private String cFlag;

	@Column(name = "DIFF_PERCENT")
	private BigDecimal DiffPercent;

	@Column(name = "OPD")
	private String opd;

	@Column(name = "ITM_NUM")
	private int itmNum;

	@Column(name = "TAX_RATE")
	private BigDecimal TaxRate;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal TaxableValue;

	@Column(name = "IGST_AMT")
	private BigDecimal igst;

	@Column(name = "CGST_AMT")
	private BigDecimal cgst;

	@Column(name = "SGST_AMT")
	private BigDecimal sgst;

	@Column(name = "CESS_AMT")
	private BigDecimal cess;

	@Column(name = "SUPPLIER_GSTIN")
	private String sgstin;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedTaxperiod;

	@Column(name = "OINUM")
	private String oinum;

	@Column(name = "OIDT")
	private LocalDate oidt;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

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

	public String getInvUpdBy() {
		return invUpdBy;
	}

	public String getInvNum() {
		return invNum;
	}

	public LocalDate getInvDate() {
		return invDate;
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

	public String getEtin() {
		return etin;
	}

	public String getInvType() {
		return invType;
	}

	public String getcFlag() {
		return cFlag;
	}

	public BigDecimal getDiffPercent() {
		return DiffPercent;
	}

	public String getOpd() {
		return opd;
	}

	public int getItmNum() {
		return itmNum;
	}

	public BigDecimal getTaxRate() {
		return TaxRate;
	}

	public BigDecimal getTaxableValue() {
		return TaxableValue;
	}

	public BigDecimal getIgst() {
		return igst;
	}

	public BigDecimal getCgst() {
		return cgst;
	}

	public BigDecimal getSgst() {
		return sgst;
	}

	public BigDecimal getCess() {
		return cess;
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

	public void setInvUpdBy(String invUpdBy) {
		this.invUpdBy = invUpdBy;
	}

	public void setInvNum(String invNum) {
		this.invNum = invNum;
	}

	public void setInvDate(LocalDate invDate) {
		this.invDate = invDate;
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

	public void setEtin(String etin) {
		this.etin = etin;
	}

	public void setInvType(String invType) {
		this.invType = invType;
	}

	public void setcFlag(String cFlag) {
		this.cFlag = cFlag;
	}

	public void setDiffPercent(BigDecimal diffPercent) {
		DiffPercent = diffPercent;
	}

	public void setOpd(String opd) {
		this.opd = opd;
	}

	public void setItmNum(int itmNum) {
		this.itmNum = itmNum;
	}

	public void setTaxRate(BigDecimal taxRate) {
		TaxRate = taxRate;
	}

	public void setTaxableValue(BigDecimal taxableValue) {
		TaxableValue = taxableValue;
	}

	public void setIgst(BigDecimal igst) {
		this.igst = igst;
	}

	public void setCgst(BigDecimal cgst) {
		this.cgst = cgst;
	}

	public void setSgst(BigDecimal sgst) {
		this.sgst = sgst;
	}

	public void setCess(BigDecimal cess) {
		this.cess = cess;
	}

	public String getSgstin() {
		return sgstin;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public Integer getDerivedTaxperiod() {
		return derivedTaxperiod;
	}

	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public void setDerivedTaxperiod(Integer derivedTaxperiod) {
		this.derivedTaxperiod = derivedTaxperiod;
	}

	public String getOinum() {
		return oinum;
	}

	public void setOinum(String oinum) {
		this.oinum = oinum;
	}

	public LocalDate getOidt() {
		return oidt;
	}

	public void setOidt(LocalDate oidt) {
		this.oidt = oidt;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	@Override
	public String toString() {
		return "GetGstr1B2bInvoicesEntity [id=" + id + ", ctin=" + ctin
				+ ", cfs=" + cfs + ", flag=" + flag + ", chkSum=" + chkSum
				+ ", invUpdBy=" + invUpdBy + ", invNum=" + invNum + ", invDate="
				+ invDate + ", invValue=" + invValue + ", pos=" + pos
				+ ", rchrg=" + rchrg + ", etin=" + etin + ", invType=" + invType
				+ ", cFlag=" + cFlag + ", DiffPercent=" + DiffPercent + ", opd="
				+ opd + ", itmNum=" + itmNum + ", TaxRate=" + TaxRate
				+ ", TaxableValue=" + TaxableValue + ", igst=" + igst
				+ ", cgst=" + cgst + ", sgst=" + sgst + ", cess=" + cess
				+ ", sgstin=" + sgstin + ", returnPeriod=" + returnPeriod
				+ ", derivedTaxperiod=" + derivedTaxperiod + ", oinum=" + oinum
				+ ", oidt=" + oidt + ", isDelete=" + isDelete + "]";
	}

}
*/