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
@Service("Gstr1TXPDASummaryStructure")
public class Gstr1TXPDASummaryStructure {

	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1TXPDAFinalStructure")
	private Gstr1TXPDAFinalStructure gstr1TXPDAFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1TXPDAResp(
			List<Gstr1SummaryScreenRespDto> txpdaEySummary) {

		List<Gstr1SummaryScreenRespDto> defaulttxpdaEYList = 
				getDefaultTXPDAEYStructure();

		List<Gstr1SummaryScreenRespDto> txpdaRespbody = gstr1TXPDAFinalStructure
				.getTXPDAEyList(defaulttxpdaEYList, txpdaEySummary);

		return txpdaRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultTXPDAEYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultTXPDAEY = new ArrayList<>();

		Gstr1SummaryScreenRespDto txpdaEy = new Gstr1SummaryScreenRespDto();
		txpdaEy.setTaxDocType("ADV ADJ-A");
		txpdaEy = defaultStructureUtil.gstr1DefaultStructure(txpdaEy);
		
		defaultTXPDAEY.add(txpdaEy);
		return defaultTXPDAEY;
	
}
	
}
