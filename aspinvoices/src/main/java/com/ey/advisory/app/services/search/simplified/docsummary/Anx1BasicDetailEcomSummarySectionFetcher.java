/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionEcomDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author BalaKrishna S
 *
 */
@Component("Anx1BasicDetailEcomSummarySectionFetcher")
public class Anx1BasicDetailEcomSummarySectionFetcher
		implements SimplBasicEcommSummarySectionFetcher {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1BasicDetailEcomSummarySectionFetcher.class);

	@Autowired
	@Qualifier("Anx1EcomSummarySectionDaoImpl")
	private BasicDocEcomSummarySectionDao basicDocEcomSummarySectionDao;

	@Override
	public Map<String, List<Annexure1SummarySectionEcomDto>> fetch(
			Annexure1SummaryReqDto req) {
		// TODO Auto-generated method stub
		LOGGER.debug("Executing LoadBasicSummarySection BEGIN ");
		List<Annexure1SummarySectionEcomDto> result = basicDocEcomSummarySectionDao
				.loadBasicSummarySection(req);
		LOGGER.debug("Executing LoadBasicSummarySection END ");
		Map<String, List<Annexure1SummarySectionEcomDto>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTableSection()));
		
		return map;
		
	}

}
