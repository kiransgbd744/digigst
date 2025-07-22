/**
 * 
 */
package com.ey.advisory.app.anx1.counterparty;

import java.util.List;

public interface CounterPartySummaryService {
	
	public List<CounterPartyInfoResponseSummaryDto> getCounterPartySummary
	(List<String> sgstin, String taxPeriod, 
			List<String> tableSection,List<String> docType);

}
