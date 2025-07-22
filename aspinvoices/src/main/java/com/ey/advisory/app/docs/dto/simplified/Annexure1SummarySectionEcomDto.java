package com.ey.advisory.app.docs.dto.simplified;

import java.math.BigDecimal;

/**
 * 
 * @author BalaKrishna S
 *
 */

public class Annexure1SummarySectionEcomDto {

	private Integer records = 0;
	private String returnPeriod;
	private String gstinNum;
	private String tableSection;
	private BigDecimal supplyMade = BigDecimal.ZERO;
	private BigDecimal supplyReturn = BigDecimal.ZERO;
	private BigDecimal netSupply = BigDecimal.ZERO;
	private BigDecimal taxPayble = BigDecimal.ZERO;
	private BigDecimal igst = BigDecimal.ZERO;
	private BigDecimal cgst = BigDecimal.ZERO;
	private BigDecimal sgst = BigDecimal.ZERO;
	private BigDecimal cess = BigDecimal.ZERO;
	private BigDecimal memoIgst = BigDecimal.ZERO;
	private BigDecimal memoSgst = BigDecimal.ZERO;
	private BigDecimal memoCgst = BigDecimal.ZERO;
	private BigDecimal memoTaxPayable = BigDecimal.ZERO;
	private Integer gstnCount = 0;
	private BigDecimal gstnSupplyMade = BigDecimal.ZERO;
	private BigDecimal gstnSupplyReturn = BigDecimal.ZERO;
	private BigDecimal gstnNetSupply = BigDecimal.ZERO;
	private BigDecimal gstnTaxPayble = BigDecimal.ZERO;
	private BigDecimal gstnIgst = BigDecimal.ZERO;
	private BigDecimal gstnCgst = BigDecimal.ZERO;
	private BigDecimal gstnSgst = BigDecimal.ZERO;
	private BigDecimal gstnCess = BigDecimal.ZERO;

	public Annexure1SummarySectionEcomDto() {

	}

	public Annexure1SummarySectionEcomDto(String section) {
		this.tableSection = section;
	}

	public Annexure1SummarySectionEcomDto(String gstinNum, String returnPeriod,
			String section, Integer count, BigDecimal netSupply,
			BigDecimal taxPayble, BigDecimal supplyMade,
			BigDecimal supplyReturn, BigDecimal igstAmt, BigDecimal cgstAmt,
			BigDecimal sgstAmt, BigDecimal cessAmt, BigDecimal memoIgst,
			BigDecimal memoSgst, BigDecimal memoCgst, BigDecimal memoCess,
			BigDecimal memoTaxPayable) {
		// Integer count = count;

		this.gstinNum = gstinNum;
		this.returnPeriod = returnPeriod;
		this.tableSection = section;

		this.records = (count != null) ? count : 0;
		this.supplyMade = (supplyMade != null) ? supplyMade : BigDecimal.ZERO;
		this.supplyReturn = (supplyReturn != null) ? supplyReturn
				: BigDecimal.ZERO;
		this.netSupply = (netSupply != null) ? netSupply : BigDecimal.ZERO;
		this.taxPayble = (taxPayble != null) ? taxPayble : BigDecimal.ZERO;
		this.igst = (igstAmt != null) ? igstAmt : BigDecimal.ZERO;
		this.cgst = (cgstAmt != null) ? cgstAmt : BigDecimal.ZERO;
		this.sgst = (sgstAmt != null) ? sgstAmt : BigDecimal.ZERO;
		this.cess = (cessAmt != null) ? cessAmt : BigDecimal.ZERO;

		this.memoIgst = (memoIgst != null) ? memoIgst : BigDecimal.ZERO;
		this.memoSgst = (memoSgst != null) ? memoSgst : BigDecimal.ZERO;
		this.memoCgst = (memoCgst != null) ? memoCgst : BigDecimal.ZERO;
		this.memoTaxPayable = (memoTaxPayable != null) ? memoTaxPayable
				: BigDecimal.ZERO;
	}

