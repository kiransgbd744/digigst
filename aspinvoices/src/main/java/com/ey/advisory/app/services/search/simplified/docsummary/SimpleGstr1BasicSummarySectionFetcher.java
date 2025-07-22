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

import com.ey.advisory.app.data.daos.client.simplified.BasicDocSummaryScreenSectionDaoImpl;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author BalaKrishna S
 *
 */
@Component("SimpleGstr1BasicSummarySectionFetcher")
public class SimpleGstr1BasicSummarySectionFetcher
		implements SimpleSummarySectionFetcher {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SimpleGstr1BasicSummarySectionFetcher.class);
	
	@Autowired
	@Qualifier("BasicDocSummaryScreenSectionDaoImpl")
	BasicDocSummaryScreenSectionDaoImpl basicDocSummaryDao;

	@Override
	public Map<String, List<Gstr1SummarySectionDto>> fetch(
			Annexure1SummaryReqDto req) {
		// TODO Auto-generated method stub
		LOGGER.debug("loadBasicSummarySection() Execution Started");
		List<Gstr1SummarySectionDto> result = basicDocSummaryDao
				.loadBasicSummarySection(req);
		LOGGER.debug("LoadBasic Sumary Section Execution Done and the list "
				+ "of detils are based on section wise---->"+result);
		
		Map<String, List<Gstr1SummarySectionDto>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTaxDocType()));
		return map;
		
	}

}
