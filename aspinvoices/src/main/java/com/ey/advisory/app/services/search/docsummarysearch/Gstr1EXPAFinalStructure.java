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
@Service("Gstr1EXPAFinalStructure")
public class Gstr1EXPAFinalStructure {

	public List<Gstr1SummaryScreenRespDto> getEXPAEyList(
			List<Gstr1SummaryScreenRespDto> expaEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {

		List<Gstr1SummaryScreenRespDto> viewExpaFiltered = eySummaryListFromView
				.stream()
				.filter(p -> "EXPORTS-A".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewExpaFiltered != null & viewExpaFiltered.size() > 0) {

			List<Gstr1SummaryScreenRespDto> defaultexpaFiltered = expaEYList
					.stream()
					.filter(p -> "EXPORTS-A".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultexpaFiltered.forEach(defaultExpa -> {
				// then remove it from List
				expaEYList.remove(defaultExpa);
			});

			viewExpaFiltered.forEach(viewexpa -> {
				Gstr1SummaryScreenRespDto summaryRespDto = new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewexpa.getTaxDocType());
				summaryRespDto
						.setAspTaxableValue(viewexpa.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewexpa.getAspTaxPayble());
				summaryRespDto
						.setAspInvoiceValue(viewexpa.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewexpa.getAspIgst());
				summaryRespDto.setAspSgst(viewexpa.getAspSgst());
				summaryRespDto.setAspCgst(viewexpa.getAspCgst());
				summaryRespDto.setAspCess(viewexpa.getAspCess());
				summaryRespDto.setAspCount(viewexpa.getAspCount());
				summaryRespDto
						.setGstnTaxableValue(viewexpa.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewexpa.getGstnTaxPayble());
				summaryRespDto
						.setGstnInvoiceValue(viewexpa.getGstnInvoiceValue());
				summaryRespDto.setGstnCount(viewexpa.getGstnCount());
				summaryRespDto.setGstnIgst(viewexpa.getGstnIgst());
				summaryRespDto.setGstnCgst(viewexpa.getGstnCgst());
				summaryRespDto.setGstnSgst(viewexpa.getGstnSgst());
				summaryRespDto.setGstnCess(viewexpa.getGstnCess());
				summaryRespDto
						.setDiffTaxableValue(viewexpa.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewexpa.getDiffTaxPayble());
				summaryRespDto
						.setDiffInvoiceValue(viewexpa.getDiffInvoiceValue());
				summaryRespDto.setDiffIgst(viewexpa.getDiffIgst());
				summaryRespDto.setDiffCgst(viewexpa.getDiffCgst());
				summaryRespDto.setDiffSgst(viewexpa.getDiffSgst());
				summaryRespDto.setDiffCess(viewexpa.getDiffCess());
				summaryRespDto.setDiffCount(viewexpa.getDiffCount());

				expaEYList.add(summaryRespDto);
			});
		}
		return expaEYList;
	}

}
