/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.anx2;

import java.util.List;

/**
 * @author Hemasundar.J
 *
 */
public interface SaveAnx2DataFetcher {

	public List<Object[]> findInvoiceLevelData(String gstin,
			String retPeriod, String groupCode, String docType,
			List<Long> docIds);

	public List<Object[]> findCancelledData(String gstin,
			String retPeriod, String groupCode, String docType);
}
