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
public class GetLiabTableDto {

	private String gstin;
	private BigDecimal LiabFrwdChrg = BigDecimal.ZERO;
	private BigDecimal LiabRevChrg = BigDecimal.ZERO;
	private BigDecimal interest = BigDecimal.ZERO;
	private BigDecimal lateFee = BigDecimal.ZERO;
	private BigDecimal paidThrItc = BigDecimal.ZERO;
	private BigDecimal cashPybl = BigDecimal.ZERO;
}
