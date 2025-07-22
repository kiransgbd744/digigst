package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Data;

@Data
public class Gstr2aReviewSummScreenRespDto {
	private String state;

	private String gstin;

	private String taxDocType;

	private String table;

	private BigInteger count = BigInteger.ZERO;
	private BigDecimal invoiceValue = BigDecimal.ZERO;
	private BigDecimal taxableValue = BigDecimal.ZERO;
	private BigDecimal taxPayble = BigDecimal.ZERO;
	private BigDecimal igst = BigDecimal.ZERO;
	private BigDecimal cgst = BigDecimal.ZERO;
	private BigDecimal sgst = BigDecimal.ZERO;
	private BigDecimal cess = BigDecimal.ZERO;
}
