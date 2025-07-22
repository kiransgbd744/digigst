package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.simplified.Ret1Payment7BasicDocSummarySectionDao;
import com.ey.advisory.app.docs.dto.Ret1PaymentSummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import lombok.extern.slf4j.Slf4j;

@Component("Ret1Payment7SimplDefaultGstr1BasicSummarySectionFetcher")
@Slf4j
public class Ret1Payment7SimplDefaultGstr1BasicSummarySectionFetcher
		implements Ret1Payment7SimplBasicSummarySectionFetcher {


		@Autowired
		@Qualifier("Ret1Payment_7_BasicDocSummarySectionDaoImpl")
		private Ret1Payment7BasicDocSummarySectionDao basicDocSummarySectionDao;

		@Override
		public Map<String, List<Ret1PaymentSummarySectionDto>> fetch(
				Annexure1SummaryReqDto req) {

			LOGGER.debug(
					"Sumary LoadBasicSummarySection Exection fOR 7.1 to 7.4  BEGIN");
			List<Ret1PaymentSummarySectionDto> result = basicDocSummarySectionDao
					.lateBasicSummarySection(req);

			LOGGER.debug(
					"Sumary LoadBasicSummarySection Exection fOR 7.1 to 7.4  END");
			Map<String, List<Ret1PaymentSummarySectionDto>> map = result.stream()
					.collect(Collectors.groupingBy(e -> e.getTable()));

			return map;
	}

}
