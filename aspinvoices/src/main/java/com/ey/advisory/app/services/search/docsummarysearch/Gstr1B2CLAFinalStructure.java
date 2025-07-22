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
@Service("Gstr1B2CLAFinalStructure")
public class Gstr1B2CLAFinalStructure {

	public List<Gstr1SummaryScreenRespDto> getB2CLAEyList(
			List<Gstr1SummaryScreenRespDto> b2claEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {

		List<Gstr1SummaryScreenRespDto> viewB2claFiltered = eySummaryListFromView
				.stream()
				.filter(p -> "B2CLA".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewB2claFiltered != null & viewB2claFiltered.size() > 0) {

			List<Gstr1SummaryScreenRespDto> defaultB2claFiltered = b2claEYList
					.stream()
					.filter(p -> "B2CLA".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2claFiltered.forEach(defaultB2cla -> {
				// then remove it from List
				b2claEYList.remove(defaultB2cla);
			});

			viewB2claFiltered.forEach(viewB2cla -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewB2cla.getTaxDocType());
				summaryRespDto
						.setAspTaxableValue(viewB2cla.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewB2cla.getAspTaxPayble());
				summaryRespDto
						.setAspInvoiceValue(viewB2cla.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewB2cla.getAspIgst());
				summaryRespDto.setAspSgst(viewB2cla.getAspSgst());
				summaryRespDto.setAspCgst(viewB2cla.getAspCgst());
				summaryRespDto.setAspCess(viewB2cla.getAspCess());
				summaryRespDto.setAspCount(viewB2cla.getAspCount());
				summaryRespDto
						.setGstnTaxableValue(viewB2cla.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewB2cla.getGstnTaxPayble());
				summaryRespDto
						.setGstnInvoiceValue(viewB2cla.getGstnInvoiceValue());
				summaryRespDto.setGstnCount(viewB2cla.getGstnCount());
				summaryRespDto.setGstnIgst(viewB2cla.getGstnIgst());
				summaryRespDto.setGstnCgst(viewB2cla.getGstnCgst());
				summaryRespDto.setGstnSgst(viewB2cla.getGstnSgst());
				summaryRespDto.setGstnCess(viewB2cla.getGstnCess());
				summaryRespDto
						.setDiffTaxableValue(viewB2cla.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewB2cla.getDiffTaxPayble());
				summaryRespDto
						.setDiffInvoiceValue(viewB2cla.getDiffInvoiceValue());
				summaryRespDto.setDiffIgst(viewB2cla.getDiffIgst());
				summaryRespDto.setDiffCgst(viewB2cla.getDiffCgst());
				summaryRespDto.setDiffSgst(viewB2cla.getDiffSgst());
				summaryRespDto.setDiffCess(viewB2cla.getDiffCess());
				summaryRespDto.setDiffCount(viewB2cla.getDiffCount());

				b2claEYList.add(summaryRespDto);
			});
		}
		return b2claEYList;
	}

}
