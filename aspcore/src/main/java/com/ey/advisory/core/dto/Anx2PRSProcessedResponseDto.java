package com.ey.advisory.core.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Anx2PRSProcessedResponseDto {

	private int count;

	private BigDecimal invValue = BigDecimal.ZERO;

	private BigDecimal taxableValue = BigDecimal.ZERO;

	private BigDecimal totalTaxPayable = BigDecimal.ZERO;

	private BigDecimal tpIGST = BigDecimal.ZERO;

	private BigDecimal tpCGST = BigDecimal.ZERO;

	private BigDecimal tpSGST = BigDecimal.ZERO;

	private BigDecimal tpCess = BigDecimal.ZERO;

	private BigDecimal totalCreditEligible = BigDecimal.ZERO;

	private BigDecimal ceIGST = BigDecimal.ZERO;

	private BigDecimal ceCGST = BigDecimal.ZERO;

	private BigDecimal ceSGST = BigDecimal.ZERO;

	private BigDecimal ceCess = BigDecimal.ZERO;

	private String gstin;

	private String returnPeriod;

	private boolean highlight;

	private LocalDateTime lastUpdated;

	private String state;

	private String status;

	private String authToken;

}