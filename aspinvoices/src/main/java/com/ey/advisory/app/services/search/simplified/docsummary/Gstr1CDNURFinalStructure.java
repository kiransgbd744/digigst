/**
 * 
 */
package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1CDNURFinalStructure")
public class Gstr1CDNURFinalStructure {

	public List<Gstr1SummaryScreenRespDto> getCDNUREyList(
			List<Gstr1SummaryScreenRespDto> cdnurEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {
				
		List<Gstr1SummaryScreenRespDto> viewCdnurFiltered = eySummaryListFromView
				.stream().filter(p -> "CDNUR".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewCdnurFiltered != null & viewCdnurFiltered.size() > 0) {
			
			List<Gstr1SummaryScreenRespDto> defaultCdnurFiltered = cdnurEYList
					.stream()
					.filter(p -> "CDNUR".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultCdnurFiltered.forEach(defaultCDNUR -> {
				// then remove it from List
				cdnurEYList.remove(defaultCDNUR);
			});
			
			viewCdnurFiltered.forEach(viewcdnur -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewcdnur.getTaxDocType());
				summaryRespDto.setAspTaxableValue(viewcdnur.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewcdnur.getAspTaxPayble());
				summaryRespDto.setAspInvoiceValue(viewcdnur.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewcdnur.getAspIgst());
				summaryRespDto.setAspSgst(viewcdnur.getAspSgst());
				summaryRespDto.setAspCgst(viewcdnur.getAspCgst());
				summaryRespDto.setAspCess(viewcdnur.getAspCess());
				summaryRespDto.setAspCount(viewcdnur.getAspCount());
				summaryRespDto.setGstnTaxableValue(viewcdnur.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewcdnur.getGstnTaxPayble());
				summaryRespDto.setGstnInvoiceValue(viewcdnur.getGstnInvoiceValue());
				summaryRespDto.setGstnCount(viewcdnur.getGstnCount());
				summaryRespDto.setGstnIgst(viewcdnur.getGstnIgst());
				summaryRespDto.setGstnCgst(viewcdnur.getGstnCgst());
				summaryRespDto.setGstnSgst(viewcdnur.getGstnSgst());
				summaryRespDto.setGstnCess(viewcdnur.getGstnCess());
				summaryRespDto.setDiffTaxableValue(viewcdnur.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewcdnur.getDiffTaxPayble());
				summaryRespDto.setDiffInvoiceValue(viewcdnur.getDiffInvoiceValue());
				summaryRespDto.setDiffIgst(viewcdnur.getDiffIgst());
				summaryRespDto.setDiffCgst(viewcdnur.getDiffCgst());
				summaryRespDto.setDiffSgst(viewcdnur.getDiffSgst());
				summaryRespDto.setDiffCess(viewcdnur.getDiffCess());
				summaryRespDto.setDiffCount(viewcdnur.getDiffCount());
				
				cdnurEYList.add(summaryRespDto);
			});
		}
		return cdnurEYList;
	}
	
}
