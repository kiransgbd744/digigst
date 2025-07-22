package com.ey.advisory.app.data.services.savetogstn.gstr8;

import java.util.List;

import com.ey.advisory.core.api.SaveToGstnOprtnType;
/**
 * 
 * @author Siva.Reddy
 *
 */
public interface Gstr8SaveDataFetcher {

	public List<Object[]> findInvoiceLevelData(String gstin, String retPeriod,
			String groupCode, String docType, Long userRequestId);
	
	public List<Object[]> findGstr8CancelledData(String gstin, String retPeriod,
			String groupCode, String section, SaveToGstnOprtnType oprtnType, Long userRequestId);


}
