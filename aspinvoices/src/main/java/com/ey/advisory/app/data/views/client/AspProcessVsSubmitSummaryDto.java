package com.ey.advisory.app.data.views.client;
import java.math.BigDecimal;

import lombok.Data;

/**
 * @author Mahesh.Golla
 *
 */
@Data
public class AspProcessVsSubmitSummaryDto {
	private String gstin;
	private String taxPeriod;
	private String derTaxPeriod;
	private String transType;
	private String processCount;
	private String processTaxableValue;
	private String processTotalTax;
	private String processIgst;
	private String processCgst;
	private String processSgst;
	private String processCess;
	private String submitCount;
	private String submitTaxable;
	private String submitTotal;
	private String submitIgst;
	private String submitCgst;
	private String submitSgst;
	private String submitCess;
	
	private String diffCount;
	private String diffTaxableValue;
	private BigDecimal diffTotalTax;
	private String diffIgst;
	private String diffCgst;
	private String diffSgst;
	private String diffCess;
	
	private String gstnGetCalls;
}
