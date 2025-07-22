/**
 * 
 */
package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenNilRespDto;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1NilFinalStructure")
public class Gstr1NilFinalStructure {

	public List<Gstr1SummaryScreenNilRespDto> getNilEyList(
			List<Gstr1SummaryScreenNilRespDto> nilEYList,
			List<Gstr1SummaryScreenNilRespDto> eySummaryListFromView) {
				
		List<Gstr1SummaryScreenNilRespDto> viewNilFiltered = eySummaryListFromView
				.stream().filter(p -> "NILEXTNON".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		
		if (viewNilFiltered != null && viewNilFiltered.size() > 0) {
			// then filter default List for NIL
			List<Gstr1SummaryScreenNilRespDto> defaultNilFiltered = nilEYList
					.stream()
					.filter(p -> "NILEXTNON".equalsIgnoreCase(p.getTaxDocType()))
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
