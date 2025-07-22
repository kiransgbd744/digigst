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
@Service("Gstr1HSNFinalStructure")
public class Gstr1HSNFinalStructure {

	public List<Gstr1SummaryScreenRespDto> getHSNEyList(
			List<Gstr1SummaryScreenRespDto> hsnEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {
				
		List<Gstr1SummaryScreenRespDto> viewHsnFiltered = eySummaryListFromView
				.stream().filter(p -> "HSN".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewHsnFiltered != null & viewHsnFiltered.size() > 0) {
			// then filter default List for HSN
			List<Gstr1SummaryScreenRespDto> defaultHsnFiltered = hsnEYList
					.stream()
					.filter(p -> "HSN".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultHsnFiltered.forEach(defaultB2b -> {
				// then remove it from List
				hsnEYList.remove(defaultB2b);
			});
			
			viewHsnFiltered.forEach(viewHsn -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewHsn.getTaxDocType());
				summaryRespDto.setAspTaxableValue(viewHsn.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewHsn.getAspTaxPayble());
				summaryRespDto.setAspInvoiceValue(viewHsn.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewHsn.getAspIgst());
				summaryRespDto.setAspSgst(viewHsn.getAspSgst());
				summaryRespDto.setAspCgst(viewHsn.getAspCgst());
				summaryRespDto.setAspCess(viewHsn.getAspCess());
				summaryRespDto.setAspCount(viewHsn.getAspCount());
				summaryRespDto.setGstnTaxableValue(viewHsn.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewHsn.getGstnTaxPayble());
				summaryRespDto.setGstnInvoiceValue(viewHsn.getGstnInvoiceValue());
				summaryRespDto.setGstnCount(viewHsn.getGstnCount());
				summaryRespDto.setGstnIgst(viewHsn.getGstnIgst());
				summaryRespDto.setGstnCgst(viewHsn.getGstnCgst());
				summaryRespDto.setGstnSgst(viewHsn.getGstnSgst());
				summaryRespDto.setGstnCess(viewHsn.getGstnCess());
				summaryRespDto.setDiffTaxableValue(viewHsn.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewHsn.getDiffTaxPayble());
				summaryRespDto.setDiffInvoiceValue(viewHsn.getDiffInvoiceValue());
				summaryRespDto.setDiffIgst(viewHsn.getDiffIgst());
				summaryRespDto.setDiffCgst(viewHsn.getDiffCgst());
				summaryRespDto.setDiffSgst(viewHsn.getDiffSgst());
				summaryRespDto.setDiffCess(viewHsn.getDiffCess());
				summaryRespDto.setDiffCount(viewHsn.getDiffCount());
				
				hsnEYList.add(summaryRespDto);
			});
		}
		return hsnEYList;
	}
	
}
