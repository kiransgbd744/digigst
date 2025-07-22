package com.ey.advisory.app.glrecon;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author ashutosh.kar
 *
 */

@Data
public class GlTaxCodeDto {

	private String transactionTypeGl;
	private String taxCodeDescriptionMs;
	private String taxTypeMs;
	private String eligibilityMs;
	private BigDecimal taxRateMs;
}
