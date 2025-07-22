package com.ey.advisory.app.docs.dto.ledger;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Hemasundar.J
 *
 */
public class TaxIgstCgstSgstCess {

	@Expose
	@SerializedName("tx")
	private BigDecimal tax;

	@Expose
	@SerializedName("intr")
	private BigDecimal interest;

	@Expose
	@SerializedName("pen")
	private BigDecimal penalty;

	@Expose
	@SerializedName("fee")
	private BigDecimal fee;

	@Expose
	@SerializedName("oth")
	private BigDecimal other;

	@Expose
	@SerializedName("tot")
	private BigDecimal total;

	public BigDecimal getTax() {
		return tax;
	}

	public BigDecimal getInterest() {
		return interest;
	}

	public BigDecimal getPenalty() {
		return penalty;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public BigDecimal getOther() {
		return other;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}

	public void setPenalty(BigDecimal penalty) {
		this.penalty = penalty;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public void setOther(BigDecimal other) {
		this.other = other;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

}
