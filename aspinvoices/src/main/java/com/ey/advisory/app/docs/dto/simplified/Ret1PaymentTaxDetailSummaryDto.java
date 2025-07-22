package com.ey.advisory.app.docs.dto.simplified;

import java.math.BigDecimal;

/**
 * 
 * @author Balakrishna.S
 *
 */
public class Ret1PaymentTaxDetailSummaryDto {

	private Long id;
	private String returnType;
	private String gstin;
	private String taxPeriod;
	private Integer sNo;
   private String desc;
   private BigDecimal taxPayableRc;
   private BigDecimal taxPayableOtherRc;
   private BigDecimal taxPaidRc;
   private BigDecimal taxPaidOtherRc;
   private BigDecimal adjLiabilityRc;
   private BigDecimal adjLiabilityOtherRc;
   private BigDecimal itcPaidIgst;
   private BigDecimal itcPaidCgst;
   private BigDecimal itcPaidSgst;
   private BigDecimal itcPaidCess;
   private BigDecimal cashPaidTax;
   private BigDecimal cashPaidInterest;
   private BigDecimal cashPaidLateFee;
   private String userDefined1;
   private String  userDefined2;
   private String userDefined3;
public String getDesc() {
	return desc;
}
public void setDesc(String desc) {
	this.desc = desc;
}
public BigDecimal getTaxPayableRc() {
	return taxPayableRc;
}
public void setTaxPayableRc(BigDecimal taxPayableRc) {
	this.taxPayableRc = taxPayableRc;
}
public BigDecimal getTaxPayableOtherRc() {
	return taxPayableOtherRc;
}
public void setTaxPayableOtherRc(BigDecimal taxPayableOtherRc) {
	this.taxPayableOtherRc = taxPayableOtherRc;
}
public BigDecimal getTaxPaidRc() {
	return taxPaidRc;
}
public void setTaxPaidRc(BigDecimal taxPaidRc) {
	this.taxPaidRc = taxPaidRc;
}
public BigDecimal getTaxPaidOtherRc() {
	return taxPaidOtherRc;
}
public void setTaxPaidOtherRc(BigDecimal taxPaidOtherRc) {
	this.taxPaidOtherRc = taxPaidOtherRc;
}
public BigDecimal getAdjLiabilityRc() {
	return adjLiabilityRc;
}
public void setAdjLiabilityRc(BigDecimal adjLiabilityRc) {
	this.adjLiabilityRc = adjLiabilityRc;
}
public BigDecimal getAdjLiabilityOtherRc() {
	return adjLiabilityOtherRc;
}
public void setAdjLiabilityOtherRc(BigDecimal adjLiabilityOtherRc) {
	this.adjLiabilityOtherRc = adjLiabilityOtherRc;
}
public BigDecimal getItcPaidIgst() {
	return itcPaidIgst;
}
public void setItcPaidIgst(BigDecimal itcPaidIgst) {
	this.itcPaidIgst = itcPaidIgst;
}
public BigDecimal getItcPaidCgst() {
	return itcPaidCgst;
}
public void setItcPaidCgst(BigDecimal itcPaidCgst) {
	this.itcPaidCgst = itcPaidCgst;
}
public BigDecimal getItcPaidSgst() {
	return itcPaidSgst;
}
public void setItcPaidSgst(BigDecimal itcPaidSgst) {
	this.itcPaidSgst = itcPaidSgst;
}
public BigDecimal getItcPaidCess() {
	return itcPaidCess;
}
public void setItcPaidCess(BigDecimal itcPaidCess) {
	this.itcPaidCess = itcPaidCess;
}
public BigDecimal getCashPaidTax() {
	return cashPaidTax;
}
public void setCashPaidTax(BigDecimal cashPaidTax) {
	this.cashPaidTax = cashPaidTax;
}
public BigDecimal getCashPaidInterest() {
	return cashPaidInterest;
}
public void setCashPaidInterest(BigDecimal cashPaidInterest) {
	this.cashPaidInterest = cashPaidInterest;
}
public BigDecimal getCashPaidLateFee() {
	return cashPaidLateFee;
}
public void setCashPaidLateFee(BigDecimal cashPaidLateFee) {
	this.cashPaidLateFee = cashPaidLateFee;
}
public String getUserDefined1() {
	return userDefined1;
}
public void setUserDefined1(String userDefined1) {
	this.userDefined1 = userDefined1;
}
public String getUserDefined2() {
	return userDefined2;
}
public void setUserDefined2(String userDefined2) {
	this.userDefined2 = userDefined2;
}
public String getUserDefined3() {
	return userDefined3;
}
public void setUserDefined3(String userDefined3) {
	this.userDefined3 = userDefined3;
}
public String getReturnType() {
	return returnType;
}
public void setReturnType(String returnType) {
	this.returnType = returnType;
}
public String getGstin() {
	return gstin;
}
public void setGstin(String gstin) {
	this.gstin = gstin;
}
public String getTaxPeriod() {
	return taxPeriod;
}
public void setTaxPeriod(String taxPeriod) {
	this.taxPeriod = taxPeriod;
}
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public Integer getsNo() {
	return sNo;
}
public void setsNo(Integer sNo) {
	this.sNo = sNo;
}



	
}
