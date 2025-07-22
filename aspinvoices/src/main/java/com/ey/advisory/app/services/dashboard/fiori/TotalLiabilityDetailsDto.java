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
public class TotalLiabilityDetailsDto {

	private BigDecimal totalLiability;
	private BigDecimal netItcAvailable;
	private BigDecimal netLiability;
	private String lastRefreshedOn;
}
