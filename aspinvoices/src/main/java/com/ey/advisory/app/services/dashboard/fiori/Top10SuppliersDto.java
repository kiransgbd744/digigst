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
public class Top10SuppliersDto {

	private String gstin;
	private BigDecimal prTotalTax;
	private BigDecimal gstr2;

}