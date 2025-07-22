package com.ey.advisory.app.services.credit.reversal;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Gstr3BRuleComputeRespDto {

	private String taxPeriod;
	private String gstin;
	private String sectionName;
	private String subSectionName;
	private BigDecimal taxableValue;
	private BigDecimal igstAmt;
	private BigDecimal cgstAmt;
	private BigDecimal sgstAmt;
	private BigDecimal cessAmt;

	private Integer derivedRetPeriod;
	private Long batchId;

}
