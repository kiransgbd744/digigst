package com.ey.advisory.app.docs.dto.simplified;
/**
 * 
 * @author Balakrishna.S
 *
 */

import java.math.BigDecimal;

public class TaxPaymentSummaryDto {

    private String table;
    private String section;
    private String desc;
    private BigDecimal usrPayable;
    private BigDecimal usrOtherPayable;
    private BigDecimal usrPaid;
    private BigDecimal usrOtherPaid;
    private BigDecimal usrLiability;
    private BigDecimal usrOtherLiability;
    private BigDecimal usrItcPaidIgst;
    private BigDecimal usrItcPaidCgst;
    private BigDecimal usrItcPaidSgst;
    private BigDecimal usrItcPaidCess;
    private BigDecimal usrCashPaidTax;
    private BigDecimal usrCashPaidInterest;
    private BigDecimal usrCashPaidLateFee;
    private BigDecimal gstnPayable;
    private BigDecimal gstnOtherPayable;
    private BigDecimal gstnPaid;
    private BigDecimal gstnOtherPaid;
    private BigDecimal gstnLiability;
    private BigDecimal gstnOtherLiability;
    private BigDecimal gstnItcPaidIgst;
    private BigDecimal gstnItcPaidCgst;
    private BigDecimal gstnItcPaidSgst;
    private BigDecimal gstnItcPaidCess;
    private BigDecimal gstnCashPaidTax;
    private BigDecimal gstnCashPaidInterest;
    private BigDecimal gstnCashPaidLateFee;
    private BigDecimal diffPayable;
    private BigDecimal diffOtherPayable;
    private BigDecimal diffPaid;
    private BigDecimal diffOtherPaid;
    private BigDecimal diffLiability;
    private BigDecimal diffOtherLiability;
    private BigDecimal diffItcPaidIgst;
    private BigDecimal diffItcPaidCgst;
    private BigDecimal diffItcPaidSgst;
    private BigDecimal diffItcPaidCess;
    private BigDecimal diffCashPaidTax;
    private BigDecimal diffCashPaidInterest;
    private BigDecimal diffCashPaidLateFee;
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
	public BigDecimal getUsrPayable() {
		return usrPayable;
	}
	public void setUsrPayable(BigDecimal usrPayable) {
		this.usrPayable = usrPayable;
	}
	public BigDecimal getUsrOtherPayable() {
		return usrOtherPayable;
	}
	public void setUsrOtherPayable(BigDecimal usrOtherPayable) {
		this.usrOtherPayable = usrOtherPayable;
	}
	public BigDecimal getUsrPaid() {
		return usrPaid;
	}
	public void setUsrPaid(BigDecimal usrPaid) {
		this.usrPaid = usrPaid;
	}
	public BigDecimal getUsrOtherPaid() {
		return usrOtherPaid;
	}
	public void setUsrOtherPaid(BigDecimal usrOtherPaid) {
		this.usrOtherPaid = usrOtherPaid;
	}
	public BigDecimal getUsrLiability() {
		return usrLiability;
	}
	public void setUsrLiability(BigDecimal usrLiability) {
		this.usrLiability = usrLiability;
	}
	public BigDecimal getUsrOtherLiability() {
		return usrOtherLiability;
	}
	public void setUsrOtherLiability(BigDecimal usrOtherLiability) {
		this.usrOtherLiability = usrOtherLiability;
	}
	public BigDecimal getUsrItcPaidIgst() {
		return usrItcPaidIgst;
	}
	public void setUsrItcPaidIgst(BigDecimal usrItcPaidIgst) {
		this.usrItcPaidIgst = usrItcPaidIgst;
	}
	public BigDecimal getUsrItcPaidCgst() {
		return usrItcPaidCgst;
	}
	public void setUsrItcPaidCgst(BigDecimal usrItcPaidCgst) {
		this.usrItcPaidCgst = usrItcPaidCgst;
	}
	public BigDecimal getUsrItcPaidSgst() {
		return usrItcPaidSgst;
	}
	public void setUsrItcPaidSgst(BigDecimal usrItcPaidSgst) {
		this.usrItcPaidSgst = usrItcPaidSgst;
	}
	public BigDecimal getUsrItcPaidCess() {
		return usrItcPaidCess;
	}
	public void setUsrItcPaidCess(BigDecimal usrItcPaidCess) {
		this.usrItcPaidCess = usrItcPaidCess;
	}
	public BigDecimal getUsrCashPaidTax() {
		return usrCashPaidTax;
	}
	public void setUsrCashPaidTax(BigDecimal usrCashPaidTax) {
		this.usrCashPaidTax = usrCashPaidTax;
	}
	public BigDecimal getUsrCashPaidInterest() {
		return usrCashPaidInterest;
	}
	public void setUsrCashPaidInterest(BigDecimal usrCashPaidInterest) {
		this.usrCashPaidInterest = usrCashPaidInterest;
	}
	public BigDecimal getUsrCashPaidLateFee() {
		return usrCashPaidLateFee;
	}
	public void setUsrCashPaidLateFee(BigDecimal usrCashPaidLateFee) {
		this.usrCashPaidLateFee = usrCashPaidLateFee;
	}
	public BigDecimal getGstnPayable() {
		return gstnPayable;
	}
	public void setGstnPayable(BigDecimal gstnPayable) {
		this.gstnPayable = gstnPayable;
	}
	public BigDecimal getGstnOtherPayable() {
		return gstnOtherPayable;
	}
	public void setGstnOtherPayable(BigDecimal gstnOtherPayable) {
		this.gstnOtherPayable = gstnOtherPayable;
	}
	public BigDecimal getGstnPaid() {
		return gstnPaid;
	}
	public void setGstnPaid(BigDecimal gstnPaid) {
		this.gstnPaid = gstnPaid;
	}
	public BigDecimal getGstnOtherPaid() {
		return gstnOtherPaid;
	}
	public void setGstnOtherPaid(BigDecimal gstnOtherPaid) {
		this.gstnOtherPaid = gstnOtherPaid;
	}
	public BigDecimal getGstnLiability() {
		return gstnLiability;
	}
	public void setGstnLiability(BigDecimal gstnLiability) {
		this.gstnLiability = gstnLiability;
	}
	public BigDecimal getGstnOtherLiability() {
		return gstnOtherLiability;
	}
	public void setGstnOtherLiability(BigDecimal gstnOtherLiability) {
		this.gstnOtherLiability = gstnOtherLiability;
	}
	public BigDecimal getGstnItcPaidIgst() {
		return gstnItcPaidIgst;
	}
	public void setGstnItcPaidIgst(BigDecimal gstnItcPaidIgst) {
		this.gstnItcPaidIgst = gstnItcPaidIgst;
	}
	public BigDecimal getGstnItcPaidCgst() {
		return gstnItcPaidCgst;
	}
	public void setGstnItcPaidCgst(BigDecimal gstnItcPaidCgst) {
		this.gstnItcPaidCgst = gstnItcPaidCgst;
	}
	public BigDecimal getGstnItcPaidSgst() {
		return gstnItcPaidSgst;
	}
	public void setGstnItcPaidSgst(BigDecimal gstnItcPaidSgst) {
		this.gstnItcPaidSgst = gstnItcPaidSgst;
	}
	public BigDecimal getGstnItcPaidCess() {
		return gstnItcPaidCess;
	}
	public void setGstnItcPaidCess(BigDecimal gstnItcPaidCess) {
		this.gstnItcPaidCess = gstnItcPaidCess;
	}
	public BigDecimal getGstnCashPaidTax() {
		return gstnCashPaidTax;
	}
	public void setGstnCashPaidTax(BigDecimal gstnCashPaidTax) {
		this.gstnCashPaidTax = gstnCashPaidTax;
	}
	public BigDecimal getGstnCashPaidInterest() {
		return gstnCashPaidInterest;
	}
	public void setGstnCashPaidInterest(BigDecimal gstnCashPaidInterest) {
		this.gstnCashPaidInterest = gstnCashPaidInterest;
	}
	public BigDecimal getGstnCashPaidLateFee() {
		return gstnCashPaidLateFee;
	}
	public void setGstnCashPaidLateFee(BigDecimal gstnCashPaidLateFee) {
		this.gstnCashPaidLateFee = gstnCashPaidLateFee;
	}
	public BigDecimal getDiffPayable() {
		return diffPayable;
	}
	public void setDiffPayable(BigDecimal diffPayable) {
		this.diffPayable = diffPayable;
	}
	public BigDecimal getDiffOtherPayable() {
		return diffOtherPayable;
	}
	public void setDiffOtherPayable(BigDecimal diffOtherPayable) {
		this.diffOtherPayable = diffOtherPayable;
	}
	public BigDecimal getDiffPaid() {
		return diffPaid;
	}
	public void setDiffPaid(BigDecimal diffPaid) {
		this.diffPaid = diffPaid;
	}
	public BigDecimal getDiffOtherPaid() {
		return diffOtherPaid;
	}
	public void setDiffOtherPaid(BigDecimal diffOtherPaid) {
		this.diffOtherPaid = diffOtherPaid;
	}
	public BigDecimal getDiffLiability() {
		return diffLiability;
	}
	public void setDiffLiability(BigDecimal diffLiability) {
		this.diffLiability = diffLiability;
	}
	public BigDecimal getDiffOtherLiability() {
		return diffOtherLiability;
	}
	public void setDiffOtherLiability(BigDecimal diffOtherLiability) {
		this.diffOtherLiability = diffOtherLiability;
	}
	public BigDecimal getDiffItcPaidIgst() {
		return diffItcPaidIgst;
	}
	public void setDiffItcPaidIgst(BigDecimal diffItcPaidIgst) {
		this.diffItcPaidIgst = diffItcPaidIgst;
	}
	public BigDecimal getDiffItcPaidCgst() {
		return diffItcPaidCgst;
	}
	public void setDiffItcPaidCgst(BigDecimal diffItcPaidCgst) {
		this.diffItcPaidCgst = diffItcPaidCgst;
	}
	public BigDecimal getDiffItcPaidSgst() {
		return diffItcPaidSgst;
	}
	public void setDiffItcPaidSgst(BigDecimal diffItcPaidSgst) {
		this.diffItcPaidSgst = diffItcPaidSgst;
	}
	public BigDecimal getDiffItcPaidCess() {
		return diffItcPaidCess;
	}
	public void setDiffItcPaidCess(BigDecimal diffItcPaidCess) {
		this.diffItcPaidCess = diffItcPaidCess;
	}
	public BigDecimal getDiffCashPaidTax() {
		return diffCashPaidTax;
	}
	public void setDiffCashPaidTax(BigDecimal diffCashPaidTax) {
		this.diffCashPaidTax = diffCashPaidTax;
	}
	public BigDecimal getDiffCashPaidInterest() {
		return diffCashPaidInterest;
	}
	public void setDiffCashPaidInterest(BigDecimal diffCashPaidInterest) {
		this.diffCashPaidInterest = diffCashPaidInterest;
	}
	public BigDecimal getDiffCashPaidLateFee() {
		return diffCashPaidLateFee;
	}
	public void setDiffCashPaidLateFee(BigDecimal diffCashPaidLateFee) {
		this.diffCashPaidLateFee = diffCashPaidLateFee;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}

    
}
