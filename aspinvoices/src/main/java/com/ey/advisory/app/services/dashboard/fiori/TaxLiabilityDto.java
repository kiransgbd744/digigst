package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;

/**
 * @author kiran
 *
 */
@Getter
@Setter
public class TaxLiabilityDto {

	private String customerGstn;
	private BigDecimal totalValue;
	private BigDecimal gstnTotal;
	private String hsnsac;
	private BigInteger drn;
	private BigInteger rn;
	private BigDecimal taxableValue;
	private BigDecimal totaltaxable;

}
