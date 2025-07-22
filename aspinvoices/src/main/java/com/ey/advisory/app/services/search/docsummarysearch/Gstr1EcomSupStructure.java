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
 * @author Siva.Reddy
 *
 */
@Service("Gstr1EcomSupStructure")
public class Gstr1EcomSupStructure {

	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1Tbl14and15FinalStructure")
	private Gstr1Tbl14and15FinalStructure gstr1Tbl14and15FinalStructure;

	public List<Gstr1SummaryScreenRespDto> gstr1tbl14and15Resp(
			List<Gstr1SummaryScreenRespDto> eySummary, String taxDocType) {

		List<Gstr1SummaryScreenRespDto> defaultEYList = getDefaulttbl14and15EYStructure(
				taxDocType);

		List<Gstr1SummaryScreenRespDto> respBody = gstr1Tbl14and15FinalStructure
				.getEyList(defaultEYList, eySummary,
						taxDocType);

		return respBody;

	}

	private List<Gstr1SummaryScreenRespDto> getDefaulttbl14and15EYStructure(
			String taxDocType) {

		List<Gstr1SummaryScreenRespDto> defaultRespBody = new ArrayList<>();

		Gstr1SummaryScreenRespDto respEy = new Gstr1SummaryScreenRespDto();
		respEy.setTaxDocType(taxDocType);
		respEy = defaultStructureUtil.gstr1DefaultStructure(respEy);

		defaultRespBody.add(respEy);
		return defaultRespBody;

	}
}
