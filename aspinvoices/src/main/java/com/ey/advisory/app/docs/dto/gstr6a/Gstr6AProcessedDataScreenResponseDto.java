package com.ey.advisory.app.docs.dto.gstr6a;

import lombok.Data;

@Data
public class Gstr6AProcessedDataScreenResponseDto {

	private String gstin;
	private String state;
	private String status;
	private String timeStamp;
	private String count;
	private String invoiceValue;
	private String taxableValue;
	private String totalTax;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;

}
