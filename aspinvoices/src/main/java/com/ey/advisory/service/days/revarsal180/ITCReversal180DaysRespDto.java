/**
 * 
 */
package com.ey.advisory.service.days.revarsal180;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class ITCReversal180DaysRespDto {
	
	private String gstin;
	
	private String status;
	
	private String state;
	
	private String authtoken;
	
	private String regType;
	
	private BigDecimal totalTax = BigDecimal.ZERO;
	
	private BigDecimal igst = BigDecimal.ZERO;
	
	private BigDecimal cgst = BigDecimal.ZERO;
	
	private BigDecimal sgst = BigDecimal.ZERO;
	
	private BigDecimal Cess = BigDecimal.ZERO;
	
	private String count;
	
	private String statusDesc;
	
	private Long computeId;
	
	
	

}
