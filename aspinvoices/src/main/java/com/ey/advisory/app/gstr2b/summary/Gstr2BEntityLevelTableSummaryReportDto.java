package com.ey.advisory.app.gstr2b.summary;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Hema G M
 *
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Gstr2BEntityLevelTableSummaryReportDto {

	private String RecipientGstin;

	private String panGstinCount;

	private Integer count;

	private String tableName;

	private BigDecimal taxablevalue = BigDecimal.ZERO;

	private BigDecimal totalTaxIgst = BigDecimal.ZERO;

	private BigDecimal totalTaxCgst = BigDecimal.ZERO;

	private BigDecimal totalTaxSgst = BigDecimal.ZERO;

	private BigDecimal totalTaxCess = BigDecimal.ZERO;

	private BigDecimal availItcIgst = BigDecimal.ZERO;

	private BigDecimal availItcCgst = BigDecimal.ZERO;

	private BigDecimal availItcSgst = BigDecimal.ZERO;

	private BigDecimal availItcCess = BigDecimal.ZERO;

	private BigDecimal nonAvailItcIgst = BigDecimal.ZERO;

	private BigDecimal nonAvailItcCgst = BigDecimal.ZERO;

	private BigDecimal nonAvailItcSgst = BigDecimal.ZERO;

	private BigDecimal nonAvailItcCess = BigDecimal.ZERO;

	private BigDecimal rejectedItcIgst = BigDecimal.ZERO;

	private BigDecimal rejectedItcCgst = BigDecimal.ZERO;

	private BigDecimal rejectedItcSgst = BigDecimal.ZERO;

	private BigDecimal rejectedItcCess = BigDecimal.ZERO;

}
