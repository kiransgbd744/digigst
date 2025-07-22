package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EinvErrorDetailsDto {

	private String year;
	private String month;
	private BigInteger errored = BigInteger.ZERO;
}
