package com.ey.advisory.app.data.views.client;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GSTR1GetEInvoicesEntityDto {

	private String gstin;
	private String tableType;
	private String invoiceStatus;
	private String count;
	private String supplies;
	private BigDecimal igstAmount = BigDecimal.ZERO;
	private BigDecimal cgstAmount = BigDecimal.ZERO;
	private BigDecimal sgstAmount = BigDecimal.ZERO;
	private BigDecimal cessAmount = BigDecimal.ZERO;
	private BigDecimal totalTax = BigDecimal.ZERO;
}
