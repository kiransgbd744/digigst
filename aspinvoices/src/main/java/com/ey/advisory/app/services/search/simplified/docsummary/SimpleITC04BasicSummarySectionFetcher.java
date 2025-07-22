package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.simplified.BasicDocSummaryScreenSectionDaoImpl;
import com.ey.advisory.app.data.daos.client.simplified.BasicItc04SummaryScreenSectionDaoImpl;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.docs.dto.simplified.ITC04SummaryRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.ITC04RequestDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Slf4j
@Component("SimpleITC04BasicSummarySectionFetcher")
public class SimpleITC04BasicSummarySectionFetcher {

	@Autowired
	@Qualifier("BasicItc04SummaryScreenSectionDaoImpl")
	BasicItc04SummaryScreenSectionDaoImpl basicDocSummaryDao;

	public Map<String, List<ITC04SummaryRespDto>> fetch(
			ITC04RequestDto req) {
		// TODO Auto-generated method stub
		LOGGER.debug("loadBasicSummarySection() Execution Started");
		List<ITC04SummaryRespDto> result = basicDocSummaryDao
				.loadBasicSummarySection(req);
		LOGGER.debug("LoadBasic Sumary Section Execution Done and the list "
				+ "of detils are based on section wise---->"+result);
		
		Map<String, List<ITC04SummaryRespDto>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTable()));
		return map;
		
	}
	
}
