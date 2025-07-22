package com.ey.advisory.app.services.savetogstn.jobs.gstr6;

import java.util.List;

/**
 * 
 * @author Sri Bhavya
 *
 */
public interface Gstr6CanDataFetcher {

	public List<Object[]> findInvoiceLevelData(String gstin, String retPeriod, 
			String groupCode, String section, String isdDocType);

}
