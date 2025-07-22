package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.simplified.Ret1BasicDocSummarySectionDao;
import com.ey.advisory.app.docs.dto.Ret1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

@Component("Ret1SimplDefaultGstr1BasicSummarySectionFetcher")
public class Ret1SimplDefaultGstr1BasicSummarySectionFetcher
		implements Ret1SimplBasicSummarySectionFetcher {

	
		private static final Logger LOGGER = LoggerFactory
				.getLogger(Ret1SimplDefaultGstr1BasicSummarySectionFetcher.class);

		@Autowired
		@Qualifier("Ret1BasicDocSummarySectionDaoImpl")
		private Ret1BasicDocSummarySectionDao basicDocSummarySectionDao;

		@Override
		public Map<String, List<Ret1SummarySectionDto>> fetch(
				Annexure1SummaryReqDto req) {

			LOGGER.debug(
					"Sumary LoadBasicSummarySection Exection fOR 3A,3B,3C,3D,3E,3F AND 3G BEGIN");
			List<Ret1SummarySectionDto> result = basicDocSummarySectionDao
					.loadBasicSummarySection(req);

			LOGGER.debug(
					"Sumary LoadBasicSummarySection Exection fOR 3A TO 3G END");
			Map<String, List<Ret1SummarySectionDto>> map = result.stream()
					.collect(Collectors.groupingBy(e -> e.getTable()));

			return map;
	}

}
