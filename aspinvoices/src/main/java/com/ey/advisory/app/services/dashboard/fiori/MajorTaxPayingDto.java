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
public class MajorTaxPayingDto {

	private String hsnsac;
	private BigDecimal taxRate;
	private BigDecimal totalValue;
	private BigDecimal hsnTotalTaxValue;
	private BigInteger drn;
	private BigInteger rn;

}
