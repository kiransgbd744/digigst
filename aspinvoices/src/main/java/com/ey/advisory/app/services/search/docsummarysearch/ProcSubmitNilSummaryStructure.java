/**
 * 
 */
package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenNilRespDto;

/**
 * @author BalaKrishna S
 *
 */
@Service("ProcSubmitNilSummaryStructure")
public class ProcSubmitNilSummaryStructure {

	public List<Gstr1SummaryScreenNilRespDto> gstr1NilResp(
			List<Gstr1SummaryScreenNilRespDto> nilEySummary,String docType) {

		List<Gstr1SummaryScreenNilRespDto> defaultNilEYList = 
				getDefaultNilEYStructure(docType);

		List<Gstr1SummaryScreenNilRespDto> nilRespbody = getNilEyList(defaultNilEYList, nilEySummary,docType);

		return nilRespbody;

	}
	
	private List<Gstr1SummaryScreenNilRespDto> getDefaultNilEYStructure(String docType) {

		List<Gstr1SummaryScreenNilRespDto> defaultNilEY = new ArrayList<>();

		Gstr1SummaryScreenNilRespDto nilEy = new Gstr1SummaryScreenNilRespDto();
		nilEy.setTaxDocType(docType);
		nilEy.setAspExempted(BigDecimal.ZERO);
		nilEy.setAspNitRated(BigDecimal.ZERO);
		nilEy.setAspNonGst(BigDecimal.ZERO);
		nilEy.setGstnExempted(BigDecimal.ZERO);
		nilEy.setGstnNitRated(BigDecimal.ZERO);
		nilEy.setGstnNonGst(BigDecimal.ZERO);
		nilEy.setDiffExempted(BigDecimal.ZERO);
		nilEy.setDiffNitRated(BigDecimal.ZERO);
		nilEy.setDiffNonGst(BigDecimal.ZERO);
		
		defaultNilEY.add(nilEy);
		return defaultNilEY;
	
}
	
	public List<Gstr1SummaryScreenNilRespDto> getNilEyList(
			List<Gstr1SummaryScreenNilRespDto> nilEYList,
			List<Gstr1SummaryScreenNilRespDto> eySummaryListFromView,String docType) {
				
		List<Gstr1SummaryScreenNilRespDto> viewNilFiltered = eySummaryListFromView
				.stream().filter(p -> docType.equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		
		if (viewNilFiltered != null && viewNilFiltered.size() > 0) {
			// then filter default List for NIL
			List<Gstr1SummaryScreenNilRespDto> defaultNilFiltered = nilEYList
					.stream()
					.filter(p -> docType.equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultNilFiltered.forEach(defaultNil -> {
				// then remove it from List
				nilEYList.remove(defaultNil);
			});
			
			viewNilFiltered.forEach(viewNil -> {
				Gstr1SummaryScreenNilRespDto summaryRespDto = 
						new Gstr1SummaryScreenNilRespDto();
				summaryRespDto.setTaxDocType(viewNil.getTaxDocType());
				summaryRespDto.setAspExempted(viewNil.getAspExempted());
				summaryRespDto.setAspNitRated(viewNil.getAspNitRated());
				summaryRespDto.setAspNonGst(viewNil.getAspNonGst());
				summaryRespDto.setGstnExempted(viewNil.getGstnExempted());
				summaryRespDto.setGstnNitRated(viewNil.getGstnNitRated());
				summaryRespDto.setGstnNonGst(viewNil.getGstnNonGst());
				summaryRespDto.setDiffExempted(viewNil.getDiffExempted());
				summaryRespDto.setDiffNitRated(viewNil.getDiffNitRated());
				summaryRespDto.setDiffNonGst(viewNil.getDiffNonGst());
				
				nilEYList.add(summaryRespDto);
			});
		}
		return nilEYList;
	}
	
	
	
}
