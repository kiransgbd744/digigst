package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class EinvGenerationDto1 {

	private BigInteger total = BigInteger.ZERO;
	private BigInteger generated = BigInteger.ZERO;
	private String summaryDate;
}
