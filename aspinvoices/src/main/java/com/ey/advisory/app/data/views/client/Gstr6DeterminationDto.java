package com.ey.advisory.app.data.views.client;

import lombok.Data;

/**
 * @author Mahesh.Golla
 *
 */
@Data
public class Gstr6DeterminationDto {
	private String retPeriod;
	private String isdGstin;
	private String reciGstin;
	private String stateCode;
	private String orgReciGstin;
	private String orgStateCode;
	private String docType;
	private String supType;
	private String docNo;
	private String docDate;
	private String orgDocNo;
	private String orgDocDate;
	private String orgCrNoteNo;
	private String orgCrNoteDate;
	private String elIndiCator;
	private String igstAsIgst;
	private String igstAsSgst;
	private String igstAsCgst;

	private String sgstAsSgst;
	private String sgstAsIgst;

	private String cgstAsCgst;
	private String cgstAsIgst;

	private String cessAmt;
	
	private String derRetPeriod;
}
