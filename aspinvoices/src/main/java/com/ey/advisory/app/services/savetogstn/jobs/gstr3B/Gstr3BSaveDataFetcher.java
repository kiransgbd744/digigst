package com.ey.advisory.app.services.savetogstn.jobs.gstr3B;

import java.util.List;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface Gstr3BSaveDataFetcher {

	public List<Object[]> findInvoiceLevelData(String jsonString,
			String groupCode);
	
	public List<Object[]> findInvoiceGstnLevelData(String jsonString,
			String groupCode);

	/*public List<List<Object[]>> findSummaryData(String jsonString,
			String groupCode, String docType);
	
	public List<List<Object[]>> findCancelledData(String jsonString,
			String groupCode, String docType);*/
}
