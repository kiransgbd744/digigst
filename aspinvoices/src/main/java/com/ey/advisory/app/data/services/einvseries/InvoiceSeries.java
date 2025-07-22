package com.ey.advisory.app.data.services.einvseries;

import lombok.Data;

@Data
public class InvoiceSeries {

	private String gstin;
	private String docType;
	private String docNum;
	private String taxPeriod;
	private String subSupplyType;
	private String supplyType;

}
