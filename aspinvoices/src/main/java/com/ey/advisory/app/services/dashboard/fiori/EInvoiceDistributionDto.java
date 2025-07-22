package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EInvoiceDistributionDto {

	private String status;
	private BigInteger count = BigInteger.ZERO;
}
