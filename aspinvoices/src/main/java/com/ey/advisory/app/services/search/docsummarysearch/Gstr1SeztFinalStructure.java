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
@Service("Gstr1SeztFinalStructure")
public class Gstr1SeztFinalStructure {

	public List<Gstr1SummaryScreenRespDto> getSeztEyList(
			List<Gstr1SummaryScreenRespDto> seztEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {
				
		List<Gstr1SummaryScreenRespDto> viewSeztFiltered = eySummaryListFromView
				.stream().filter(p -> "SEZWP".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewSeztFiltered != null & viewSeztFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr1SummaryScreenRespDto> defaultAtFiltered = seztEYList
					.stream()
					.filter(p -> "SEZWP".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultAtFiltered.forEach(defaultSezt -> {
				// then remove it from List
				seztEYList.remove(defaultSezt);
			});
			
			viewSeztFiltered.forEach(viewSezt -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewSezt.getTaxDocType());
				summaryRespDto.setAspTaxableValue(viewSezt.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewSezt.getAspTaxPayble());
				summaryRespDto.setAspInvoiceValue(viewSezt.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewSezt.getAspIgst());
				summaryRespDto.setAspSgst(viewSezt.getAspSgst());
				summaryRespDto.setAspCgst(viewSezt.getAspCgst());
				summaryRespDto.setAspCess(viewSezt.getAspCess());
				summaryRespDto.setAspCount(viewSezt.getAspCount());
				seztEYList.add(summaryRespDto);
			});
		}
		return seztEYList;
	}
	
}
