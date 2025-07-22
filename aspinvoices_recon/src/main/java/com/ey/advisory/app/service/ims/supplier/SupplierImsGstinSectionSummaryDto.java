package com.ey.advisory.app.service.ims.supplier;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
public class SupplierImsGstinSectionSummaryDto {

	private String sectionType;
	private Integer count = 0;
	private BigDecimal totalTaxableValue = BigDecimal.ZERO;
	private BigDecimal totalTax = BigDecimal.ZERO;
	private BigDecimal igst = BigDecimal.ZERO;
	private BigDecimal cgst = BigDecimal.ZERO;
	private BigDecimal sgst = BigDecimal.ZERO;
	private BigDecimal cess = BigDecimal.ZERO;
}
