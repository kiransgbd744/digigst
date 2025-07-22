/**
 * 
 */
package com.ey.advisory.app.docs.dto.gstr9;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class GSTR9TaxPaidReportDownloadInnerDto {

	private String gstin;

	private String subSection;

	private String natureOfSupply;

	private BigDecimal taxPayable = BigDecimal.ZERO;

	private BigDecimal pdCash = BigDecimal.ZERO;

	private BigDecimal pdIgst = BigDecimal.ZERO;

	private BigDecimal pdCgst = BigDecimal.ZERO;

	private BigDecimal pdSgst = BigDecimal.ZERO;

	private BigDecimal pdCess = BigDecimal.ZERO;

}
