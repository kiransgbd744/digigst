package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ravindra V S
 *
 */
@Getter
@Setter
@ToString
public class Gstr1FioriDashboardInwardChartDto {

	private String xAxis;
	private BigDecimal yAxis;
}
