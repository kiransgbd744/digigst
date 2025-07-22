/**
 * 
 */
package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenDocRespDto;

/**
 * @author BalaKrishna S
 *
 */
@Service("ProcsSubmitDocSummaryStructure")
public class ProcsSubmitDocSummaryStructure {

	@Autowired
	@Qualifier("Gstr1DocFinalStructure")
	private Gstr1DocFinalStructure gstr1DocFinalStructure;


	public List<Gstr1SummaryScreenDocRespDto> gstr1DocResp(
			List<Gstr1SummaryScreenDocRespDto> docEySummary,String docType) {

		List<Gstr1SummaryScreenDocRespDto> defaultB2bEYList = 
				getDefaultDocEYStructure(docType);

		List<Gstr1SummaryScreenDocRespDto> docRespbody = getDocEyList(defaultB2bEYList, docEySummary,docType);

		return docRespbody;

	}
	
	private List<Gstr1SummaryScreenDocRespDto> getDefaultDocEYStructure(String docType) {

		List<Gstr1SummaryScreenDocRespDto> defaultDocEY = new ArrayList<>();

		Gstr1SummaryScreenDocRespDto docEy = new Gstr1SummaryScreenDocRespDto();
		docEy.setTaxDocType(docType);
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
	
	public List<Gstr1SummaryScreenDocRespDto> getDocEyList(
			List<Gstr1SummaryScreenDocRespDto> docEYList,
			List<Gstr1SummaryScreenDocRespDto> eySummaryListFromView,String docType) {
				
		List<Gstr1SummaryScreenDocRespDto> viewDocFiltered = eySummaryListFromView
				.stream().filter(p -> docType.equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		
		if (viewDocFiltered != null & viewDocFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr1SummaryScreenDocRespDto> defaultdocFiltered = docEYList
					.stream()
					.filter(p -> docType.equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultdocFiltered.forEach(defaultdoc -> {
				// then remove it from List
				docEYList.remove(defaultdoc);
			});
			
			viewDocFiltered.forEach(viewdoc -> {
				Gstr1SummaryScreenDocRespDto summaryRespDto = 
						new Gstr1SummaryScreenDocRespDto();
				summaryRespDto.setAspCancelled(viewdoc.getAspCancelled());
				summaryRespDto.setAspNetIssued(viewdoc.getAspNetIssued());
				summaryRespDto.setAspTotal(viewdoc.getAspTotal());
				summaryRespDto.setGstnCancelled(viewdoc.getGstnCancelled());
				summaryRespDto.setGstnNetIssued(viewdoc.getGstnNetIssued());
				summaryRespDto.setGstnTotal(viewdoc.getGstnTotal());
				summaryRespDto.setDiffCancelled(viewdoc.getDiffCancelled());
				summaryRespDto.setDiffNetIssued(viewdoc.getDiffNetIssued());
				summaryRespDto.setDiffTotal(viewdoc.getDiffTotal());
				summaryRespDto.setTaxDocType(viewdoc.getTaxDocType());
				docEYList.add(summaryRespDto);
			});
		}
		return docEYList;
	}
	
	
}
