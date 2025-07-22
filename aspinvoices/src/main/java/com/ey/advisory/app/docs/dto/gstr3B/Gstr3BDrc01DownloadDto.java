package com.ey.advisory.app.docs.dto.gstr3B;


import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Harsh
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gstr3BDrc01DownloadDto {
	
	private String gstin;
	
	private String taxPeriod;
	
	private String refId;
	
	private String profile;
	
	
	private BigDecimal gstr1Igst = BigDecimal.ZERO;

	private BigDecimal gstr1Cgst = BigDecimal.ZERO;

	private BigDecimal gstr1Sgst = BigDecimal.ZERO;

	private BigDecimal gstr1Cess = BigDecimal.ZERO;
	
	private BigDecimal gstr1Total = BigDecimal.ZERO;
	
	private BigDecimal gstr3bIgst = BigDecimal.ZERO;
	
	private BigDecimal gstr3bCgst = BigDecimal.ZERO;
	
	private BigDecimal gstr3bSgst = BigDecimal.ZERO;
	
	private BigDecimal gstr3bCess = BigDecimal.ZERO;
	
	private BigDecimal gstr3bTotal = BigDecimal.ZERO;
	
    private BigDecimal diffIgst = BigDecimal.ZERO;
	
	private BigDecimal diffCgst = BigDecimal.ZERO;
	
	private BigDecimal diffSgst = BigDecimal.ZERO;
	
	private BigDecimal diffCess = BigDecimal.ZERO;
	
	private BigDecimal diffTotal = BigDecimal.ZERO;
	
	private String statusDrc01b;
	
	private String getCallDateTime;
	
	private String reasonCode;
	
	private String reasonDesc;
	
	private String filingStatusDrc01Reply;
	
	private String createdBy;
	
}
