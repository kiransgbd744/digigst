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
public class Pr2a2bDataDto {

	private String transactionType;
	private BigDecimal prTaxableValue;
	private BigDecimal prTotalValue;
	private BigDecimal prInvoiceValue;
	private BigDecimal gstr2aTaxableValue;
	private BigDecimal gstr2a;
	private BigDecimal gstr2aInvoiceValue;
	private BigDecimal gstr2bTaxableValue;
	private BigDecimal gstr2b;
	private BigDecimal gstr2bInvoiceValue;
}
