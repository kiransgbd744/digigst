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
@Service("Gstr1B2BFinalStructure")
public class Gstr1B2BFinalStructure {
	
	public List<Gstr1SummaryScreenRespDto> getB2BEyList(
			List<Gstr1SummaryScreenRespDto> b2bEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {
				
		List<Gstr1SummaryScreenRespDto> viewB2bFiltered = eySummaryListFromView
				.stream().filter(p -> "B2B".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewB2bFiltered != null & viewB2bFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr1SummaryScreenRespDto> defaultB2bFiltered = b2bEYList
					.stream()
					.filter(p -> "B2B".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2bFiltered.forEach(defaultB2b -> {
				// then remove it from List
				b2bEYList.remove(defaultB2b);
			});
			
			viewB2bFiltered.forEach(viewB2b -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewB2b.getTaxDocType());
				summaryRespDto.setAspTaxableValue(viewB2b.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewB2b.getAspTaxPayble());
				summaryRespDto.setAspInvoiceValue(viewB2b.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewB2b.getAspIgst());
				summaryRespDto.setAspSgst(viewB2b.getAspSgst());
				summaryRespDto.setAspCgst(viewB2b.getAspCgst());
				summaryRespDto.setAspCess(viewB2b.getAspCess());
				summaryRespDto.setAspCount(viewB2b.getAspCount());
				summaryRespDto.setGstnTaxableValue(viewB2b.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewB2b.getGstnTaxPayble());
				summaryRespDto.setGstnInvoiceValue(viewB2b.getGstnInvoiceValue());
				summaryRespDto.setGstnCount(viewB2b.getGstnCount());
				summaryRespDto.setGstnIgst(viewB2b.getGstnIgst());
				summaryRespDto.setGstnCgst(viewB2b.getGstnCgst());
				summaryRespDto.setGstnSgst(viewB2b.getGstnSgst());
				summaryRespDto.setGstnCess(viewB2b.getGstnCess());
				summaryRespDto.setDiffTaxableValue(viewB2b.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewB2b.getDiffTaxPayble());
				summaryRespDto.setDiffInvoiceValue(viewB2b.getDiffInvoiceValue());
				summaryRespDto.setDiffIgst(viewB2b.getDiffIgst());
				summaryRespDto.setDiffCgst(viewB2b.getDiffCgst());
				summaryRespDto.setDiffSgst(viewB2b.getDiffSgst());
				summaryRespDto.setDiffCess(viewB2b.getDiffCess());
				summaryRespDto.setDiffCount(viewB2b.getDiffCount());
				
				b2bEYList.add(summaryRespDto);
			});
		}
		return b2bEYList;
	}
}
