package com.ey.advisory.app.docs.dto.simplified;
/**
 * 
 * @author Balakrishna.S
 *
 */

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Ret1AspVerticalSummaryDto {

	private String type;
	private Integer count = 0;
	private BigDecimal docAmount = BigDecimal.ZERO;
	private BigDecimal invoiceValue = BigDecimal.ZERO;
	private BigDecimal taxableValue = BigDecimal.ZERO;
	private BigDecimal igst = BigDecimal.ZERO;
	private BigDecimal cgst = BigDecimal.ZERO;
	private BigDecimal sgst = BigDecimal.ZERO;
	private BigDecimal cess = BigDecimal.ZERO;
	private String pos;
	private String transType;
	private BigDecimal rate = BigDecimal.ZERO;
	private String order;

}
