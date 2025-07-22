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
@Service("Gstr1B2BAFinalStructure")
public class Gstr1B2BAFinalStructure {
	
	public List<Gstr1SummaryScreenRespDto> getB2BAEyList(
			List<Gstr1SummaryScreenRespDto> b2baEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {
				
		List<Gstr1SummaryScreenRespDto> viewB2baFiltered = eySummaryListFromView
				.stream().filter(p -> "B2BA".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewB2baFiltered != null & viewB2baFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr1SummaryScreenRespDto> defaultB2baFiltered = b2baEYList
					.stream()
					.filter(p -> "B2BA".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2baFiltered.forEach(defaultB2ba -> {
				// then remove it from List
				b2baEYList.remove(defaultB2ba);
			});
			
			viewB2baFiltered.forEach(viewB2ba -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewB2ba.getTaxDocType());
				summaryRespDto.setAspTaxableValue(viewB2ba.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewB2ba.getAspTaxPayble());
				summaryRespDto.setAspInvoiceValue(viewB2ba.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewB2ba.getAspIgst());
				summaryRespDto.setAspSgst(viewB2ba.getAspSgst());
				summaryRespDto.setAspCgst(viewB2ba.getAspCgst());
				summaryRespDto.setAspCess(viewB2ba.getAspCess());
				summaryRespDto.setAspCount(viewB2ba.getAspCount());
				summaryRespDto.setGstnTaxableValue(viewB2ba.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewB2ba.getGstnTaxPayble());
				summaryRespDto.setGstnInvoiceValue(viewB2ba.getGstnInvoiceValue());
				summaryRespDto.setGstnCount(viewB2ba.getGstnCount());
				summaryRespDto.setGstnIgst(viewB2ba.getGstnIgst());
				summaryRespDto.setGstnCgst(viewB2ba.getGstnCgst());
				summaryRespDto.setGstnSgst(viewB2ba.getGstnSgst());
				summaryRespDto.setGstnCess(viewB2ba.getGstnCess());
				summaryRespDto.setDiffTaxableValue(viewB2ba.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewB2ba.getDiffTaxPayble());
				summaryRespDto.setDiffInvoiceValue(viewB2ba.getDiffInvoiceValue());
				summaryRespDto.setDiffIgst(viewB2ba.getDiffIgst());
				summaryRespDto.setDiffCgst(viewB2ba.getDiffCgst());
				summaryRespDto.setDiffSgst(viewB2ba.getDiffSgst());
				summaryRespDto.setDiffCess(viewB2ba.getDiffCess());
				summaryRespDto.setDiffCount(viewB2ba.getDiffCount());
				
				b2baEYList.add(summaryRespDto);
			});
		}
		return b2baEYList;
	}

}
