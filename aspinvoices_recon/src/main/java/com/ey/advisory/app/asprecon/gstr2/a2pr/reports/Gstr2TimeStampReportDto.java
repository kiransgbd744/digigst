package com.ey.advisory.app.asprecon.gstr2.a2pr.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Vishal.Verma
 *
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Gstr2TimeStampReportDto {

	private String RecipientGSTIN;
	private String TaxPeriod;
	private String B2B;
	private String B2BAmendments;
	private String CreditDebitNotes;
	private String CreditDebitNotesAmendments;
	private String ISD;
	private String ISDAmendments;

}
