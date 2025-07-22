package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.simplified.BasicGstr2PRDocSummaryDaoImpl;
import com.ey.advisory.app.docs.dto.Gstr2PRSummarySectionDto;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("SimpleGstr2PRSummarySectionFetcher")
public class SimpleGstr2PRSummarySectionFetcher
		implements SimpleSummaryPRSectionFetcher {

	@Autowired
	@Qualifier("BasicGstr2PRDocSummaryDaoImpl")
	BasicGstr2PRDocSummaryDaoImpl basicSummary;
	
	@Override
	public Map<String, List<Gstr2PRSummarySectionDto>> fetch(
			Gstr2ProcessedRecordsReqDto req) {
		// TODO Auto-generated method stub
		
		List<Gstr2PRSummarySectionDto> result = basicSummary.loadBasicSummarySection(req);
		
		Map<String, List<Gstr2PRSummarySectionDto>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTable()));
		return map;
		
	}

}
