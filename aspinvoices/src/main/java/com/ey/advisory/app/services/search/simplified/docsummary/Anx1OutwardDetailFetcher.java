package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.simplified.Anx1OutwardDetailSummarySectionDaoImpl;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Anx1OutwardDetailFetcher")
public class Anx1OutwardDetailFetcher implements Anx1SummaryDetailFetcher {

	@Autowired
	@Qualifier("Anx1OutwardDetailSummarySectionDaoImpl")
	Anx1OutwardDetailSummarySectionDaoImpl basicDocSummarySectionDao;

	@Override
	public Map<String, List<Annexure1SummarySectionDto>> fetch(
			Annexure1SummaryReqDto req) {
		List<Annexure1SummarySectionDto> result = basicDocSummarySectionDao
				.loadBasicSummarySection(req);

		Map<String, List<Annexure1SummarySectionDto>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTableSection()));

		return map;
	}

}
