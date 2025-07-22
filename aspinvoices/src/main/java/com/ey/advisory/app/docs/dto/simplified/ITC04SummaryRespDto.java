package com.ey.advisory.app.docs.dto.simplified;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Data
public class ITC04SummaryRespDto {

	private String table;
	private int aspCount;
	private BigDecimal aspTaxableValue;
	private int gstnCount;
	private BigDecimal gstnTaxableValue;
	private int diffCount;
	private BigDecimal diffTaxableValue;

}
