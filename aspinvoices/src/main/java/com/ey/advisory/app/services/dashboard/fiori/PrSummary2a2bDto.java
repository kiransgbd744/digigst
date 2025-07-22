package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Saif.S
 *
 */
@Getter
@Setter
@ToString
public class PrSummary2a2bDto {

	private String transactionType;
	private BigDecimal prTotalTax;
	private BigDecimal A2TotalTax;
	private BigDecimal brTotalTax;
	private BigDecimal prTaxableValue;
	private BigDecimal a2TaxableValue;
	private BigDecimal b2TaxableValue;
}
