package com.ey.advisory.app.dashboard.homeOld;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author mohit.basak
 *
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DashboardHOOutwardSupplyDto {

	@Expose
	private BigDecimal gSTR1TaxableValue = BigDecimal.ZERO;

	@Expose
	private BigDecimal gSTR1ToatalTax = BigDecimal.ZERO;

	@Expose
	private BigDecimal gSTR3BTaxableValue = BigDecimal.ZERO;

	@Expose
	private BigDecimal gSTR3BTotalTax = BigDecimal.ZERO;

	@Expose
	private BigDecimal differenceInTaxableValue = BigDecimal.ZERO;

	@Expose
	private BigDecimal differenceInTotalTax = BigDecimal.ZERO;
}
