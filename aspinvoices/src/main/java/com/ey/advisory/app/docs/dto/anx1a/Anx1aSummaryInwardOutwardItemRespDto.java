package com.ey.advisory.app.docs.dto.anx1a;

import java.math.BigDecimal;

public class Anx1aSummaryInwardOutwardItemRespDto {

	private String taxDocType;
	private String table;

	private Integer aspCount = 0;
	private BigDecimal aspInvoiceValue = BigDecimal.ZERO;
	private BigDecimal aspTaxableValue = BigDecimal.ZERO;
	private BigDecimal aspTaxPayble = BigDecimal.ZERO;
	private BigDecimal aspIgst = BigDecimal.ZERO;
	private BigDecimal aspCgst = BigDecimal.ZERO;
	private BigDecimal aspSgst = BigDecimal.ZERO;
	private BigDecimal aspCess = BigDecimal.ZERO;

	private Integer gstnCount = 0;
	private BigDecimal gstnInvoiceValue = BigDecimal.ZERO;
	private BigDecimal gstnTaxableValue = BigDecimal.ZERO;
	private BigDecimal gstnTaxPayble = BigDecimal.ZERO;
	private BigDecimal gstnIgst = BigDecimal.ZERO;
	private BigDecimal gstnCgst = BigDecimal.ZERO;
	private BigDecimal gstnSgst = BigDecimal.ZERO;
	private BigDecimal gstnCess = BigDecimal.ZERO;

	private Integer diffCount = 0;
	private BigDecimal diffInvoiceValue = BigDecimal.ZERO;
	private BigDecimal diffTaxableValue = BigDecimal.ZERO;
	private BigDecimal diffTaxPayble = BigDecimal.ZERO;
	private BigDecimal diffIgst = BigDecimal.ZERO;
	private BigDecimal diffCgst = BigDecimal.ZERO;
	private BigDecimal diffSgst = BigDecimal.ZERO;
	private BigDecimal diffCess = BigDecimal.ZERO;

	public String getTaxDocType() {
		return taxDocType;
	}

