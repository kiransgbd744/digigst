package com.ey.advisory.app.anx1.recipientsummary;

import java.util.List;


/**
 * @author Arun KA
 *
 **/

public interface RecipientResponseSummaryDetailDao {

	public List<RecipientDBResponseDto> getAllResponseSummaryDetails(
			String condition, RecipientSummaryRequestDto  requestRecipientSummary);

}
