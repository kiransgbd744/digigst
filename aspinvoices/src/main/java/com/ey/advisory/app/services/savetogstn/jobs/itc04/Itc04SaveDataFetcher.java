package com.ey.advisory.app.services.savetogstn.jobs.itc04;

import java.util.List;

/**
 * 
 * @author SriBhavya
 *
 */
public interface Itc04SaveDataFetcher {

	public List<Object[]> findInvoiceLevelData(String gstin, String retPeriod, String groupCode, String section,
			List<Long> docIds);
	
	public List<Object[]> findCanInvoiceLevelData(String gstin, String retPeriod, String groupCode, String section,
			List<Long> docIds);

}
