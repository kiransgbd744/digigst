package com.ey.advisory.app.data.views.client;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Gstr8EntityLevelSummaryDto {

	private String gstin;
	private String section;
	private String subSection;
	private String taxPeriod;
	private BigDecimal grossSuppliesMadeDigi = BigDecimal.ZERO;
	private BigDecimal grossSuppliesReturnedDigi = BigDecimal.ZERO;
	private BigDecimal netSuppliesDigi = BigDecimal.ZERO;
	private BigDecimal igstDigi = BigDecimal.ZERO;
	private BigDecimal cgstDigi = BigDecimal.ZERO;
	private BigDecimal sgstDigi = BigDecimal.ZERO;
	private BigDecimal grossSuppliesMadeGstn = BigDecimal.ZERO;
	private BigDecimal grossSuppliesReturnedGstn = BigDecimal.ZERO;
	private BigDecimal netSuppliesGstn = BigDecimal.ZERO;
	private BigDecimal igstGstn = BigDecimal.ZERO;
	private BigDecimal cgstGstn = BigDecimal.ZERO;
	private BigDecimal sgstGstn = BigDecimal.ZERO;
	private BigDecimal grossSuppliesMadeDifference = BigDecimal.ZERO;
	private BigDecimal grossSuppliesReturnedDifference = BigDecimal.ZERO;
	private BigDecimal netSuppliesDifference = BigDecimal.ZERO;
	private BigDecimal igstDifference = BigDecimal.ZERO;
	private BigDecimal cgstDifference = BigDecimal.ZERO;
	private BigDecimal sgstDifference = BigDecimal.ZERO;

}
