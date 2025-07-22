package com.ey.advisory.app.data.views.client;

import java.math.BigDecimal;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class Gstr7DiffSummaryRespDto {
	private String section;
	private Integer diffCount;
	private BigDecimal diffTotalAmount = BigDecimal.ZERO;
	private BigDecimal diffIgst = BigDecimal.ZERO;
	private BigDecimal diffCgst = BigDecimal.ZERO;
	private BigDecimal diffSgst = BigDecimal.ZERO;
}
