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
public class DashboardHOReturnsStatusUIDto {

	@Expose
	private BigInteger gstr1TotalCount = BigInteger.ZERO;

	@Expose
	private BigInteger gstr1TotalAvailableCount = BigInteger.ZERO;

	@Expose
	private BigDecimal gstr1TotalPercentage = BigDecimal.ZERO;

	@Expose
	private BigInteger gstr1ATotalCount = BigInteger.ZERO;

	@Expose
	private BigInteger gstr1ATotalAvailableCount = BigInteger.ZERO;

	@Expose
	private BigDecimal gstr1ATotalPercentage = BigDecimal.ZERO;
	
	@Expose
	private BigInteger gstr3bTotalCount = BigInteger.ZERO;

	@Expose
	private BigInteger gstr3bTotalAvailableCount = BigInteger.ZERO;

	@Expose
	private BigDecimal gstr3bTotalPercentage = BigDecimal.ZERO;

	@Expose
	private BigInteger gstr9TotalCount = BigInteger.ZERO;

	@Expose
	private BigInteger gstr9TotalAvailableCount = BigInteger.ZERO;

	@Expose
	private BigDecimal gstr9TotalPercentage = BigDecimal.ZERO;

	@Expose
	private BigInteger gstr6TotalCount = BigInteger.ZERO;

	@Expose
	private BigInteger gstr6TotalAvailableCount = BigInteger.ZERO;

	@Expose
	private BigDecimal gstr6TotalPercentage = BigDecimal.ZERO;

	@Expose
	private BigInteger gstr7TotalCount = BigInteger.ZERO;

	@Expose
	private BigInteger gstr7TotalAvailableCount = BigInteger.ZERO;

	@Expose
	private BigDecimal gstr7TotalPercentage = BigDecimal.ZERO;

	@Expose
	private BigInteger gstr8TotalCount = BigInteger.ZERO;

	@Expose
	private BigInteger gstr8TotalAvailableCount = BigInteger.ZERO;

	@Expose
	private BigDecimal gstr8TotalPercentage = BigDecimal.ZERO;
	
	@Expose
	private BigInteger itc04TotalCount = BigInteger.ZERO;

	@Expose
	private BigInteger itc04TotalAvailableCount = BigInteger.ZERO;

	@Expose
	private BigDecimal itc04TotalPercentage = BigDecimal.ZERO;

}
