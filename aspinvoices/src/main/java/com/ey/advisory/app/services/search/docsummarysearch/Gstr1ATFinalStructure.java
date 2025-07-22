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
@Service("Gstr1ATFinalStructure")
public class Gstr1ATFinalStructure {

	public List<Gstr1SummaryScreenRespDto> getATEyList(
			List<Gstr1SummaryScreenRespDto> atEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {
				
		List<Gstr1SummaryScreenRespDto> viewATFiltered = eySummaryListFromView
				.stream().filter(p -> "ADV REC".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewATFiltered != null & viewATFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr1SummaryScreenRespDto> defaultAtFiltered = atEYList
					.stream()
					.filter(p -> "ADV REC".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultAtFiltered.forEach(defaultAt -> {
				// then remove it from List
				atEYList.remove(defaultAt);
			});
			
			viewATFiltered.forEach(viewAt -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewAt.getTaxDocType());
				summaryRespDto.setAspTaxableValue(viewAt.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewAt.getAspTaxPayble());
				summaryRespDto.setAspInvoiceValue(viewAt.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewAt.getAspIgst());
				summaryRespDto.setAspSgst(viewAt.getAspSgst());
				summaryRespDto.setAspCgst(viewAt.getAspCgst());
				summaryRespDto.setAspCess(viewAt.getAspCess());
				summaryRespDto.setAspCount(viewAt.getAspCount());
				summaryRespDto.setGstnTaxableValue(viewAt.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewAt.getGstnTaxPayble());
				summaryRespDto.setGstnInvoiceValue(viewAt.getGstnInvoiceValue());
				summaryRespDto.setGstnCount(viewAt.getGstnCount());
				summaryRespDto.setGstnIgst(viewAt.getGstnIgst());
				summaryRespDto.setGstnCgst(viewAt.getGstnCgst());
				summaryRespDto.setGstnSgst(viewAt.getGstnSgst());
				summaryRespDto.setGstnCess(viewAt.getGstnCess());
				summaryRespDto.setDiffTaxableValue(viewAt.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewAt.getDiffTaxPayble());
				summaryRespDto.setDiffInvoiceValue(viewAt.getDiffInvoiceValue());
				summaryRespDto.setDiffIgst(viewAt.getDiffIgst());
				summaryRespDto.setDiffCgst(viewAt.getDiffCgst());
				summaryRespDto.setDiffSgst(viewAt.getDiffSgst());
				summaryRespDto.setDiffCess(viewAt.getDiffCess());
				summaryRespDto.setDiffCount(viewAt.getDiffCount());
				
				atEYList.add(summaryRespDto);
			});
		}
		return atEYList;
	}
}
