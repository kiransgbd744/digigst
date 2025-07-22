package com.ey.advisory.services.gstr3b.entity.setoff;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr3BSetOffComputeSaveInnerDto {
	
	private String gstin;
	
	private String taxPeriod;
	
	
	private BigDecimal otrIgst = BigDecimal.ZERO;

	private BigDecimal otrCgst = BigDecimal.ZERO;

	private BigDecimal otrSgst = BigDecimal.ZERO;

	private BigDecimal otrCess = BigDecimal.ZERO;
	

	private BigDecimal rcIgst = BigDecimal.ZERO;

	private BigDecimal rcCgst = BigDecimal.ZERO;

	private BigDecimal rcSgst = BigDecimal.ZERO;

	private BigDecimal rcCess = BigDecimal.ZERO;
	

	private BigDecimal ipIgst = BigDecimal.ZERO;

	private BigDecimal ipCgst = BigDecimal.ZERO;

	private BigDecimal ipSgst = BigDecimal.ZERO;

	private BigDecimal ipCess = BigDecimal.ZERO;
	

	private BigDecimal lateFeeIgst = BigDecimal.ZERO;

	private BigDecimal lateFeeCgst = BigDecimal.ZERO;

	private BigDecimal lateFeeSgst = BigDecimal.ZERO;

	private BigDecimal lateFeeCess = BigDecimal.ZERO;
	

	private BigDecimal ucbIgst = BigDecimal.ZERO;

	private BigDecimal ucbCgst = BigDecimal.ZERO;

	private BigDecimal ucbSgst = BigDecimal.ZERO;

	private BigDecimal ucbCess = BigDecimal.ZERO;
	

	private BigDecimal acrIgst = BigDecimal.ZERO;

	private BigDecimal acrCgst = BigDecimal.ZERO;

	private BigDecimal acrSgst = BigDecimal.ZERO;

	private BigDecimal acrCess = BigDecimal.ZERO;
	
	//negative laibility
	private BigDecimal adjnegliabIgst2i = BigDecimal.ZERO;

	private BigDecimal adjnegliabCgst2i = BigDecimal.ZERO;

	private BigDecimal adjnegliabSgst2i = BigDecimal.ZERO;

	private BigDecimal adjnegliabCess2i = BigDecimal.ZERO;
	
	
	private BigDecimal adjnegliabIgst8a = BigDecimal.ZERO;

	private BigDecimal adjnegliabCgst8a = BigDecimal.ZERO;

	private BigDecimal adjnegliabSgst8a = BigDecimal.ZERO;

	private BigDecimal adjnegliabCess8a = BigDecimal.ZERO;
	
	
	private BigDecimal netOtherRc2iiIgst = BigDecimal.ZERO;

	private BigDecimal netOtherRc2iiCgst = BigDecimal.ZERO;

	private BigDecimal netOtherRc2iiSgst = BigDecimal.ZERO;

	private BigDecimal netOtherRc2iiCess = BigDecimal.ZERO;
	
	
	private BigDecimal rci9Igst = BigDecimal.ZERO;

	private BigDecimal rci9Cgst = BigDecimal.ZERO;

	private BigDecimal rci9Sgst = BigDecimal.ZERO;

	private BigDecimal rci9Cess = BigDecimal.ZERO;
	
	

}
