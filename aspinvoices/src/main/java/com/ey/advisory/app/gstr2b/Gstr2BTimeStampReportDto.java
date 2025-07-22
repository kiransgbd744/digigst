package com.ey.advisory.app.gstr2b;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Hema G M
 *
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr2BTimeStampReportDto {

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
