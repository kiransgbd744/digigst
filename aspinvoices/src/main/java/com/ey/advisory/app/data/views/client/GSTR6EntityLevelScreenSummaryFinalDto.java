package com.ey.advisory.app.data.views.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GSTR6EntityLevelScreenSummaryFinalDto {

	private String gstin;
	private String docType;
	private String tableDescription;
	private String aspCount;
	private String aspInvValue;
	private String aspTaxbValue;
	private String aspTotTax;
	private String aspIgst;
	private String aspCgst;
	private String aspSgst;
	private String aspCess;

	private String gstnCount;
	private String gstnInvValue;
	private String gstnTaxbValue;
	private String gstnTotTax;
	private String gstnIgst;
	private String gstnCgst;
	private String gstnSgst;
	private String gstnCess;

	private String diffCount;
	private String diffInvValue;
	private String diffTaxbValue;
	private String diffTotTax;
	private String diffIgst;
	private String diffCgst;
	private String diffSgst;
	private String diffCess;
}
