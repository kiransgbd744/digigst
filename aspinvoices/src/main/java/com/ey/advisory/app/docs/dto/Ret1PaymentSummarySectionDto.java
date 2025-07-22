package com.ey.advisory.app.docs.dto;
/**
 * 
 * @author Balakrishna.S
 *
 */

import java.math.BigDecimal;

public class Ret1PaymentSummarySectionDto {

	private String table;
	private String supplyType;
	private BigDecimal asptaxPayableRc = BigDecimal.ZERO;
	private BigDecimal asptaxPayableOtherRc = BigDecimal.ZERO;
	private BigDecimal asptaxAlreadyPaidRc = BigDecimal.ZERO;
	private BigDecimal asptaxAlreadyPaidotherRc = BigDecimal.ZERO;
	private BigDecimal aspadjOflibRc = BigDecimal.ZERO;
	private BigDecimal aspadjOflibOtherRc = BigDecimal.ZERO;
	private BigDecimal asppaidThroughIgst = BigDecimal.ZERO;
	private BigDecimal asppaidThroughCgst = BigDecimal.ZERO;
	private BigDecimal asppaidThroughSgst = BigDecimal.ZERO;
	private BigDecimal asppaidThroughCess = BigDecimal.ZERO;
	private BigDecimal asppaidIncash_tax = BigDecimal.ZERO;
	private BigDecimal asppaidIncash_interest = BigDecimal.ZERO;
	private BigDecimal asppaidIncash_latefee = BigDecimal.ZERO;
	private BigDecimal gstntaxPayable_rc = BigDecimal.ZERO;
	private BigDecimal gstntaxPayable_Other_rc = BigDecimal.ZERO;
	private BigDecimal gstnalready_paid_rc = BigDecimal.ZERO;
	private BigDecimal gstnalready_paid_Other_rc = BigDecimal.ZERO;
	private BigDecimal gstnadjOflibRc = BigDecimal.ZERO;
	private BigDecimal gstnadjOflibOtherRc = BigDecimal.ZERO;
	private BigDecimal gstnpaidThroughIgst = BigDecimal.ZERO;
	private BigDecimal gstnpaidThroughCgst = BigDecimal.ZERO;
	private BigDecimal gstnpaidThroughSgst = BigDecimal.ZERO;
	private BigDecimal gstnpaidThroughCess = BigDecimal.ZERO;
	private BigDecimal gstnpaidIncash_tax = BigDecimal.ZERO;
	private BigDecimal gstnpaidIncash_interest = BigDecimal.ZERO;
	private BigDecimal gstnpaidIncash_latefee = BigDecimal.ZERO;
	/**
	 * @return the table
	 */
	public String getTable() {
		return table;
	}
	/**
	 * @param table the table to set
	 */
	public void setTable(String table) {
		this.table = table;
	}
	/**
	 * @return the supplyType
	 */
	public String getSupplyType() {
		return supplyType;
	}
	/**
	 * @param supplyType the supplyType to set
	 */
	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}
	/**
	 * @return the asptaxPayableRc
	 */
	public BigDecimal getAsptaxPayableRc() {
		return asptaxPayableRc;
	}
	/**
	 * @param asptaxPayableRc the asptaxPayableRc to set
	 */
	public void setAsptaxPayableRc(BigDecimal asptaxPayableRc) {
		this.asptaxPayableRc = asptaxPayableRc;
	}
	/**
	 * @return the asptaxPayableOtherRc
	 */
	public BigDecimal getAsptaxPayableOtherRc() {
		return asptaxPayableOtherRc;
	}
	/**
	 * @param asptaxPayableOtherRc the asptaxPayableOtherRc to set
	 */
	public void setAsptaxPayableOtherRc(BigDecimal asptaxPayableOtherRc) {
		this.asptaxPayableOtherRc = asptaxPayableOtherRc;
	}
	/**
	 * @return the asptaxAlreadyPaidRc
	 */
	public BigDecimal getAsptaxAlreadyPaidRc() {
		return asptaxAlreadyPaidRc;
	}
	/**
	 * @param asptaxAlreadyPaidRc the asptaxAlreadyPaidRc to set
	 */
	public void setAsptaxAlreadyPaidRc(BigDecimal asptaxAlreadyPaidRc) {
		this.asptaxAlreadyPaidRc = asptaxAlreadyPaidRc;
	}
	/**
	 * @return the asptaxAlreadyPaidotherRc
	 */
	public BigDecimal getAsptaxAlreadyPaidotherRc() {
		return asptaxAlreadyPaidotherRc;
	}
	/**
	 * @param asptaxAlreadyPaidotherRc the asptaxAlreadyPaidotherRc to set
	 */
	public void setAsptaxAlreadyPaidotherRc(BigDecimal asptaxAlreadyPaidotherRc) {
		this.asptaxAlreadyPaidotherRc = asptaxAlreadyPaidotherRc;
	}
	/**
	 * @return the aspadjOflibRc
	 */
	public BigDecimal getAspadjOflibRc() {
		return aspadjOflibRc;
	}
	/**
	 * @param aspadjOflibRc the aspadjOflibRc to set
	 */
	public void setAspadjOflibRc(BigDecimal aspadjOflibRc) {
		this.aspadjOflibRc = aspadjOflibRc;
	}
	/**
	 * @return the aspadjOflibOtherRc
	 */
	public BigDecimal getAspadjOflibOtherRc() {
		return aspadjOflibOtherRc;
	}
	/**
	 * @param aspadjOflibOtherRc the aspadjOflibOtherRc to set
	 */
	public void setAspadjOflibOtherRc(BigDecimal aspadjOflibOtherRc) {
		this.aspadjOflibOtherRc = aspadjOflibOtherRc;
	}
	/**
	 * @return the asppaidThroughIgst
	 */
	public BigDecimal getAsppaidThroughIgst() {
		return asppaidThroughIgst;
	}
	/**
	 * @param asppaidThroughIgst the asppaidThroughIgst to set
	 */
	public void setAsppaidThroughIgst(BigDecimal asppaidThroughIgst) {
		this.asppaidThroughIgst = asppaidThroughIgst;
	}
	/**
	 * @return the asppaidThroughCgst
	 */
	public BigDecimal getAsppaidThroughCgst() {
		return asppaidThroughCgst;
	}
	/**
	 * @param asppaidThroughCgst the asppaidThroughCgst to set
	 */
	public void setAsppaidThroughCgst(BigDecimal asppaidThroughCgst) {
		this.asppaidThroughCgst = asppaidThroughCgst;
	}
	/**
	 * @return the asppaidThroughSgst
	 */
	public BigDecimal getAsppaidThroughSgst() {
		return asppaidThroughSgst;
	}
	/**
	 * @param asppaidThroughSgst the asppaidThroughSgst to set
	 */
	public void setAsppaidThroughSgst(BigDecimal asppaidThroughSgst) {
		this.asppaidThroughSgst = asppaidThroughSgst;
	}
	/**
	 * @return the asppaidThroughCess
	 */
	public BigDecimal getAsppaidThroughCess() {
		return asppaidThroughCess;
	}
	/**
	 * @param asppaidThroughCess the asppaidThroughCess to set
	 */
	public void setAsppaidThroughCess(BigDecimal asppaidThroughCess) {
		this.asppaidThroughCess = asppaidThroughCess;
	}
	/**
	 * @return the asppaidIncash_tax
	 */
	public BigDecimal getAsppaidIncash_tax() {
		return asppaidIncash_tax;
	}
	/**
	 * @param asppaidIncash_tax the asppaidIncash_tax to set
	 */
	public void setAsppaidIncash_tax(BigDecimal asppaidIncash_tax) {
		this.asppaidIncash_tax = asppaidIncash_tax;
	}
	/**
	 * @return the asppaidIncash_interest
	 */
	public BigDecimal getAsppaidIncash_interest() {
		return asppaidIncash_interest;
	}
	/**
	 * @param asppaidIncash_interest the asppaidIncash_interest to set
	 */
	public void setAsppaidIncash_interest(BigDecimal asppaidIncash_interest) {
		this.asppaidIncash_interest = asppaidIncash_interest;
	}
	/**
	 * @return the asppaidIncash_latefee
	 */
	public BigDecimal getAsppaidIncash_latefee() {
		return asppaidIncash_latefee;
	}
	/**
	 * @param asppaidIncash_latefee the asppaidIncash_latefee to set
	 */
	public void setAsppaidIncash_latefee(BigDecimal asppaidIncash_latefee) {
		this.asppaidIncash_latefee = asppaidIncash_latefee;
	}
	/**
	 * @return the gstntaxPayable_rc
	 */
	public BigDecimal getGstntaxPayable_rc() {
		return gstntaxPayable_rc;
	}
	/**
	 * @param gstntaxPayable_rc the gstntaxPayable_rc to set
	 */
	public void setGstntaxPayable_rc(BigDecimal gstntaxPayable_rc) {
		this.gstntaxPayable_rc = gstntaxPayable_rc;
	}
	/**
	 * @return the gstntaxPayable_Other_rc
	 */
	public BigDecimal getGstntaxPayable_Other_rc() {
		return gstntaxPayable_Other_rc;
	}
	/**
	 * @param gstntaxPayable_Other_rc the gstntaxPayable_Other_rc to set
	 */
	public void setGstntaxPayable_Other_rc(BigDecimal gstntaxPayable_Other_rc) {
		this.gstntaxPayable_Other_rc = gstntaxPayable_Other_rc;
	}
	/**
	 * @return the gstnalready_paid_rc
	 */
	public BigDecimal getGstnalready_paid_rc() {
		return gstnalready_paid_rc;
	}
	/**
	 * @param gstnalready_paid_rc the gstnalready_paid_rc to set
	 */
	public void setGstnalready_paid_rc(BigDecimal gstnalready_paid_rc) {
		this.gstnalready_paid_rc = gstnalready_paid_rc;
	}
	/**
	 * @return the gstnalready_paid_Other_rc
	 */
	public BigDecimal getGstnalready_paid_Other_rc() {
		return gstnalready_paid_Other_rc;
	}
	/**
	 * @param gstnalready_paid_Other_rc the gstnalready_paid_Other_rc to set
	 */
	public void setGstnalready_paid_Other_rc(BigDecimal gstnalready_paid_Other_rc) {
		this.gstnalready_paid_Other_rc = gstnalready_paid_Other_rc;
	}
	/**
	 * @return the gstnadjOflibRc
	 */
	public BigDecimal getGstnadjOflibRc() {
		return gstnadjOflibRc;
	}
	/**
	 * @param gstnadjOflibRc the gstnadjOflibRc to set
	 */
	public void setGstnadjOflibRc(BigDecimal gstnadjOflibRc) {
		this.gstnadjOflibRc = gstnadjOflibRc;
	}
	/**
	 * @return the gstnadjOflibOtherRc
	 */
	public BigDecimal getGstnadjOflibOtherRc() {
		return gstnadjOflibOtherRc;
	}
	/**
	 * @param gstnadjOflibOtherRc the gstnadjOflibOtherRc to set
	 */
	public void setGstnadjOflibOtherRc(BigDecimal gstnadjOflibOtherRc) {
		this.gstnadjOflibOtherRc = gstnadjOflibOtherRc;
	}
	/**
	 * @return the gstnpaidThroughIgst
	 */
	public BigDecimal getGstnpaidThroughIgst() {
		return gstnpaidThroughIgst;
	}
	/**
	 * @param gstnpaidThroughIgst the gstnpaidThroughIgst to set
	 */
	public void setGstnpaidThroughIgst(BigDecimal gstnpaidThroughIgst) {
		this.gstnpaidThroughIgst = gstnpaidThroughIgst;
	}
	/**
	 * @return the gstnpaidThroughCgst
	 */
	public BigDecimal getGstnpaidThroughCgst() {
		return gstnpaidThroughCgst;
	}
	/**
	 * @param gstnpaidThroughCgst the gstnpaidThroughCgst to set
	 */
	public void setGstnpaidThroughCgst(BigDecimal gstnpaidThroughCgst) {
		this.gstnpaidThroughCgst = gstnpaidThroughCgst;
	}
	/**
	 * @return the gstnpaidThroughSgst
	 */
	public BigDecimal getGstnpaidThroughSgst() {
		return gstnpaidThroughSgst;
	}
	/**
	 * @param gstnpaidThroughSgst the gstnpaidThroughSgst to set
	 */
	public void setGstnpaidThroughSgst(BigDecimal gstnpaidThroughSgst) {
		this.gstnpaidThroughSgst = gstnpaidThroughSgst;
	}
	/**
	 * @return the gstnpaidThroughCess
	 */
	public BigDecimal getGstnpaidThroughCess() {
		return gstnpaidThroughCess;
	}
	/**
	 * @param gstnpaidThroughCess the gstnpaidThroughCess to set
	 */
	public void setGstnpaidThroughCess(BigDecimal gstnpaidThroughCess) {
		this.gstnpaidThroughCess = gstnpaidThroughCess;
	}
	/**
	 * @return the gstnpaidIncash_tax
	 */
	public BigDecimal getGstnpaidIncash_tax() {
		return gstnpaidIncash_tax;
	}
	/**
	 * @param gstnpaidIncash_tax the gstnpaidIncash_tax to set
	 */
	public void setGstnpaidIncash_tax(BigDecimal gstnpaidIncash_tax) {
		this.gstnpaidIncash_tax = gstnpaidIncash_tax;
	}
	/**
	 * @return the gstnpaidIncash_interest
	 */
	public BigDecimal getGstnpaidIncash_interest() {
		return gstnpaidIncash_interest;
	}
	/**
	 * @param gstnpaidIncash_interest the gstnpaidIncash_interest to set
	 */
	public void setGstnpaidIncash_interest(BigDecimal gstnpaidIncash_interest) {
		this.gstnpaidIncash_interest = gstnpaidIncash_interest;
	}
	/**
	 * @return the gstnpaidIncash_latefee
	 */
	public BigDecimal getGstnpaidIncash_latefee() {
		return gstnpaidIncash_latefee;
	}
	/**
	 * @param gstnpaidIncash_latefee the gstnpaidIncash_latefee to set
	 */
	public void setGstnpaidIncash_latefee(BigDecimal gstnpaidIncash_latefee) {
		this.gstnpaidIncash_latefee = gstnpaidIncash_latefee;
	}

}
