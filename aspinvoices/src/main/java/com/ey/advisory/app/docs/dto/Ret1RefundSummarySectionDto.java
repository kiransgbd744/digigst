package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

/**
 * 
 * @author Balakrishna.S
 *
 */
public class Ret1RefundSummarySectionDto {

	private String table;
    private String desc;
    private BigDecimal usrTax = BigDecimal.ZERO;
    private BigDecimal usrInterest = BigDecimal.ZERO;
    private BigDecimal usrPenality = BigDecimal.ZERO;
    private BigDecimal usrFee = BigDecimal.ZERO;
    private BigDecimal usrOther = BigDecimal.ZERO;
    private BigDecimal usrTotal = BigDecimal.ZERO;
    private BigDecimal gstnTax = BigDecimal.ZERO;
    private BigDecimal gstnInterest = BigDecimal.ZERO;
    private BigDecimal gstnPenality = BigDecimal.ZERO;
    private BigDecimal gstnFee = BigDecimal.ZERO;
    private BigDecimal gstnOther = BigDecimal.ZERO;
    private BigDecimal gstnTotal = BigDecimal.ZERO;
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public BigDecimal getUsrTax() {
		return usrTax;
	}
	public void setUsrTax(BigDecimal usrTax) {
		this.usrTax = usrTax;
	}
	public BigDecimal getUsrInterest() {
		return usrInterest;
	}
	public void setUsrInterest(BigDecimal usrInterest) {
		this.usrInterest = usrInterest;
	}
	public BigDecimal getUsrPenality() {
		return usrPenality;
	}
	public void setUsrPenality(BigDecimal usrPenality) {
		this.usrPenality = usrPenality;
	}
	public BigDecimal getUsrFee() {
		return usrFee;
	}
	public void setUsrFee(BigDecimal usrFee) {
		this.usrFee = usrFee;
	}
	public BigDecimal getUsrOther() {
		return usrOther;
	}
	public void setUsrOther(BigDecimal usrOther) {
		this.usrOther = usrOther;
	}
	public BigDecimal getUsrTotal() {
		return usrTotal;
	}
	public void setUsrTotal(BigDecimal usrTotal) {
		this.usrTotal = usrTotal;
	}
	public BigDecimal getGstnTax() {
		return gstnTax;
	}
	public void setGstnTax(BigDecimal gstnTax) {
		this.gstnTax = gstnTax;
	}
	public BigDecimal getGstnInterest() {
		return gstnInterest;
	}
	public void setGstnInterest(BigDecimal gstnInterest) {
		this.gstnInterest = gstnInterest;
	}
	public BigDecimal getGstnPenality() {
		return gstnPenality;
	}
	public void setGstnPenality(BigDecimal gstnPenality) {
		this.gstnPenality = gstnPenality;
	}
	public BigDecimal getGstnFee() {
		return gstnFee;
	}
	public void setGstnFee(BigDecimal gstnFee) {
		this.gstnFee = gstnFee;
	}
	public BigDecimal getGstnOther() {
		return gstnOther;
	}
	public void setGstnOther(BigDecimal gstnOther) {
		this.gstnOther = gstnOther;
	}
	public BigDecimal getGstnTotal() {
		return gstnTotal;
	}
	public void setGstnTotal(BigDecimal gstnTotal) {
		this.gstnTotal = gstnTotal;
	}
    
    
}
