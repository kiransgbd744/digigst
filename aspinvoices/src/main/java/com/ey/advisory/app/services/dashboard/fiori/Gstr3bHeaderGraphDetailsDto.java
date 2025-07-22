package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Sakshi.jain
 *
 */
@Getter
@Setter
public class Gstr3bHeaderGraphDetailsDto {

	private BigDecimal totalLiab = BigDecimal.ZERO;
	private BigDecimal liabItc = BigDecimal.ZERO;
	private BigDecimal liabCash = BigDecimal.ZERO;
	private List<Gstr3BLiabilityMonthDto> totalLiabList;
	private List<Gstr3BLiabilityMonthDto> liabItcList;
	private List<Gstr3BLiabilityMonthDto> liabCashList;
	private String refreshedOn;

}
