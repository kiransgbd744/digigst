package com.ey.advisory.app.anx1.recipientsummary;

import java.util.List;

/**
 * @author vishal.verma
 *
 */

public interface RecipientResponseDetailService {
	
	public List<RecipientResponseDetailsDto> getRecipientResponseDetail
	(String reqData, RecipientSummaryRequestDto req);

}
