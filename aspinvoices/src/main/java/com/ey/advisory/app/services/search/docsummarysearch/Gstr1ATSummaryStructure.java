/**
 * 
 */
package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1ATSummaryStructure")
public class Gstr1ATSummaryStructure {

	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1ATFinalStructure")
	private Gstr1ATFinalStructure gstr1ATFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1ATResp(
			List<Gstr1SummaryScreenRespDto> atEySummary) {

		List<Gstr1SummaryScreenRespDto> defaultAtEYList = 
				getDefaultATEYStructure();

		List<Gstr1SummaryScreenRespDto> atRespbody = gstr1ATFinalStructure
				.getATEyList(defaultAtEYList, atEySummary);

		return atRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultATEYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultATEY = new ArrayList<>();

		Gstr1SummaryScreenRespDto atEy = new Gstr1SummaryScreenRespDto();
		atEy.setTaxDocType("ADV REC");
		atEy = defaultStructureUtil.gstr1DefaultStructure(atEy);
		
		defaultATEY.add(atEy);
		return defaultATEY;
	
}
}
