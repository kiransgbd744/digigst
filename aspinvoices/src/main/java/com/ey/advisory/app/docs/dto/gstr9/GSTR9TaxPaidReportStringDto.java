
package com.ey.advisory.app.docs.dto.gstr9;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class GSTR9TaxPaidReportStringDto {

	private String gstin;

	private String subSection;

	private String natureOfSupply;

	private String taxPayableFiledAutoComp;

	private String pdCashFiledAutoComp;

	private String pdIgstFiledAutoComp;

	private String pdCgstFiledAutoComp;

	private String pdSgstFiledAutoComp;

	private String pdCessFiledAutoComp;

	private String taxPayableAutoCal;

	private String pdCashAutoCal;

	private String pdIgstAutoCal;

	private String pdCgstAutoCal;

	private String pdSgstAutoCal;

	private String pdCessAutoCal;

	private String taxPayableUserInput;

	private String pdCashUserInput;

	private String pdIgstUserInput;

	private String pdCgstUserInput;

	private String pdSgstUserInput;

	private String pdCessUserInput;

	private String taxPayableGstn;

	private String pdCashGstn;

	private String pdIgstGstn;

	private String pdCgstGstn;

	private String pdSgstGstn;

	private String pdCessGstn;

	private String taxPayableDiff;

	private String pdCashDiff;

	private String pdIgstDiff;

	private String pdCgstDiff;

	private String pdSgstDiff;

	private String pdCessDiff;

	
}
