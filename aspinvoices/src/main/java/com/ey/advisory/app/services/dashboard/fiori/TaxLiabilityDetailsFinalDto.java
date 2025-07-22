package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author kiran s
 *
 */
@Getter
@Setter
public class TaxLiabilityDetailsFinalDto {

	private String transactionType;
	private BigDecimal invoiceValue;
	//private BigDecimal taxableValue;
	private String order;
	private String level = "L1";
	private BigDecimal taxableValue;
	List<TaxLiabilityDetailsHsnDto> hsndto;
}
