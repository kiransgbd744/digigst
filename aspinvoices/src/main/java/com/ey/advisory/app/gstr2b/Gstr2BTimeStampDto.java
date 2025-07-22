package com.ey.advisory.app.gstr2b;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr2BTimeStampDto {
	
	private String recipientGSTIN;
	private String taxPeriod;
	private String b2b;
	private String b2bAmendment;
	private String creditOrDebitNotes;
	private String creditOrDebitNoteAmendments;
	private String isd;
	private String isdAmendment;
	private String impg;
	private String impgsez;
	
	
}
