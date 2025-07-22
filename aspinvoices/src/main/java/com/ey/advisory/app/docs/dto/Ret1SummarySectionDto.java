package com.ey.advisory.app.docs.dto;
/**
 * 
 * @author Balakrishna.S
 *
 */

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Ret1SummarySectionDto {

	private String returnPeriod;
	private String sgstn;
	private String table;
	private String supplyType;
	private BigDecimal taxableValue = BigDecimal.ZERO;
	private BigDecimal aspIgst = BigDecimal.ZERO;
	private BigDecimal aspCgst = BigDecimal.ZERO;
	private BigDecimal aspSgst = BigDecimal.ZERO;
	private BigDecimal aspCess = BigDecimal.ZERO;
	private BigDecimal usrValue = BigDecimal.ZERO;
    private BigDecimal usrIgst = BigDecimal.ZERO;
    private BigDecimal usrCgst = BigDecimal.ZERO;
    private BigDecimal usrSgst = BigDecimal.ZERO;
    private BigDecimal usrCess = BigDecimal.ZERO;
	private BigDecimal gstnValue = BigDecimal.ZERO;
	private BigDecimal gstnIgst = BigDecimal.ZERO;
	private BigDecimal gstnCgst = BigDecimal.ZERO;
	private BigDecimal gstnSgst = BigDecimal.ZERO;
	private BigDecimal gstnCess = BigDecimal.ZERO;
	
	}
