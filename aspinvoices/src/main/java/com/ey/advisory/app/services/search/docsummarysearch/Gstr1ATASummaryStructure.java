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
@Service("Gstr1ATASummaryStructure")
public class Gstr1ATASummaryStructure {

	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1ATAFinalStructure")
	private Gstr1ATAFinalStructure gstr1ATAFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1ATAResp(
			List<Gstr1SummaryScreenRespDto> ataEySummary) {

		List<Gstr1SummaryScreenRespDto> defaultAtaEYList = 
				getDefaultATAEYStructure();

		List<Gstr1SummaryScreenRespDto> ataRespbody = gstr1ATAFinalStructure
				.getATAEyList(defaultAtaEYList, ataEySummary);

		return ataRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultATAEYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultATAEY = new ArrayList<>();

		Gstr1SummaryScreenRespDto ataEy = new Gstr1SummaryScreenRespDto();
		ataEy.setTaxDocType("ADV REC-A");
		ataEy = defaultStructureUtil.gstr1DefaultStructure(ataEy);
		
		defaultATAEY.add(ataEy);
		return defaultATAEY;
	
}
}
