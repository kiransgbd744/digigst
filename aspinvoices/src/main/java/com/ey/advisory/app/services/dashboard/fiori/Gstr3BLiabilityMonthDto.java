package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Sakshi.jain
 *
 */
@Getter
@Setter
public class Gstr3BLiabilityMonthDto {

	private String xAxis;
	private BigDecimal yAxis;
	
	public Gstr3BLiabilityMonthDto (String taxPrd, BigDecimal value)
	{
		this.xAxis=taxPrd;
		this.yAxis = value;
	}

}
