package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.simplified.Ret1Refund8SummarySectionDaoImpl;
import com.ey.advisory.app.docs.dto.Ret1RefundSummarySectionDto;
import com.ey.advisory.app.services.search.simplified.docsummary.Ret1Refund8SimplBasicSummarySectionFetcher;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Ret1Refund8SimplDefaultSectionFetcherImpl")
@Slf4j
public class Ret1Refund8SimplDefaultSectionFetcherImpl
		implements Ret1Refund8SimplBasicSummarySectionFetcher {

	@Autowired
	@Qualifier("Ret1Refund8SummarySectionDaoImpl")
	private Ret1Refund8SummarySectionDaoImpl basicDocSummarySectionDao;

	public Map<String, List<Ret1RefundSummarySectionDto>> fetch(
			Annexure1SummaryReqDto req) {
		// TODO Auto-generated method stub

		LOGGER.debug(
				"Sumary LoadBasicSummarySection Exection fOR 7.1 to 7.4  BEGIN");
		List<Ret1RefundSummarySectionDto> result = basicDocSummarySectionDao
				.lateBasicSummarySection(req);

		LOGGER.debug(
				"Sumary LoadBasicSummarySection Exection fOR 7.1 to 7.4  END");
		Map<String, List<Ret1RefundSummarySectionDto>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTable()));

		return map;

	}

}
