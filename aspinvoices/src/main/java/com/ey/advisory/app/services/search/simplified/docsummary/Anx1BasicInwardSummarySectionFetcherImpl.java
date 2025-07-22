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
@Component("Anx1BasicInwardSummarySectionFetcherImpl")
public class Anx1BasicInwardSummarySectionFetcherImpl
		implements Anx1BasicInwardDetailSummarySectionFetcher {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1BasicInwardSummarySectionFetcherImpl.class);

	
	@Autowired
	@Qualifier("Anx1InwardDetailSummarySectionDaoImpl")
	private BasicDocInwardSummarySectionDao basicDocInSummarySectionDao;

	@Override
	public Map<String, List<Annexure1SummarySectionDto>> fetchInward(Annexure1SummaryReqDto req) {

		LOGGER.debug("Executing LoadBasicSummarySection BEGIN ");
		List<Annexure1SummarySectionDto> result = basicDocInSummarySectionDao
				.loadBasicSummarySection(req);

		Map<String, List<Annexure1SummarySectionDto>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTableSection()));
		
		return map;
	}

}
