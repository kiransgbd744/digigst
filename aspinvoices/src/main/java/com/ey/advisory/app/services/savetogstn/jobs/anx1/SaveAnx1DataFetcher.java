package com.ey.advisory.app.services.savetogstn.jobs.anx1;

import java.util.List;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface SaveAnx1DataFetcher {
	
	public List<Object[]> findAnx1InvoiceLevelData(String gstin,
			String retPeriod,
			String groupCode, String docType, List<Long> docIds);

	public List<Object[]> findAnx1SummaryData(String gstin,
			String retPeriod,
			String groupCode, String docType);
	
	/*public List<List<Object[]>> findAnx1Data(Anx1SaveToGstnReqDto dto,
			String groupCode, String docType, List<Long> docIds);*/
	
	public List<Object[]> findAnx1CancelledData(String gstin,
			String retPeriod,
			String groupCode,
			String docType);
}
