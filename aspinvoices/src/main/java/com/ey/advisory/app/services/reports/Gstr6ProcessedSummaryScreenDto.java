package com.ey.advisory.app.services.reports;

import java.math.BigDecimal;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Data
public class Gstr6ProcessedSummaryScreenDto {
	private String GSTIN;
	private int count;

	private String invoiceValue =BigDecimal.ZERO.toString();
	private String taxableValue = BigDecimal.ZERO.toString();
	private String totalTax = BigDecimal.ZERO.toString();
	private String tpIgst = BigDecimal.ZERO.toString();
	private String tpCgst = BigDecimal.ZERO.toString();
	private String tpSgst = BigDecimal.ZERO.toString();
	private String tpCess = BigDecimal.ZERO.toString();
	private String totCreElig = BigDecimal.ZERO.toString();
	private String ceIgst = BigDecimal.ZERO.toString();
	private String ceCgst = BigDecimal.ZERO.toString();
	private String ceSgst = BigDecimal.ZERO.toString();
	private String ceCess = BigDecimal.ZERO.toString();
}
