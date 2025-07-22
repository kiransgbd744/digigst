package com.ey.advisory.app.services.search.simplified.docsummary;

/**
 * 
 */


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;

/**
 * @author BalaKrishna S
 *
 */
@Service("Gstr1CDNRFinalStructure")
public class Gstr1CDNRFinalStructure {

	public List<Gstr1SummaryScreenRespDto> getCDNREyList(
			List<Gstr1SummaryScreenRespDto> cdnrEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {
				
		List<Gstr1SummaryScreenRespDto> viewCdnrFiltered = eySummaryListFromView
				.stream().filter(p -> "CDNR".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewCdnrFiltered != null & viewCdnrFiltered.size() > 0) {
			
			List<Gstr1SummaryScreenRespDto> defaultCdnrFiltered = cdnrEYList
					.stream()
					.filter(p -> "CDNR".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultCdnrFiltered.forEach(defaultCDNR -> {
				// then remove it from List
				cdnrEYList.remove(defaultCDNR);
			});
			
			viewCdnrFiltered.forEach(viewcdnr -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewcdnr.getTaxDocType());
				summaryRespDto.setAspTaxableValue(viewcdnr.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewcdnr.getAspTaxPayble());
				summaryRespDto.setAspInvoiceValue(viewcdnr.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewcdnr.getAspIgst());
				summaryRespDto.setAspSgst(viewcdnr.getAspSgst());
				summaryRespDto.setAspCgst(viewcdnr.getAspCgst());
				summaryRespDto.setAspCess(viewcdnr.getAspCess());
				summaryRespDto.setAspCount(viewcdnr.getAspCount());
				summaryRespDto.setGstnTaxableValue(viewcdnr.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewcdnr.getGstnTaxPayble());
				summaryRespDto.setGstnInvoiceValue(viewcdnr.getGstnInvoiceValue());
				summaryRespDto.setGstnCount(viewcdnr.getGstnCount());
				summaryRespDto.setGstnIgst(viewcdnr.getGstnIgst());
				summaryRespDto.setGstnCgst(viewcdnr.getGstnCgst());
				summaryRespDto.setGstnSgst(viewcdnr.getGstnSgst());
				summaryRespDto.setGstnCess(viewcdnr.getGstnCess());
				summaryRespDto.setDiffTaxableValue(viewcdnr.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewcdnr.getDiffTaxPayble());
				summaryRespDto.setDiffInvoiceValue(viewcdnr.getDiffInvoiceValue());
				summaryRespDto.setDiffIgst(viewcdnr.getDiffIgst());
				summaryRespDto.setDiffCgst(viewcdnr.getDiffCgst());
				summaryRespDto.setDiffSgst(viewcdnr.getDiffSgst());
				summaryRespDto.setDiffCess(viewcdnr.getDiffCess());
				summaryRespDto.setDiffCount(viewcdnr.getDiffCount());
				
				cdnrEYList.add(summaryRespDto);
			});
		}
		return cdnrEYList;
	}
}
