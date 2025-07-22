/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.simplified.BasicDocGstnSummaryScreenSectionDaoImpl;
import com.ey.advisory.app.docs.dto.Gstr1SummaryDocSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryNilSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * @author BalaKrishna S
 *
 */
@Component("SimpleGstr1BasicGstnSummarySectionFetcher")
public class SimpleGstr1BasicGstnSummarySectionFetcher
		implements SimpleSummaryGstnSectionFetcher {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SimpleGstr1BasicGstnSummarySectionFetcher.class);
	
	@Autowired
	@Qualifier("BasicDocGstnSummaryScreenSectionDaoImpl")
	BasicDocGstnSummaryScreenSectionDaoImpl basicDocSummaryDao;

	@Override
	public Map<String, List<Gstr1SummarySectionDto>> fetch(
			Annexure1SummaryReqDto req) {
		// TODO Auto-generated method stub
		LOGGER.debug("loadBasicSummarySection() Execution Started");
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
	
	public Map<String, List<Gstr1SummaryNilSectionDto>> fetchNil(
			Annexure1SummaryReqDto req) {

		
		LOGGER.debug("loadBasicSummarySection() Execution Started");
		List<Gstr1SummaryNilSectionDto> result = basicDocSummaryDao
				.loadBasicSummarySectionNil(req);
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("LoadBasic Sumary Section Execution Done "
				+ "For Doc Issued and the list "
				+ "of detils are based on section wise---->"+result);
		}
		Map<String, List<Gstr1SummaryNilSectionDto>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTaxDocType()));
		return map;
	}
	
	@Override
	public Map<String, List<Gstr1SummaryDocSectionDto>> fetchDoc(
			Annexure1SummaryReqDto req) {

		
		LOGGER.debug("loadBasicSummarySection() Execution Started");
		List<Gstr1SummaryDocSectionDto> result = basicDocSummaryDao
				.loadBasicSummaryDocSection(req);
			
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("LoadBasic Sumary Section Execution Done "
				+ "For Doc Issued and the list "
				+ "of detils are based on section wise---->"+result);
		}
		Map<String, List<Gstr1SummaryDocSectionDto>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTaxDocType()));
		return map;
	}
}
