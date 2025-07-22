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
@Service("Gstr1TXPDAFinalStructure")
public class Gstr1TXPDAFinalStructure {

	public List<Gstr1SummaryScreenRespDto> getTXPDAEyList(
			List<Gstr1SummaryScreenRespDto> txpdaEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {
				
		List<Gstr1SummaryScreenRespDto> viewTXPDAFiltered = eySummaryListFromView
				.stream().filter(p -> "ADV ADJ-A".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewTXPDAFiltered != null & viewTXPDAFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr1SummaryScreenRespDto> defaultAtFiltered = txpdaEYList
					.stream()
					.filter(p -> "ADV ADJ-A".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultAtFiltered.forEach(defaultTxpda -> {
				// then remove it from List
				txpdaEYList.remove(defaultTxpda);
			});
			
			viewTXPDAFiltered.forEach(viewTxpda -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewTxpda.getTaxDocType());
				summaryRespDto.setAspTaxableValue(viewTxpda.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewTxpda.getAspTaxPayble());
				summaryRespDto.setAspInvoiceValue(viewTxpda.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewTxpda.getAspIgst());
				summaryRespDto.setAspSgst(viewTxpda.getAspSgst());
				summaryRespDto.setAspCgst(viewTxpda.getAspCgst());
				summaryRespDto.setAspCess(viewTxpda.getAspCess());
				summaryRespDto.setAspCount(viewTxpda.getAspCount());
				summaryRespDto.setGstnTaxableValue(viewTxpda.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewTxpda.getGstnTaxPayble());
				summaryRespDto.setGstnInvoiceValue(viewTxpda.getGstnInvoiceValue());
				summaryRespDto.setGstnCount(viewTxpda.getGstnCount());
				summaryRespDto.setGstnIgst(viewTxpda.getGstnIgst());
				summaryRespDto.setGstnCgst(viewTxpda.getGstnCgst());
				summaryRespDto.setGstnSgst(viewTxpda.getGstnSgst());
				summaryRespDto.setGstnCess(viewTxpda.getGstnCess());
				summaryRespDto.setDiffTaxableValue(viewTxpda.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewTxpda.getDiffTaxPayble());
				summaryRespDto.setDiffInvoiceValue(viewTxpda.getDiffInvoiceValue());
				summaryRespDto.setDiffIgst(viewTxpda.getDiffIgst());
				summaryRespDto.setDiffCgst(viewTxpda.getDiffCgst());
				summaryRespDto.setDiffSgst(viewTxpda.getDiffSgst());
				summaryRespDto.setDiffCess(viewTxpda.getDiffCess());
				summaryRespDto.setDiffCount(viewTxpda.getDiffCount());
				
				txpdaEYList.add(summaryRespDto);
			});
		}
		return txpdaEYList;
	}
	
}
