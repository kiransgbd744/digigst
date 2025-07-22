package com.ey.advisory.app.anx1.recipientsummary;

import java.util.List;

public interface RecipientResponseSummaryService {

	/**
	 * @author Arun KA
	 *
	 **/	
public List<RecipientResponseSummaryDto> getRecipientResponseSummary(
			String condition, RecipientSummaryRequestDto request);

}
