package com.ey.advisory.app.docs.dto.gstr2;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaxCrMatchSummaryDto {
	
	@Expose
	@SerializedName("perticulars")
	private String perticulars;
	@Expose
	@SerializedName("purchaseCountOfTransn")
	private Long purchaseCountOfTransn;
	@Expose
	@SerializedName("purRegPercentage")
	private Long purchaseRegPercentage;
	@Expose
	@SerializedName("purchaseTaxableValue")
	private BigDecimal purTaxableValue;
	@Expose
	@SerializedName("purchaseTotalTax")
	private BigDecimal purTotalTax;
	@Expose
	@SerializedName("gstr2ACountOfTransactions")
	private Long gstr2ACountOfTransn;
	@Expose
	@SerializedName("gstr2APercentage")
	private Long gstr2APercentage;
	@Expose
	@SerializedName("gstr2AtaxableValue")
	private BigDecimal gstr2AtaxableValue;
	@Expose
	@SerializedName("gstr2ATotalTax")
	private BigDecimal gstr2ATotalTax;
	/**
	 * @return the perticulars
	 */
	public String getPerticulars() {
		return perticulars;
	}
	/**
	 * @param perticulars the perticulars to set
	 */
	public void setPerticulars(String perticulars) {
		this.perticulars = perticulars;
	}
	/**
	 * @return the purchaseCountOfTransn
	 */
	public Long getPurchaseCountOfTransn() {
		return purchaseCountOfTransn;
	}
	/**
	 * @param purchaseCountOfTransn the purchaseCountOfTransn to set
	 */
	public void setPurchaseCountOfTransn(Long purchaseCountOfTransn) {
		this.purchaseCountOfTransn = purchaseCountOfTransn;
	}
	/**
	 * @return the purchaseRegPercentage
	 */
	public Long getPurchaseRegPercentage() {
		return purchaseRegPercentage;
	}
	/**
	 * @param purchaseRegPercentage the purchaseRegPercentage to set
	 */
	public void setPurchaseRegPercentage(Long purchaseRegPercentage) {
		this.purchaseRegPercentage = purchaseRegPercentage;
	}
	/**
	 * @return the purTaxableValue
	 */
	public BigDecimal getPurTaxableValue() {
		return purTaxableValue;
	}
	/**
	 * @param purTaxableValue the purTaxableValue to set
	 */
	public void setPurTaxableValue(BigDecimal purTaxableValue) {
		this.purTaxableValue = purTaxableValue;
	}
	/**
	 * @return the purTotalTax
	 */
	public BigDecimal getPurTotalTax() {
		return purTotalTax;
	}
	/**
	 * @param purTotalTax the purTotalTax to set
	 */
	public void setPurTotalTax(BigDecimal purTotalTax) {
		this.purTotalTax = purTotalTax;
	}
	/**
	 * @return the gstr2ACountOfTransn
	 */
	public Long getGstr2ACountOfTransn() {
		return gstr2ACountOfTransn;
	}
	/**
	 * @param gstr2aCountOfTransn the gstr2ACountOfTransn to set
	 */
	public void setGstr2ACountOfTransn(Long gstr2aCountOfTransn) {
		gstr2ACountOfTransn = gstr2aCountOfTransn;
	}
	/**
	 * @return the gstr2APercentage
	 */
	public Long getGstr2APercentage() {
		return gstr2APercentage;
	}
	/**
	 * @param gstr2aPercentage the gstr2APercentage to set
	 */
	public void setGstr2APercentage(Long gstr2aPercentage) {
		gstr2APercentage = gstr2aPercentage;
	}
	/**
	 * @return the gstr2AtaxableValue
	 */
	public BigDecimal getGstr2AtaxableValue() {
		return gstr2AtaxableValue;
	}
	/**
	 * @param gstr2AtaxableValue the gstr2AtaxableValue to set
	 */
	public void setGstr2AtaxableValue(BigDecimal gstr2AtaxableValue) {
		this.gstr2AtaxableValue = gstr2AtaxableValue;
	}
	/**
	 * @return the gstr2ATotalTax
	 */
	public BigDecimal getGstr2ATotalTax() {
		return gstr2ATotalTax;
	}
	/**
	 * @param gstr2aTotalTax the gstr2ATotalTax to set
	 */
	public void setGstr2ATotalTax(BigDecimal gstr2aTotalTax) {
		gstr2ATotalTax = gstr2aTotalTax;
	}
	
	

}
