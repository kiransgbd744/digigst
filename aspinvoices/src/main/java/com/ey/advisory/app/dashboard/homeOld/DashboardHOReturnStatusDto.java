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
public class DashboardHOReturnStatusDto {
	
	@Expose
	private String taxType;

	@Expose
	private BigInteger totalCount = BigInteger.ZERO;

	@Expose
	private BigInteger availableCount = BigInteger.ZERO;

	@Expose
	private BigDecimal totalPercentage = BigDecimal.ZERO;

}
