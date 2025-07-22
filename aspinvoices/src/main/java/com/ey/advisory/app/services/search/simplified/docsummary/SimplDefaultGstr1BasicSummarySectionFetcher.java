package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.simplified.BasicDocSummarySectionDao;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * 
 * @author Mohana.Dasari
 *
 */

@Component("SimplDefaultGstr1BasicSummarySectionFetcher")
public class SimplDefaultGstr1BasicSummarySectionFetcher
		implements SimplBasicSummarySectionFetcher {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SimplDefaultGstr1BasicSummarySectionFetcher.class);

	@Autowired
	@Qualifier("B2BDocSummarySectionDaoImpl")
	private BasicDocSummarySectionDao basicDocSummarySectionDao;

	@Override
	public Map<String, List<Annexure1SummarySectionDto>> fetch(
			Annexure1SummaryReqDto req) {

		LOGGER.debug(
				"Sumary LoadBasicSummarySection Exection fOR 3A,3B,3C,3D,3E,3F AND 3G BEGIN");
		List<Annexure1SummarySectionDto> result = basicDocSummarySectionDao
				.loadBasicSummarySection(req);

		LOGGER.debug(
				"Sumary LoadBasicSummarySection Exection fOR 3A TO 3G END");
		Map<String, List<Annexure1SummarySectionDto>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTableSection()));

		return map;
	}

	@Override
	public Map<String, List<Annexure1SummarySectionDto>> fetchb2c(
			Annexure1SummaryReqDto req) {
		// ANX_B2C Data Retriving
		LOGGER.debug("Sumary LoadBasicSummarySection Exection fOR 3A BEGIN");
		List<Annexure1SummarySectionDto> result = basicDocSummarySectionDao
				.loadBasicb2cSummarySection(req);
		LOGGER.debug(
				"Sumary LoadBasicSummarySection Exection fOR 3A TO 3G END");
		Map<String, List<Annexure1SummarySectionDto>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTableSection()));

		return map;
	}

}
