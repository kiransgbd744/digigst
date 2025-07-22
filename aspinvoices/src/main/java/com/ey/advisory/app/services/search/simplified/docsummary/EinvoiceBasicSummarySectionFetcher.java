/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import org.checkerframework.common.value.qual.MinLenFieldInvariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.simplified.BasicDocSummaryScreenSectionDaoImpl;
import com.ey.advisory.app.data.daos.client.simplified.EinvoiceSummaryScreenSectionDaoImpl;
import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.EinvoiceProcessedReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author BalaKrishna S
 *
 */
@Slf4j
@Component("EinvoiceBasicSummarySectionFetcher")
public class EinvoiceBasicSummarySectionFetcher {
	
	@Autowired
	@Qualifier("EinvoiceSummaryScreenSectionDaoImpl")
	EinvoiceSummaryScreenSectionDaoImpl basicDocSummaryDao;

	public Map<String, List<Gstr1SummarySectionDto>> fetch(
			EinvoiceProcessedReqDto req) {
		// TODO Auto-generated method stub
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("loadBasicSummarySection() Execution Started");
		}
		List<Gstr1SummarySectionDto> result = basicDocSummaryDao
				.loadBasicSummarySection(req);
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("LoadBasic Sumary Section Execution Done and the list "
				+ "of detils are based on section wise---->"+result);
		}
		
		Map<String, List<Gstr1SummarySectionDto>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTaxDocType()));
		return map;
		
	}

}
