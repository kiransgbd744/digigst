package com.ey.advisory.services.gstr3b.entity.setoff;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class Gstr3BSetOffLiabilityAndRevChrgDto {
	
	//3.1(a) +(b)
	private BigDecimal otsIgst;
	private BigDecimal otsCgst;
	private BigDecimal otsSgst;
	private BigDecimal otsCess;
	
	//3.1(d)
	private BigDecimal libRevChrgIgst;
	private BigDecimal libRevChrgCgst;
	private BigDecimal libRevChrgSgst;
	private BigDecimal libRevChrgCess;
	
	//4(c)
	private BigDecimal netItcAvailIgst;
	private BigDecimal netItcAvailCgst;
	private BigDecimal netItcAvailSgst;
	private BigDecimal netItcAvailCess;
	

}
