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
public class OutwardSupplyDetailsDto {

	private String gstin;
	private String stateName;
	private String regType;
	private BigDecimal igstAmt = BigDecimal.ZERO;
	private BigDecimal cgstAmt = BigDecimal.ZERO;
	private BigDecimal sgstAmt = BigDecimal.ZERO;
	private BigDecimal cessAmt = BigDecimal.ZERO;
	private BigDecimal totalTax = BigDecimal.ZERO;
	private BigDecimal invoiceVal = BigDecimal.ZERO;
	private BigDecimal taxableVal = BigDecimal.ZERO;
}
