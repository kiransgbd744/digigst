package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Data
public class Gstr2PRSummarySectionDto {

	
	private String table;
	private String docType;
    private Integer count = 0;
    private Integer index;
    private BigDecimal invoiceValue = BigDecimal.ZERO;
    private BigDecimal taxableValue = BigDecimal.ZERO;
    private BigDecimal taxPayable = BigDecimal.ZERO;
    private BigDecimal taxPayableIgst = BigDecimal.ZERO;
    private BigDecimal taxPayableCgst = BigDecimal.ZERO;
    private BigDecimal taxPayableSgst = BigDecimal.ZERO;
    private BigDecimal taxPayableCess = BigDecimal.ZERO;
    private BigDecimal crEligibleTotal = BigDecimal.ZERO;
    private BigDecimal crEligibleIgst = BigDecimal.ZERO;
    private BigDecimal crEligibleCgst = BigDecimal.ZERO;
    private BigDecimal crEligibleSgst = BigDecimal.ZERO;
    private BigDecimal crEligibleCess = BigDecimal.ZERO;

	
}
