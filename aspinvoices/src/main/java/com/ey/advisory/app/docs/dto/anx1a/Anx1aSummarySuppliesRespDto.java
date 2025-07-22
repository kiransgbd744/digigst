package com.ey.advisory.app.docs.dto.anx1a;

import java.math.BigDecimal;

public class Anx1aSummarySuppliesRespDto {

	private String taxDocType;
	private String table;

	private BigDecimal aspSupplyMade = BigDecimal.ZERO;
	private BigDecimal aspSupplyReturn = BigDecimal.ZERO;
	private BigDecimal aspNetSupply = BigDecimal.ZERO;
	private BigDecimal aspTaxPayble = BigDecimal.ZERO;
	private BigDecimal aspIgst = BigDecimal.ZERO;
	private BigDecimal aspCgst = BigDecimal.ZERO;
	private BigDecimal aspSgst = BigDecimal.ZERO;
	private BigDecimal aspCess = BigDecimal.ZERO;

	private BigDecimal gstnSupplyMade = BigDecimal.ZERO;
	private BigDecimal gstnSupplyReturn = BigDecimal.ZERO;
	private BigDecimal gstnNetSupply = BigDecimal.ZERO;
	private BigDecimal gstnTaxPayble = BigDecimal.ZERO;
	private BigDecimal gstnIgst = BigDecimal.ZERO;
	private BigDecimal gstnCgst = BigDecimal.ZERO;
	private BigDecimal gstnSgst = BigDecimal.ZERO;
	private BigDecimal gstnCess = BigDecimal.ZERO;

	private BigDecimal diffSupplyMade = BigDecimal.ZERO;
	private BigDecimal diffSupplyReturn = BigDecimal.ZERO;
	private BigDecimal diffNetSupply = BigDecimal.ZERO;
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

	public BigDecimal getAspSupplyMade() {
		return aspSupplyMade;
	}

	public void setAspSupplyMade(BigDecimal aspSupplyMade) {
		this.aspSupplyMade = aspSupplyMade;
	}

	public BigDecimal getAspSupplyReturn() {
		return aspSupplyReturn;
	}

	public void setAspSupplyReturn(BigDecimal aspSupplyReturn) {
		this.aspSupplyReturn = aspSupplyReturn;
	}

	public BigDecimal getAspNetSupply() {
		return aspNetSupply;
	}

	public void setAspNetSupply(BigDecimal aspNetSupply) {
		this.aspNetSupply = aspNetSupply;
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

	public BigDecimal getGstnSupplyMade() {
		return gstnSupplyMade;
	}

	public void setGstnSupplyMade(BigDecimal gstnSupplyMade) {
		this.gstnSupplyMade = gstnSupplyMade;
	}

	public BigDecimal getGstnSupplyReturn() {
		return gstnSupplyReturn;
	}

	public void setGstnSupplyReturn(BigDecimal gstnSupplyReturn) {
		this.gstnSupplyReturn = gstnSupplyReturn;
	}

	public BigDecimal getGstnNetSupply() {
		return gstnNetSupply;
	}

	public void setGstnNetSupply(BigDecimal gstnNetSupply) {
		this.gstnNetSupply = gstnNetSupply;
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

	public BigDecimal getDiffSupplyMade() {
		return diffSupplyMade;
	}

	public void setDiffSupplyMade(BigDecimal diffSupplyMade) {
		this.diffSupplyMade = diffSupplyMade;
	}

	public BigDecimal getDiffSupplyReturn() {
		return diffSupplyReturn;
	}

	public void setDiffSupplyReturn(BigDecimal diffSupplyReturn) {
		this.diffSupplyReturn = diffSupplyReturn;
	}

	public BigDecimal getDiffNetSupply() {
		return diffNetSupply;
	}

	public void setDiffNetSupply(BigDecimal diffNetSupply) {
		this.diffNetSupply = diffNetSupply;
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

}
