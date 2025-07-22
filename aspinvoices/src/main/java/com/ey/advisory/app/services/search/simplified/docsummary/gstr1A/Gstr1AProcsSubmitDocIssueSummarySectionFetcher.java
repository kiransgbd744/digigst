/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary.gstr1A;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1SummaryDocSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryNilSectionDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@Service("Gstr1AProcsSubmitDocIssueSummarySectionFetcher")
public class Gstr1AProcsSubmitDocIssueSummarySectionFetcher
		implements SimpleSummaryGstr1ADocSectionFetcher {

	@Autowired
	@Qualifier("Gstr1AProcsSubmitDocIssuedScreenSectionDaoImpl")
	Gstr1AProcsSubmitDocIssuedScreenSectionDaoImpl basicDocSummaryDao;

	@Override
	public Map<String, List<Gstr1SummaryDocSectionDto>> fetch(
			Annexure1SummaryReqDto req) {

		LOGGER.debug("loadBasicSummarySection() Execution Started");
		List<Gstr1SummaryDocSectionDto> result = basicDocSummaryDao
				.loadBasicSummarySection(req);
		LOGGER.debug("LoadBasic Sumary Section Execution Done "
				+ "For Doc Issued and the list "
				+ "of detils are based on section wise---->" + result);

		Map<String, List<Gstr1SummaryDocSectionDto>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTaxDocType()));
		return map;
	}

	/**
	 * For Nil Section Fetching Data
	 */
	@Override
	public Map<String, List<Gstr1SummaryNilSectionDto>> fetchNil(
			Annexure1SummaryReqDto req) {

		LOGGER.debug("loadBasicSummarySection() Execution Started");
		List<Gstr1SummaryNilSectionDto> result = basicDocSummaryDao
				.loadBasicSummarySectionNil(req);
		LOGGER.debug("LoadBasic Sumary Section Execution Done "
				+ "For Doc Issued and the list "
				+ "of detils are based on section wise---->" + result);

		Map<String, List<Gstr1SummaryNilSectionDto>> map = result.stream()
				.collect(Collectors.groupingBy(e -> e.getTaxDocType()));
		return map;
	}

}
