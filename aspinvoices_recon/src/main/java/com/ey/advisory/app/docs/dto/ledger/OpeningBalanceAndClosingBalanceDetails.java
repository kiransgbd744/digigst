package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class OpeningBalanceAndClosingBalanceDetails {

	@Expose
	@SerializedName("desc")
	private String desc;

	@Expose
	@SerializedName("igstTaxBal")
	private BigDecimal igstTaxBal;

	@Expose
	@SerializedName("cgstTaxBal")
	private BigDecimal cgstTaxBal;

	@Expose
	@SerializedName("sgstTaxBal")
	private BigDecimal sgstTaxBal;

	@Expose
	@SerializedName("cessTaxBal")
	private BigDecimal cessTaxBal;

	@Expose
	@SerializedName("tot_rng_bal")
	private BigDecimal totRunningBal;

	public String getDesc() {
		return desc;
	}

	public BigDecimal getIgstTaxBal() {
		return igstTaxBal;
	}

	public BigDecimal getCgstTaxBal() {
		return cgstTaxBal;
	}

	public BigDecimal getSgstTaxBal() {
		return sgstTaxBal;
	}

	public BigDecimal getCessTaxBal() {
		return cessTaxBal;
	}

	public BigDecimal getTotRunningBal() {
		return totRunningBal;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setIgstTaxBal(BigDecimal igstTaxBal) {
		this.igstTaxBal = igstTaxBal;
	}

	public void setCgstTaxBal(BigDecimal cgstTaxBal) {
		this.cgstTaxBal = cgstTaxBal;
	}

	public void setSgstTaxBal(BigDecimal sgstTaxBal) {
		this.sgstTaxBal = sgstTaxBal;
	}

	public void setCessTaxBal(BigDecimal cessTaxBal) {
		this.cessTaxBal = cessTaxBal;
	}

	public void setTotRunningBal(BigDecimal totRunningBal) {
		this.totRunningBal = totRunningBal;
	}

}
