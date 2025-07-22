package com.ey.advisory.app.gstr1.einv;

import java.util.List;

public interface Gstr1EinvResponseReconSummaryDao {

	List<Object[]> getResponseSummary(List<String> gstins,
			Integer returnPeriod);
}
