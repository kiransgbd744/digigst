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
@Service("Gstr1EXPFinalStructure")
public class Gstr1EXPFinalStructure {

	public List<Gstr1SummaryScreenRespDto> getEXPEyList(
			List<Gstr1SummaryScreenRespDto> expEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {
				
		List<Gstr1SummaryScreenRespDto> viewExpFiltered = eySummaryListFromView
				.stream().filter(p -> "EXPORTS".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewExpFiltered != null & viewExpFiltered.size() > 0) {
			
			List<Gstr1SummaryScreenRespDto> defaultExpFiltered = expEYList
					.stream()
					.filter(p -> "EXPORTS".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultExpFiltered.forEach(defaultExp -> {
				// then remove it from List
				expEYList.remove(defaultExp);
			});
			
			viewExpFiltered.forEach(viewexp -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewexp.getTaxDocType());
				summaryRespDto.setAspTaxableValue(viewexp.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewexp.getAspTaxPayble());
				summaryRespDto.setAspInvoiceValue(viewexp.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewexp.getAspIgst());
				summaryRespDto.setAspSgst(viewexp.getAspSgst());
				summaryRespDto.setAspCgst(viewexp.getAspCgst());
				summaryRespDto.setAspCess(viewexp.getAspCess());
				summaryRespDto.setAspCount(viewexp.getAspCount());
				summaryRespDto.setGstnTaxableValue(viewexp.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewexp.getGstnTaxPayble());
				summaryRespDto.setGstnInvoiceValue(viewexp.getGstnInvoiceValue());
				summaryRespDto.setGstnCount(viewexp.getGstnCount());
				summaryRespDto.setGstnIgst(viewexp.getGstnIgst());
				summaryRespDto.setGstnCgst(viewexp.getGstnCgst());
				summaryRespDto.setGstnSgst(viewexp.getGstnSgst());
				summaryRespDto.setGstnCess(viewexp.getGstnCess());
				summaryRespDto.setDiffTaxableValue(viewexp.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewexp.getDiffTaxPayble());
				summaryRespDto.setDiffInvoiceValue(viewexp.getDiffInvoiceValue());
				summaryRespDto.setDiffIgst(viewexp.getDiffIgst());
				summaryRespDto.setDiffCgst(viewexp.getDiffCgst());
				summaryRespDto.setDiffSgst(viewexp.getDiffSgst());
				summaryRespDto.setDiffCess(viewexp.getDiffCess());
				summaryRespDto.setDiffCount(viewexp.getDiffCount());
				
				expEYList.add(summaryRespDto);
			});
		}
		return expEYList;
	}
}
