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
@Service("Gstr1TXPDFinalStructure")
public class Gstr1TXPDFinalStructure {

	public List<Gstr1SummaryScreenRespDto> getTXPDEyList(
			List<Gstr1SummaryScreenRespDto> txpdEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {
				
		List<Gstr1SummaryScreenRespDto> viewTXPDFiltered = eySummaryListFromView
				.stream().filter(p -> "ADV ADJ".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewTXPDFiltered != null & viewTXPDFiltered.size() > 0) {
			// then filter default List for 4A
			List<Gstr1SummaryScreenRespDto> defaultAtFiltered = txpdEYList
					.stream()
					.filter(p -> "ADV ADJ".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultAtFiltered.forEach(defaultTxpd -> {
				// then remove it from List
				txpdEYList.remove(defaultTxpd);
			});
			
			viewTXPDFiltered.forEach(viewTxpd -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewTxpd.getTaxDocType());
				summaryRespDto.setAspTaxableValue(viewTxpd.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewTxpd.getAspTaxPayble());
				summaryRespDto.setAspInvoiceValue(viewTxpd.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewTxpd.getAspIgst());
				summaryRespDto.setAspSgst(viewTxpd.getAspSgst());
				summaryRespDto.setAspCgst(viewTxpd.getAspCgst());
				summaryRespDto.setAspCess(viewTxpd.getAspCess());
				summaryRespDto.setAspCount(viewTxpd.getAspCount());
				summaryRespDto.setGstnTaxableValue(viewTxpd.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewTxpd.getGstnTaxPayble());
				summaryRespDto.setGstnInvoiceValue(viewTxpd.getGstnInvoiceValue());
				summaryRespDto.setGstnCount(viewTxpd.getGstnCount());
				summaryRespDto.setGstnIgst(viewTxpd.getGstnIgst());
				summaryRespDto.setGstnCgst(viewTxpd.getGstnCgst());
				summaryRespDto.setGstnSgst(viewTxpd.getGstnSgst());
				summaryRespDto.setGstnCess(viewTxpd.getGstnCess());
				summaryRespDto.setDiffTaxableValue(viewTxpd.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewTxpd.getDiffTaxPayble());
				summaryRespDto.setDiffInvoiceValue(viewTxpd.getDiffInvoiceValue());
				summaryRespDto.setDiffIgst(viewTxpd.getDiffIgst());
				summaryRespDto.setDiffCgst(viewTxpd.getDiffCgst());
				summaryRespDto.setDiffSgst(viewTxpd.getDiffSgst());
				summaryRespDto.setDiffCess(viewTxpd.getDiffCess());
				summaryRespDto.setDiffCount(viewTxpd.getDiffCount());
				
				txpdEYList.add(summaryRespDto);
			});
		}
		return txpdEYList;
	}
	
}
