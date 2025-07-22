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
@Service("Gstr1CDNRAFinalStructure")
public class Gstr1CDNRAFinalStructure {


	public List<Gstr1SummaryScreenRespDto> getCDNRAEyList(
			List<Gstr1SummaryScreenRespDto> cdnraEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {
				
		List<Gstr1SummaryScreenRespDto> viewCdnraFiltered = eySummaryListFromView
				.stream().filter(p -> "CDNRA".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewCdnraFiltered != null & viewCdnraFiltered.size() > 0) {
			
			List<Gstr1SummaryScreenRespDto> defaultCdnraFiltered = cdnraEYList
					.stream()
					.filter(p -> "CDNRA".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultCdnraFiltered.forEach(defaultCDNRA -> {
				// then remove it from List
				cdnraEYList.remove(defaultCDNRA);
			});
			
			viewCdnraFiltered.forEach(viewcdnra -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewcdnra.getTaxDocType());
				summaryRespDto.setAspTaxableValue(viewcdnra.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewcdnra.getAspTaxPayble());
				summaryRespDto.setAspInvoiceValue(viewcdnra.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewcdnra.getAspIgst());
				summaryRespDto.setAspSgst(viewcdnra.getAspSgst());
				summaryRespDto.setAspCgst(viewcdnra.getAspCgst());
				summaryRespDto.setAspCess(viewcdnra.getAspCess());
				summaryRespDto.setAspCount(viewcdnra.getAspCount());
				summaryRespDto.setGstnTaxableValue(viewcdnra.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewcdnra.getGstnTaxPayble());
				summaryRespDto.setGstnInvoiceValue(viewcdnra.getGstnInvoiceValue());
				summaryRespDto.setGstnCount(viewcdnra.getGstnCount());
				summaryRespDto.setGstnIgst(viewcdnra.getGstnIgst());
				summaryRespDto.setGstnCgst(viewcdnra.getGstnCgst());
				summaryRespDto.setGstnSgst(viewcdnra.getGstnSgst());
				summaryRespDto.setGstnCess(viewcdnra.getGstnCess());
				summaryRespDto.setDiffTaxableValue(viewcdnra.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewcdnra.getDiffTaxPayble());
				summaryRespDto.setDiffInvoiceValue(viewcdnra.getDiffInvoiceValue());
				summaryRespDto.setDiffIgst(viewcdnra.getDiffIgst());
				summaryRespDto.setDiffCgst(viewcdnra.getDiffCgst());
				summaryRespDto.setDiffSgst(viewcdnra.getDiffSgst());
				summaryRespDto.setDiffCess(viewcdnra.getDiffCess());
				summaryRespDto.setDiffCount(viewcdnra.getDiffCount());
				
				cdnraEYList.add(summaryRespDto);
			});
		}
		return cdnraEYList;
	}

	
}
