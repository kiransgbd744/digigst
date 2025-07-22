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
@Service("Gstr1B2CSFinalStructure")
public class Gstr1B2CSFinalStructure {

	public List<Gstr1SummaryScreenRespDto> getB2CSEyList(
			List<Gstr1SummaryScreenRespDto> b2csEYList,
			List<Gstr1SummaryScreenRespDto> eySummaryListFromView) {
				
		List<Gstr1SummaryScreenRespDto> viewb2csFiltered = eySummaryListFromView
				.stream().filter(p -> "B2CS".equalsIgnoreCase(p.getTaxDocType()))
				.collect(Collectors.toList());
		if (viewb2csFiltered != null & viewb2csFiltered.size() > 0) {
			
			List<Gstr1SummaryScreenRespDto> defaultB2csFiltered = b2csEYList
					.stream()
					.filter(p -> "B2CS".equalsIgnoreCase(p.getTaxDocType()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultB2csFiltered.forEach(defaultB2CS -> {
				// then remove it from List
				b2csEYList.remove(defaultB2CS);
			});
			
			viewb2csFiltered.forEach(viewb2cs -> {
				Gstr1SummaryScreenRespDto summaryRespDto = 
						new Gstr1SummaryScreenRespDto();
				summaryRespDto.setTaxDocType(viewb2cs.getTaxDocType());
				summaryRespDto.setAspTaxableValue(viewb2cs.getAspTaxableValue());
				summaryRespDto.setAspTaxPayble(viewb2cs.getAspTaxPayble());
				summaryRespDto.setAspInvoiceValue(viewb2cs.getAspInvoiceValue());
				summaryRespDto.setAspIgst(viewb2cs.getAspIgst());
				summaryRespDto.setAspSgst(viewb2cs.getAspSgst());
				summaryRespDto.setAspCgst(viewb2cs.getAspCgst());
				summaryRespDto.setAspCess(viewb2cs.getAspCess());
				summaryRespDto.setAspCount(viewb2cs.getAspCount());
				summaryRespDto.setGstnTaxableValue(viewb2cs.getGstnTaxableValue());
				summaryRespDto.setGstnTaxPayble(viewb2cs.getGstnTaxPayble());
				summaryRespDto.setGstnInvoiceValue(viewb2cs.getGstnInvoiceValue());
				summaryRespDto.setGstnCount(viewb2cs.getGstnCount());
				summaryRespDto.setGstnIgst(viewb2cs.getGstnIgst());
				summaryRespDto.setGstnCgst(viewb2cs.getGstnCgst());
				summaryRespDto.setGstnSgst(viewb2cs.getGstnSgst());
				summaryRespDto.setGstnCess(viewb2cs.getGstnCess());
				summaryRespDto.setDiffTaxableValue(viewb2cs.getDiffTaxableValue());
				summaryRespDto.setDiffTaxPayble(viewb2cs.getDiffTaxPayble());
				summaryRespDto.setDiffInvoiceValue(viewb2cs.getDiffInvoiceValue());
				summaryRespDto.setDiffIgst(viewb2cs.getDiffIgst());
				summaryRespDto.setDiffCgst(viewb2cs.getDiffCgst());
				summaryRespDto.setDiffSgst(viewb2cs.getDiffSgst());
				summaryRespDto.setDiffCess(viewb2cs.getDiffCess());
				summaryRespDto.setDiffCount(viewb2cs.getDiffCount());
				
				b2csEYList.add(summaryRespDto);
			});
		}
		return b2csEYList;
	}

}
