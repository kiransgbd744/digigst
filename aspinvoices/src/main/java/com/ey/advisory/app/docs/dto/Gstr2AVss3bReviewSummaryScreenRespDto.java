package com.ey.advisory.app.docs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gstr2AVss3bReviewSummaryScreenRespDto {

	private String gstin;
	private String supplies;
	private String formula;
	private String taxPeriod;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;

}
