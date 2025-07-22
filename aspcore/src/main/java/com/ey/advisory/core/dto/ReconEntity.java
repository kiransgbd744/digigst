package com.ey.advisory.core.dto;

import java.math.BigDecimal;

public class ReconEntity {

	protected Integer aid2;

	protected Integer prid;

	protected String a2InvType;

	protected String prInvType;

	protected BigDecimal a2TaxableValue;

	protected BigDecimal prTaxableValue;

	protected BigDecimal a2Igst;

	protected BigDecimal prIgst;

	protected BigDecimal a2Cgst;

	protected BigDecimal prCgst;

	protected BigDecimal a2Sgst;

	protected BigDecimal prSgst;

	protected BigDecimal prCess;

	protected BigDecimal a2Cess;

	protected BigDecimal avilableIgst;

	protected BigDecimal avilableCgst;

	protected BigDecimal avilableSgst;

	protected BigDecimal avilableCess;

	public Integer getAid2() {
		return aid2;
	}

	public void setAid2(Integer aid2) {
		this.aid2 = aid2;
	}

	public Integer getPrid() {
		return prid;
	}

	public void setPrid(Integer prid) {
		this.prid = prid;
	}

	public String getA2InvType() {
		return a2InvType;
	}

	public void setA2InvType(String a2InvType) {
		this.a2InvType = a2InvType;
	}

	public String getPrInvType() {
		return prInvType;
	}

	public void setPrInvType(String prInvType) {
		this.prInvType = prInvType;
	}

	public BigDecimal getA2TaxableValue() {
		return a2TaxableValue;
	}

	public void setA2TaxableValue(BigDecimal a2TaxableValue) {
		this.a2TaxableValue = a2TaxableValue;
	}

	public BigDecimal getPrTaxableValue() {
		return prTaxableValue;
	}

	public void setPrTaxableValue(BigDecimal prTaxableValue) {
		this.prTaxableValue = prTaxableValue;
	}

	public BigDecimal getA2Igst() {
		return a2Igst;
	}

	public void setA2Igst(BigDecimal a2Igst) {
		this.a2Igst = a2Igst;
	}

	public BigDecimal getPrIgst() {
		return prIgst;
	}

	public void setPrIgst(BigDecimal prIgst) {
		this.prIgst = prIgst;
	}

	public BigDecimal getA2Cgst() {
		return a2Cgst;
	}

	public void setA2Cgst(BigDecimal a2Cgst) {
		this.a2Cgst = a2Cgst;
	}

	public BigDecimal getPrCgst() {
		return prCgst;
	}

	public void setPrCgst(BigDecimal prCgst) {
		this.prCgst = prCgst;
	}

	public BigDecimal getA2Sgst() {
		return a2Sgst;
	}

	public void setA2Sgst(BigDecimal a2Sgst) {
		this.a2Sgst = a2Sgst;
	}

	public BigDecimal getPrSgst() {
		return prSgst;
	}

	public void setPrSgst(BigDecimal prSgst) {
		this.prSgst = prSgst;
	}

	public BigDecimal getPrCess() {
		return prCess;
	}

	public void setPrCess(BigDecimal prCess) {
		this.prCess = prCess;
	}

	public BigDecimal getA2Cess() {
		return a2Cess;
	}

	public void setA2Cess(BigDecimal a2Cess) {
		this.a2Cess = a2Cess;
	}

	public BigDecimal getAvilableIgst() {
		return avilableIgst;
	}

	public void setAvilableIgst(BigDecimal avilableIgst) {
		this.avilableIgst = avilableIgst;
	}

	public BigDecimal getAvilableCgst() {
		return avilableCgst;
	}

	public void setAvilableCgst(BigDecimal avilableCgst) {
		this.avilableCgst = avilableCgst;
	}

	public BigDecimal getAvilableSgst() {
		return avilableSgst;
	}

	public void setAvilableSgst(BigDecimal avilableSgst) {
		this.avilableSgst = avilableSgst;
	}

	public BigDecimal getAvilableCess() {
		return avilableCess;
	}

	public void setAvilableCess(BigDecimal avilableCess) {
		this.avilableCess = avilableCess;
	}

	@Override
	public String toString() {
		return "ReconEntity [aid2=" + aid2 + ", prid=" + prid + ", a2InvType="
				+ a2InvType + ", prInvType=" + prInvType + ", a2TaxableValue="
				+ a2TaxableValue + ", prTaxableValue=" + prTaxableValue
				+ ", a2Igst=" + a2Igst + ", prIgst=" + prIgst + ", a2Cgst="
				+ a2Cgst + ", prCgst=" + prCgst + ", a2Sgst=" + a2Sgst
				+ ", prSgst=" + prSgst + ", prCess=" + prCess + ", a2Cess="
				+ a2Cess + ", avilableIgst=" + avilableIgst + ", avilableCgst="
				+ avilableCgst + ", avilableSgst=" + avilableSgst
				+ ", avilableCess=" + avilableCess + "]";
	}

}