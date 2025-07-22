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
public class Gstr3BDrc01cDownloadDto {
	
	private String gstin;
	
	private String taxPeriod;
	
	private String refId;
	
	private String profile;
	
	
	private BigDecimal gstr2bIgst = BigDecimal.ZERO;

	private BigDecimal gstr2bCgst = BigDecimal.ZERO;

	private BigDecimal gstr2bSgst = BigDecimal.ZERO;

	private BigDecimal gstr2bCess = BigDecimal.ZERO;
	
	private BigDecimal gstr2bTotal = BigDecimal.ZERO;
	
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
	
	private String statusDrc01c;
	
	private String getCallDateTime;
	
	private String reasonCode;
	
	private String reasonDesc;
	
	private String suppDocId;
	
	private String filingStatusDrc01c;
	
	private String createdBy;
	
}
