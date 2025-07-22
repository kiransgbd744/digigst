/**
 * 
 */
package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1SezwtFinalStructure")
public class Gstr1SezwtFinalStructure {

	public List<Gstr1SummaryScreenRespDto> getSezwtEyList(
			List<Gstr1SummaryScreenRespDto> sezwtEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {
				
		List<Gstr1SummaryScreenRespDto> viewSezwtFiltered = eySummaryListFromView
				.stream().filter(p -> "SEZWOP".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewSezwtFiltered != null & viewSezwtFiltered.size() > 0) {
			List<Gstr1SummaryScreenRespDto> defaultSezwtFiltered = sezwtEYList
					.stream()
					.filter(p -> "SEZWOP".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultSezwtFiltered.forEach(defaultSezwt -> {
				// then remove it from List
				sezwtEYList.remove(defaultSezwt);
			});
			
			viewSezwtFiltered.forEach(viewSezwt -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewSezwt.getTaxDocType());
				summaryRespDto.setAspTaxableValue(viewSezwt.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewSezwt.getAspTaxPayble());
				summaryRespDto.setAspInvoiceValue(viewSezwt.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewSezwt.getAspIgst());
				summaryRespDto.setAspSgst(viewSezwt.getAspSgst());
				summaryRespDto.setAspCgst(viewSezwt.getAspCgst());
				summaryRespDto.setAspCess(viewSezwt.getAspCess());
				summaryRespDto.setAspCount(viewSezwt.getAspCount());
			
				sezwtEYList.add(summaryRespDto);
			});
		}
		return sezwtEYList;
	}
}
