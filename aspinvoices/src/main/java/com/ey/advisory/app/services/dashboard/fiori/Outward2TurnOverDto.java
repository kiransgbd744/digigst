package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Outward2TurnOverDto {

	private BigDecimal turnOver = BigDecimal.ZERO;
	private BigDecimal tax = BigDecimal.ZERO;
}
