package com.ey.advisory.app.services.dashboard.fiori;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EinvIrnDto {

	private BigDecimal irn = BigDecimal.ZERO;
	private String lastUpdatedOn;
}
