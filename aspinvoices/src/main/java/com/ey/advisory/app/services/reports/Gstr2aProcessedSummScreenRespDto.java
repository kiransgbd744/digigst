package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Data;

@Data
public class Gstr2aProcessedSummScreenRespDto {
	private String gstin;

	private String state;

	private String status;

	private String authToken;

	private String timeStamp;

	private BigInteger count;

	private BigDecimal invoiceValue;

	private BigDecimal taxableValue;

	private BigDecimal taxPayable;

	private BigDecimal igst;

	private BigDecimal cgst;

	private BigDecimal sgst;

	private BigDecimal cess;

}
