package com.ey.advisory.app.service.ims.supplier;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
public class SupplierEntityRecordDetail {

	private Integer count = 0;
	private BigDecimal totalTaxableValue = BigDecimal.ZERO;
	private BigDecimal totalTax = BigDecimal.ZERO;
}
