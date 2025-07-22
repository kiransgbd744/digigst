package com.ey.advisory.app.docs.dto.gstr6a;

import lombok.Data;

@Data
public class Gstr6ASummaryDataScreenResponseDto {

	private String table;
	private String count;
	private String inVoiceVal;
	private String taxableValue;
	private String totalTax;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;

}
