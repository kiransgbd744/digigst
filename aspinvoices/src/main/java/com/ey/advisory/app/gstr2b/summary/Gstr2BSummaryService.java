package com.ey.advisory.app.gstr2b.summary;

import java.util.List;

public interface Gstr2BSummaryService {

	public List<Gstr2BSummaryDto> getGstr2bSummary(List<String> gstins,
		String toTaxPeriod, String fromTaxPeriod);

	//public Gstr2BSummaryDto getGstr2bSummary(Long valueOf);

}
