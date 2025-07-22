package com.ey.advisory.app.services.savetogstn.jobs.gstr2x;

import java.util.List;
/**
 * 
 * @author SriBhavya
 *
 */
public interface Gstr2XSaveDataFetcher {
	
	public List<Object[]> findInvoiceLevelData(String gstin, String retPeriod,
			String groupCode, String docType);

}
