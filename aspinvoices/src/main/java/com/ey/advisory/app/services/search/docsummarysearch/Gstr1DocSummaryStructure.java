/**
 * 
 */
package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenDocRespDto;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1DocSummaryStructure")
public class Gstr1DocSummaryStructure {

	@Autowired
	@Qualifier("Gstr1DocFinalStructure")
	private Gstr1DocFinalStructure gstr1DocFinalStructure;


	public List<Gstr1SummaryScreenDocRespDto> gstr1DocResp(
			List<Gstr1SummaryScreenDocRespDto> docEySummary) {

		List<Gstr1SummaryScreenDocRespDto> defaultB2bEYList = 
				getDefaultDocEYStructure();

		List<Gstr1SummaryScreenDocRespDto> docRespbody = gstr1DocFinalStructure
				.getDocEyList(defaultB2bEYList, docEySummary);

		return docRespbody;

	}
	
	private List<Gstr1SummaryScreenDocRespDto> getDefaultDocEYStructure() {

		List<Gstr1SummaryScreenDocRespDto> defaultDocEY = new ArrayList<>();

		Gstr1SummaryScreenDocRespDto docEy = new Gstr1SummaryScreenDocRespDto();
		docEy.setTaxDocType("DOC ISSUED");
		docEy.setTotal(0);
		docEy.setAspCancelled(0);
		docEy.setAspNetIssued(0);
		docEy.setAspTotal(0);
		docEy.setGstnCancelled(0);
		docEy.setGstnNetIssued(0);
		docEy.setGstnTotal(0);
		docEy.setDiffCancelled(0);
		docEy.setDiffNetIssued(0);
		docEy.setDiffTotal(0);
		
		defaultDocEY.add(docEy);
		return defaultDocEY;
	
}
	
}
