package com.ey.advisory.app.docs.dto.simplified;

import java.math.BigDecimal;

/**
 * This class is responsible for sending the response data to Review Summary
 * View
 * 
 * @author Mohana.Dasari
 *
 */
public class Annexure1SummaryResp1Dto {
	
	private Integer index =0;
	private String tableSection;
	private String docType;
	private Integer eyCount = 0;
	private BigDecimal eyInvoiceValue = BigDecimal.ZERO;
	private BigDecimal eyTaxableValue = BigDecimal.ZERO;
	private BigDecimal eyTaxPayble = BigDecimal.ZERO;
	private BigDecimal eyIgst = BigDecimal.ZERO;
	private BigDecimal eyCgst = BigDecimal.ZERO;
	private BigDecimal eySgst = BigDecimal.ZERO;
	private BigDecimal eyCess = BigDecimal.ZERO;
	private Integer memoCount = 0;
	private BigDecimal memoInvoiceValue = BigDecimal.ZERO;
	private BigDecimal memoTaxableValue = BigDecimal.ZERO;
	private BigDecimal memoTaxPayble = BigDecimal.ZERO;
	private BigDecimal memoIgst = BigDecimal.ZERO;
	private BigDecimal memoCgst = BigDecimal.ZERO;
	private BigDecimal memoSgst = BigDecimal.ZERO;
	private BigDecimal memoCess = BigDecimal.ZERO;
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
	
	public String getTableSection() {
		return tableSection;
	}
	public void setTableSection(String tableSection) {
		this.tableSection = tableSection;
	}
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public Integer getEyCount() {
		return eyCount;
	}
	public void setEyCount(Integer eyCount) {
		this.eyCount = eyCount;
	}
	public BigDecimal getEyInvoiceValue() {
		return eyInvoiceValue;
	}
	public void setEyInvoiceValue(BigDecimal eyInvoiceValue) {
		this.eyInvoiceValue = eyInvoiceValue;
	}
	public BigDecimal getEyTaxableValue() {
		return eyTaxableValue;
	}
	public void setEyTaxableValue(BigDecimal eyTaxableValue) {
		this.eyTaxableValue = eyTaxableValue;
	}
	public BigDecimal getEyTaxPayble() {
		return eyTaxPayble;
	}
	public void setEyTaxPayble(BigDecimal eyTaxPayble) {
		this.eyTaxPayble = eyTaxPayble;
	}
	public BigDecimal getEyIgst() {
		return eyIgst;
	}
	public void setEyIgst(BigDecimal eyIgst) {
		this.eyIgst = eyIgst;
	}
	public BigDecimal getEyCgst() {
		return eyCgst;
	}
	public void setEyCgst(BigDecimal eyCgst) {
		this.eyCgst = eyCgst;
	}
	public BigDecimal getEySgst() {
		return eySgst;
	}
	public void setEySgst(BigDecimal eySgst) {
		this.eySgst = eySgst;
	}
	public BigDecimal getEyCess() {
		return eyCess;
	}
	public void setEyCess(BigDecimal eyCess) {
		this.eyCess = eyCess;
	}
	public Integer getMemoCount() {
		return memoCount;
	}
	public void setMemoCount(Integer memoCount) {
		this.memoCount = memoCount;
	}
	public BigDecimal getMemoInvoiceValue() {
		return memoInvoiceValue;
	}
	public void setMemoInvoiceValue(BigDecimal memoInvoiceValue) {
		this.memoInvoiceValue = memoInvoiceValue;
	}
	public BigDecimal getMemoTaxableValue() {
		return memoTaxableValue;
	}
	public void setMemoTaxableValue(BigDecimal memoTaxableValue) {
		this.memoTaxableValue = memoTaxableValue;
	}
	public BigDecimal getMemoTaxPayble() {
		return memoTaxPayble;
	}
	public void setMemoTaxPayble(BigDecimal memoTaxPayble) {
		this.memoTaxPayble = memoTaxPayble;
	}
	public BigDecimal getMemoIgst() {
		return memoIgst;
	}
	public void setMemoIgst(BigDecimal memoIgst) {
		this.memoIgst = memoIgst;
	}
	public BigDecimal getMemoCgst() {
		return memoCgst;
	}
	public void setMemoCgst(BigDecimal memoCgst) {
		this.memoCgst = memoCgst;
	}
	public BigDecimal getMemoSgst() {
		return memoSgst;
	}
	public void setMemoSgst(BigDecimal memoSgst) {
		this.memoSgst = memoSgst;
	}
	public BigDecimal getMemoCess() {
		return memoCess;
	}
	public void setMemoCess(BigDecimal memoCess) {
		this.memoCess = memoCess;
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
	/**
	 * @return the index
	 */
	public Integer getIndex() {
		return index;
	}
	/**
	 * @param index the index to set
	 */
	public void setIndex(Integer index) {
		this.index = index;
	}
	
	
}
