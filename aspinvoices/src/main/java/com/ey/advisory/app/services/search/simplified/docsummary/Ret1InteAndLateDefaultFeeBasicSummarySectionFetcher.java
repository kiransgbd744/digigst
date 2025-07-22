package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.simplified.Ret1Int6BasicDocSummarySectionDao;
import com.ey.advisory.app.docs.dto.Ret1LateFeeSummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Ret1InteAndLateDefaultFeeBasicSummarySectionFetcher")
@Slf4j
public class Ret1InteAndLateDefaultFeeBasicSummarySectionFetcher
		implements Ret1int6SimplBasicSummarySectionFetcher {


		@Autowired
		@Qualifier("Ret1InteLateFeeBasicDocSummarySectionDaoImpl")
		private Ret1Int6BasicDocSummarySectionDao basicDocSummarySectionDao;

		@Override
		public Map<String, List<Ret1LateFeeSummarySectionDto>> fetch(
				Annexure1SummaryReqDto req) {

			LOGGER.debug(
					"Sumary LoadBasicSummarySection Exection fOR 5.1 to 5.4  BEGIN");
			List<Ret1LateFeeSummarySectionDto> result = basicDocSummarySectionDao
					.lateBasicSummarySection(req);

			LOGGER.debug(
					"Sumary LoadBasicSummarySection Exection fOR 5.1 to 5.4  END");
			Map<String, List<Ret1LateFeeSummarySectionDto>> map = result.stream()
					.collect(Collectors.groupingBy(e -> e.getTable()));

			return map;
	}

}
