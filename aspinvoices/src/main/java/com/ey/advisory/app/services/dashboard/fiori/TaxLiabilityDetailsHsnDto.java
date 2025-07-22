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
public class TaxLiabilityDetailsHsnDto {

	private String transactionType;
	private BigDecimal invoiceValue;
	private BigDecimal taxableValue;
	private String order;
	private String level = "L2";
	
	
	
}
