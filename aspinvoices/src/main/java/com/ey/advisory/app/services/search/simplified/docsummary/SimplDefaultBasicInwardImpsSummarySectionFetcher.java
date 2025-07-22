package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.simplified.BasicDocInwardSummarySectionDao;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("SimplDefaultBasicInwardImpsSummarySectionFetcher")
public class SimplDefaultBasicInwardImpsSummarySectionFetcher
		implements SimplBasicInwardSummarySectionFetcher {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SimplDefaultBasicInwardImpsSummarySectionFetcher.class);

	
	@Autowired
	@Qualifier("InwardImpsSummarysectionDaoImpl")
	private BasicDocInwardSummarySectionDao basicDocImpsSummarySectionDao;
	
	@Override
	public Map<String, List<Annexure1SummarySectionDto>> fetch(Annexure1SummaryReqDto req) {
		
		LOGGER.debug("Calling LoadBasicSummarySection Method ");
		List<Annexure1SummarySectionDto> result = basicDocImpsSummarySectionDao
				.loadBasicSummarySection(req);

		Map<String, List<Annexure1SummarySectionDto>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTableSection()));
		
		return map;
	}

	
}
