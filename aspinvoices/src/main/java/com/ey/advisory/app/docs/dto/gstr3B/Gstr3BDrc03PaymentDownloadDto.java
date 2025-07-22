package com.ey.advisory.app.docs.dto.gstr3B;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gstr3BDrc03PaymentDownloadDto {
	
    private String gstin;
	
	private String taxPeriod;
	
	private String drc03Arn;
	
	private BigDecimal taxIgst = BigDecimal.ZERO;

	private BigDecimal taxCgst = BigDecimal.ZERO;

	private BigDecimal taxSgst = BigDecimal.ZERO;

	private BigDecimal taxCess = BigDecimal.ZERO;
	
	private BigDecimal taxTotal = BigDecimal.ZERO;
	
	private BigDecimal intrIgst = BigDecimal.ZERO;
	
	private BigDecimal intrCgst = BigDecimal.ZERO;
	
	private BigDecimal intrSgst = BigDecimal.ZERO;
	
	private BigDecimal intrCess = BigDecimal.ZERO;
	
	private BigDecimal intrTotal = BigDecimal.ZERO;
	
    private BigDecimal feeIgst = BigDecimal.ZERO;
	
	private BigDecimal feeCgst = BigDecimal.ZERO;
	
	private BigDecimal feeSgst = BigDecimal.ZERO;
	
	private BigDecimal feeCess = BigDecimal.ZERO;
	
	private BigDecimal feeTotal = BigDecimal.ZERO;
	
	private BigDecimal penIgst = BigDecimal.ZERO;
		
	private BigDecimal penCgst = BigDecimal.ZERO;
		
	private BigDecimal penSgst = BigDecimal.ZERO;
		
	private BigDecimal penCess = BigDecimal.ZERO;
		
	private BigDecimal penTotal = BigDecimal.ZERO;
	
	private BigDecimal othIgst = BigDecimal.ZERO;
	
	private BigDecimal othCgst = BigDecimal.ZERO;
		
	private BigDecimal othSgst = BigDecimal.ZERO;
		
	private BigDecimal othCess = BigDecimal.ZERO;
		
	private BigDecimal othTotal = BigDecimal.ZERO;
	
	private String getCallStatus;
	
	private String CreatedOn;
}
