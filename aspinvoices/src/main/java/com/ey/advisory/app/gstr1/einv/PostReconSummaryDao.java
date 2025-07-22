package com.ey.advisory.app.gstr1.einv;

import java.util.List;

public interface PostReconSummaryDao {

	List<Object[]> getPostReconSummaryData(List<String> recipientGstins,
			Integer taxPeriod);

}