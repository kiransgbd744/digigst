package com.ey.advisory.app.data.daos.client.simplified;

import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr2PRSummarySectionDto;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
public interface BasicGstr2PRDocSummaryDao {

	public abstract List<Gstr2PRSummarySectionDto> loadBasicSummarySection(
			Gstr2ProcessedRecordsReqDto req);
	
}