	public Annexure1SummarySectionEcomDto add(
			Annexure1SummarySectionEcomDto other) {
		this.records = this.records + other.records;
		this.tableSection = other.tableSection;
		this.supplyMade = this.supplyMade.add(other.supplyMade);
		this.taxPayble = this.taxPayble.add(other.taxPayble);
		this.supplyReturn = this.supplyReturn.add(other.supplyReturn);
		this.netSupply = this.netSupply.add(other.netSupply);
		this.igst = this.igst.add(other.igst);
		this.sgst = this.sgst.add(other.sgst);
		this.cgst = this.cgst.add(other.cgst);
		this.cess = this.cess.add(other.cess);
		this.memoIgst = this.memoIgst.add(other.memoIgst);
		this.memoSgst = this.memoSgst.add(other.memoSgst);
		this.memoCgst = this.memoCgst.add(other.memoCgst);
		this.memoTaxPayable = this.memoTaxPayable.add(other.memoTaxPayable);
		return this;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getGstinNum() {
		return gstinNum;
	}

	public void setGstinNum(String gstinNum) {
		this.gstinNum = gstinNum;
	}

	/**
	 * @return the records
	 */
	public Integer getRecords() {
		return records;
	}

	/**
	 * @param records
	 *            the records to set
	 */
	public void setRecords(Integer records) {
		this.records = records != null ? records : 0;
	}

	/**
	 * @return the tableSection
	 */
	public String getTableSection() {
		return tableSection;
	}

	/**
	 * @param tableSection
	 *            the tableSection to set
	 */
	public void setTableSection(String tableSection) {
		this.tableSection = tableSection;
	}

	/**
	 * @return the supplyMade
	 */
	public BigDecimal getSupplyMade() {
		return supplyMade;
	}

	/**
	 * @param supplyMade
	 *            the supplyMade to set
	 */
	public void setSupplyMade(BigDecimal supplyMade) {
		this.supplyMade = supplyMade != null ? supplyMade : BigDecimal.ZERO;
	}

	/**
	 * @return the supplyReturn
	 */
	public BigDecimal getSupplyReturn() {
		return supplyReturn;
	}

	/**
	 * @param supplyReturn
	 *            the supplyReturn to set
	 */
	public void setSupplyReturn(BigDecimal supplyReturn) {
		this.supplyReturn = supplyReturn != null ? supplyReturn
				: BigDecimal.ZERO;
	}

	/**
	 * @return the netSupply
	 */
	public BigDecimal getNetSupply() {
		return netSupply;
	}

	/**
	 * @param netSupply
	 *            the netSupply to set
	 */
	public void setNetSupply(BigDecimal netSupply) {
		this.netSupply = netSupply != null ? netSupply : BigDecimal.ZERO;
	}

	/**
	 * @return the taxPayble
	 */
	public BigDecimal getTaxPayble() {
		return taxPayble;
	}

	/**
	 * @param taxPayble
	 *            the taxPayble to set
	 */
	public void setTaxPayble(BigDecimal taxPayble) {
		this.taxPayble = taxPayble != null ? taxPayble : BigDecimal.ZERO;
	}

	/**
	 * @return the igst
	 */
	public BigDecimal getIgst() {
		return igst;
	}

	/**
	 * @param igst
	 *            the igst to set
	 */
	public void setIgst(BigDecimal igst) {
		this.igst = igst != null ? igst : BigDecimal.ZERO;
	}

	/**
	 * @return the cgst
	 */
	public BigDecimal getCgst() {
		return cgst;
	}

	/**
	 * @param cgst
	 *            the cgst to set
	 */
	public void setCgst(BigDecimal cgst) {
		this.cgst = cgst != null ? cgst : BigDecimal.ZERO;
	}

	/**
	 * @return the sgst
	 */
	public BigDecimal getSgst() {
		return sgst;
	}

	/**
	 * @param sgst
	 *            the sgst to set
	 */
	public void setSgst(BigDecimal sgst) {
		this.sgst = sgst != null ? sgst : BigDecimal.ZERO;
	}

	/**
	 * @return the cess
	 */
	public BigDecimal getCess() {
		return cess;
	}

	/**
	 * @param cess
	 *            the cess to set
	 */
	public void setCess(BigDecimal cess) {
		this.cess = cess != null ? cess : BigDecimal.ZERO;
	}

	/**
	 * @return the memoIgst
	 */
	public BigDecimal getMemoIgst() {
		return memoIgst;
	}

	/**
	 * @param memoIgst
	 *            the memoIgst to set
	 */
	public void setMemoIgst(BigDecimal memoIgst) {
		this.memoIgst = memoIgst != null ? memoIgst : BigDecimal.ZERO;
	}

	/**
	 * @return the memoSgst
	 */
	public BigDecimal getMemoSgst() {
		return memoSgst;
	}

	/**
	 * @param memoSgst
	 *            the memoSgst to set
	 */
	public void setMemoSgst(BigDecimal memoSgst) {
		this.memoSgst = memoSgst != null ? memoSgst : BigDecimal.ZERO;
	}

	/**
	 * @return the memoCgst
	 */
	public BigDecimal getMemoCgst() {
		return memoCgst;
	}

	/**
	 * @param memoCgst
	 *            the memoCgst to set
	 */
	public void setMemoCgst(BigDecimal memoCgst) {
		this.memoCgst = memoCgst != null ? memoCgst : BigDecimal.ZERO;
	}

	/**
	 * @return the memoTaxPayable
	 */
	public BigDecimal getMemoTaxPayable() {
		return memoTaxPayable;
	}

	/**
	 * @param memoTaxPayable
	 *            the memoTaxPayable to set
	 */
	public void setMemoTaxPayable(BigDecimal memoTaxPayable) {
		this.memoTaxPayable = memoTaxPayable != null ? memoTaxPayable
				: BigDecimal.ZERO;
	}

	public Integer getGstnCount() {
		return gstnCount;
	}

	public void setGstnCount(Integer gstnCount) {
		this.gstnCount = gstnCount;
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

}
