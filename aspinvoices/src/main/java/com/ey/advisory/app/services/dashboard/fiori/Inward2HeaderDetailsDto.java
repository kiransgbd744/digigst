package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Ravindra V S
 *
 */
@Getter
@Setter
public class Inward2HeaderDetailsDto {

	private Long noOfSuppliersPr;
	private Long noOfSuppliers2a;
	private Long noOfSuppliers2b;
	private BigDecimal totalTaxPr = BigDecimal.ZERO;
	private BigDecimal totalTax2a = BigDecimal.ZERO;
	private BigDecimal totalTax2b = BigDecimal.ZERO;

}
