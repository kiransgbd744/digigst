/**
 * 
 */
package com.ey.advisory.app.docs.dto.simplified;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author Sasidhar Reddy
 *
 */
@Data
public class Gstr1ProcessedScreenDiffRespDto {

	private String section;
	private Integer count = 0;
	private BigDecimal invoiceValue = BigDecimal.ZERO;
	private BigDecimal taxableValue = BigDecimal.ZERO;
	private BigDecimal taxPayble = BigDecimal.ZERO;
	private BigDecimal igst = BigDecimal.ZERO;
	private BigDecimal cgst = BigDecimal.ZERO;
	private BigDecimal sgst = BigDecimal.ZERO;
	private BigDecimal cess = BigDecimal.ZERO;

	private String taxDocType;
	private Integer aspTotal = 0;
	private Integer aspCancelled = 0;
	private Integer aspNetIssued = 0;
	private Integer gstnTotal = 0;
	private Integer gstnCancelled = 0;
	private Integer gstnNetIssued = 0;
	private Integer diffTotal = 0;
	private Integer diffCancelled = 0;
	private Integer diffNetIssued = 0;

	private BigDecimal aspNitRated = BigDecimal.ZERO;
	private BigDecimal aspExempted = BigDecimal.ZERO;
	private BigDecimal aspNonGst = BigDecimal.ZERO;
	private BigDecimal gstnNitRated = BigDecimal.ZERO;
	private BigDecimal gstnExempted = BigDecimal.ZERO;
	private BigDecimal gstnNonGst = BigDecimal.ZERO;
	private BigDecimal diffNitRated = BigDecimal.ZERO;
	private BigDecimal diffExempted = BigDecimal.ZERO;
	private BigDecimal diffNonGst = BigDecimal.ZERO;
}
