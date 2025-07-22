package com.ey.advisory.app.docs.dto.simplified;

import java.math.BigDecimal;

/**
 * 
 * @author Balakrishna.S
 *
 */
public class Ret1AspDetailRespDto {

	private Long id;
	private String gstin;
	private String taxPeriod;
    private String returnTable;
    private String returnType;
    private BigDecimal value = BigDecimal.ZERO;
    private BigDecimal igstAmt = BigDecimal.ZERO;
    private BigDecimal cgstAmt = BigDecimal.ZERO;
    private BigDecimal sgstAmt = BigDecimal.ZERO;
    private BigDecimal cessAmt = BigDecimal.ZERO;
    private String profitCenter;
    private String plant;
    private String division;
    private String location;
    private String salesOrg;
    private String distrChannel;
    private String userAccess1;
    private String userAccess2;
    private String userAccess3;
    private String userAccess4;
    private String userAccess5;
    private String userAccess6;
    private String userDefined1;
    private String userDefined2;
    private String userDefined3;
	public String getReturnTable() {
		return returnTable;
	}
	public void setReturnTable(String returnTable) {
		this.returnTable = returnTable;
	}
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	public BigDecimal getIgstAmt() {
		return igstAmt;
	}
	public void setIgstAmt(BigDecimal igstAmt) {
		this.igstAmt = igstAmt;
	}
	public BigDecimal getCgstAmt() {
		return cgstAmt;
	}
	public void setCgstAmt(BigDecimal cgstAmt) {
		this.cgstAmt = cgstAmt;
	}
	public BigDecimal getSgstAmt() {
		return sgstAmt;
	}
	public void setSgstAmt(BigDecimal sgstAmt) {
		this.sgstAmt = sgstAmt;
	}
	public BigDecimal getCessAmt() {
		return cessAmt;
	}
	public void setCessAmt(BigDecimal cessAmt) {
		this.cessAmt = cessAmt;
	}
	public String getProfitCenter() {
		return profitCenter;
	}
	public void setProfitCenter(String profitCenter) {
		this.profitCenter = profitCenter;
	}
	public String getPlant() {
		return plant;
	}
	public void setPlant(String plant) {
		this.plant = plant;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getSalesOrg() {
		return salesOrg;
	}
	public void setSalesOrg(String salesOrg) {
		this.salesOrg = salesOrg;
	}
	public String getDistrChannel() {
		return distrChannel;
	}
	public void setDistrChannel(String distrChannel) {
		this.distrChannel = distrChannel;
	}
	public String getUserAccess1() {
		return userAccess1;
	}
	public void setUserAccess1(String userAccess1) {
		this.userAccess1 = userAccess1;
	}
	public String getUserAccess2() {
		return userAccess2;
	}
	public void setUserAccess2(String userAccess2) {
		this.userAccess2 = userAccess2;
	}
	public String getUserAccess3() {
		return userAccess3;
	}
	public void setUserAccess3(String userAccess3) {
		this.userAccess3 = userAccess3;
	}
	public String getUserAccess4() {
		return userAccess4;
	}
	public void setUserAccess4(String userAccess4) {
		this.userAccess4 = userAccess4;
	}
	public String getUserAccess5() {
		return userAccess5;
	}
	public void setUserAccess5(String userAccess5) {
		this.userAccess5 = userAccess5;
	}
	public String getUserAccess6() {
		return userAccess6;
	}
	public void setUserAccess6(String userAccess6) {
		this.userAccess6 = userAccess6;
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
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	
	
    
}
