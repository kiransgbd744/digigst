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
public class TaxRateWiseDistributionDto {

	private BigDecimal taxRate;
	private BigDecimal invoiceValue;
}
