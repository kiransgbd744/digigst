/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.simplified.ProcsSubmitScreenHsnSectionDaoImpl;
import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author BalaKrishna S
 *
 */
@Slf4j
@Service("ProcsSubmitSummaryHsnSectionFetcher")
public class ProcsSubmitSummaryHsnSectionFetcher
		implements SimpleSummaryHsnSectionFetcher {

	@Autowired
	@Qualifier("ProcsSubmitScreenHsnSectionDaoImpl")
	ProcsSubmitScreenHsnSectionDaoImpl basicDocSummaryDao;
	
	
	@Override
	public Map<String, List<Gstr1SummaryCDSectionDto>> fetch(
			Annexure1SummaryReqDto req) {
		
		// TODO Auto-generated method stub
				LOGGER.debug("loadBasicSummarySection() Execution Started");
				List<Gstr1SummaryCDSectionDto> result = basicDocSummaryDao
						.loadBasicSummarySection(req);
				LOGGER.debug("LoadBasic Sumary Section Execution Done and the list "
						+ "of detils are based on section wise---->"+result);
				
				Map<String, List<Gstr1SummaryCDSectionDto>> map = result.stream()
						.collect(Collectors.groupingBy(e -> e.getTaxDocType()));
				return map;
				
		
	}

	
	
}
