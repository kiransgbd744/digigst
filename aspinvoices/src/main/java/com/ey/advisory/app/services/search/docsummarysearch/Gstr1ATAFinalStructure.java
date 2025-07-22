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
@Service("Gstr1ATAFinalStructure")
public class Gstr1ATAFinalStructure {

	public List<Gstr1SummaryScreenRespDto> getATAEyList(
			List<Gstr1SummaryScreenRespDto> ataEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {
				
		List<Gstr1SummaryScreenRespDto> viewATAFiltered = eySummaryListFromView
				.stream().filter(p -> "ADV REC-A".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewATAFiltered != null & viewATAFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr1SummaryScreenRespDto> defaultAtaFiltered = ataEYList
					.stream()
					.filter(p -> "ADV REC-A".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultAtaFiltered.forEach(defaultAta -> {
				// then remove it from List
				ataEYList.remove(defaultAta);
			});
			
			viewATAFiltered.forEach(viewAta -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewAta.getTaxDocType());
				summaryRespDto.setAspTaxableValue(viewAta.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewAta.getAspTaxPayble());
				summaryRespDto.setAspInvoiceValue(viewAta.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewAta.getAspIgst());
				summaryRespDto.setAspSgst(viewAta.getAspSgst());
				summaryRespDto.setAspCgst(viewAta.getAspCgst());
				summaryRespDto.setAspCess(viewAta.getAspCess());
				summaryRespDto.setAspCount(viewAta.getAspCount());
				summaryRespDto.setGstnTaxableValue(viewAta.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewAta.getGstnTaxPayble());
				summaryRespDto.setGstnInvoiceValue(viewAta.getGstnInvoiceValue());
				summaryRespDto.setGstnCount(viewAta.getGstnCount());
				summaryRespDto.setGstnIgst(viewAta.getGstnIgst());
				summaryRespDto.setGstnCgst(viewAta.getGstnCgst());
				summaryRespDto.setGstnSgst(viewAta.getGstnSgst());
				summaryRespDto.setGstnCess(viewAta.getGstnCess());
				summaryRespDto.setDiffTaxableValue(viewAta.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewAta.getDiffTaxPayble());
				summaryRespDto.setDiffInvoiceValue(viewAta.getDiffInvoiceValue());
				summaryRespDto.setDiffIgst(viewAta.getDiffIgst());
				summaryRespDto.setDiffCgst(viewAta.getDiffCgst());
				summaryRespDto.setDiffSgst(viewAta.getDiffSgst());
				summaryRespDto.setDiffCess(viewAta.getDiffCess());
				summaryRespDto.setDiffCount(viewAta.getDiffCount());
				
				ataEYList.add(summaryRespDto);
			});
		}
		return ataEYList;
	}
}