	public void setTaxDocType(String taxDocType) {
		this.taxDocType = taxDocType;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public Integer getAspCount() {
		return aspCount;
	}

	public void setAspCount(Integer aspCount) {
		this.aspCount = aspCount;
	}

	public BigDecimal getAspInvoiceValue() {
		return aspInvoiceValue;
	}

	public void setAspInvoiceValue(BigDecimal aspInvoiceValue) {
		this.aspInvoiceValue = aspInvoiceValue;
	}

	public BigDecimal getAspTaxableValue() {
		return aspTaxableValue;
	}

	public void setAspTaxableValue(BigDecimal aspTaxableValue) {
		this.aspTaxableValue = aspTaxableValue;
	}

	public BigDecimal getAspTaxPayble() {
		return aspTaxPayble;
	}

	public void setAspTaxPayble(BigDecimal aspTaxPayble) {
		this.aspTaxPayble = aspTaxPayble;
	}

	public BigDecimal getAspIgst() {
		return aspIgst;
	}

	public void setAspIgst(BigDecimal aspIgst) {
		this.aspIgst = aspIgst;
	}

	public BigDecimal getAspCgst() {
		return aspCgst;
	}

	public void setAspCgst(BigDecimal aspCgst) {
		this.aspCgst = aspCgst;
	}

	public BigDecimal getAspSgst() {
		return aspSgst;
	}

	public void setAspSgst(BigDecimal aspSgst) {
		this.aspSgst = aspSgst;
	}

	public BigDecimal getAspCess() {
		return aspCess;
	}

	public void setAspCess(BigDecimal aspCess) {
		this.aspCess = aspCess;
	}

	public Integer getGstnCount() {
		return gstnCount;
	}

	public void setGstnCount(Integer gstnCount) {
		this.gstnCount = gstnCount;
	}

	public BigDecimal getGstnInvoiceValue() {
		return gstnInvoiceValue;
	}

	public void setGstnInvoiceValue(BigDecimal gstnInvoiceValue) {
		this.gstnInvoiceValue = gstnInvoiceValue;
	}

	public BigDecimal getGstnTaxableValue() {
		return gstnTaxableValue;
	}

	public void setGstnTaxableValue(BigDecimal gstnTaxableValue) {
		this.gstnTaxableValue = gstnTaxableValue;
	}

	public BigDecimal getGstnTaxPayble() {
		return gstnTaxPayble;
	}

	public void setGstnTaxPayble(BigDecimal gstnTaxPayble) {
		this.gstnTaxPayble = gstnTaxPayble;
	}

	public BigDecimal getGstnIgst() {
		return gstnIgst;
	}

	public void setGstnIgst(BigDecimal gstnIgst) {
		this.gstnIgst = gstnIgst;
	}

	public BigDecimal getGstnCgst() {
		return gstnCgst;
	}

	public void setGstnCgst(BigDecimal gstnCgst) {
		this.gstnCgst = gstnCgst;
	}

	public BigDecimal getGstnSgst() {
		return gstnSgst;
	}

	public void setGstnSgst(BigDecimal gstnSgst) {
		this.gstnSgst = gstnSgst;
	}

	public BigDecimal getGstnCess() {
		return gstnCess;
	}

	public void setGstnCess(BigDecimal gstnCess) {
		this.gstnCess = gstnCess;
	}

	public Integer getDiffCount() {
		return diffCount;
	}

	public void setDiffCount(Integer diffCount) {
		this.diffCount = diffCount;
	}

	public BigDecimal getDiffInvoiceValue() {
		return diffInvoiceValue;
	}

	public void setDiffInvoiceValue(BigDecimal diffInvoiceValue) {
		this.diffInvoiceValue = diffInvoiceValue;
	}

	public BigDecimal getDiffTaxableValue() {
		return diffTaxableValue;
	}

	public void setDiffTaxableValue(BigDecimal diffTaxableValue) {
		this.diffTaxableValue = diffTaxableValue;
	}

	public BigDecimal getDiffTaxPayble() {
		return diffTaxPayble;
	}

	public void setDiffTaxPayble(BigDecimal diffTaxPayble) {
		this.diffTaxPayble = diffTaxPayble;
	}

	public BigDecimal getDiffIgst() {
		return diffIgst;
	}

	public void setDiffIgst(BigDecimal diffIgst) {
		this.diffIgst = diffIgst;
	}

	public BigDecimal getDiffCgst() {
		return diffCgst;
	}

	public void setDiffCgst(BigDecimal diffCgst) {
		this.diffCgst = diffCgst;
	}

	public BigDecimal getDiffSgst() {
		return diffSgst;
	}

	public void setDiffSgst(BigDecimal diffSgst) {
		this.diffSgst = diffSgst;
	}

	public BigDecimal getDiffCess() {
		return diffCess;
	}

	public void setDiffCess(BigDecimal diffCess) {
		this.diffCess = diffCess;
	}

	@Override
	public String toString() {
		return "Anx1aSummaryInwardOutwardRespDto [taxDocType=" + taxDocType
				+ ", table=" + table + ", aspCount=" + aspCount
				+ ", aspInvoiceValue=" + aspInvoiceValue + ", aspTaxableValue="
				+ aspTaxableValue + ", aspTaxPayble=" + aspTaxPayble
				+ ", aspIgst=" + aspIgst + ", aspCgst=" + aspCgst + ", aspSgst="
				+ aspSgst + ", aspCess=" + aspCess + ", gstnCount=" + gstnCount
				+ ", gstnInvoiceValue=" + gstnInvoiceValue
				+ ", gstnTaxableValue=" + gstnTaxableValue + ", gstnTaxPayble="
				+ gstnTaxPayble + ", gstnIgst=" + gstnIgst + ", gstnCgst="
				+ gstnCgst + ", gstnSgst=" + gstnSgst + ", gstnCess=" + gstnCess
				+ ", diffCount=" + diffCount + ", diffInvoiceValue="
				+ diffInvoiceValue + ", diffTaxableValue=" + diffTaxableValue
				+ ", diffTaxPayble=" + diffTaxPayble + ", diffIgst=" + diffIgst
				+ ", diffCgst=" + diffCgst + ", diffSgst=" + diffSgst
				+ ", diffCess=" + diffCess + "]";
	}

}
