package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Saif.S
 *
 */
@Getter
@Setter
public class Outward3HeaderDetailsDto {

	private BigDecimal itcBal = BigDecimal.ZERO;
	private BigDecimal totalLiab = BigDecimal.ZERO;
	private BigDecimal paidThrItc = BigDecimal.ZERO;
	private BigDecimal netLiab = BigDecimal.ZERO;
	private BigDecimal itcBalIgst = BigDecimal.ZERO;
	private BigDecimal itcBalCgst = BigDecimal.ZERO;
	private BigDecimal itcBalSgst = BigDecimal.ZERO;
	private BigDecimal itcBalCess = BigDecimal.ZERO;
	private BigDecimal interestPybl = BigDecimal.ZERO;
	private BigDecimal lateFeePybl = BigDecimal.ZERO;
	private String lastRefreshedOn;
}
