package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.simplified.Ret1ABasicDocSummarySectionDaoImpl;
import com.ey.advisory.app.docs.dto.Ret1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component("Ret1ASimplDefaultGstr1BasicSummarySectionFetcher")
public class Ret1ASimplDefaultGstr1BasicSummarySectionFetcher
		/*implements Ret1SimplBasicSummarySectionFetcher*/ {

		@Autowired
		@Qualifier("Ret1ABasicDocSummarySectionDaoImpl")
		private Ret1ABasicDocSummarySectionDaoImpl basicDocSummarySectionDao;

		//@Override
		public Map<String, List<Ret1SummarySectionDto>> fetch(
				Annexure1SummaryReqDto req) {

			LOGGER.debug(
					"Sumary LoadBasicSummarySection Exection fOR 3A to 4B BEGIN");
			List<Ret1SummarySectionDto> result = basicDocSummarySectionDao
					.loadBasicSummarySection(req);

			LOGGER.debug(
					"Sumary LoadBasicSummarySection Exection fOR 3A TO 4B END");
			Map<String, List<Ret1SummarySectionDto>> map = result.stream()
					.collect(Collectors.groupingBy(e -> e.getTable()));

			return map;
	}

}
