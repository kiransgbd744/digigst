package com.ey.advisory.app.docs.dto.simplified;

import java.math.BigDecimal;

/**
 * 
 * @author Balakrishna.S
 *
 */
public class Ret1LateFeeDetailSummaryDto {

	private String returnType;
	private Long id;
	private Integer sNo;
	private String gstin;
	private String returnTable;
	private String taxPeriod;
	private BigDecimal igstInterest = BigDecimal.ZERO;
	private BigDecimal cgstInterest = BigDecimal.ZERO;
	private BigDecimal sgstInterest = BigDecimal.ZERO;
	private BigDecimal cessInterest = BigDecimal.ZERO;
	private BigDecimal cgstLateFee = BigDecimal.ZERO;
	private BigDecimal sgstLateFee = BigDecimal.ZERO;
	private String userDefined1;
	private String userDefined2;
	private String userDefined3;

	public String getReturnTable() {
		return returnTable;
	}

	public void setReturnTable(String returnTable) {
		this.returnTable = returnTable;
	}

	public BigDecimal getIgstInterest() {
		return igstInterest;
	}

	public void setIgstInterest(BigDecimal igstInterest) {
		this.igstInterest = igstInterest;
	}

	public BigDecimal getCgstInterest() {
		return cgstInterest;
	}

	public void setCgstInterest(BigDecimal cgstInterest) {
		this.cgstInterest = cgstInterest;
	}

	public BigDecimal getSgstInterest() {
		return sgstInterest;
	}

	public void setSgstInterest(BigDecimal sgstInterest) {
		this.sgstInterest = sgstInterest;
	}

	public BigDecimal getCessInterest() {
		return cessInterest;
	}

	public void setCessInterest(BigDecimal cessInterest) {
		this.cessInterest = cessInterest;
	}

	public BigDecimal getCgstLateFee() {
		return cgstLateFee;
	}

	public void setCgstLateFee(BigDecimal cgstLateFee) {
		this.cgstLateFee = cgstLateFee;
	}

	public BigDecimal getSgstLateFee() {
		return sgstLateFee;
	}

	public void setSgstLateFee(BigDecimal sgstLateFee) {
		this.sgstLateFee = sgstLateFee;
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

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	public Integer getsNo() {
		return sNo;
	}

	public void setsNo(Integer sNo) {
		this.sNo = sNo;
	}

	
}
