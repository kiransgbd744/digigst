package com.ey.advisory.app.gstr2b;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2BGetReportsService {

	public String getReports(List<String> taxPeriods, List<String> gstins,
			String reportType, String userName);

}
