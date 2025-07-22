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
@Service("Gstr1B2CSAFinalStructure")
public class Gstr1B2CSAFinalStructure {

	public List<Gstr1SummaryScreenRespDto> getB2CSAEyList(
			List<Gstr1SummaryScreenRespDto> b2csaEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {
				
		List<Gstr1SummaryScreenRespDto> viewb2csaFiltered = eySummaryListFromView
				.stream().filter(p -> "B2CSA".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewb2csaFiltered != null & viewb2csaFiltered.size() > 0) {
			
			List<Gstr1SummaryScreenRespDto> defaultB2csFiltered = b2csaEYList
					.stream()
					.filter(p -> "B2CSA".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2csFiltered.forEach(defaultB2CSA -> {
				// then remove it from List
				b2csaEYList.remove(defaultB2CSA);
			});
			
			viewb2csaFiltered.forEach(viewb2csa -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewb2csa.getTaxDocType());
				summaryRespDto.setAspTaxableValue(viewb2csa.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewb2csa.getAspTaxPayble());
				summaryRespDto.setAspInvoiceValue(viewb2csa.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewb2csa.getAspIgst());
				summaryRespDto.setAspSgst(viewb2csa.getAspSgst());
				summaryRespDto.setAspCgst(viewb2csa.getAspCgst());
				summaryRespDto.setAspCess(viewb2csa.getAspCess());
				summaryRespDto.setAspCount(viewb2csa.getAspCount());
				summaryRespDto.setGstnTaxableValue(viewb2csa.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewb2csa.getGstnTaxPayble());
				summaryRespDto.setGstnInvoiceValue(viewb2csa.getGstnInvoiceValue());
				summaryRespDto.setGstnCount(viewb2csa.getGstnCount());
				summaryRespDto.setGstnIgst(viewb2csa.getGstnIgst());
				summaryRespDto.setGstnCgst(viewb2csa.getGstnCgst());
				summaryRespDto.setGstnSgst(viewb2csa.getGstnSgst());
				summaryRespDto.setGstnCess(viewb2csa.getGstnCess());
				summaryRespDto.setDiffTaxableValue(viewb2csa.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewb2csa.getDiffTaxPayble());
				summaryRespDto.setDiffInvoiceValue(viewb2csa.getDiffInvoiceValue());
				summaryRespDto.setDiffIgst(viewb2csa.getDiffIgst());
				summaryRespDto.setDiffCgst(viewb2csa.getDiffCgst());
				summaryRespDto.setDiffSgst(viewb2csa.getDiffSgst());
				summaryRespDto.setDiffCess(viewb2csa.getDiffCess());
				summaryRespDto.setDiffCount(viewb2csa.getDiffCount());
				
				b2csaEYList.add(summaryRespDto);
			});
		}
		return b2csaEYList;
	}

}
