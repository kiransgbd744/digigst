package com.ey.advisory.app.gstr3b;

import com.ey.advisory.common.AppException;

/**
 * @author vishal.verma
 *
 */
public interface Gstr3bComuteCopyToUserInputService {

	public String copyToUserInput(String taxPeriod, String gstin,
			String inwardFlag, String outwardFlag, 
			String interestAndLateFeeFlag) throws AppException;
}
