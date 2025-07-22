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
@Service("Gstr1TXPDSummaryStructure")
public class Gstr1TXPDSummaryStructure {

	@Autowired
	@Qualifier("Gstr1SummaryScreenStructureUtil")
	private Gstr1SummaryScreenStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("Gstr1TXPDFinalStructure")
	private Gstr1TXPDFinalStructure gstr1TXPDFinalStructure;


	public List<Gstr1SummaryScreenRespDto> gstr1TXPDResp(
			List<Gstr1SummaryScreenRespDto> txpdEySummary) {

		List<Gstr1SummaryScreenRespDto> defaulttxpdEYList = 
				getDefaultTXPDEYStructure();

		List<Gstr1SummaryScreenRespDto> txpdRespbody = gstr1TXPDFinalStructure
				.getTXPDEyList(defaulttxpdEYList, txpdEySummary);

		return txpdRespbody;

	}
	
	private List<Gstr1SummaryScreenRespDto> getDefaultTXPDEYStructure() {

		List<Gstr1SummaryScreenRespDto> defaultTXPDEY = new ArrayList<>();

		Gstr1SummaryScreenRespDto txpdEy = new Gstr1SummaryScreenRespDto();
		txpdEy.setTaxDocType("ADV ADJ");
		txpdEy = defaultStructureUtil.gstr1DefaultStructure(txpdEy);
		
		defaultTXPDEY.add(txpdEy);
		return defaultTXPDEY;
	
}
}
