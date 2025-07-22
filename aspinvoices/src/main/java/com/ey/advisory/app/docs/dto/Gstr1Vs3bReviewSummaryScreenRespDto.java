package com.ey.advisory.app.docs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gstr1Vs3bReviewSummaryScreenRespDto {

	private String gstin;
	private String supplies;
	private String formula;
	private String taxPeriod;
	private String taxableValue;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;

}
