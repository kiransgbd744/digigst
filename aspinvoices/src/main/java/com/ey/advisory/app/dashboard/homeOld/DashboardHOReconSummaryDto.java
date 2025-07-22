package com.ey.advisory.app.dashboard.homeOld;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author mohit.basak
 *
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DashboardHOReconSummaryDto {
	@Expose
	private BigDecimal exactMatch = BigDecimal.ZERO;
	@Expose
	private BigDecimal matchWithTolerance = BigDecimal.ZERO;
	@Expose
	private BigDecimal valueMismatch = BigDecimal.ZERO;
	@Expose
	private BigDecimal posMismatch = BigDecimal.ZERO;
	@Expose
	private BigDecimal documentDateMismatch = BigDecimal.ZERO;
	@Expose
	private BigDecimal documentTypeMismatch = BigDecimal.ZERO;
	@Expose
	private BigDecimal documentNumberMismatch1 = BigDecimal.ZERO;
	@Expose
	private BigDecimal multiMismatch = BigDecimal.ZERO;
	@Expose
	private BigDecimal potential1 = BigDecimal.ZERO;
	@Expose
	private BigDecimal documentNumberMismatch2 = BigDecimal.ZERO;
	@Expose
	private BigDecimal documentNumberDocDateMismatch = BigDecimal.ZERO;
	@Expose
	private BigDecimal potential2 = BigDecimal.ZERO;
	@Expose
	private BigDecimal logicalMatch = BigDecimal.ZERO;
	@Expose
	private BigDecimal forcedMatch = BigDecimal.ZERO;
	@Expose
	private BigDecimal additionalInPR = BigDecimal.ZERO;
	@Expose
	private BigDecimal additionalIn2A = BigDecimal.ZERO;
	@Expose
	private BigInteger reconPerformed = BigInteger.ZERO;
	@Expose
	private BigInteger totalGstns = BigInteger.ZERO;
	@Expose
	private BigDecimal totalTaxPR = BigDecimal.ZERO;
	@Expose
	private BigDecimal totalTaxA2 = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal exactMatchImpg = BigDecimal.ZERO;
	@Expose
	private BigDecimal misMatchImpg = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal additionInPRImports = BigDecimal.ZERO;
	@Expose
	private BigDecimal additionIn2AImports = BigDecimal.ZERO;

}
