/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.ret;

import java.util.List;

/**
 * @author Hemasundar.J
 *
 */
public interface SaveRetDataFetcher {
	
	
	/*public List<Object[]> findRetInvoiceLevelData(String gstin,
			String retPeriod, String groupCode, String docType,
			List<Long> docIds);*/

	public List<Object[]> findRetSummaryData(String gstin,
			String retPeriod, String groupCode, String docType);
	
	/*public List<List<Object[]>> findRetData(String jsonString,
			String groupCode, String docType, List<Long> docIds);*/
	
	public List<Object[]> findRetCancelledData(String gstin, String retPeriod,
			String groupCode, String docType);


}
