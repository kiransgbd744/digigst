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
@Service("Gstr1B2CLFinalStructure")
public class Gstr1B2CLFinalStructure {

	public List<Gstr1SummaryScreenRespDto> getB2CLEyList(
			List<Gstr1SummaryScreenRespDto> b2clEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {

		List<Gstr1SummaryScreenRespDto> viewB2clFiltered = eySummaryListFromView
				.stream()
				.filter(p -> "B2CL".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewB2clFiltered != null & viewB2clFiltered.size() > 0) {

			List<Gstr1SummaryScreenRespDto> defaultB2clFiltered = b2clEYList
					.stream()
					.filter(p -> "B2CL".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2clFiltered.forEach(defaultB2cl -> {
				// then remove it from List
				b2clEYList.remove(defaultB2cl);
			});

			viewB2clFiltered.forEach(viewB2cl -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewB2cl.getTaxDocType());
				summaryRespDto
						.setAspTaxableValue(viewB2cl.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewB2cl.getAspTaxPayble());
				summaryRespDto
						.setAspInvoiceValue(viewB2cl.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewB2cl.getAspIgst());
				summaryRespDto.setAspSgst(viewB2cl.getAspSgst());
				summaryRespDto.setAspCgst(viewB2cl.getAspCgst());
				summaryRespDto.setAspCess(viewB2cl.getAspCess());
				summaryRespDto.setAspCount(viewB2cl.getAspCount());
				summaryRespDto
						.setGstnTaxableValue(viewB2cl.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewB2cl.getGstnTaxPayble());
				summaryRespDto
						.setGstnInvoiceValue(viewB2cl.getGstnInvoiceValue());
				summaryRespDto.setGstnCount(viewB2cl.getGstnCount());
				summaryRespDto.setGstnIgst(viewB2cl.getGstnIgst());
				summaryRespDto.setGstnCgst(viewB2cl.getGstnCgst());
				summaryRespDto.setGstnSgst(viewB2cl.getGstnSgst());
				summaryRespDto.setGstnCess(viewB2cl.getGstnCess());
				summaryRespDto
						.setDiffTaxableValue(viewB2cl.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewB2cl.getDiffTaxPayble());
				summaryRespDto
						.setDiffInvoiceValue(viewB2cl.getDiffInvoiceValue());
				summaryRespDto.setDiffIgst(viewB2cl.getDiffIgst());
				summaryRespDto.setDiffCgst(viewB2cl.getDiffCgst());
				summaryRespDto.setDiffSgst(viewB2cl.getDiffSgst());
				summaryRespDto.setDiffCess(viewB2cl.getDiffCess());
				summaryRespDto.setDiffCount(viewB2cl.getDiffCount());

				b2clEYList.add(summaryRespDto);
			});
		}
		return b2clEYList;
	}

}
