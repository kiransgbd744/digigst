/**
 * 
 */
package com.ey.advisory.app.reconewbvsitc04;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ravindra V S
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EwbVsItc04SummaryInitiateReconLineItemDto {
	
	private String section;
	private BigInteger itcCount;
	private BigDecimal itcTaxableValue;
	private BigDecimal itcIgst;
	private BigDecimal itcCgst;
	private BigDecimal itcSgst;
	private BigDecimal itcCess;
	private BigInteger ewbCount;
	private BigDecimal ewbTaxableValue;
	private BigDecimal ewbIgst;
	private BigDecimal ewbCgst;
	private BigDecimal ewbSgst;
	private BigDecimal ewbCess;
	
	

}
