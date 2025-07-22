package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Hema G M
 *
 */

@Getter
@Setter
@ToString
public class EinvHeaderDetailsDto {

	private BigInteger totalIrns = BigInteger.ZERO;
	private BigInteger generated = BigInteger.ZERO;
	private BigInteger cancelled = BigInteger.ZERO;
	private BigInteger duplicate = BigInteger.ZERO;
	private BigInteger error = BigInteger.ZERO;
	private BigDecimal irn;
	private String lastUpdatedOn;
}
