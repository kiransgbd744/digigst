package com.ey.advisory.app.gstr1.einv;

import java.util.List;

public interface Gstr1EinvPreReconSummaryDao {

	List<Object[]> getPreReconSummary(List<String> gstins,
			Integer returnPeriod);

}
