package com.ey.advisory.app.data.views.client;

import lombok.Data;

@Data
public class Gstr6DistrubutionStatusReportsRespDto {
	private String category;
	private String returnPeriod;
	private String isdGstin;
	private String recipentGstin;
	private String stateCode;
	private String originalRecipeintGSTIN;
	private String originalStatecode;
	private String documentType;
	private String supplyType;
	private String documentNumber;
	private String documentDate;
	private String originalDocumentNumber;
	private String originalDocumentDate;
	private String originalCreditNoteNumber;
	private String originalCreditNoteDate;
	private String eligibleIndicator;
	private String igstAsIgst;
	private String igstAsSgst;
	private String igstAsCgst;
	private String sgstAsSgst;
	private String sgstAsIgst;
	private String cgstAsCgst;
	private String cgstAsIgst;
	private String cessAmount;
}
