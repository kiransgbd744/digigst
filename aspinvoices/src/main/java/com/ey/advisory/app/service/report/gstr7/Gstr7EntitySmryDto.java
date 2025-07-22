/**
 * 
 */
package com.ey.advisory.app.service.report.gstr7;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author vishal.verma	
 *
 */

@Data
public class Gstr7EntitySmryDto {

	private String section;
	
	private String sectionDesc;
	
	private Integer aspCount;
	
	private BigDecimal aspTotalAmount = BigDecimal.ZERO;
	
	private BigDecimal aspIgst = BigDecimal.ZERO;
	
	private BigDecimal aspCgst = BigDecimal.ZERO;
	
	private BigDecimal aspSgst = BigDecimal.ZERO;
	
	private Integer gstnCount;
	
	private BigDecimal gstnTotalAmount = BigDecimal.ZERO;
	
	private BigDecimal gstnIgst = BigDecimal.ZERO;
	
	private BigDecimal gstnCgst = BigDecimal.ZERO;
	
	private BigDecimal gstnSgst = BigDecimal.ZERO;
	
	private Integer diffCount;
	
	private BigDecimal diffTotalAmount = BigDecimal.ZERO;
	
	private BigDecimal diffIgst = BigDecimal.ZERO;
	
	private BigDecimal diffCgst = BigDecimal.ZERO;
	
	private BigDecimal diffSgst = BigDecimal.ZERO;
	
	private String gstin;
}
