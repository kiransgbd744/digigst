package com.ey.advisory.app.services.credit.reversal;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CreditRevFinancialRespDto {

	private String taxPeriod;
	private String gstin;
	private String status;
	private BigDecimal taxbleValueRatio1;
	private BigDecimal cgstARaio1;
	private BigDecimal sgstARaio1;
	private BigDecimal igstARaio1;
	private BigDecimal cessARaio1;

	private BigDecimal taxbleValueRatio2;
	private BigDecimal cgstARaio2;
	private BigDecimal sgstARaio2;
	private BigDecimal igstARaio2;
	private BigDecimal cessARaio2;

	private BigDecimal taxbleValueRatio3;
	private BigDecimal cgstARaio3;
	private BigDecimal sgstARaio3;
	private BigDecimal igstARaio3;
	private BigDecimal cessARaio3;

}
