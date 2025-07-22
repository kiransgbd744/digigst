package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItcPaidInnerDto {
	
	private BigDecimal othrcIgst;
	private BigDecimal othrcCgst;
	private BigDecimal othrcSgst;
	private BigDecimal othrcCess;
	
	
	private BigDecimal pthITIgst;
	private BigDecimal pthITCgst;
	private BigDecimal pthITSgst;
	
	
	private BigDecimal pthCTIgst;
	private BigDecimal pthCTCgst;
	
	
	private BigDecimal pthSTIgst;
	private BigDecimal pthSTSgst;
	
	
	private BigDecimal pthCSCess;
	
	
	private BigDecimal rctpIgst;
	private BigDecimal rctpCgst;
	private BigDecimal rctpSgst;
	private BigDecimal rctpCess;
	
	
	private BigDecimal ipIgst;
	private BigDecimal ipCgst;
	private BigDecimal ipSgst;
	private BigDecimal ipCess;
	
	private BigDecimal lateFeeIgst;
	private BigDecimal lateFeeCgst;
	private BigDecimal lateFeeSgst;
	private BigDecimal lateFeeCess;
	
	
	
	
}
