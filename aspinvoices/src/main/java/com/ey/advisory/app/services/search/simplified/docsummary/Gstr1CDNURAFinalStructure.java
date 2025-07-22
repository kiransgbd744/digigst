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
@Service("Gstr1CDNURAFinalStructure")
public class Gstr1CDNURAFinalStructure {

	public List<Gstr1SummaryScreenRespDto> getCDNURAEyList(
			List<Gstr1SummaryScreenRespDto> cdnuraEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {
				
		List<Gstr1SummaryScreenRespDto> viewCdnuraFiltered = eySummaryListFromView
				.stream().filter(p -> "CDNURA".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewCdnuraFiltered != null & viewCdnuraFiltered.size() > 0) {
			
			List<Gstr1SummaryScreenRespDto> defaultCdnurFiltered = cdnuraEYList
					.stream()
					.filter(p -> "CDNURA".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultCdnurFiltered.forEach(defaultCDNURA -> {
				// then remove it from List
				cdnuraEYList.remove(defaultCDNURA);
			});
			
			viewCdnuraFiltered.forEach(viewcdnura -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewcdnura.getTaxDocType());
				summaryRespDto.setAspTaxableValue(viewcdnura.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewcdnura.getAspTaxPayble());
				summaryRespDto.setAspInvoiceValue(viewcdnura.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewcdnura.getAspIgst());
				summaryRespDto.setAspSgst(viewcdnura.getAspSgst());
				summaryRespDto.setAspCgst(viewcdnura.getAspCgst());
				summaryRespDto.setAspCess(viewcdnura.getAspCess());
				summaryRespDto.setAspCount(viewcdnura.getAspCount());
				summaryRespDto.setGstnTaxableValue(viewcdnura.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewcdnura.getGstnTaxPayble());
				summaryRespDto.setGstnInvoiceValue(viewcdnura.getGstnInvoiceValue());
				summaryRespDto.setGstnCount(viewcdnura.getGstnCount());
				summaryRespDto.setGstnIgst(viewcdnura.getGstnIgst());
				summaryRespDto.setGstnCgst(viewcdnura.getGstnCgst());
				summaryRespDto.setGstnSgst(viewcdnura.getGstnSgst());
				summaryRespDto.setGstnCess(viewcdnura.getGstnCess());
				summaryRespDto.setDiffTaxableValue(viewcdnura.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewcdnura.getDiffTaxPayble());
				summaryRespDto.setDiffInvoiceValue(viewcdnura.getDiffInvoiceValue());
				summaryRespDto.setDiffIgst(viewcdnura.getDiffIgst());
				summaryRespDto.setDiffCgst(viewcdnura.getDiffCgst());
				summaryRespDto.setDiffSgst(viewcdnura.getDiffSgst());
				summaryRespDto.setDiffCess(viewcdnura.getDiffCess());
				summaryRespDto.setDiffCount(viewcdnura.getDiffCount());
				
				cdnuraEYList.add(summaryRespDto);
			});
		}
		return cdnuraEYList;
	}
	
}
