package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalItcDetailsDto {

	private BigDecimal totalItc;
	private BigDecimal inputGoods;
	private BigDecimal inputServices;
	private BigDecimal capitalGoods;
	private BigDecimal inEligibility;

}
