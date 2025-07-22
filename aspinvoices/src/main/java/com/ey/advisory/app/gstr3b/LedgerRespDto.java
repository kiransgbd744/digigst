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
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LedgerRespDto {
	
	private BigDecimal igstIntr;
	
	private BigDecimal igstTx;
	
	private BigDecimal igstLateFee;
	
	
	private BigDecimal cgstIntr;
	
	private BigDecimal cgstTx;
	
	private BigDecimal cgstLateFee;
	
	
	private BigDecimal sgstIntr;
	
	private BigDecimal sgstTx;
	
	private BigDecimal sgstLateFee;
	
	
	private BigDecimal csgstIntr;
	
	private BigDecimal csgstTx;
	
	private BigDecimal csgstLateFee;
	
	
	private BigDecimal crIgst;
	
	private BigDecimal crCgst;
	
	private BigDecimal crSgst; 
	
	private BigDecimal crCess; 
	
	
	private String errorMsg;
}
