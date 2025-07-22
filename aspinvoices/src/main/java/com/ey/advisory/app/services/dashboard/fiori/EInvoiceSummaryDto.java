package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EInvoiceSummaryDto {

	private BigInteger generated = BigInteger.ZERO;
	private BigInteger cancelled = BigInteger.ZERO;
	private BigInteger duplicate = BigInteger.ZERO;
	private BigInteger error = BigInteger.ZERO;
	private String entityName;
}
