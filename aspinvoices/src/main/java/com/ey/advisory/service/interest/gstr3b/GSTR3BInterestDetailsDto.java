/**
 * 
 */
package com.ey.advisory.service.interest.gstr3b;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class GSTR3BInterestDetailsDto {

	private String supplierGSTIN;
	private String returnPeriod;
	private String profile;
	private String dueDate;
	private String annualAggregateTurnover;
	private String aaroFinFlag;
	private String aatoFY;
	private String notificationName;
	private String notificationDate;
	private String interestLiabilityConsidered;
	private String interestLiabilityDeclared;
	private String challanPaidReturnPeriod;
	private String cashPaidChallan;
	private String retunPeriodStartDate;
	private String retunPeriodEndDate;
	private String interestRateApplicable;
	private String noOfDaysDelayed;
	private String systemCalculatedInterest;

}
