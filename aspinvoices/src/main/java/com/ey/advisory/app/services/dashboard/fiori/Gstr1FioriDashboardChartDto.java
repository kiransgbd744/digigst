package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Saif.S
 *
 */
@Getter
@Setter
@ToString
public class Gstr1FioriDashboardChartDto {

	private String xAxis;
	private BigDecimal yAxis;
	private String order;
}
