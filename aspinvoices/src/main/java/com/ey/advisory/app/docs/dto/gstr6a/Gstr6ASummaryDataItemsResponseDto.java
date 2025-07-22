package com.ey.advisory.app.docs.dto.gstr6a;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class Gstr6ASummaryDataItemsResponseDto {

	private String table;
	private BigInteger count = BigInteger.ZERO;
	private BigDecimal inVoiceVal = BigDecimal.ZERO;
	private BigDecimal taxableValue = BigDecimal.ZERO;
	private BigDecimal totalTax = BigDecimal.ZERO;
	private BigDecimal igst = BigDecimal.ZERO;
	private BigDecimal cgst = BigDecimal.ZERO;
	private BigDecimal sgst = BigDecimal.ZERO;
	private BigDecimal cess = BigDecimal.ZERO;

	public Gstr6ASummaryDataItemsResponseDto() {
		super();
	}
}
