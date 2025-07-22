/**
 * 
 */
package com.ey.advisory.app.anx1.recipientsummary;

import java.util.List;

/**
 * @author Nikhil.Duseja
 *
 */
public interface RecipientSRSummaryDao {
	
	public List<RecipientSRSummaryDto> getAllRecipientSRSummary(
			RecipientSummaryRequestDto requestSummaryDto, 
			String validationQuery);
}
