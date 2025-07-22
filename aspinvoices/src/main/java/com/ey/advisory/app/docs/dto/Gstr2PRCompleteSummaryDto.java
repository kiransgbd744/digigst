package com.ey.advisory.app.docs.dto;

import lombok.Data;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Data
public class Gstr2PRCompleteSummaryDto {

	private Gstr2PRBasicSectionSummaryDto b2b;
	private Gstr2PRBasicSectionSummaryDto b2ba;
	private Gstr2PRBasicSectionSummaryDto cdn;
	private Gstr2PRBasicSectionSummaryDto cdna;
	private Gstr2PRBasicSectionSummaryDto isd;
	private Gstr2PRBasicSectionSummaryDto isda;
	private Gstr2PRBasicSectionSummaryDto imp;
//	private Gstr2PRBasicSectionSummaryDto impsa;
	private Gstr2PRBasicSectionSummaryDto impa;
//	private Gstr2PRBasicSectionSummaryDto impga;
//	private Gstr2PRBasicSectionSummaryDto impgs;
//	private Gstr2PRBasicSectionSummaryDto impgsa;
	private Gstr2PRBasicSectionSummaryDto rcurd;
	private Gstr2PRBasicSectionSummaryDto rcurda;
	private Gstr2PRBasicSectionSummaryDto rcmadv;
	}
