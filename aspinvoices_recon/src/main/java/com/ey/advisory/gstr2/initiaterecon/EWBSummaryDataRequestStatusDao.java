/**
 * 
 */
package com.ey.advisory.gstr2.initiaterecon;

import java.util.List;

/**
 * @author vishal.verma
 *
 */
public interface EWBSummaryDataRequestStatusDao {

	List<EWBSummaryDataRequestStatusDto> getRequestDataSummaryStatus(List<String> gstinlist, 
					String criteria, String fromDate,String toDate);
}
