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
public class GSTR3BInterestDto {

	private String supplierGSTIN;
	private String returnPeriod;
	private String previousFiledReturnPeriod;
	private String filingDate;
	private String taxPayable;
	private String pdCash;
	private String pdITC;
	private String interest;
	private String interestComputationDate;

}
