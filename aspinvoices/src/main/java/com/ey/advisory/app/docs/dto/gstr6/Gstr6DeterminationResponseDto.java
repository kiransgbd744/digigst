package com.ey.advisory.app.docs.dto.gstr6;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Data
public class Gstr6DeterminationResponseDto {

	private String id;
	
	private List<Long> ids;

	private String gstin;
	
	private String isdGstin;
	
	private String currentRetPer;

	private String state;
	
	private String stateName;

	private String getGstr1Status;

	private BigDecimal toDigiGst;

	private BigDecimal toGstn;

	private String regType;

	private String authToken;

	private String turnoverGstnStatus;

	private String turnoverDigiStatus;

	private String distribStatus;

	private BigDecimal totalTax;

	private BigDecimal disEligIGST;

	private BigDecimal disEligCGST;

	private BigDecimal disEligSGST;

	private BigDecimal disEligCESS;

	private BigDecimal disInEligIGST;

	private BigDecimal disInEligCGST;

	private BigDecimal disInEligSGST;

	private BigDecimal disInEligCESS;

	private String turnoverGstnTimestamp;

	private String turnoverDigiTimestamp;

	private String distribTimestamp;

	private String gstr1GetTime;
	
	private String userInputData;

	private BigDecimal turnoverDigiGST;

	private BigDecimal turnoverGstn;

	private BigDecimal turnoverUserEdited;
	
	private String type;
}
