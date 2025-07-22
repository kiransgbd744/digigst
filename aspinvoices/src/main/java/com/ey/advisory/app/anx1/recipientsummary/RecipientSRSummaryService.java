package com.ey.advisory.app.anx1.recipientsummary;

import java.util.List;

public interface RecipientSRSummaryService {

	public  List<RecipientSRSummaryDto> getAnx1SRSummary(
			RecipientSummaryRequestDto request, String validQuery);

}
