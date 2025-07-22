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
public class Outward2HeaderDetailsDto {

	private Long noOfInvoices;
	private Long noOfCreditNotes;
	private Long noOfDebitNotes;
	private Long noOfSelfInvoices;
	private Long noOfDlc;
	private Long noOfB2BCustomers;
	private BigDecimal totalTurnOver = BigDecimal.ZERO;
	private BigDecimal totalTax = BigDecimal.ZERO;
	private String lastRefreshedOn;
}
