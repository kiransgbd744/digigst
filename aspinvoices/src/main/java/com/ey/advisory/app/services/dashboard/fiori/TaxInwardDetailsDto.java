package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Ravindra V S
 *
 */
@Getter
@Setter
public class TaxInwardDetailsDto {

	private String transactionType;
	private BigDecimal invoiceValue;
	private BigDecimal taxableValue;
	private BigDecimal igst;
	private BigDecimal cgst;
	private BigDecimal sgst;
	private BigDecimal cess;
	private BigDecimal igstCredit;
	private BigDecimal cgstCredit;
	private BigDecimal sgstCredit;
	private BigDecimal cessCredit;
	
	private String order;
	private String level = "L1";
	
}
