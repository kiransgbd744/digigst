package com.ey.advisory.app.services.credit.reversal;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CreditReversalSummaryDto {

	private String sectionName;
	
	private BigDecimal ratio1 = BigDecimal.ZERO;

	private BigDecimal ratio2 = BigDecimal.ZERO;

	private BigDecimal ratio3 = BigDecimal.ZERO;

}
