/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Component("Gstr1AProcessSubmitBasicSummarySectionFetcher")
public class Gstr1AProcessSubmitBasicSummarySectionFetcher
		implements Gstr1ASimpleSummarySectionFetcher {

	@Autowired
	@Qualifier("Gstr1AProcessSubmitSummaryScreenSectionDaoImpl")
	Gstr1AProcessSubmitSummaryScreenSectionDaoImpl basicDocSummaryDao;

	@Override
	public Map<String, List<Gstr1SummarySectionDto>> fetch(
			Annexure1SummaryReqDto req) {
		// TODO Auto-generated method stub
		LOGGER.debug("loadBasicSummarySection() Execution Started");
		List<Gstr1SummarySectionDto> result = basicDocSummaryDao
				.loadBasicSummarySection(req);
		LOGGER.debug("LoadBasic Sumary Section Execution Done and the list "
				+ "of detils are based on section wise---->" + result);

		Map<String, List<Gstr1SummarySectionDto>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTaxDocType()));
		return map;

	}

}
