package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.simplified.Ret1A6BasicDocSummarySectionDao;
import com.ey.advisory.app.docs.dto.simplified.TaxSectionPaymentSummaryDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Ret1A6BasicSummarySectionFetcher")
@Slf4j
public class Ret1A6BasicSummarySectionFetcher
		implements Ret1A6SimplBasicSummarySectionFetcher {

	@Autowired
	@Qualifier("Ret1APayment_6_BasicDocSummarySectionDaoImpl")
	private Ret1A6BasicDocSummarySectionDao basicDocSummarySectionDao;

	public Map<String, List<TaxSectionPaymentSummaryDto>> fetch(
			Annexure1SummaryReqDto req) {
		LOGGER.debug(
				"Sumary LoadBasicSummarySection Exection fOR 6.1 to 6.4  BEGIN");
		List<TaxSectionPaymentSummaryDto> result = basicDocSummarySectionDao
				.loadBasicSummarySection(req);

		LOGGER.debug(
				"Sumary LoadBasicSummarySection Exection fOR 6.1 to 6.4  END");
		Map<String, List<TaxSectionPaymentSummaryDto>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTable()));

		return map;
	}

}
