package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Saif.S
 *
 */
@Getter
@Setter
public class TaxLiabilityDetailsDto {

	private String transactionType;
	private BigDecimal invoiceValue;
	private BigDecimal taxableValue;
	private BigDecimal igstAmt;
	private BigDecimal cgstAmt;
	private BigDecimal sgstAmt;
	private BigDecimal cessAmt;
	private String order;
	private String level = "L1";
}
