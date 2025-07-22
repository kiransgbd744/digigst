package com.ey.advisory.app.docs.dto.erp.gstr6;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Gstr6AReviewSummaryRespDto {

	private String tableType;

	private String documentType;

	private BigInteger docCount;

	private BigDecimal taxableValue;

	private BigDecimal totalTax;

	private BigDecimal igst;

	private BigDecimal sgst;

	private BigDecimal cgst;

	private BigDecimal cess;

	private BigDecimal invoiceValue;
}
