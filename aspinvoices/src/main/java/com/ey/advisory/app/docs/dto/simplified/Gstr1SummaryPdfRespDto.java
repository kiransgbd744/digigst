/**
 * 
 */
package com.ey.advisory.app.docs.dto.simplified;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class Gstr1SummaryPdfRespDto {

	private String aspCount;
	private String aspInvoiceValue;
	private String aspTaxableValue;
	private String aspTaxPayble;
	private String aspIgst;
	private String aspCgst;
	private String aspSgst;
	private String aspCess;

	private String aspTotal;
	private String aspCancelled;
	private String aspNetIssued;

	private String aspNitRated;
	private String aspExempted;
	private String aspNonGst;
	private String total;

}
