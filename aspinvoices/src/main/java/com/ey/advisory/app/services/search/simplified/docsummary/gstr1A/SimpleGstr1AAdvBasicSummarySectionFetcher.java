/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author Shashikant.Shukla
 *
 */
@Service("SimpleGstr1AAdvBasicSummarySectionFetcher")
public class SimpleGstr1AAdvBasicSummarySectionFetcher
		implements Gstr1AAdvSimpleSummarySectionFetcher {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SimpleGstr1AAdvBasicSummarySectionFetcher.class);

	@Autowired
	@Qualifier("AdvBasicGstr1ADocSummaryScreenSectionDaoImpl")
	AdvBasicGstr1ADocSummaryScreenSectionDaoImpl basicDocSummaryDao;

	@Override
	public Map<String, List<Gstr1SummarySectionDto>> fetchAT(
			Annexure1SummaryReqDto req) {
		// TODO Auto-generated method stub
		List<Gstr1SummarySectionDto> result = basicDocSummaryDao
				.loadBasicSummaryATSection(req);
		LOGGER.debug("loadBasicSummaryATSection() method executed "
				+ "and return AT Section Data---->" + result);
		Map<String, List<Gstr1SummarySectionDto>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTaxDocType()));
		return map;
	}

}
