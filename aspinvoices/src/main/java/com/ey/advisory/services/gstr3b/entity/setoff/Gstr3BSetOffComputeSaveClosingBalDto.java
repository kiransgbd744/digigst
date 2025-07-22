package com.ey.advisory.services.gstr3b.entity.setoff;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr3BSetOffComputeSaveClosingBalDto {
	
	private String desc;
	
	private BigDecimal i = BigDecimal.ZERO;

	private BigDecimal c = BigDecimal.ZERO;

	private BigDecimal s = BigDecimal.ZERO;

	private BigDecimal cs = BigDecimal.ZERO;
	

	private BigDecimal cri = BigDecimal.ZERO;

	private BigDecimal crs = BigDecimal.ZERO;

	private BigDecimal crc= BigDecimal.ZERO;

	private BigDecimal crcs = BigDecimal.ZERO;
	
	private BigDecimal Total= BigDecimal.ZERO;

	private BigDecimal Total1 = BigDecimal.ZERO;
	
	
	private BigDecimal negLiabIgst = BigDecimal.ZERO;

	private BigDecimal negLiabCgst = BigDecimal.ZERO;

	private BigDecimal negLiabSgst= BigDecimal.ZERO;

	private BigDecimal negLiabCess = BigDecimal.ZERO;
	
	/*private BigDecimal Total= BigDecimal.ZERO;

	private BigDecimal Total1 = BigDecimal.ZERO;*/
}
