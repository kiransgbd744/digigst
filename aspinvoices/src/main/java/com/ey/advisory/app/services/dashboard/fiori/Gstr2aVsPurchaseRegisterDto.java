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
public class Gstr2aVsPurchaseRegisterDto {

	private String reportType;
	private BigDecimal prTotalTax = BigDecimal.ZERO;
	private BigDecimal gstr2TotalTax = BigDecimal.ZERO;

}
