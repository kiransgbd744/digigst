/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import lombok.Data;

/**
 * @author BalaKrishna S
 *
 */
@Data
public class Gstr1CompleteSummaryDto {

	private Gstr1BasicSectionSummaryDto b2b;

	private Gstr1BasicSectionSummaryDto b2ba;

	private Gstr1BasicSectionSummaryDto b2cl;
	private Gstr1BasicSectionSummaryDto b2cla;
	
	private Gstr1BasicCDSectionSummaryDto hsn;

	private Gstr1BasicSectionSummaryDto exp;
	private Gstr1BasicSectionSummaryDto expa;
	private Gstr1BasicSectionSummaryDto cdnr;

	private Gstr1BasicSectionSummaryDto cdnra;

	private Gstr1BasicSectionSummaryDto cdnur;

	private Gstr1BasicSectionSummaryDto cdnura;

	private Gstr1BasicSectionSummaryDto b2cs;

	private Gstr1BasicSectionSummaryDto b2csa;
	
	private Gstr1BasicSectionSummaryDto gstnCdnr;

	// Gstn Get Tables
	private Gstr1BasicSectionSummaryDto gstnCdnra;

	private Gstr1BasicSectionSummaryDto gstnCdnur;

	private Gstr1BasicSectionSummaryDto gstnCdnura;

	private Gstr1BasicSectionSummaryDto gstnB2cs;

	private Gstr1BasicSectionSummaryDto gstnB2csa;
	private Gstr1BasicSectionSummaryDto gstnHsn;
	private Gstr1BasicSectionSummaryDto gstnHsnB2b;
	private Gstr1BasicSectionSummaryDto gstnHsnB2c;

	
	private Gstr1BasicSectionSummaryDto at;
	private Gstr1BasicSectionSummaryDto ata;
	private Gstr1BasicSectionSummaryDto txpd;
	private Gstr1BasicSectionSummaryDto txpda;
	private Gstr1BasicSectionSummaryDto sezt;
	private Gstr1BasicSectionSummaryDto sezwt;
	private Gstr1BasicDocSectionSummaryDto docIssues;
	private Gstr1BasicNilSectionSummaryDto nil;
	
	
	private Gstr1BasicSectionSummaryDto tbl14Sec;
	private Gstr1BasicSectionSummaryDto tbl14ofOne;
	private Gstr1BasicSectionSummaryDto tbl14ofTwo;
	private Gstr1BasicSectionSummaryDto tbl14AmdSec;
	private Gstr1BasicSectionSummaryDto tbl14AmdOne;
	private Gstr1BasicSectionSummaryDto tbl14AmdTwo;
	private Gstr1BasicSectionSummaryDto tbl15Sec;
	private Gstr1BasicSectionSummaryDto tbl15ofOne;
	private Gstr1BasicSectionSummaryDto tbl15ofTwo;
	private Gstr1BasicSectionSummaryDto tbl15ofThree;
	private Gstr1BasicSectionSummaryDto tbl15dofFour;
	private Gstr1BasicSectionSummaryDto tbl15AmdOneSec;
	private Gstr1BasicSectionSummaryDto tbl15AmdTwoSec;
	private Gstr1BasicSectionSummaryDto tbl15AmdOne;
	private Gstr1BasicSectionSummaryDto tbl15AmdTwo;
	private Gstr1BasicSectionSummaryDto tbl15AmdThree;
	private Gstr1BasicSectionSummaryDto tbl15AmdFour;

	
}
